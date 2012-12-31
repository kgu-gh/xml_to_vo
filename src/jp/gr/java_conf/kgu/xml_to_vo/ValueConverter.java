package jp.gr.java_conf.kgu.xml_to_vo;

import java.util.HashMap;
import java.util.Map;

/**
 * �l�̃R���o�[�^�N���X�B
 * 
 * @author kgu
 *
 */
public final class ValueConverter {

	/**
	 * �R���o�[�^��Map�B
	 */
	private static final Map<String, Class<?>> converterMap = new HashMap<String, Class<?>>();
	
	/**
	 * �f�t�H���g�R���X�g���N�^�B
	 */
	private ValueConverter() {
	}
	
	/**
	 * static�R���X�g���N�^�B
	 */
	static {
		converterMap.put("byte", Byte.class);
		converterMap.put("short", Short.class);
		converterMap.put("int", Integer.class);
		converterMap.put("long", Long.class);
		//converterMap.put("char", Character.class);	���Ή�
		converterMap.put("float", Float.class);
		converterMap.put("double", Double.class);
		converterMap.put("boolean", Boolean.class);
	}
	
	/**
	 * String�l���w��̌^�փR���o�[�g����B
	 * 
	 * @param type �w��̌^
	 * @param value String�l
	 * @param printError ��O�������X�^�b�N�g���[�X���o�͂��邩�ǂ���
	 * @return �R���o�[�g�����l�B���s����null�B
	 */
	@SuppressWarnings("unchecked")
	public static <V> V convert(Class<?> type, String value, boolean printError) {
		
		V convertedValue = null;
		try {
			Class<?> converter = getConvertor(type);
			// �R���o�[�^�[�͈�����String���Ƃ�R���X�g���N�^�����N���X��O��ɂ��Ă���
			convertedValue = (V) converter.getConstructor(String.class).newInstance(value);
		} catch (Exception e) {
			if (printError) {
				e.printStackTrace();
			}
		}
		
		return convertedValue;
	}
	
	/**
	 * �^�ɍ������R���o�[�^���擾����B
	 * 
	 * @param type �^
	 * @return �^�ɂ������R���o�[�^�B�Ȃ���΁A�����̌^�B
	 */
	private static Class<?> getConvertor(Class<?> type) {
		
		Class<?> converter = converterMap.get(type.getName());
		if (converter == null) {
			converter = type;
		}
		
		return converter;
	}
}
