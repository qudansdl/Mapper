# Mybatis 일반 Mapper 확장 방법

[![Maven central](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-extra/badge.svg)](https://maven-badges.herokuapp.com/maven-central/tk.mybatis/mapper-extra)

## 확장 방법 소개

### InsertListMapper

대량 등록

- mysql, h2 등과 같이 배치 삽입을 지원하는 모든 데이터베이스를 사용할 수 있습니다.

    `tk.mybatis.mapper.additional.insert.InsertListMapper`

    SQL은 다음과 같습니다 `insert table(xxx) values (xxx), (xxx) ...`

- Oracle 대량 등록 
    `tk.mybatis.mapper.additional.dialect.oracle.InsertListMapper`

    SQL은 다음과 같습니다.
    ```sql
     INSERT ALL
     INTO demo_country ( country_id,country_name,country_code ) VALUES ( ?,?,? )
     INTO demo_country ( country_id,country_name,country_code ) VALUES ( ?,?,? )
     INTO demo_country ( country_id,country_name,country_code ) VALUES ( ?,?,? )
     SELECT 1 FROM DUAL
    ```

    **문법적 제한으로 인해 시퀀스는 일시적으로 지원되지 않습니다..**

### UpdateByPrimaryKeySelectiveForceMapper

빈 필드의 강제 업데이트

`UpdateByPrimaryKeySelectiveMapper`중간 널값 시나리오가 제공하는 솔루션은 또한 설정 될 필요가있다.

참조: [https://github.com/abel533/Mapper/issues/133](https://github.com/abel533/Mapper/issues/133)