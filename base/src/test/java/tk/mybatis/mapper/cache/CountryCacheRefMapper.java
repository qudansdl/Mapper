package tk.mybatis.mapper.cache;

import org.apache.ibatis.annotations.CacheNamespaceRef;
import tk.mybatis.mapper.base.Country;
import tk.mybatis.mapper.common.Mapper;

/**
 * 이 예에서 캐시는 XML로 구성되고 여기에서 주석은 XML의 캐시 구성을 참조하는 데 사용됩니다.
 *
 * 네임 스페이스를 구성하는 방법에는 두 가지가 있습니다. 다음 두 줄의 주석을 참조하십시오.
 */
@CacheNamespaceRef(CountryCacheRefMapper.class)
//@CacheNamespaceRef(name = "tk.mybatis.mapper.cache.CountryCacheRefMapper")
public interface CountryCacheRefMapper extends Mapper<Country> {

    /**
     * XML로 정의 된 메서드
     *
     * @param id
     * @return
     */
    Country selectById(Integer id);
}
