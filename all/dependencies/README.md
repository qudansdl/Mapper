# Mybatis Universal Mapper 통합 Jar 

[![Maven central](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-all-dependencies/badge.svg)](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-all-dependencies)

mapper-core, mapper-extra, mapper-generator, mapper-spring, mapper-weekend 프로젝트를 통합.

mapper-all 종속성 추가.

```xml
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-all-dependencies</artifactId>
    <version>버전</version>
</dependency>
```

다른 종속성은 자동 추가.

pom에 다음 해당 속성을 추가하여 버전 번호 수정： 

```xml
<properties>
    <mapper-core.version>4.0.0-SNAPSHOT</mapper-core.version>
    <mapper-extra.version>1.0.0-SNAPSHOT</mapper-extra.version>
    <mapper-spring.version>1.0.0-SNAPSHOT</mapper-spring.version>
    <mapper-weekend.version>1.1.3-SNAPSHOT</mapper-weekend.version>
    <mapper-generator.version>1.0.0-SNAPSHOT</mapper-generator.version>
</properties>
```

>위의 특정 버전 번호는 예시 일 뿐이며 기본적으로 설정할 필요가 없습니다.。