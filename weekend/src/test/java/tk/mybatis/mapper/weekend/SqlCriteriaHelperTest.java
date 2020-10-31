package tk.mybatis.mapper.weekend;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.weekend.entity.Country;
import tk.mybatis.mapper.weekend.mapper.CountryMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cheng.Wei
 */
public class SqlCriteriaHelperTest {
    /**
     * 널값 문제 무시
     */
    @Test
    public void ignore() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);

            List<Country> selectBySqlCriteriaHelper= mapper.selectByExample(new Example.Builder(Country.class)
                    .where(SqlCriteriaHelper.custom(Country.class)
                            .andEqualTo(Country::getCountryname, null)
                            .andLike(Country::getCountryname, "China")).build());

            List<Country> selectByWeekendSqls = mapper.selectByExample(new Example.Builder(Country.class)
                    .where(WeekendSqls.<Country>custom()
                            .andEqualTo(Country::getCountryname, null)
                            .andLike(Country::getCountrycode, "China")).build());
        } finally {
            sqlSession.close();
        }
    }


    /**
     * 널 속성을 무시하지 마십시오.
     * 속성이 null이고 무시되지 않으면 조회가 동일한 null에서 null로 변환됩니다.
     */
    @Test
    public void required() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);

            List<Country> selectBySqlCriteriaHelper= mapper.selectByExample(new Example.Builder(Country.class)
                    .where(SqlCriteriaHelper.custom(Country.class)
                            // 필수 = 조회를 계속하려면 true
                            .andEqualTo(Country::getCountryname, null, true)).build());

            List<Country> selectByWeekendSqls = mapper.selectByExample(new Example.Builder(Country.class)
                    .where(WeekendSqls.<Country>custom()
                            .andEqualTo(Country::getCountryname, null)).build());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 조회 자동 스티칭 %
     */
    @Test
    public void like() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);

            List<Country> selectBySqlCriteriaHelper= mapper.selectByExample(new Example.Builder(Country.class)
                    .where(SqlCriteriaHelper.custom(Country.class)
                            .andLike(Country::getCountryname, "Chin")
                            .orLike(Country::getCountryname, "A")).build());

            List<Country> selectByWeekendSqls = mapper.selectByExample(new Example.Builder(Country.class)
                    .where(WeekendSqls.<Country>custom()
                            .andLike(Country::getCountryname, "Chin")
                            .orLike(Country::getCountryname, "A")).build());
            //두 결과 배열의 내용이 동일한 지 확인
            Assert.assertArrayEquals(selectBySqlCriteriaHelper.toArray(), selectByWeekendSqls.toArray());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * 조회 빈 컬렉션 문제
     */
    @Test
    public void list() {
        SqlSession sqlSession = MybatisHelper.getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);

            List<Country> selectBySqlCriteriaHelper= mapper.selectByExample(new Example.Builder(Country.class)
                    .where(SqlCriteriaHelper.custom(Country.class)
                            .andIn(Country::getCountryname, new ArrayList())
                            .orLike(Country::getCountryname, "A")).build());

            List<Country> selectByWeekendSqls = mapper.selectByExample(new Example.Builder(Country.class)
                    .where(WeekendSqls.<Country>custom()
                            .andIn(Country::getCountryname, new ArrayList())
                            .orLike(Country::getCountryname, "A")).build());
            //두 결과 배열의 내용이 동일한 지 확인
            Assert.assertArrayEquals(selectBySqlCriteriaHelper.toArray(), selectByWeekendSqls.toArray());
        } finally {
            sqlSession.close();
        }
    }
}
