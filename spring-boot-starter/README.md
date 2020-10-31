# MyBatis Mapper와 Spring Boot 통합

[![Maven central](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-spring-boot-starter)

Mapper-Spring-Boot-Starter는 일반적인 Mapper를 Spring Boot에 통합하는 데 도움이됩니다.

Mapper-Spring-Boot-Starter will help you use Mapper with Spring Boot.

## How to use
pom.xml에 다음 종속성 추가:

Add the following dependency to your pom.xml: 
```xml
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-spring-boot-starter</artifactId>
    <version>1.2.3</version>
</dependency>
```

## 1.2.3 - 2018-01-24

- 추가 tk.mybatis.spring.mapper.SpringBootBindUtil되어 기본 모드에서 Spring Boot 1.x 및 2.x 버전과 호환되며 더 이상 relax 값의 문제가 없으며 이전 구성을 수정없이 사용할 수 있습니다.
- 특별한주의, @MapperScan댓글이 있으면 댓글을 이용하세요 tk.mybatis.spring.annotation.MapperScan.
- General Mapper가 버전 3.5.2로 업그레이드되었습니다.

## 1.2.2

- 주말 버전의 종속성으로 인해이 버전은 문제가 있으며 공식적으로 출시되지 않았습니다.

## 1.2.1 - 2018-01-10

- 호환성을 향상시키기 위해 MapperAutoConfiguration증가 @AutoConfigureBefore(name = "org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration") , 매퍼 자동 설정이 우선적으로 사용될 수있다하더라도 MyBatis로의 존재에 의존하여 스타터 트리거 후속 MyBatis로 생성되지@Bean
- 간단한 릴랙스 바인딩을 지원합니다. 예를 들어 비어 있지 않은 것은 스프링 부트 구성과 호환되는 notEmpty로 변환됩니다.


## 1.2.0 - 2018-01-08

- 일반 매퍼 3.5.0
- mybatis-spring-boot-starter의 종속성을 제거하고 더 이상 mybatis의 공식 스타터를 사용하지 않으며 일반 매퍼를 사용할 때 공식 스타터를 도입하지 않습니다.
- mybatis 공식 스타터를 참조하여 fengcbo의 매퍼 초기화  [pr#5 by fengcbo](https://github.com/abel533/mapper-boot-starter/pull/5)에서 가능한 문제를 다시 구현하고 해결하십시오.
- @MapperScantk의 시작 부분 을 선택 해야하는 경우tk.mybatis.spring.annotation.MapperScan

## 1.1.7 - 2017-12-17

- 일반 매퍼 3.4.6
- spring-boot 1.5.9.RELEASE

## 1.1.6 - 2017-11-11

- 일반 매퍼 3.4.5

## 1.1.5 - 2017-10-21

- 일반 매퍼 3.4.4
- mybatis-starter 1.3.1
- spring-boot 1.5.8.RELEASE

## 1.1.4 - 2017-08-18

- 일반 매퍼  3.4.3

## 1.1.3 - 2017-07-18

- 일반 매퍼  3.4.2

## 1.1.2 - 2017-07-17

- 일반 매퍼 3.4.1
- mybatis 3.4.4
- mybatis-spring-boot 1.3.0
- spring-boot 1.5.4.RELEASE

## 1.1.1 - 2017-03-28

- 버전 1.1.0의 불일치 문제 해결
- [PR #2](https://github.com/abel533/mapper-boot-starter/pull/2)에 대한 Qiu Zhanbo 덕분에 여러 데이터 소스에 대한 지원 증가

## 1.1.0 - 2017-02-19

- mybatis가 3.4.2로 업그레이드 됨
- 3.4.0으로 업그레이드 된 Mapper
- mybatis-spring이 1.3.1로 업그레이드 됨
- mybatis-spring-boot가 1.2.0으로 업그레이드 됨
- 1.4.4.RELEASE로 Spring-boot 업그레이드

## Example
>https://github.com/abel533/MyBatis-Spring-Boot

## Special Configurations
정상적인 상황에서는 구성을 수행 할 필요가 없습니다.

Normally, you don't need to do any configuration.

구성해야하는 경우 다음 방법을 사용하여 구성 할 수 있습니다:

You can config PageHelper as the following:

application.properties:
```properties
mapper.propertyName=propertyValue
```

예:
```properties
mapper.mappers[0]=tk.mybatis.sample.mapper.BaseMapper
mapper.mappers[1]=tk.mybatis.mapper.common.Mapper
```
기본적으로 매퍼 구성이 없으면 자동으로 등록됩니다. tk.mybatis.mapper.common.Mapper

일반 Mapper는 매개 변수 수신에 사용되는 객체 인 고정 속성이기 때문에 Spring Boot 구성 규칙에 따라 모든 대문자가 수평선이있는 소문자로 변경되었습니다. 예를 들어 IDENTITY (ID에 해당)는 모두 소문자 ID 구성을 제공합니다. IDE가 자동으로 프롬프트를 표시 할 수 있으면 자동 프롬프트를 확인하십시오.

IDE는 자동으로 다음과 같은 메시지를 표시해야합니다:  

![自动提示属性](properties.png)

## MyBatis Mapper
>https://github.com/abel533/Mapper
