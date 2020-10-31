# mapper-weekend

[![Maven central](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-weekend/badge.svg)](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-weekend)

작성자：[liuyuyu](https://github.com/liuyuyu)

# jdk 8+ 지원

## 기술

작성자의 동의 후 프로젝트 패키지 이름과 Maven GAV 정보가 수정되고 프로젝트가 패키지되어 공식 Maven Repository에 업로드되었습니다.

이 프로젝트는 독립적 인 프로젝트이지만 사용할 때이 프로젝트를 참조 할 필요가 없습니다.

이 독립적 인 프로젝트는 jdk 8로 패키징되고 패키징 된 클래스는 일반 Mapper에 통합됩니다 (주 코드는 jdk 6으로 컴파일 됨).

## https://github.com/abel533/Mapper 기반 개선 사항

Example.Criteria메서드의 pass lambada를 조건화 할 수 있습니다 (더 이상 데이터베이스 변경에 대해 걱정할 필요가 없습니다 ...).

```java
UserMapper    userMapper = sqlSession.getMapper(UserMapper.class);
Weekend<User> weekend    = Weekend.of(User.class);
weekend.weekendCriteria()
      .andIsNull(User::getId)
      .andBetween(User::getId,0,10)
      .andIn(User::getUserName, Arrays.asList("a","b","c"));
```

작성자： [XuYin](https://github.com/chinaerserver)） 

```java
CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
List<Country> selectByWeekendSql = mapper.selectByExample(new Example.Builder(Country.class)
        .where(WeekendSqls.<Country>custom().andLike(Country::getCountryname, "China")).build());
```