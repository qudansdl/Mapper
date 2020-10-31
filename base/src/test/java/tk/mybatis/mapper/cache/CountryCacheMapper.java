package tk.mybatis.mapper.cache;

import org.apache.ibatis.annotations.CacheNamespace;
import tk.mybatis.mapper.base.Country;
import tk.mybatis.mapper.common.Mapper;

/**
 * 인터페이스 만있는 경우 다음 주석을 추가하십시오.
 */
@CacheNamespace
public interface CountryCacheMapper extends Mapper<Country> {

}
