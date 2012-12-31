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
 * XML TO VO�̃n���h���N���X�B
 * 
 * @author kgu
 *
 */
public class XmlToVoHandler<T> extends DefaultHandler {

	/**
	 * �G�������g�p�X�̋�؂蕶���B
	 */
	private static final String ELEMENT_DELIMITER = "/";
	
	/**
	 * �A�g���r���[�g�̋�؂蕶���B
	 */
	private static final String ATTRIBUTE_DELIMITER = "@";

	/**
	 * XML�̃p�X��\���X�^�b�N�B
	 */
	private Stack<String> bindPathStack = new Stack<String>();
	
	/**
	 * �J�n�G�������g�Ŏ擾�����A�g���r���[�g�B
	 */
	private Attributes attrs;
	
	/**
	 * �G�������g�ɂ͂��܂ꂽ�f�[�^��ێ�����o�b�t�@�B
	 */
	private StringBuffer contentBuf;
	
	/**
	 * �t�B�[���h�ƃo�C���h�p�X���}�b�s���O����Map�B
	 */
	private Map<String, Field> fieldMapByBindPath;
	
	/**
	 * VO�B
	 */
	private T vo;
	
	/**
	 * �G���[���o�͂��邩�ǂ����B
	 */
	private boolean printError;
	
