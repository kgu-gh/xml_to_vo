package jp.gr.java_conf.kgu.xml_to_vo;

/**
 * XML TO VO�̗�O�N���X�B
 * 
 * @author kgu
 *
 */
public class XmlToVoException extends RuntimeException {

	/**
	 * �V���A���o�[�W����ID�B
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * �f�t�H���g�R���X�g���N�^�B
	 */
	public XmlToVoException() {
		super();
	}

	/**
	 * �R���X�g���N�^�B
	 * 
	 * @param message ���b�Z�[�W
	 */
	public XmlToVoException(String message) {
		super(message);
	}

	/**
	 * �R���X�g���N�^�B
	 * 
	 * @param message ���b�Z�[�W
	 * @param cause ����
	 */
	public XmlToVoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * �R���X�g���N�^�B
	 * 
	 * @param cause ����
	 */
	public XmlToVoException(Throwable cause) {
		super(cause);
	}
}
