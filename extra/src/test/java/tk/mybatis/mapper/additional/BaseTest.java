/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
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
 */

package tk.mybatis.mapper.additional;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;

/**
 * 테스트 기본 클래스
 *
 * @author liuzh
 */
public abstract class BaseTest {
    private SqlSessionFactory sqlSessionFactory;

    @Before
    public final void init(){
        try {
            Reader reader = getConfigFileAsReader();
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            reader.close();
            //일반 구성 Mapper
            configMapperHelper();
            //초기화 수행 SQL
            runSql(getSqlFileAsReader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 일반 구성 Mapper
     */
    protected void configMapperHelper(){
        SqlSession session = getSqlSession();
        try {
            //MapperHelper 만들기
            MapperHelper mapperHelper = new MapperHelper();
            //구성 설정
            mapperHelper.setConfig(getConfig());
            //구성이 완료된 후 다음 작업을 수행하십시오.
            mapperHelper.processConfiguration(session.getConfiguration());
        } finally {
            session.close();
        }
    }

    /**
     * 수행 Sql
     *
     * @param reader
     */
    protected void runSql(Reader reader) {
        if(reader == null){
            return;
        }
        SqlSession sqlSession = getSqlSession();
        try {
            Connection conn = sqlSession.getConnection();
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setLogWriter(null);
            runner.runScript(reader);
            try {
                reader.close();
            } catch (IOException e) {}
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 매퍼 구성 가져 오기
     *
     * @return
     */
    protected Config getConfig(){
        return new Config();
    }

    /**
     * mybatis 구성 가져 오기
     *
     * @return
     */
    protected Reader getConfigFileAsReader() throws IOException {
        URL url = BaseTest.class.getResource("mybatis-config.xml");
        return toReader(url);
    };

    /**
     * 초기화 가져 오기 sql
     *
     * @return
     */
    protected Reader getSqlFileAsReader() throws IOException {
        URL url = BaseTest.class.getResource("CreateDB.sql");
        return toReader(url);
    };

    /**
     * 로 변하다 Reader
     *
     * @param url
     * @return
     * @throws IOException
     */
    protected Reader toReader(URL url) throws IOException {
        return Resources.getUrlAsReader(url.toString());
    }

    /**
     * 세션 받기
     *
     * @return
     */
    protected SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }
}
