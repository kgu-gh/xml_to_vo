xml_to_vo
=========

This library converts XML into VO.

[概要]  
VOのフィールドにアノテーションで、データを取得したいタグや属性までのパスを書いておくと、XMLパース時にデータをバインドする。  

[使い方]  
タグに挟まれたテキストデータなら、ルートからそのデータのタグまでを/で区切って記述する。  

@BindPath("/company/name")  
private String companyName;  

属性のデータなら、ルートからその属性のタグまでを/で区切って記述し、@属性名をつける。  

@BindPath("/company/address@tel")  
private String tel;

XmlToVoFacade#parseXmlToVoというメソッドを実行すると、XMLデータがVOにマッピングされる。

Company company = XmlToVoFacade.parseXmlToVo(Company.class, 
	new FileInputStream("company.xml"));

[制限]  
・DTDとかXMLスキーマとの妥当性検証はできない。  
・マッピングできるフィールドの型は、char以外のプリミティブ型とそのラッパークラス、String、あとコンストラクタにStringをとれるクラスのみ。  
・配列にはマッピングできない(List、Setはできる。)  
・ネームスペースでの識別に対応していない(ローカルネームでしかアクセスしてないので、ネームスペースが違う同名のタグは同一とみなされる。)

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
・This library cannot verify whether the data suits the DTD or the XML Schemer.  
・This library can bind to the type that is a primitive, a wrapper, the class of 'String', or a class that the constractor has an argument of 'String'. (but excludes 'char' or 'Character')  
・This library cannot bind to the Array. (but can bind to the 'List' or the 'Set'.)  
・This library cannot understand the namespace.(Since This library accesses to the localname, the element that has the different namespace cannot distinguish.)
