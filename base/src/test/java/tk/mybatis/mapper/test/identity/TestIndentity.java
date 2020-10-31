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

package tk.mybatis.mapper.test.identity;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.mapper.CountryIMapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.CountryI;

import java.util.List;

/**
 * Created by liuzh on 2014/11/21.
 */
public class TestIndentity {
    /**
     * 완전한 데이터 Insert
     */
    @Test
    public void testINDENTITYInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryIMapper mapper = sqlSession.getMapper(CountryIMapper.class);
            CountryI country = new CountryI();
            country.setCountrycode("CN");
            Assert.assertEquals(1, mapper.insert(country));
            //ID가 다시 작성됩니다
            Assert.assertNotNull(country.getId());
            //다른 테스트에 영향을주지 않도록 Insert 된 데이터 삭제
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(country.getId()));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 완전한 데이터 Insert
     */
    @Test
    public void testINDENTITYInsert2() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryIMapper mapper = sqlSession.getMapper(CountryIMapper.class);
            CountryI country = new CountryI();
            country.setId(10086);
            country.setCountrycode("CN");
            country.setCountryname("천상의 왕조");
            Assert.assertEquals(1, mapper.insert(country));

            //CN 결과 조회
            country = new CountryI();
            country.setCountrycode("CN");
            List<CountryI> list = mapper.select(country);

            Assert.assertEquals(1, list.size());
            Assert.assertNotNull(list.get(0).getCountryname());
            Assert.assertEquals("천상의 왕조", list.get(0).getCountryname());
            //다른 테스트에 영향을주지 않도록 Insert 된 데이터 삭제
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 완전한 데이터 Insert
     */
    @Test
    public void testINDENTITYInsertSelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryIMapper mapper = sqlSession.getMapper(CountryIMapper.class);
            CountryI country = new CountryI();
            Assert.assertEquals(1, mapper.insertSelective(country));
            //ID가 다시 작성됩니다
            Assert.assertNotNull(country.getId());
            //기본값이있는 기타 속성은 자동으로 다시 기록되지 않으며 수동으로 조회해야합니다.
            country = mapper.selectByPrimaryKey(country);
            //조회 후 기본값은null
            Assert.assertNotNull(country.getCountrycode());
            Assert.assertEquals("HH", country.getCountrycode());
            //다른 테스트에 영향을주지 않도록 Insert 된 데이터 삭제
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(country.getId()));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 완전한 데이터 Insert
     */
    @Test
    public void testINDENTITYInsertSelective2() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryIMapper mapper = sqlSession.getMapper(CountryIMapper.class);
            CountryI country = new CountryI();
            country.setId(10086);
            country.setCountrycode("CN");
            country.setCountryname("천상의 왕조");
            Assert.assertEquals(1, mapper.insertSelective(country));

            //CN 결과 조회
            country = new CountryI();
            country.setCountrycode("CN");
            List<CountryI> list = mapper.select(country);

            Assert.assertEquals(1, list.size());
            Assert.assertNotNull(list.get(0).getCountryname());
            Assert.assertEquals("천상의 왕조", list.get(0).getCountryname());
            //다른 테스트에 영향을주지 않도록 Insert 된 데이터 삭제
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(10086));
        } finally {
            sqlSession.close();
        }
    }
}
