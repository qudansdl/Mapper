package tk.mybatis.mapper.gensql;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;

/**
 * SQL 생성, 초기화 중 실행
 *
 * @author liuzh
 */
public interface GenSql {

    String genSql(EntityTable entityTable, EntityColumn entityColumn);

    class NULL implements GenSql {
        @Override
        public String genSql(EntityTable entityTable, EntityColumn entityColumn) {
            throw new UnsupportedOperationException();
        }
    }
}
