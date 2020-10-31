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

import java.util.List;

/**
 * 엔티티 클래스 속성 별 조회
 *
 * @author liuzh
 */
public class TestSelect {

    /**
     * 모두 조회
     */
    @Test
    public void testDynamicSelectAll() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            List<Country> countryList;
            //country.setDynamicTableName123("country_123");
            //countryList = mapper.select(country);
            //총 조회 수
            //Assert.assertEquals(2, countryList.size());

            country.setDynamicTableName123(null);
            countryList = mapper.select(country);
            //총 조회 수
            Assert.assertEquals(183, countryList.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 모두 조회
     */
    @Test
    public void testDynamicSelectPage() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setCountrycode("US");
            List<Country> countryList = mapper.selectPage(country, 0, 10);
            //총 조회 수
            Assert.assertEquals(1, countryList.size());

            countryList = mapper.selectPage(null, 100, 10);
            //총 조회 수
            Assert.assertEquals(10, countryList.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 모두 조회
     */
    @Test
    public void testAllColumns() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            //35,'China','CN'
            country.setCountrycode("CN");
            country.setId(35);
            country.setCountryname("China");
            List<Country> countryList = mapper.select(country);
            Assert.assertEquals(1, countryList.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 입력 매개 변수가 null 인 경우 모두 조회
     */
    @Test
    public void testDynamicSelectAllByNull() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            mapper.select(null);
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 조회 조건에 따른 조회
     */
    @Test
    public void testDynamicSelect() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setCountrycode("CN");
            List<Country> countryList = mapper.select(country);

            Assert.assertEquals(1, countryList.size());
            Assert.assertEquals(true, countryList.get(0).getId() == 35);
            Assert.assertEquals("China", countryList.get(0).getCountryname());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 존재하지 않는 조회 결과
     */
    @Test
    public void testDynamicSelectZero() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = new Country();
            country.setCountrycode("CN");
            country.setCountryname("천상의 왕조");//실제로 China
            List<Country> countryList = mapper.select(country);

            Assert.assertEquals(0, countryList.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 상속 된 클래스를 사용할 수 있지만 추가 속성이 유효하지 않습니다.
     */
    @Test
    public void testDynamicSelectNotFoundKeyProperties() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            //기본 키를 기반으로 삭제
            Assert.assertEquals(183, mapper.select(new Key()).size());

            Key key = new Key();
            key.setCountrycode("CN");
            key.setCountrytel("+86");
            Assert.assertEquals(1, mapper.select(key).size());
        } finally {
            sqlSession.close();
        }
    }

    class Key extends Country {
        private String countrytel;

        public String getCountrytel() {
            return countrytel;
        }

        public void setCountrytel(String countrytel) {
            this.countrytel = countrytel;
        }
    }

}
