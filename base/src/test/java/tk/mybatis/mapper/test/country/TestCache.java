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
import org.junit.Test;
import tk.mybatis.mapper.mapper.CachedCountryMapper;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.model.Country;

/**
 * 엔티티 클래스 속성 별 조회
 *
 * @author liuzh
 */
public class TestCache {

    @Test
    public void testCache() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CachedCountryMapper mapper = sqlSession.getMapper(CachedCountryMapper.class);
            Country country = new Country();
            country.setCountrycode("CN");
            //첫 번째 조회가 캐시됩니다.
            country = mapper.selectOne(country);
            //닫기 만 실제로 캐시되며 첫 번째 레벨 캐시가 아닙니다.
            sqlSession.close();

            //======================================================================
            sqlSession = MybatisHelper.getSqlSession();
            mapper = sqlSession.getMapper(CachedCountryMapper.class);
            country = new Country();
            country.setCountrycode("CN");
            //두 번째 조회는 첫 번째 캐시를 사용합니다.
            country = mapper.selectOne(country);
            //닫기 만 실제로 캐시되며 첫 번째 레벨 캐시가 아닙니다.
            sqlSession.close();
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testCache2() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            //첫 번째 조회가 캐시됩니다.
            sqlSession.selectOne("selectCache", 35);
            //닫기 만 실제로 캐시되며 첫 번째 레벨 캐시가 아닙니다.
            sqlSession.close();

            //======================================================================
            sqlSession = MybatisHelper.getSqlSession();
            sqlSession.selectOne("selectCache", 35);
            sqlSession.close();
        } finally {
            sqlSession.close();
        }
    }
}
