package tk.mybatis.mapper.cache;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import tk.mybatis.mapper.base.BaseTest;
import tk.mybatis.mapper.base.Country;
import tk.mybatis.mapper.base.CountryMapper;

import java.io.IOException;
import java.io.Reader;

/**
 * @author liuzh
 */
public class CacheTest extends BaseTest {

    @Override
    protected Reader getConfigFileAsReader() throws IOException {
        return toReader(CacheTest.class.getResource("mybatis-config-cache.xml"));
    }

    @Test
    public void testNoCache() {
        SqlSession sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = mapper.selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            //CountryMapper는 보조 캐시를 사용하지 않으므로 다음 설정은 다음 조회에 영향을주지 않습니다 (다른 SqlSession).
            country.setCountryname("중국");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        //아래에서 새로운 것을 얻으십시오 sqlSession
        sqlSession = getSqlSession();
        try {
            CountryMapper mapper = sqlSession.getMapper(CountryMapper.class);
            Country country = mapper.selectByPrimaryKey(35);

            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());

            Assert.assertNotEquals("중국", country.getCountryname());
            Assert.assertNotEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testSingleInterfaceCache() {
        //2 단계 캐시의 더티 데이터 기능을 사용하여 2 단계 캐시를 확인합니다.
        SqlSession sqlSession = getSqlSession();
        try {
            CountryCacheMapper mapper = sqlSession.getMapper(CountryCacheMapper.class);
            Country country = mapper.selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            //여기에서 수정하면 더티 데이터가 생성됩니다. 이것은 보조 캐시를 확인하기위한 것입니다.
            country.setCountryname("중국");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        //이전 sqlSession.close ()가 캐시되고 새 sqlSession.close ()가 아래에서 확보됩니다. sqlSession
        sqlSession = getSqlSession();
        try {
            CountryCacheMapper mapper = sqlSession.getMapper(CountryCacheMapper.class);
            Country country = mapper.selectByPrimaryKey(35);
            Assert.assertEquals("중국", country.getCountryname());
            Assert.assertEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
        //캐시를 지우고 다시 시도하십시오.
        sqlSession = getSqlSession();
        try {
            CountryCacheMapper mapper = sqlSession.getMapper(CountryCacheMapper.class);
            //업데이트를 호출하여 캐시를 지우십시오.
            mapper.updateByPrimaryKey(new Country());
            Country country = mapper.selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    public void testCountryCacheRefMapper() {
        //--------------------selectByPrimaryKey---------------------
        //2 단계 캐시의 더티 데이터 기능을 사용하여 2 단계 캐시를 확인합니다.
        SqlSession sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            Country country = mapper.selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            //여기에서 수정하면 더티 데이터가 생성됩니다. 이것은 보조 캐시를 확인하기위한 것입니다.
            country.setCountryname("중국");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        //이전 sqlSession.close ()가 캐시되고 새 sqlSession.close ()가 아래에서 확보됩니다. sqlSession
        sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            Country country = mapper.selectByPrimaryKey(35);
            Assert.assertEquals("중국", country.getCountryname());
            Assert.assertEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }

        //--------------------selectById---------------------
        sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            Country country = mapper.selectById(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            //여기에서 수정하면 더티 데이터가 생성됩니다. 이것은 보조 캐시를 확인하기위한 것입니다.
            country.setCountryname("중국");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        //이전 sqlSession.close ()가 캐시되고 새 sqlSession.close ()가 아래에서 확보됩니다. sqlSession
        sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            Country country = mapper.selectById(35);
            Assert.assertEquals("중국", country.getCountryname());
            Assert.assertEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
        //캐시를 지우고 다시 시도하십시오.
        sqlSession = getSqlSession();
        try {
            CountryCacheRefMapper mapper = sqlSession.getMapper(CountryCacheRefMapper.class);
            //업데이트를 호출하여 캐시를 지우십시오.
            mapper.updateByPrimaryKey(new Country());
            Country country = mapper.selectById(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

    @Test
    @Ignore("MyBatis 버그가 있습니다.이 방법은 현재 작동하지 않습니다.")
    public void testCountryCacheWithXmlMapper() {
        //--------------------selectByPrimaryKey---------------------
        //2 단계 캐시의 더티 데이터 기능을 사용하여 2 단계 캐시를 확인합니다.
        SqlSession sqlSession = getSqlSession();
        try {
            CountryCacheWithXmlMapper mapper = sqlSession.getMapper(CountryCacheWithXmlMapper.class);
            Country country = mapper.selectByPrimaryKey(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            //여기에서 수정하면 더티 데이터가 생성됩니다. 이것은 보조 캐시를 확인하기위한 것입니다.
            country.setCountryname("중국");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        //이전 sqlSession.close ()가 캐시되고 새 sqlSession.close ()가 아래에서 확보됩니다. sqlSession
        sqlSession = getSqlSession();
        try {
            CountryCacheWithXmlMapper mapper = sqlSession.getMapper(CountryCacheWithXmlMapper.class);
            Country country = mapper.selectByPrimaryKey(35);
            Assert.assertEquals("중국", country.getCountryname());
            Assert.assertEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }

        //--------------------selectById---------------------
        sqlSession = getSqlSession();
        try {
            CountryCacheWithXmlMapper mapper = sqlSession.getMapper(CountryCacheWithXmlMapper.class);
            Country country = mapper.selectById(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
            //여기에서 수정하면 더티 데이터가 생성됩니다. 이것은 보조 캐시를 확인하기위한 것입니다.
            country.setCountryname("중국");
            country.setCountrycode("ZH");
        } finally {
            sqlSession.close();
        }
        //이전 sqlSession.close ()가 캐시되고 새 sqlSession.close ()가 아래에서 확보됩니다. sqlSession
        sqlSession = getSqlSession();
        try {
            CountryCacheWithXmlMapper mapper = sqlSession.getMapper(CountryCacheWithXmlMapper.class);
            Country country = mapper.selectById(35);
            Assert.assertEquals("중국", country.getCountryname());
            Assert.assertEquals("ZH", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
        //캐시를 지우고 다시 시도하십시오.
        sqlSession = getSqlSession();
        try {
            CountryCacheWithXmlMapper mapper = sqlSession.getMapper(CountryCacheWithXmlMapper.class);
            //업데이트를 호출하여 캐시를 지우십시오.
            mapper.updateByPrimaryKey(new Country());
            Country country = mapper.selectById(35);
            Assert.assertEquals("China", country.getCountryname());
            Assert.assertEquals("CN", country.getCountrycode());
        } finally {
            sqlSession.close();
        }
    }

}