	/**
	 * �R���X�g���N�^�B
	 * 
	 * @param cls VO�N���X
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
		
		// �o�C���h�p�X���擾���A�G�������g�̃p�X�����O
		String[] attrsOnly = toBindPathStrs();
		attrsOnly[0] = null;
		// VO�̃t�B�[���h�ϐ��ɃA�g���r���[�g�l��ݒ�
		setFieldValue(attrsOnly);
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		// VO�̃t�B�[���h�ϐ��ɃG�������g�ɂ͂��܂ꂽ�f�[�^��ݒ�
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
	 * XML�p�[�X���ɃX�^�b�N�ɐݒ肵�������o�C���h�p�X������ɕϊ�����B
	 * 
	 * @return �o�C���h�p�X������̔z��
	 */
	private String[] toBindPathStrs() {
		
		// �G�������g+�A�g���r���[�g�����̔z��m��
		String[] bindPaths = new String[1 + (attrs == null ? 0 : attrs.getLength())];
		
		StringBuffer sb = new StringBuffer();
		
		// �o�C���h�p�X�쐬
		sb.append(ELEMENT_DELIMITER);
		for (int i = 0; i < bindPathStack.size(); i++) {
			sb.append(bindPathStack.get(i));
			if (i != (bindPathStack.size() - 1)) {
				sb.append(ELEMENT_DELIMITER);
			}
		}
		
		String bindPath = sb.toString();
		// �G�������g�̃o�C���h�p�X�Ƃ��ĕێ�
		bindPaths[0] = bindPath;
		
		for (int i = 1; i <= attrs.getLength(); i++) {
			// �A�g���r���[�g�̃o�C���h�p�X�Ƃ��ĕێ�
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
	 * �o�C���h�p�X�ƃt�B�[���h�ϐ����}�b�s���O����Map��Ԃ��B
	 * 
	 * @param cls VO�N���X
	 * @return �o�C���h�p�X�ƃt�B�[���h�ϐ����}�b�s���O����Map
	 */
	private Map<String, Field> getFieldMapByBindPath(Class<T> cls) {
		
		// �o�C���h�p�X�ƃt�B�[���h�ϐ��̃}�b�s���O
		Map<String, Field> fieldMapByBindPath = new HashMap<String, Field>();
		// VO�̃t�B�[���h�ϐ������擾
		Field[] fields = cls.getDeclaredFields();
		
		for (Field field : fields) {
			
			// �t�B�[���h�ϐ��ɐݒ肳��Ă���A�m�e�[�V�������擾
			Annotation[] annotations = field.getAnnotations();
			
			for (Annotation annotation : annotations) {
				if (annotation instanceof BindPath) {
					// �A�m�e�[�V������BindPath�Ȃ�A�o�C���h�p�X�ƃt�B�[���h�ϐ����Ђ��Â�
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
			// �o�C���h�p�X�Ƀ}�b�s���O����Ă���t�B�[���h�ϐ����擾
			Field field = fieldMapByBindPath.get(bindPathStr);
			if (field == null) {
				// �}�b�s���O�Ȃ�
				continue;
			}
			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), vo.getClass());
				// �t�B�[���h�ϐ��̃Z�b�^�[���\�b�h�擾
				Method setter = pd.getWriteMethod();
				// �Z�b�^�[���\�b�h��0�Ԗڂ̈����擾
				Class<?> type = setter.getParameterTypes()[0];
				
				Object convertedValue = null;
				if (isCollections(type)) {
					// �Z�b�^�[���\�b�h�̈������R���N�V����(Collection,List,Set)�̂Ƃ�
					// �Q�b�^�[���\�b�h����t�B�[���h�ϐ��̒l���擾
					Method getter = pd.getReadMethod();
					Collection collection = (Collection) getter.invoke(vo);
					if (collection == null) {
						// �����N���X�̃C���X�^���X�𐶐�
						collection = createConcreteClass(type);
					}
					
					if (field.getGenericType() instanceof ParameterizedType) {
						// �R���N�V�����ɃW�F�l���N�X���w�肳��Ă���Ƃ��A�w�肳�ꂽ�^���g��
						ParameterizedType pType = (ParameterizedType) field.getGenericType();
						type = (Class<?>) pType.getActualTypeArguments()[0];
					} else {
						// �R���N�V�����ɃW�F�l���N�X���w�肳��Ă��Ȃ��Ƃ��AString�^���g��
						type = String.class;
					}
					// �ݒ肷��l���Z�b�^�[���\�b�h�̌^�ɍ��킹�Ď擾
					convertedValue = ValueConverter.convert(type, getContent(bindPathStr), printError);
					collection.add(convertedValue);
					convertedValue = collection;
					
				} else {
					// �Z�b�^�[���\�b�h�̈������R���N�V����(Collection,List,Set)�ȊO�̂Ƃ�
					// �ݒ肷��l���Z�b�^�[���\�b�h�̌^�ɍ��킹�Ď擾
					convertedValue = ValueConverter.convert(type, getContent(bindPathStr), printError);
				}
				
				// �Z�b�^�[���\�b�h�o�R�Ńt�B�[���h�ϐ��ɒl��ݒ�
				setter.invoke(vo, convertedValue);
				
			} catch (Exception e) {
				if (printError) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * XML����擾�����l��Ԃ��B
	 * 
	 * @param bindPathStr �o�C���h�p�X������
	 * @return XML����擾�����l
	 */
	private String getContent(String bindPathStr) {
		int i = bindPathStr.indexOf(ATTRIBUTE_DELIMITER);
		String value = null;
		if (i > -1) {
			// �A�g���r���[�g�l���擾
			String attr = bindPathStr.substring(i + 1);
			value = attrs.getValue(attr);
		} else {
			// �G�������g�ł͂��܂ꂽ�e�L�X�g�f�[�^���擾
			value = contentBuf.toString();
		}
		return value;
	}
	
	/**
	 * �R���N�V�������������Ă��邩���肷��B
	 * 
	 * @param type �^
	 * @return �R���N�V�������������Ă��邩�ǂ���
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
	 * ��ۃN���X�𐶐�����B
	 * 
	 * @param type �^
	 * @return ��ۃN���X
	 */
	@SuppressWarnings("rawtypes")
	private Collection createConcreteClass(Class<?> type) {
		
		Collection collection = null;
		
		if (Set.class.getName().equals(type.getName())) {
			// �^��Set�C���^�[�t�F�[�X�̂Ƃ�
			collection = new HashSet();
		} else if (List.class.getName().equals(type.getName())) {
			// �^��List�C���^�[�t�F�[�X�̂Ƃ�
			collection = new ArrayList();
		} else if (Collection.class.getName().equals(type.getName())) {
			// �^��Collection�C���^�[�t�F�[�X�̂Ƃ�
			collection = new ArrayList();
		} else {
			// �^���N���X�̂Ƃ�
			try {
				// ���̃N���X�̃f�t�H���g�R���X�g���N�^�ŃC���X�^���X����
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
	 * VO���擾����B
	 * 
	 * @return VO
	 */
	public T getVo() {
		return vo;
	}

	/**
	 * �G���[���o�͂��邩�ǂ����t���O��ݒ肷��B
	 * 
	 * @param printError �G���[���o�͂��邩�ǂ����̃t���O 
	 */
	public void setPrintError(boolean printError) {
		this.printError = printError;
	}
}