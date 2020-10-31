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

package tk.mybatis.mapper.test.country2;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.Country2Mapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.Country2;

import java.util.List;

/**
 * 엔티티 클래스 속성을 통해 Insert -Country2가 자동으로 ID 증가
 *
 * @author liuzh
 */
public class TestInsert {

    /**
     * 빈 데이터를 Insert하세요. ID는 null 일 수 없습니다. 오류가보고됩니다.
     */
    @Test
    public void testDynamicInsertAll() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            Country2Mapper mapper = sqlSession.getMapper(Country2Mapper.class);
            Country2 country2 = new Country2();
            country2.setCountrycode("CN");
            country2.setId(100);
            Assert.assertEquals(1, mapper.insert(country2));

            country2 = mapper.select(country2).get(0);
            Assert.assertNotNull(country2);

            Assert.assertEquals(1, mapper.deleteByPrimaryKey(country2.getId()));

        } finally {
            sqlSession.close();
        }
    }

    /**
     * Insert 할 수 없습니다.null
     */
    @Test
    public void testDynamicInsertAllByNull() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            Country2Mapper mapper = sqlSession.getMapper(Country2Mapper.class);
            mapper.insert(null);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 완전한 데이터 Insert
     */
    @Test
    public void testDynamicInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            Country2Mapper mapper = sqlSession.getMapper(Country2Mapper.class);
            Country2 country = new Country2();
            country.setId(10086);
            country.setCountrycode("CN");
            country.setCountryname("천상의 왕조");
            Assert.assertEquals(1, mapper.insert(country));

            //2 CN 결과
            country = new Country2();
            country.setCountrycode("CN");
            List<Country2> list = mapper.select(country);

            Assert.assertEquals(1, list.size());
            //다른 테스트에 영향을주지 않도록 Insert 된 데이터 삭제
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 코드가 null 인 데이터를 Insert합니다. 기본값 HH는 사용되지 않습니다.
     */
    @Test
    public void testDynamicInsertNull() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            Country2Mapper mapper = sqlSession.getMapper(Country2Mapper.class);
            Country2 country = new Country2();
            country.setId(10086);
            country.setCountryname("천상의 왕조");
            Assert.assertEquals(1, mapper.insert(country));

            //2 CN 결과
            country = new Country2();
            country.setId(10086);
            List<Country2> list = mapper.select(country);

            Assert.assertEquals(1, list.size());
            Assert.assertNull(list.get(0).getCountrycode());
            //다른 테스트에 영향을주지 않도록 Insert 된 데이터 삭제
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }

}
