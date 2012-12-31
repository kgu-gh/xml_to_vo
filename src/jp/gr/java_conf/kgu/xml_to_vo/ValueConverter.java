package jp.gr.java_conf.kgu.xml_to_vo;

import java.util.HashMap;
import java.util.Map;

/**
 * 値のコンバータクラス。
 * 
 * @author kgu
 *
 */
public final class ValueConverter {

	/**
	 * コンバータのMap。
	 */
	private static final Map<String, Class<?>> converterMap = new HashMap<String, Class<?>>();
	
	/**
	 * デフォルトコンストラクタ。
	 */
	private ValueConverter() {
	}
	
	/**
	 * staticコンストラクタ。
	 */
	static {
		converterMap.put("byte", Byte.class);
		converterMap.put("short", Short.class);
		converterMap.put("int", Integer.class);
		converterMap.put("long", Long.class);
		//converterMap.put("char", Character.class);	未対応
		converterMap.put("float", Float.class);
		converterMap.put("double", Double.class);
		converterMap.put("boolean", Boolean.class);
	}
	
	/**
	 * String値を指定の型へコンバートする。
	 * 
	 * @param type 指定の型
	 * @param value String値
	 * @param printError 例外発生時スタックトレースを出力するかどうか
	 * @return コンバートした値。失敗時はnull。
	 */
	@SuppressWarnings("unchecked")
	public static <V> V convert(Class<?> type, String value, boolean printError) {
		
		V convertedValue = null;
		try {
			Class<?> converter = getConvertor(type);
			// コンバーターは引数にStringをとるコンストラクタを持つクラスを前提にしている
			convertedValue = (V) converter.getConstructor(String.class).newInstance(value);
		} catch (Exception e) {
			if (printError) {
				e.printStackTrace();
			}
		}
		
		return convertedValue;
	}
	
	/**
	 * 型に合ったコンバータを取得する。
	 * 
	 * @param type 型
	 * @return 型にあったコンバータ。なければ、引数の型。
	 */
	private static Class<?> getConvertor(Class<?> type) {
		
		Class<?> converter = converterMap.get(type.getName());
		if (converter == null) {
			converter = type;
		}
		
		return converter;
	}
}
