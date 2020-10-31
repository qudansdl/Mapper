# Mybatis Universal Mapper 코드 생성기

[![Maven central](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-generator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-generator)

일반 Mapper와 관련된 전체 플러그인에 적은 양의 코드 만 있으며 직접적인 종속성은 없습니다.

이 코드 생성기는 실제로 MyBatis Generator의 확장입니다.이 확장을 사용하면 Freemarker 템플릿 언어를 사용하여 코드를 쉽게 작성할 수 있습니다.

## 테스트

src/test/java 아래에 tk.mybatis.mapper.generator패키지 아래에 테스트 클래스 가 Generator있습니다.

이 테스트 클래스를 직접 실행하여 생성 된 코드의 효과를 확인할 수 있습니다. src/test/java/test 삭제하기 쉬운 디렉토리에 생성 된 모든 코드 .

사용 된 hsqldb 메모리 데이터베이스, src / test / resources에 내장 된 SQL 데이터베이스 테이블을 테스트 CreateDB.sql합니다.

코드 생성기는 폐기됩니다 generatorConfig.xml.

# 코드 생성기 문서

코드 생성기는 MBG 플러그인을 기반으로하므로 MBG와 함께 사용해야합니다.

간단한 MBG 구성은 다음과 같습니다.：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE generatorConfiguration
        PUBLIC "-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN"
        "http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd">

<generatorConfiguration>
    <context id="Mysql" targetRuntime="MyBatis3Simple" defaultModelType="flat">
        <property name="javaFileEncoding" value="UTF-8"/>
        <!--Universal Mapper와 함께 제공되는 주석 확장을 사용할지 여부 구성. 기본값은 true -->
        <!--<property name="useMapperCommentGenerator" value="false"/>-->

        <!--주석이 달린 엔티티 클래스를 생성 할 수있는 Universal Mapper 플러그인 -->
        <plugin type="tk.mybatis.mapper.generator.MapperPlugin">
            <property name="mappers" value="tk.mybatis.mapper.common.Mapper,tk.mybatis.mapper.hsqldb.HsqldbMapper"/>
            <property name="caseSensitive" value="true"/>
            <property name="forceAnnotation" value="true"/>
            <property name="beginningDelimiter" value="`"/>
            <property name="endingDelimiter" value="`"/>
            <!--lombok 활성화 여부 구성 및 다음 6 개의 주석 지원 -->
            <!--Data 구성 후 Getter Setter ToString EqualsAndHashCode 무시-->
            <property name="lombok" value="Getter,Setter,Data,ToString,Accessors,EqualsAndHashCode"/>
        </plugin>

        <!--범용 코드 생성기 플러그인 -->
        <plugin type="tk.mybatis.mapper.generator.TemplateFilePlugin">
            <property name="targetProject" value="src/test/java"/>
            <property name="targetPackage" value="test.mapper"/>
            <property name="templatePath" value="generator/mapper.ftl"/>
            <property name="mapperSuffix" value="Dao"/>
            <property name="fileName" value="${tableClass.shortClassName}${mapperSuffix}.java"/>
        </plugin>

        <jdbcConnection driverClass="org.hsqldb.jdbcDriver"
                                connectionURL="jdbc:hsqldb:mem:generator"
                                userId="sa"
                                password="">
        </jdbcConnection>

        <!--MyBatis 생성기는 모델 생성 만 필요합니다-->
        <javaModelGenerator targetPackage="test.model" targetProject="./src/test/java"/>

        <table tableName="user%">
            <generatedKey column="id" sqlStatement="JDBC"/>
        </table>
    </context>
</generatorConfiguration>
```
이 구성에서는 tk.mybatis.mapper.generator.TemplateFilePlugin.

## 템플릿 기반 플러그인 TemplateFilePlugin

이 플러그인에 필요한 몇 가지 속성 외에도 모든 속성을 추가 할 수 있습니다. 속성은 전적으로 템플릿에 데이터를 제공하기위한 것입니다.
먼저 기본 전체 구성을 살펴보십시오.

```xml
<!--테스트는 단일 파일을 출력하고 각 테이블은 해당 파일을 생성합니다-->
<plugin type="tk.mybatis.mapper.generator.TemplateFilePlugin">
    <property name="singleMode" value="false"/>
    <property name="targetProject" value="src/test/resources"/>
    <property name="targetPackage" value=""/>
    <property name="templatePath" value="generator/test-one.ftl"/>
    <property name="fileName" value="${tableClass.shortClassName}Test.txt"/>
    <!--기본값은 다음과 같으며 생략 할 수 있습니다 -->
    <property name="templateFormatter" value="tk.mybatis.mapper.generator.formatter.FreemarkerTemplateFormatter"/>
</plugin>
```

필수 속성은 아래에 설명되어 있습니다.

### 1. `targetProject`

일반적으로 src/main/java또는 src/main/resource이러한 디렉토리 에서 대상 프로젝트를 지정하는 데 사용됩니다.
src/test/java또는 src/test/resource이 디렉토리 일 수도 있습니다 .

다중 모듈 프로젝트에서 상대 경로를 통해 다른 디렉토리를 지정할 수도 있습니다. 예를 들면 다음과 같습니다.

```xml
<property name="targetProject" value="../myproject-api/src/main/java"/>
```

**이 속성 값에 대한 요구 사항이 있습니다. 즉, 디렉토리가 있어야합니다. 그렇지 않으면 코드가 생성되지 않습니다!**

### 2. `targetPackage`

패키지를 지정하는 데 사용되는 부분은 이름이지만 실제로는 경로입니다.

**이 속성에 지정된 경로가 존재하지 않으면 자동으로 생성됩니다.**

이 속성의 값은 비어있을 수 있습니다.

예를 들어, 디렉토리 mapper/admin를 생성 mapper/admin/하거나 tk.mybatis.mapper패킷 (또는 본질적으로 디렉토리)을 생성합니다.

이 속성은 또한 특별한 장소가 있으며 템플릿 사용을 지원하며 바로 아래 fileName에 간단한 사용 시나리오를 제공합니다.

>프론트 엔드 코드를 생성 할 때 자신의 디렉토리에있는 테이블에 해당하는 JSP를 생성 할 수 있습니다. 이때 다음과 같이 구성 할 수 있습니다. 
>
>`<property name="targetPackage" value="WEB-INF/jsp/${tableClass.lowerCaseName}/"/>`
>
>템플릿에서 사용할 수있는 속성은 여기에서 사용할 수 있으며 다른 속성은 나중에 소개합니다.

또한이 경로에서 플러그인을 구성하면 템플릿을 기반으로 지정된 위치 (targetProject 및 targetPackage에 의해 결정된 디렉토리)에만 파일이 생성 될 수 있음을 알 수 있습니다.

### 3. `templatePath`

ClassLoader를 통해 얻을 수있는 모든 위치가 될 수있는 템플릿 경로를 지정하며 파일 유형은 제한되지 않습니다.

예를 들어 generator/test-one.ftl.

**이 속성을 지정해야합니다. 그렇지 않으면 코드가 생성되지 않습니다!**

### 4. `fileName`

이 속성은 생성 된 파일의 이름을 지정하는 데 사용됩니다.이 값은 위와 같은 템플릿 사용을 지원합니다. 사용 ${tableClass.shortClassName}Test.txt가능한 특정 속성은 나중에 소개됩니다.

**이 속성을 지정해야합니다. 그렇지 않으면 코드가 생성되지 않습니다!**

### 5. `templateFormatter`

**이 속성은 선택 사항이며 FreeMarker 기반 구현이 기본적으로 사용됩니다!**

기본적으로 다음 종속성을 추가해야합니다.：

```xml
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.23</version>
</dependency>
```

기본 구현 클래스：`tk.mybatis.mapper.generator.formatter.FreemarkerTemplateFormatter`。

두 개의 인터페이스를 구현 `TemplateFormatter, ListTemplateFormatter`。

`singleMode` 매개 변수 값 true및 false. 하나의 테이블이 하나의 파일을 생성하거나 여러 테이블이 하나의 파일을 생성합니다.

일반적으로 첫 번째 경우입니다. 그러나 구성 파일에서는 여러 테이블의 정보를 사용할 수 있습니다.

다른 템플릿 엔진을 사용하려는 경우 위의 인터페이스를 직접 구현할 수 있습니다.

### 6. `singleMode`

위에서 언급했듯이 기본값은 true입니다.

테이블이 파일을 생성 할 때, 참조는 속성 generator/test-one.ftl, 속성 테이블에서 사용할 수 있습니다 tableClass.

문서 참조의 복수의 테이블을 생성하는 것은 사용 가능한 속성 generator/test-all.ftl일 수 있으며 , 모든 테이블의 속성은 tableClassSet순회를 통해 개별 정보를 획득 할 수 있습니다.

### 7. 필요한 기타 속성

필요한 템플릿 특정 정보는 <property>직접 사용되는 템플릿의 속성 이름을 사용하여 여기에 정의 된 바와 같이 임의의 적절한 수단에 의해 이루어질 수 있으며 , 다음 예에서는 mapperSuffix이 속성이 있습니다.

## `TemplateFilePlugin` 구성 예

비즈니스에 따라 템플릿을 디자인해야하므로 여기에는 두 개의 간단한 매퍼 목표와 완전한 속성이있는 두 개의 샘플 템플릿 만 제공됩니다.

템플릿은 한 가지 유형의 파일 만 생성 할 수 있기 때문에 여러 개의 다른 파일을 생성하려면 여러 플러그인을 구성해야합니다.

>이 디자인은 자유도가 높기 때문에 매우 유연하므로 가격이 더 많이 구성됩니다.
>
>그러나 정상적인 상황에서는 업무에 맞게 디자인 된 템플릿 세트가 기본적으로 고정되어 있으며 너무 많은 변경이 없을 것이므로 사용하는데 번거롭지 않습니다.

예를 들면 다음과 같습니다：

```xml
<!--범용 코드 생성기 플러그인-->
<!--mapper 인터페이스-->
<plugin type="tk.mybatis.mapper.generator.TemplateFilePlugin">
    <property name="targetProject" value="src/test/java"/>
    <property name="targetPackage" value="test.mapper"/>
    <property name="templatePath" value="generator/mapper.ftl"/>
    <property name="mapperSuffix" value="Dao"/>
    <property name="fileName" value="${tableClass.shortClassName}${mapperSuffix}.java"/>
</plugin>
<!--mapper.xml-->
<plugin type="tk.mybatis.mapper.generator.TemplateFilePlugin">
    <property name="targetProject" value="src/test/resources"/>
    <property name="targetPackage" value="mappers"/>
    <property name="mapperPackage" value="test.mapper"/>
    <property name="templatePath" value="generator/mapperXml.ftl"/>
    <property name="mapperSuffix" value="Dao"/>
    <property name="fileName" value="${tableClass.shortClassName}${mapperSuffix}.xml"/>
</plugin>
<!--테스트 출력 단일 파일, 각 테이블은 해당 파일을 생성합니다-->
<plugin type="tk.mybatis.mapper.generator.TemplateFilePlugin">
    <property name="targetProject" value="src/test/resources"/>
    <property name="targetPackage" value=""/>
    <property name="templatePath" value="generator/test-one.ftl"/>
    <property name="fileName" value="${tableClass.shortClassName}Test.txt"/>
    <!-- 기본값은 다음과 같으며 생략 할 수 있습니다 -->
    <property name="templateFormatter"
              value="tk.mybatis.mapper.generator.formatter.FreemarkerTemplateFormatter"/>
</plugin>
<!-- 테스트는 전체 파일을 출력하고 모든 테이블을 사용할 수 있으며 한 번에 하나의 파일 만 생성되어 모든 테이블을 집계하는 데 사용됩니다  -->
<plugin type="tk.mybatis.mapper.generator.TemplateFilePlugin">
    <property name="singleMode" value="false"/>
    <property name="targetProject" value="src/test/resources"/>
    <property name="targetPackage" value=""/>
    <property name="templatePath" value="generator/test-all.ftl"/>
    <property name="fileName" value="TestAll.txt"/>
</plugin>
```

처음 두 인터페이스 mapperSuffix는 접미사를 구성하기위한 매개 변수가있는 XML Mapper Dao 접미사를 생성 하며, mapperPackageXML 인터페이스 패키지 이름을 생성 할 때 획득됩니다 (여기에서와 targetPackage다를 수 있음).

후자의 두 플러그인은 사용 가능한 모든 속성을 보여주는 데 사용되며 두 가지 다른 모드입니다.

테이블 및 엔터티에서 사용할 수있는 모든 속성은 다음과 같습니다.：

```
특수 : targetPackage 값은 $ {package}에 있습니다.

<!-- 자세한 날짜 사용 참조：http://freemarker.apache.org/docs/ref_builtins_date.html -->
현재 시간：
<#assign dateTime = .now>
일자：${dateTime?date}
시간：${dateTime?time}
Format：${dateTime?string["yyyy-MM-dd HH:mm:ss"]}


구성된 모든 속성 정보:
<#list props?keys as key>
${key} - ${props[key]}
</#list>

엔터티 및 테이블에 대한 정보：
테이블 이름：${tableClass.tableName}
변수 이름：${tableClass.variableName}
소문자 이름：${tableClass.lowerCaseName}
클래스 이름：${tableClass.shortClassName}
성명：${tableClass.fullClassName}
패키지 이름：${tableClass.packageName}

컬럼 정보：
=====================================
<#if tableClass.pkFields??>
기본 키：
    <#list tableClass.pkFields as field>
    -------------------------------------
    열 이름 : $ {field.columnName}
    열 유형 : $ {field.jdbcType}
    필드 이름 : $ {field.fieldName}
    설명 : $ {field.remarks}
    패키지 이름 입력 : $ {field.typePackage}
    짧은 이름 입력 : $ {field.shortTypeName}
    전체 이름 입력 : $ {field.fullTypeName}
    기본 키 : $ {field.identity?c}
    nullable 여부 : $ {field.nullable?c}
    BLOB 컬럼 : $ {field.blobColumn?c}
    문자열 : $ {field.stringColumn?c}
    문자열 : $ {field.jdbcCharacterColumn?c}
    날짜 : $ {field.jdbcDateColumn?c}
    시간 : $ {field.jdbcTimeColumn?c}
    시퀀스 : $ {field.sequenceColumn?c}
    길이 : $ {field.length? c}
    Scale : $ {field.scale}
    </#list>
</#if>

<#if tableClass.baseFields??>
베이스 컬럼：
    <#list tableClass.baseFields as field>
    -------------------------------------
    열 이름 : $ {field.columnName}
         열 유형 : $ {field.jdbcType}
         필드 이름 : $ {field.fieldName}
         설명 : $ {field.remarks}
         패키지 이름 입력 : $ {field.typePackage}
         짧은 이름 입력 : $ {field.shortTypeName}
         전체 이름 입력 : $ {field.fullTypeName}
         기본 키 : $ {field.identity?c}
         nullable 여부 : $ {field.nullable?c}
         BLOB : $ {field.blobColumn?c}
         문자열 : $ {field.stringColumn?c}
         문자열 : $ {field.jdbcCharacterColumn?c}
         날짜 : $ {field.jdbcDateColumn?c}
         시간  : $ {field.jdbcTimeColumn?c}
         시퀀스 : $ {field.sequenceColumn?c}
         길이 : $ {field.length?c}
         Scale : $ {field.scale}
    </#list>
</#if>

<#if tableClass.blobFields??>
Blob：
    <#list tableClass.blobFields as field>
    -------------------------------------
    열 이름 : $ {field.columnName}
    열 유형 : $ {field.jdbcType}
    필드 이름 : $ {field.fieldName}
    설명 : $ {field.remarks}
    패키지 이름 입력 : $ {field.typePackage}
    짧은 이름 입력 : $ {field.shortTypeName}
    전체 이름 입력 : $ {field.fullTypeName}
    기본 키 : $ {field.identity? c}
    nullable 여부 : $ {field.nullable? c}
    BLOB : $ {field.blobColumn? c}
    문자열 : $ {field.stringColumn? c}
    문자열 : $ {field.jdbcCharacterColumn? c}
    날짜 : $ {field.jdbcDateColumn? c}
    시간 : $ {field.jdbcTimeColumn? c}
    시퀀스 : $ {field.sequenceColumn? c}
    길이 : $ {field.length? c}
    Scale : $ {field.scale}
    </#list>
</#if>

=====================================
모든 열 (pk, base, blob 필드 포함, 사용 가능한 속성은 위와 동일 함) :
<#if tableClass.allFields??>
열 이름-필드 이름
    <#list tableClass.allFields as field>
    ${field.columnName} - ${field.fieldName}
    </#list>
</#if>
```

## 테스트 실행

위의 예제는`src/test/resources/generator/generatorConfig.xml`에있는이 프로젝트의 테스트 코드입니다.

또한 자바 코딩 모드로 동작하는 클래스를 제공하며, 위의 xml에서 데이터베이스 정보를 설정하여`src / test / java /`의`tk.mybatis.mapper.generator.Generator`를 생성 할 수있다.

테스트에서 생성 된 **부분** 결과는 다음과 같습니다.

Entity：
```java
@Table(name = "`user_info`")
public class UserInfo {
    @Id
    @Column(name = "`Id`")
    @GeneratedValue(generator = "JDBC")
    private Integer id;
```

Dao：
```java
package test.mapper;

import test.model.UserInfo;

/**
* 범용 매퍼 코드 생성기
*
* @author mapper-generator
*/
public interface UserInfoDao extends tk.mybatis.mapper.common.Mapper<UserInfo> {

}
```

XML：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="test.mapper.UserInfoDao">

</mapper>
```

test-one.ftl 생성되는 정보는 다음과 같습니다 ：
```java
대상 package: 

현재 시간：
2017-11-6
22:00:45
2017-11-06 22:00:45

구성된 모든 속성 정보:
targetPackage - 
templateFormatter - tk.mybatis.mapper.generator.formatter.FreemarkerTemplateFormatter
templatePath - generator/test-one.ftl
targetProject - src/test/resources
fileName - ${tableClass.shortClassName}Test.txt

엔티티 및 테이블에 대한 정보 :
테이블 이름 : user_info
변수 이름 : userInfo
소문자 이름 : userinfo
클래스 이름 : UserInfo 
전체 이름 : test.model.UserInfo 
패키지 이름 : test.model


## 마침

기본 코드 생성기는 Java 맞춤법 문자열의 출력과 매우 유사하며 매우 간단합니다. 여기서는 템플릿 만 사용됩니다.

거의 모든 사람이 일종의 템플릿입니다. JSP EL에서 사용할 수 <c:forEach 있지만 여기서는 Lenovo 코드 생성기 만 사용할 수 없습니다
 
향후 https://github.com/abel533/Mybatis-Spring 프로젝트에서 템플릿 세트가 예제로 제공 될 것 입니다.

>프로젝트의 개발은 당신의 지원과 뗄 수없는 관계이며, 저자는 커피 한잔에 초대 받았습니다!
>
>알리 페이
>
><img width="360" src="https://camo.githubusercontent.com/4af3ab81f88d87abfb9c67f9c6bba84047b079e1/68747470733a2f2f6d7962617469732e746b2f696d672f616c695f7061792e706e67" alt="支付宝" data-canonical-src="https://mybatis.io/img/ali_pay.png">
>
>위챗
>
><img width="360" src="https://camo.githubusercontent.com/56a0b0aa0c09116cb0ef2f3ebe7f6d7103705a98/68747470733a2f2f6d7962617469732e746b2f696d672f77785f7061792e706e67" alt="微信" data-canonical-src="https://mybatis.io/img/wx_pay.png">

