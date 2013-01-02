xml_to_vo
=========

This library converts XML into VO.

[�T�v]  
VO�̃t�B�[���h�ɃA�m�e�[�V�����ŁA�f�[�^���擾�������^�O�⑮���܂ł̃p�X�������Ă����ƁAXML�p�[�X���Ƀf�[�^���o�C���h����B  

[�g����]  
�^�O�ɋ��܂ꂽ�e�L�X�g�f�[�^�Ȃ�A���[�g���炻�̃f�[�^�̃^�O�܂ł�/�ŋ�؂��ċL�q����B  

@BindPath("/company/name")  
private String companyName;  

�����̃f�[�^�Ȃ�A���[�g���炻�̑����̃^�O�܂ł�/�ŋ�؂��ċL�q���A@������������B  

@BindPath("/company/address@tel")  
private String tel;

XmlToVoFacade#parseXmlToVo�Ƃ������\�b�h�����s����ƁAXML�f�[�^��VO�Ƀ}�b�s���O�����B

Company company = XmlToVoFacade.parseXmlToVo(Company.class, 
	new FileInputStream("company.xml"));

[����]  
�EDTD�Ƃ�XML�X�L�[�}�Ƃ̑Ó������؂͂ł��Ȃ��B  
�E�}�b�s���O�ł���t�B�[���h�̌^�́Achar�ȊO�̃v���~�e�B�u�^�Ƃ��̃��b�p�[�N���X�AString�A���ƃR���X�g���N�^��String���Ƃ��N���X�̂݁B  
�E�z��ɂ̓}�b�s���O�ł��Ȃ�(List�ASet�͂ł���B)  
�E�l�[���X�y�[�X�ł̎��ʂɑΉ����Ă��Ȃ�(���[�J���l�[���ł����A�N�Z�X���ĂȂ��̂ŁA�l�[���X�y�[�X���Ⴄ�����̃^�O�͓���Ƃ݂Ȃ����B)

[Outline]  
This library binds the data that you want to obtain, by the annotation.

[How to use]  
The data Between elements describes from the root element to its element by '/'.

@BindPath("/company/name")  
private String companyName; 

The attribute data describes from the root element to its element by '/', and its attribute describes by '@'.

@BindPath("/company/address@tel")  
private String tel;

The XML data is bound to the VO after the method of 'XmlToVoFacade#parseXmlToVo' was executed.

Company company = XmlToVoFacade.parseXmlToVo(Company.class, 
	new FileInputStream("company.xml"));
	
[Restrictions]  
�EThis library cannot verify whether the data suits the DTD or the XML Schemer.  
�EThis library can bind to the type that is a primitive, a wrapper, the class of 'String', or a class that the constractor has an argument of 'String'. (but excludes 'char' or 'Character')  
�EThis library cannot bind to the Array. (but can bind to the 'List' or the 'Set'.)  
�EThis library cannot understand the namespace.(Since This library accesses to the localname, the element that has the different namespace cannot distinguish.)
