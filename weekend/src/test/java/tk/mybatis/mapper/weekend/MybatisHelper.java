/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package tk.mybatis.mapper.weekend;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.common.SqlServerMapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;

/**
 * Description: MybatisHelper
 * Author: liuzh
 * Update: liuzh(2014-06-06 13:33)
 */
public class MybatisHelper {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            //SqlSessionFactory 만들기
            Reader reader = Resources.getResourceAsReader("mybatis-java.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            reader.close();
            //데이터베이스 생성
            SqlSession session = null;
            try {
                session = sqlSessionFactory.openSession();
                //MapperHelper 만들기
                MapperHelper mapperHelper = new MapperHelper();
                //특수 구성
                Config config = new Config();
                // UUID 생성 전략 설정
                // UUID 생성 전략을 구성하려면 OGNL 표현식이 필요합니다.
                // 기본값 32 비트 길이:@java.util.UUID@randomUUID().toString().replace("-", "")
                //config.setUUID("");
                // 기본 키 자동 증가 및 쓰기 되돌림 방법, 기본값은 MYSQL입니다. 자세한 내용은 문서를 참조하십시오.
                config.setIDENTITY("HSQLDB");
                // 지원 방법 주석
                // 3.3.1 버전 증가
                config.setEnableMethodAnnotation(true);
                config.setNotEmpty(true);
                //예제의 유형이 일치하는지 확인
                config.setCheckExampleEntityClass(true);
                //단순 유형 활성화
                config.setUseSimpleType(true);
                // 시퀀스 획득 규칙, {num} 형식 지정 매개 변수 사용, Oracle의 경우 기본값은 {0} .nextval입니다.
                // 각각 0,1,2에 해당하는 3 개의 선택적 매개 변수가 있습니다.SequenceName，ColumnName, PropertyName
                //config.setSeqFormat("NEXT VALUE FOR {0}");
                // 글로벌 카탈로그를 설정합니다. 기본값은 비어 있고 값이 설정되면 테이블을 작동 할 때 SQL이 catalog.tablename이됩니다.
                //config.setCatalog("");
                // 전역 스키마를 설정합니다. 기본값은 비어 있습니다. 값이 설정되면 테이블 작동시 SQL이schema.tablename
                // 카탈로그가 동시에 설정된 경우 먼저 사용catalog.tablename
                //config.setSchema("");
                // 기본 키 자동 증가 및 쓰기 되돌림 방법의 실행 순서, 기본값은 AFTER, 선택적 값(BEFORE|AFTER)
                //config.setOrder("AFTER");
                //구성 설정
                mapperHelper.setConfig(config);
                // 일반 Mapper 인터페이스 등록-상속 된 인터페이스 자동 등록 가능
                mapperHelper.registerMapper(Mapper.class);
                mapperHelper.registerMapper(MySqlMapper.class);
                mapperHelper.registerMapper(SqlServerMapper.class);
                mapperHelper.registerMapper(IdsMapper.class);
                //구성이 완료된 후 다음 작업을 수행하십시오.
                mapperHelper.processConfiguration(session.getConfiguration());
                //OK - mapperHelper의 작업이 완료되었으며 무시할 수 있습니다.

                Connection conn = session.getConnection();
                reader = Resources.getResourceAsReader("CreateDB.sql");
                ScriptRunner runner = new ScriptRunner(conn);
                runner.setLogWriter(null);
                runner.runScript(reader);
                reader.close();
            } finally {
                if (session != null) {
                    session.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 세션 받기
     * @return
     */
    public static SqlSession getSqlSession(){
        return sqlSessionFactory.openSession();
    }
}
