package jp.gr.java_conf.kgu.xml_to_vo;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * XML TO VOのファサードクラス。
 * 
 * @author kgu
 *
 */
public final class XmlToVoFacade {
	
	/**
	 * XMLパース中例外発生でスタックトレースを出力するかどうか。
	 */
	private static boolean printError;
	
	/**
	 * コンストラクタ。
	 */
	private XmlToVoFacade() {
	}
	
	/**
	 * XMLから取得した値を、VOクラスのバインドパスに基づいてフィールド変数に設定し、返す。
	 * 
	 * @param target VOクラス
	 * @param xml XML
	 * @return バインドパスに基づいて値設定されたVOのインスタンス
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
	 * XMLパース中例外発生でスタックトレースを出力するかどうかフラグを設定する。
	 * 
	 * @param printError スタックトレースを出力するかどうかのフラグ
	 */
	public static void setPrintError(boolean printError) {
		XmlToVoFacade.printError = printError;
	}
}
