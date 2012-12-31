package jp.gr.java_conf.kgu.xml_to_vo;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * XML TO VO�̃t�@�T�[�h�N���X�B
 * 
 * @author kgu
 *
 */
public final class XmlToVoFacade {
	
	/**
	 * XML�p�[�X����O�����ŃX�^�b�N�g���[�X���o�͂��邩�ǂ����B
	 */
	private static boolean printError;
	
	/**
	 * �R���X�g���N�^�B
	 */
	private XmlToVoFacade() {
	}
	
	/**
	 * XML����擾�����l���AVO�N���X�̃o�C���h�p�X�Ɋ�Â��ăt�B�[���h�ϐ��ɐݒ肵�A�Ԃ��B
	 * 
	 * @param target VO�N���X
	 * @param xml XML
	 * @return �o�C���h�p�X�Ɋ�Â��Ēl�ݒ肳�ꂽVO�̃C���X�^���X
	 */
	public static <T> T parseXmlToVo(Class<T> target, InputStream xml) {
		
		T vo = null;
		
		try {
		
		    SAXParserFactory factory = SAXParserFactory.newInstance();
	        factory.setValidating(false);
	        factory.setNamespaceAware(true);
	        
	        SAXParser parser = factory.newSAXParser();
	        
	        XmlToVoHandler<T> handler = new XmlToVoHandler<T>(target);
	        handler.setPrintError(printError);
	        
	        parser.parse(xml, handler);
	     
	        vo = handler.getVo();
	        
		} catch (XmlToVoException xtve) {
			throw xtve;
		}  catch (Exception e) {
			throw new XmlToVoException(e);
		}
		
        return vo;
	}

	/**
	 * XML�p�[�X����O�����ŃX�^�b�N�g���[�X���o�͂��邩�ǂ����t���O��ݒ肷��B
	 * 
	 * @param printError �X�^�b�N�g���[�X���o�͂��邩�ǂ����̃t���O
	 */
	public static void setPrintError(boolean printError) {
		XmlToVoFacade.printError = printError;
	}
}
