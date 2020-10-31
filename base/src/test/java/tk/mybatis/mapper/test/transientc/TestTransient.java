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

package tk.mybatis.mapper.test.transientc;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.CountryTMapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.CountryT;

import java.util.List;

/**
 * Created by liuzh on 2014/11/21.
 */
public class TestTransient {
    /**
     * 완전한 데이터 Insert
     */
    @Test
    public void testDynamicInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryTMapper mapper = sqlSession.getMapper(CountryTMapper.class);
            CountryT country = new CountryT();
            country.setId(10086);
            country.setCountrycode("CN");
            country.setCountryname("천상의 왕조");
            Assert.assertEquals(1, mapper.insert(country));

            //CN 결과 조회
            country = new CountryT();
            country.setCountrycode("CN");
            List<CountryT> list = mapper.select(country);

            Assert.assertEquals(2, list.size());
            //차단 된 데이터는null
            Assert.assertNull(list.get(0).getCountrycode());
            //다른 테스트에 영향을주지 않도록 Insert 된 데이터 삭제
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 조회 조건에 따른 조회
     */
    @Test
    public void testDynamicUpdateByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryTMapper mapper = sqlSession.getMapper(CountryTMapper.class);
            CountryT country = new CountryT();
            country.setId(174);
            country.setCountryname("미국");
            country.setCountrycode("US");
            Assert.assertEquals(1, mapper.updateByPrimaryKey(country));

            country = mapper.selectByPrimaryKey(174);
            Assert.assertNotNull(country);
            Assert.assertEquals(174, (int) country.getId());
            Assert.assertEquals("미국",country.getCountryname());
            Assert.assertNull(country.getCountrycode());
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
            CountryTMapper mapper = sqlSession.getMapper(CountryTMapper.class);
            CountryT country = new CountryT();
            country.setId(174);
            country.setCountrycode("US");
            List<CountryT> countryList = mapper.select(country);

            Assert.assertEquals(1, countryList.size());
            Assert.assertEquals(true, countryList.get(0).getId() == 174);
            Assert.assertNotNull(countryList.get(0).getCountryname());
            Assert.assertNull(countryList.get(0).getCountrycode());
        } finally {
            sqlSession.close();
        }
    }
}
