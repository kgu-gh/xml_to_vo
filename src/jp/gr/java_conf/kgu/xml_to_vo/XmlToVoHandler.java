package jp.gr.java_conf.kgu.xml_to_vo;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML TO VOのハンドラクラス。
 * 
 * @author kgu
 *
 */
public class XmlToVoHandler<T> extends DefaultHandler {

	/**
	 * エレメントパスの区切り文字。
	 */
	private static final String ELEMENT_DELIMITER = "/";
	
	/**
	 * アトリビュートの区切り文字。
	 */
	private static final String ATTRIBUTE_DELIMITER = "@";

	/**
	 * XMLのパスを表すスタック。
	 */
	private Stack<String> bindPathStack = new Stack<String>();
	
	/**
	 * 開始エレメントで取得したアトリビュート。
	 */
	private Attributes attrs;
	
	/**
	 * エレメントにはさまれたデータを保持するバッファ。
	 */
	private StringBuffer contentBuf;
	
	/**
	 * フィールドとバインドパスをマッピングしたMap。
	 */
	private Map<String, Field> fieldMapByBindPath;
	
	/**
	 * VO。
	 */
	private T vo;
	
	/**
	 * エラーを出力するかどうか。
	 */
	private boolean printError;
	
	/**
	 * コンストラクタ。
	 * 
	 * @param cls VOクラス
	 */
	public XmlToVoHandler(Class<T> cls) {
		
		try {
			vo = cls.newInstance();
			
			fieldMapByBindPath = getFieldMapByBindPath(cls);
			
		} catch (Exception e) {
			throw new XmlToVoException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		bindPathStack.push(localName);
		attrs = attributes;
		contentBuf = new StringBuffer();
		
		// バインドパスを取得し、エレメントのパスを除外
		String[] attrsOnly = toBindPathStrs();
		attrsOnly[0] = null;
		// VOのフィールド変数にアトリビュート値を設定
		setFieldValue(attrsOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		// VOのフィールド変数にエレメントにはさまれたデータを設定
		setFieldValue(toBindPathStrs()[0]);
		
		bindPathStack.pop();
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char ch[], int start, int length)
			throws SAXException {
		contentBuf.append(ch, start, length);
	}
	
	/**
	 * XMLパース中にスタックに設定した情報をバインドパス文字列に変換する。
	 * 
	 * @return バインドパス文字列の配列
	 */
	private String[] toBindPathStrs() {
		
		// エレメント+アトリビュート数分の配列確保
		String[] bindPaths = new String[1 + (attrs == null ? 0 : attrs.getLength())];
		
		StringBuffer sb = new StringBuffer();
		
		// バインドパス作成
		sb.append(ELEMENT_DELIMITER);
		for (int i = 0; i < bindPathStack.size(); i++) {
			sb.append(bindPathStack.get(i));
			if (i != (bindPathStack.size() - 1)) {
				sb.append(ELEMENT_DELIMITER);
			}
		}
		
		String bindPath = sb.toString();
		// エレメントのバインドパスとして保持
		bindPaths[0] = bindPath;
		
		for (int i = 1; i <= attrs.getLength(); i++) {
			// アトリビュートのバインドパスとして保持
			bindPaths[i] = 
				new StringBuffer()
					.append(bindPath)
					.append(ATTRIBUTE_DELIMITER)
					.append(attrs.getLocalName(i - 1))
					.toString();
		}
		
		return bindPaths;
	}
	
	/**
	 * バインドパスとフィールド変数をマッピングしたMapを返す。
	 * 
	 * @param cls VOクラス
	 * @return バインドパスとフィールド変数をマッピングしたMap
	 */
	private Map<String, Field> getFieldMapByBindPath(Class<T> cls) {
		
		// バインドパスとフィールド変数のマッピング
		Map<String, Field> fieldMapByBindPath = new HashMap<String, Field>();
		// VOのフィールド変数情報を取得
		Field[] fields = cls.getDeclaredFields();
		
		for (Field field : fields) {
			
			// フィールド変数に設定されているアノテーションを取得
			Annotation[] annotations = field.getAnnotations();
			
			for (Annotation annotation : annotations) {
				if (annotation instanceof BindPath) {
					// アノテーションがBindPathなら、バインドパスとフィールド変数をひもづけ
					BindPath bindPath = (BindPath) annotation;
					fieldMapByBindPath.put(bindPath.value(), field);
				}
			}
		}
		
		return fieldMapByBindPath;
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	private void setFieldValue(String... bindPathStrs) {
		
		for (String bindPathStr : bindPathStrs) {
			// バインドパスにマッピングされているフィールド変数情報取得
			Field field = fieldMapByBindPath.get(bindPathStr);
			if (field == null) {
				// マッピングなし
				continue;
			}
			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), vo.getClass());
				// フィールド変数のセッターメソッド取得
				Method setter = pd.getWriteMethod();
				// セッターメソッドの0番目の引数取得
				Class<?> type = setter.getParameterTypes()[0];
				
				Object convertedValue = null;
				if (isCollections(type)) {
					// セッターメソッドの引数がコレクション(Collection,List,Set)のとき
					// ゲッターメソッドからフィールド変数の値を取得
					Method getter = pd.getReadMethod();
					Collection collection = (Collection) getter.invoke(vo);
					if (collection == null) {
						// 実装クラスのインスタンスを生成
						collection = createConcreteClass(type);
					}
					
					if (field.getGenericType() instanceof ParameterizedType) {
						// コレクションにジェネリクスが指定されているとき、指定された型を使う
						ParameterizedType pType = (ParameterizedType) field.getGenericType();
						type = (Class<?>) pType.getActualTypeArguments()[0];
					} else {
						// コレクションにジェネリクスが指定されていないとき、String型を使う
						type = String.class;
					}
					// 設定する値をセッターメソッドの型に合わせて取得
					convertedValue = ValueConverter.convert(type, getContent(bindPathStr), printError);
					collection.add(convertedValue);
					convertedValue = collection;
					
				} else {
					// セッターメソッドの引数がコレクション(Collection,List,Set)以外のとき
					// 設定する値をセッターメソッドの型に合わせて取得
					convertedValue = ValueConverter.convert(type, getContent(bindPathStr), printError);
				}
				
				// セッターメソッド経由でフィールド変数に値を設定
				setter.invoke(vo, convertedValue);
				
			} catch (Exception e) {
				if (printError) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * XMLから取得した値を返す。
	 * 
	 * @param bindPathStr バインドパス文字列
	 * @return XMLから取得した値
	 */
	private String getContent(String bindPathStr) {
		int i = bindPathStr.indexOf(ATTRIBUTE_DELIMITER);
		String value = null;
		if (i > -1) {
			// アトリビュート値を取得
			String attr = bindPathStr.substring(i + 1);
			value = attrs.getValue(attr);
		} else {
			// エレメントではさまれたテキストデータを取得
			value = contentBuf.toString();
		}
		return value;
	}
	
	/**
	 * コレクションを実装しているか判定する。
	 * 
	 * @param type 型
	 * @return コレクションを実装しているかどうか
	 */
	private boolean isCollections(Class<?> type) {
		
		Class<?>[] interfaces = type.getInterfaces();
		
		for (Class<?> ifc : interfaces) {
			if (Iterable.class.getName().equals(ifc.getName()) 
					|| Collection.class.getName().equals(ifc.getName()) 
					|| List.class.getName().equals(ifc.getName()) 
					|| Set.class.getName().equals(ifc.getName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 具象クラスを生成する。
	 * 
	 * @param type 型
	 * @return 具象クラス
	 */
	@SuppressWarnings("rawtypes")
	private Collection createConcreteClass(Class<?> type) {
		
		Collection collection = null;
		
		if (Set.class.getName().equals(type.getName())) {
			// 型がSetインターフェースのとき
			collection = new HashSet();
		} else if (List.class.getName().equals(type.getName())) {
			// 型がListインターフェースのとき
			collection = new ArrayList();
		} else if (Collection.class.getName().equals(type.getName())) {
			// 型がCollectionインターフェースのとき
			collection = new ArrayList();
		} else {
			// 型がクラスのとき
			try {
				// そのクラスのデフォルトコンストラクタでインスタンス生成
				collection = (Collection) type.getConstructor().newInstance();
			} catch (Exception e) {
				if (printError) {
					e.printStackTrace();
				}
			}
		}
		
		return collection;
	}
	
	/**
	 * VOを取得する。
	 * 
	 * @return VO
	 */
	public T getVo() {
		return vo;
	}

	/**
	 * エラーを出力するかどうかフラグを設定する。
	 * 
	 * @param printError エラーを出力するかどうかのフラグ 
	 */
	public void setPrintError(boolean printError) {
		this.printError = printError;
	}
}