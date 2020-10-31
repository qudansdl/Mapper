package tk.mybatis.mapper.mapperhelper.resolve;

import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityTable;

/**
 * 엔티티 클래스 인터페이스 해결
 *
 * @author liuzh
 */
public interface EntityResolve {

    /**
     * 파싱 클래스는 EntityTable
     *
     * @param entityClass
     * @param config
     * @return
     */
    EntityTable resolveEntity(Class<?> entityClass, Config config);

}
