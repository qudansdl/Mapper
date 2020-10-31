package tk.mybatis.mapper.base.delete;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import tk.mybatis.mapper.base.BaseTest;
import tk.mybatis.mapper.base.Country;
import tk.mybatis.mapper.base.CountryMapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.Example;

public class SafeDeleteByMethodTest extends BaseTest {

    @Override
    protected Config getConfig() {
        Config config = super.getConfig();
        config.setSafeDelete(true);
        //테스트와 SafeDeleteByFieldTest의 차이점은 여기에 있으며, 나중에 EntityField.getValue를 호출하면 getter 메서드를 사용하여 값을 가져옵니다.
        config.setEnableMethodAnnotation(true);
        return config;
    }

    @Test(expected = PersistenceException.class)
    public void testSafeDelete() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            mapper.delete(new Country());
        } finally {
            sqlSession.close();
        }
    }

    @Test(expected = PersistenceException.class)
    public void testSafeDeleteNull() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            mapper.delete(null);
        } finally {
            sqlSession.close();
        }
    }


    @Test(expected = PersistenceException.class)
    public void testSafeDeleteByExample() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            mapper.deleteByExample(new Example(Country.class));
        } finally {
            sqlSession.close();
        }
    }

    @Test(expected = PersistenceException.class)
    public void testSafeDeleteByExampleNull() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            mapper.deleteByExample(null);
        } finally {
            sqlSession.close();
        }
    }

}
