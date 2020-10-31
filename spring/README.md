# Mybatis Universal Mapper 및 Spring 통합

[![Maven central](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-spring/badge.svg)](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-spring)

## 종속성

프로젝트의 자체 종속성은 전달되지 않으므로 ( <scope>provided</scope>), 다른 종속성을 직접 제공해야합니다.

> 정상적인 상황에서는 이러한 종속성을 기반으로 매퍼 스프링도 추가됩니다.

```xml
<!-- 추가해야하는 종속성 -->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-spring</artifactId>
    <version>버전</version>
</dependency>
```

기타 종속성

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>버전</version>
</dependency>
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper</artifactId>
    <version>버전</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>버전</version>
</dependency>

<!-- Spring -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>버전</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>버전</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>버전</version>
</dependency>
```

## 설정

이 프로젝트는 주로 두 가지 주요 구성 방법을 제공합니다.

- `MapperScannerConfigurer`  xml 빈 구성
- `@MapperScan` 주석

또한 상위 버전의 MyBatis (3.4.0+) 및 mybatis-spring (1.3.0+) 권장 tk.mybatis.mapper.session.Configuration구성 도 있습니다.

> 3.6.0 Universal Mapper가 자동으로 @RegisterMapper주석이 달린 기본 클래스 인터페이스에 등록 된 후 필수 구성 매퍼 속성이 아니라 기본 클래스에 @RegisterMapper주석을 추가 할 수 있습니다.

### 처음으로 Universal Mapper를 사용할 때주의하십시오.

다음 예제는 구성 방법만을 보여줍니다. 특정 구성 매개 변수는 직접 선택해야합니다!

구성 가능한 모든 매개 변수는 일반 매퍼 문서를 참조하십시오.

> https://github.com/abel533/Mapper/blob/master/wiki/mapper3/2.Integration.md

### 하나, MapperScannerConfigurerxml bean 구성

```xml
<bean class="tk.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="tk.mybatis.mapper.mapper"/>
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
    <property name="properties">
        <value>
            mappers=tk.mybatis.mapper.common.Mapper
        </value>
    </property>
</bean>
```
다음 두 가지 사항에주의하십시오：

 1. tk.mybatis.spring.mapper.MapperScannerConfigurer공식이 아닌 여기서 사용됨org.xxx
 2. 일반 Mapper의 모든 구성은 위의 mappers = xxx를 참조하고 한 줄에 하나의 구성 만 작성하십시오.

### 둘째, @MapperScan주석

Pure Annotation을 사용하는 경우 일반 Mapper의 매개 변수는 이전과 같이 직접 구성 할 수 없으며이 방법에 적응하기 위해 세 가지 방법이 제공됩니다.

다음은 내림차순으로 주석 구성의 사용법을 설명합니다.

#### 1. mapperHelperRef구성

```java
@Configuration
@MapperScan(value = "tk.mybatis.mapper.mapper", mapperHelperRef = "mapperHelper")
public static class MyBatisConfigRef {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .addScript("CreateDB.sql")
                .build();
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        return sessionFactory.getObject();
    }

    @Bean
    public MapperHelper mapperHelper() {
        Config config = new Config();
        List<Class> mappers = new ArrayList<Class>();
        mappers.add(Mapper.class);
        config.setMappers(mappers);

        MapperHelper mapperHelper = new MapperHelper();
        mapperHelper.setConfig(config);
        return mapperHelper;
    }
}
```

이 예제에서는 MapperHelper bean을 지정 @MapperScan하는 특수 mapperHelperRef특성이 있는 유일한 장소이며 name, 여기서 이름과 코드 mapperHelper()는 동일한 메소드 이름으로 구성됩니다 .

>Spring의 기본 이름은 메소드 이름이며 @Bean, 주석 도 지정할 수 있습니다 name.

이 구성에서는 구성을 쉽게 제어 할 수 있습니다 MapperHelper.

#### 2. properties구성 사용

```java
@Configuration
@MapperScan(value = "tk.mybatis.mapper.mapper",
    properties = {
            "mappers=tk.mybatis.mapper.common.Mapper",
            "notEmpty=true"
    }
)
public static class MyBatisConfigProperties {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .addScript("CreateDB.sql")
                .build();
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        return sessionFactory.getObject();
    }
}
```
위의 코드에서 볼 수 있듯이,이 구성과 방식은 xml bean에 더 가깝게 xx=xxx, 공통 매퍼 구성에서 한 줄씩 통과 하며, 여기의 예제를 참조하여 구성 중에 구성 할 수 있습니다.

#### 3. 스프링 부트 환경 application.[yml|properties]프로파일

Spring Boot에서 Mapper를 사용할 때 어노테이션 방법을 사용하도록 선택하면 (매퍼-스타터 종속성이 도입되지 않을 수 있음) 세 번째 방법을 선택할 수 있습니다.

>주의 : 구성 파일 방법은 Spring Boot에서 일반적이며 환경 변수 또는 런타임 매개 변수를 사용하여 구성 할 수 있으며 이러한 구성은 일반 Mapper에 효과적 일 수 있습니다.

예를 들어 yml 형식으로 구성합니다.
```yml
mapper:
  mappers:
    - tk.mybatis.mapper.common.Mapper
    - tk.mybatis.mapper.common.Mapper2
  not-empty: true
