package tk.mybatis.mapper.cache;

import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.base.Country;
import tk.mybatis.mapper.common.Mapper;

/**
 * 이 예에서 인터페이스는 캐시를 정의하고 캐시는 해당 XML에서 참조됩니다.
 *
 * TODO MyBatis 버그가 있습니다.이 방법은 현재 작동하지 않습니다.
 */
@CacheNamespace
public interface CountryCacheWithXmlMapper extends Mapper<Country> {

    /**
     * XML로 정의 된 메서드
     *
     * @param id
     * @return
     */
    Country selectById(Integer id);
}
