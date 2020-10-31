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

package tk.mybatis.mapper.test.user;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import tk.mybatis.mapper.mapper.MybatisHelper;
import tk.mybatis.mapper.mapper.UserInfoMapMapper;
import tk.mybatis.mapper.model.UserInfoMap;

import java.util.List;

/**
 * 테스트 추가, 삭제, 수정 및지도 확인은 더 이상 직접 지원되지 않습니다.
 *
 * @author liuzh
 */
public class TestMap {


    /**
     * 더하다
     */
//    @Test
    public void testInsert() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserInfoMapMapper mapper = sqlSession.getMapper(UserInfoMapMapper.class);
            UserInfoMap userInfoMap = new UserInfoMap();
            userInfoMap.setUserName("abel533");
            userInfoMap.setPassword("123456");
            userInfoMap.setUserType("2");

            Assert.assertEquals(1, mapper.insert(userInfoMap));

            Assert.assertNotNull(userInfoMap.getId());
            Assert.assertEquals(6, (int)userInfoMap.getId());

            Assert.assertEquals(1,mapper.deleteByPrimaryKey(userInfoMap));
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 메인 테스트 삭제
     */
//    @Test
    public void testDelete() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserInfoMapMapper mapper = sqlSession.getMapper(UserInfoMapMapper.class);
            //총 조회 수
            Assert.assertEquals(5, mapper.selectCount(new UserInfoMap()));
            //조회100
            UserInfoMap userInfoMap = mapper.selectByPrimaryKey(1);

            //기본 키를 기반으로 삭제
            Assert.assertEquals(1, mapper.deleteByPrimaryKey(1));

            //총 조회 수
            Assert.assertEquals(4, mapper.selectCount(new UserInfoMap()));
            //끼워 넣다
            Assert.assertEquals(1, mapper.insert(userInfoMap));
        } finally {
            sqlSession.close();
        }
    }


    /**
     * 조회
     */
//    @Test
    public void testSelect() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserInfoMapMapper mapper = sqlSession.getMapper(UserInfoMapMapper.class);
            UserInfoMap userInfoMap = new UserInfoMap();
            userInfoMap.setUserType("1");
            List<UserInfoMap> UserInfoMaps = mapper.select(userInfoMap);
            Assert.assertEquals(3, UserInfoMaps.size());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 기본 키를 기반으로 전체 업데이트
     */
//    @Test
    public void testUpdateByPrimaryKey() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserInfoMapMapper mapper = sqlSession.getMapper(UserInfoMapMapper.class);
            UserInfoMap userInfoMap = mapper.selectByPrimaryKey(2);
            Assert.assertNotNull(userInfoMap);
            userInfoMap.setUserType(null);
            userInfoMap.setRealName("liuzh");
            //업데이트되지 않음user_type
            Assert.assertEquals(1, mapper.updateByPrimaryKey(userInfoMap));

            userInfoMap = mapper.selectByPrimaryKey(userInfoMap);
            Assert.assertNull(userInfoMap.getUserType());
            Assert.assertEquals("liuzh",userInfoMap.getRealName());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 비 업데이트null
     */
//    @Test
    public void testUpdateByPrimaryKeySelective() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            UserInfoMapMapper mapper = sqlSession.getMapper(UserInfoMapMapper.class);
            UserInfoMap userInfoMap = mapper.selectByPrimaryKey(1);
            Assert.assertNotNull(userInfoMap);
            userInfoMap.setUserType(null);
            userInfoMap.setRealName("liuzh");
            //업데이트되지 않음user_type
            Assert.assertEquals(1, mapper.updateByPrimaryKeySelective(userInfoMap));

            userInfoMap = mapper.selectByPrimaryKey(1);
            Assert.assertEquals("1", userInfoMap.getUserType());
            Assert.assertEquals("liuzh",userInfoMap.getRealName());
        } finally {
            sqlSession.close();
        }
    }


}
