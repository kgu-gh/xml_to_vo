package jp.gr.java_conf.kgu.xml_to_vo;

/**
 * XML TO VOの例外クラス。
 * 
 * @author kgu
 *
 */
public class XmlToVoException extends RuntimeException {

	/**
	 * シリアルバージョンID。
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * デフォルトコンストラクタ。
	 */
	public XmlToVoException() {
		super();
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param message メッセージ
	 */
	public XmlToVoException(String message) {
		super(message);
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param message メッセージ
	 * @param cause 原因
	 */
	public XmlToVoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param cause 原因
	 */
	public XmlToVoException(Throwable cause) {
		super(cause);
	}
}
