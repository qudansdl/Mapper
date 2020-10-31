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

package tk.mybatis.mapper.test.country;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.CountryMapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.Country;

import java.util.HashMap;
import java.util.Map;

/**
 * 기본 키로 조회
 *
 * @author liuzh
 */
public class TestSelectByPrimaryKey {

    /**
     * PK 기반 조회
     */
    @Test
    public void testDynamicSelectByPrimaryKey2() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = mapper.selectByPrimaryKey(35);

            Assert.assertNotNull(country);
            Assert.assertEquals(true, country.getId() == 35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 기본 키를 포함하는 개체를 매개 변수로 사용할 수 있습니다.
     */
    @Test
    public void testDynamicSelectByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setId(35);
            country = mapper.selectByPrimaryKey(country);
            Assert.assertNotNull(country);
            Assert.assertEquals(true, country.getId() == 35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 존재하지 않는 조회 결과
     */
    @Test
    public void testDynamicSelectByPrimaryKeyZero() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Assert.assertNull(mapper.selectByPrimaryKey(new Country()));
            Assert.assertNull(mapper.selectByPrimaryKey(new HashMap<String,Object>()));
            Assert.assertNull(mapper.selectByPrimaryKey(-10));
            Assert.assertNull(mapper.selectByPrimaryKey(0));
            Assert.assertNull(mapper.selectByPrimaryKey(1000));
            Assert.assertNull(mapper.selectByPrimaryKey(null));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 지도는 무료 일 수 있습니다
     */
    @Test
    public void testSelectByPrimaryKeyMap() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);

            Map map = new HashMap();
            map.put("id", 35);
            Country country = mapper.selectByPrimaryKey(map);
            Assert.assertNotNull(country);
            Assert.assertEquals(true, country.getId() == 35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());

            map = new HashMap();
            map.put("countryname", "China");
            Assert.assertNull(mapper.selectByPrimaryKey(map));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 개체에 기본 키가 없습니다.
     */
    @Test(expected = Exception.class)
    public void testDynamicDeleteNotFoundKeyProperties() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            mapper.selectByPrimaryKey(new Key());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 잘못된 기본 키 형식
     */
    @Test
    public void testDynamicDeleteException() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            mapper.selectByPrimaryKey(100);
        } finally {
            sqlSession.close();
        }
    }

    class Key {
    }
}
