# MyBatis Universal Mapper3

[![Build Status](https://travis-ci.org/abel533/Mapper.svg?branch=master)](https://travis-ci.org/abel533/Mapper)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper/badge.svg)](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper)
[![Dependency Status](https://www.versioneye.com/user/projects/593212c722f278006540a1d1/badge.svg?style=flat)](https://www.versioneye.com/user/projects/593212c722f278006540a1d1)

General Mapper는 개발자를 크게 지원할 수 있습니다. 필요에 따라 일반적인 방법을 마음대로 선택할 수 있으며 자신 만의 일반적인 방법을 매우 편리하게 개발할 수도 있습니다.

MyBatis 단일 테이블을 사용하여 추가, 삭제, 수정 및 확인하는 것은 매우 편리합니다.

단일 테이블 작업을 지원하고 일반 다중 테이블 공동 쿼리는 지원하지 않습니다.

## 시작에서 마스터로 MyBatis

![MyBatis 시작에서 마스터](https://github.com/mybatis-book/book/raw/master/book.png)

구매 주소 ：[Jingdong](https://item.jd.com/12103309.html)，[Dangdang](http://product.dangdang.com/25098208.html)，[Amazon](https://www.amazon.cn/MyBatis从入门到精通-刘增辉/dp/B072RC11DM/ref=sr_1_18?ie=UTF8&qid=1498007125&sr=8-18&keywords=mybatis)

CSDN：http://blog.csdn.net/isea533/article/details/73555400

GitHub：https://github.com/mybatis-book/book

## General Mapper는 Mybatis-3.2.4 이상을 지원합니다.
## 테이블의 속성 필드에 @Transient주석을 추가하면 안됩니다 .

## Spring DevTools 구성
[emf1002](https://github.com/emf1002)。

DevTools를 사용할 때 일반 Mapper가 자주 나타납니다 class x.x.A cannot be cast to x.x.A.

동일한 클래스가 다른 클래스 로더를 사용하는 경우 이러한 오류가 발생하므로 해결 방법은 일반 Mapper 및 엔티티 클래스에 대해 동일한 클래스 로더를 사용하는 것입니다.

기본적으로 DevTools는 IDE에 도입 된 모든 프로젝트에 대해 다시 시작 클래스 로더를 사용하고 일반 매퍼의 jar 패키지가 다시 시작 클래스 로더를 사용하는 한 가져온 jar 패키지에 대해 기본 클래스 로더를 사용합니다.

`src/main/resources`에 META-INF/spring-devtools.properties 파일 생성：
```properties
restart.include.mapper=/mapper-[\\w-\\.]+jar
restart.include.pagehelper=/pagehelper-[\\w-\\.]+jar
```
이 구성을 사용한 후 다시 시작 클래스를 사용하여 포함 된 jar 패키지를로드합니다.

## 프로젝트 문서

### https://mapperhelper.github.io

Universal Mapper를 사용하기 전에 다음 문서를 반드시 읽으십시오. 처음 사용하는 동안 많은 사람들이 겪는 문제의 99 %가 문서에 설명되어 있습니다! !

1. [일반 매퍼를 통합하는 방법](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/2.Integration.md)
2. [Universal Mapper 사용 방법](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/3.Use.md)
2. [버전 3.3.0 사용 문서의 새로운 기능](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/3.2.Use330.md)
3. [필요에 따라 인터페이스 사용자 지정](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/4.Professional.md)
4. [Mapper3 일반 인터페이스 완료](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/5.Mappers.md)
5. [확장 된 공통 인터페이스](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/6.MyMapper.md)
6. [Mapper 용 MyBatis 생성기 플러그인 사용](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/7.UseMBG.md)
7. [Spring 4에서 Universal Mapper 사용](http://git.oschina.net/free/Mapper2/blob/master/wiki/mapper/4.Spring4.md)
8. [Mapper3 일반적인 문제 및 사용법](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/9.QA.md)

### 저자는 당신을위한 일반적인 방법을 어떻게 개발할 수 있습니까?

사실, 위의 여섯 번째 문서를 보면 직접 개발할 수 있습니다.

아니면 저자가 개발 수 있도록 저자에게 10 ~ 50위안을 후원 할 수 일반적인 방법을 사용자의 필요에 따라 .

후원 후 스크린 샷을 보관하고 스크린 샷과 필수 콘텐츠를  abel533@gmail.com이메일을 보내 저자 에게 연락하십시오.

오픈 소스 중국어 크라우드 소싱 구매 서비스를 통해[MyBatis Universal Mapper Universal Method](https://zb.oschina.net/market/opus/92cda9e3bc85365f)

## 일반 매퍼 간단한 사용 예

모두 단일 테이블 작업의 경우 각 엔티티 클래스는 일반 메소드를 얻기 위해 일반 Mapper 인터페이스를 상속해야합니다.

샘플 코드：

    CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
    //전체 조회
    List<Country> countryList = mapper.select(new Country());
    //갯수
    Assert.assertEquals(183, countryList.size());

    //일반 예제 쿼리
    Example example = new Example(Country.class);
    example.createCriteria().andGreaterThan("id", 100);
    countryList = mapper.selectByExample(example);
    Assert.assertEquals(83, countryList.size());

    //MyBatis-Generator에서 생성 한 예제 쿼리
    CountryExample example2 = new CountryExample();
    example2.createCriteria().andIdGreaterThan(100);
    countryList = mapper.selectByExample(example2);
    Assert.assertEquals(83, countryList.size());

CountryMapper：

    public interface CountryMapper extends Mapper<Country> {
    }

여기서 더 구체적인 내용에 대해서는 언급하지 않겠습니다. 관심이 있으시면 다음 <b>프로젝트 문서</b>를 확인하세요.

## 엔티티 클래스 주석

위의 효과에서 이것이 최대 절전 모드와 같은 사용이라고 느낄 수도 있으므로 엔티티와 테이블도 대응해야하므로 JPA 주석이 사용됩니다. 자세한 내용은 다음 <b>프로젝트 문서</b>를 참조하십시오.

Country：

    public class Country {
        @Id
        private Integer id;
        @Column
        private String countryname;
        private String countrycode;
        //setter 및 getter 메서드 생략
    }
    
이러한 (주석이있는) 엔티티 클래스는[Mapper 용 MyBatis Generator 플러그인](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/7.UseMBG.md)을 사용하여 쉽게 생성 할 수 있습니다.

## Maven 사용
```xml
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper</artifactId>
    <version>버전</version>
</dependency>
```
Spring Boot를 사용하는 경우：
```xml
<!--mapper-->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-spring-boot-starter</artifactId>
    <version>버전</version>
</dependency>
```
구체적인 사용법은 다음을 참조하십시오：[MyBatis-Spring-Boot](https://github.com/abel533/MyBatis-Spring-Boot) 

## Jar 패키지 소개, 다운로드 링크：

https://oss.sonatype.org/content/repositories/releases/tk/mybatis/mapper

http://repo1.maven.org/maven2/tk/mybatis/mapper

Universal Mapper는 JPA를 사용하므로 persistence-api-1.0.jar도 다운로드해야합니다.

http://repo1.maven.org/maven2/javax/persistence/persistence-api/1.0/

## [更新日志](http://git.oschina.net/free/Mapper/blob/master/wiki/Changelog.md)

##업데이트 로그 

MyBatis:[https://mybatis.io](https://mybatis.io)

블로그：http://blog.csdn.net/isea533

이메일： abel533@gmail.com

그룹 추가가 필요한 경우  https://mybatis.io 홈페이지 버튼을 통해 그룹을 추가 하세요.

Mybatis 페이징 플러그인 사용 권장 :[PageHelper分页插件](https://github.com/pagehelper/Mybatis-PageHelper)