```

propertie 설정：
```properties
mapper.mappers=tk.mybatis.mapper.common.Mapper,tk.mybatis.mapper.common.Mapper2
mapper.not-empty=true
```

>주의 : Spring Boot는 relax 모드 매개 변수 구성을 지원하지만 처음 두 가지 방법은 지원되지 않습니다. 처음 두 매개 변수를 구성 할 때 대소 문자가 일치하는지 확인해야합니다!

### 셋, tk.mybatis.mapper.session.Configuration구성

**사용 요구 사항 : MyBatis (3.4.0+) 및 mybatis-spring (1.3.0+)**

클래스의 패키지 이름 인이 클래스는 다음과 같이 Configuration클래스의 MyBatis를 상속 하고 addMappedStatement메서드를 재정의합니다 .

```java
@Override
public void addMappedStatement(MappedStatement ms) {
    try {
        super.addMappedStatement(ms);
        //여기서 처리 할 때 모든 메서드가 올바르게 처리된다는 것이 더 보장됩니다. 
        if (this.mapperHelper != null) {
            this.mapperHelper.processMappedStatement(ms);
        }
    } catch (IllegalArgumentException e) {
        //여기서 예외는 Spring이 무한 루프를 시작하게하는 주요 위치입니다. 후속 삼키는 예외를 피하기 위해 직접 출력 
        e.printStackTrace();
        throw new RuntimeException(e);
    }
}
```

tk.mybatis.mapper.session.Configuration 다음과 같이 일반 매퍼를 구성하는 세 가지 방법이 있습니다.
```java
/**
 * mapperHelper 직접 주입 
 *
 * @param mapperHelper
 */
public void setMapperHelper(MapperHelper mapperHelper) {
    this.mapperHelper = mapperHelper;
}

/**
 * 속성을 사용하여 구성 
 *
 * @param properties
 */
public void setMapperProperties(Properties properties) {
    if (this.mapperHelper == null) {
        this.mapperHelper = new MapperHelper();
    }
    this.mapperHelper.setProperties(properties);
}

/**
 *  Config를 사용하여 구성 
 *
 * @param config
 */
public void setConfig(Config config) {
    if (mapperHelper == null) {
        mapperHelper = new MapperHelper();
    }
    mapperHelper.setConfig(config);
}
```

tk.mybatis.mapper.session.ConfigurationSpring에서 두 가지 구성 방법 사용

#### 1. Spring XML 설정

구성은 다음과 같습니다：

```xml
<!--Configuration-->
<bean id="mybatisConfig" class="tk.mybatis.mapper.session.Configuration">
    <property name="mapperProperties">
        <value>
            notEmpty=true
        </value>
    </property>
</bean>

<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="configuration" ref="mybatisConfig"/>
</bean>

<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="tk.mybatis.mapper.configuration"/>
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
</bean>
``` 

>주의：이 경우 MapperScannerConfigurer는 tk의 시작이 아니라 공식 mybatis-spring에서 제공되는 클래스입니다!

여기의 구성을 참조하고 다른 방법과의 차이점에주의하십시오.

여기에 tk Configuration에서 제공되는 직접 구성이 다음으로 주입됩니다 SqlSessionFactoryBean.

#### 2. 주석

```java
@Bean
public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());

    tk.mybatis.mapper.session.Configuration configuration = new tk.mybatis.mapper.session.Configuration();

    configuration.setMapperHelper(new MapperHelper());
    sessionFactory.setConfiguration(configuration);
    
    return sessionFactory.getObject();
}
```

