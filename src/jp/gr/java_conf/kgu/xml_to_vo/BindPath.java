package jp.gr.java_conf.kgu.xml_to_vo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * �o�C���h�p�X��\���A�m�e�[�V�����B
 * 
 * @author kgu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindPath {
	String value();
}
