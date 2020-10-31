package tk.mybatis.mapper.additional.delete;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.additional.select.SelectPropertyProvider;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.MetaObjectUtil;

/**
 * @author jingkaihui
 * @date 2020/3/30
 */
public class DeletePropertyProvider extends MapperTemplate {

    public DeletePropertyProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 속성에 따라 삭제, 조건에 등호 사용
     *
     * @param ms
     * @return
     */
    public String deleteByProperty(MappedStatement ms) {
        String propertyHelper = DeletePropertyProvider.class.getName();
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        // 삭제 표시 인 경우 수정하여 테이블을 업데이트하고 삭제 표시 필드의 값을 수정합니다.
        if (SqlHelper.hasLogicDeleteColumn(entityClass)) {
            sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
            sql.append("<set>");
            sql.append(SqlHelper.logicDeleteColumnEqualsValue(entityClass, true));
            sql.append("</set>");
            MetaObjectUtil.forObject(ms).setValue("sqlCommandType", SqlCommandType.UPDATE);
        } else {
            sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        }
        sql.append("<where>\n");
        sql.append("<if test=\"false==");
        sql.append("@");
        sql.append(propertyHelper);
        sql.append("@isNull(value, ");
        sql.append(getConfig().isSafeDelete());
        sql.append(")");
        sql.append("\">\n");
        String entityClassName = entityClass.getName();
        //엔티티 클래스 이름으로 런타임 속성에 해당하는 필드를 가져옵니다.
        String ognl = new StringBuilder("${@")
                .append(propertyHelper)
                .append("@getColumnByProperty(@java.lang.Class@forName(\"")
                .append(entityClassName)
                .append("\"), @tk.mybatis.mapper.weekend.reflection.Reflections@fnToFieldName(fn))}").toString();
        sql.append(ognl + " = #{value}\n");
        sql.append("</if>\n");
        sql.append("</where>");
        return sql.toString();
    }

     /**
     * 속성에 따라 삭제, 조건에 등호 사용
     *
     * @param ms
     * @return
     */
    public String deleteInByProperty(MappedStatement ms) {
        String propertyHelper = DeletePropertyProvider.class.getName();
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        // 삭제 표시 인 경우 수정하여 테이블을 업데이트하고 삭제 표시 필드의 값을 수정합니다.
        if (SqlHelper.hasLogicDeleteColumn(entityClass)) {
            sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
            sql.append("<set>");
            sql.append(SqlHelper.logicDeleteColumnEqualsValue(entityClass, true));
            sql.append("</set>");
            MetaObjectUtil.forObject(ms).setValue("sqlCommandType", SqlCommandType.UPDATE);
        } else {
            sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        }
        sql.append("<where>\n");
        String entityClassName = entityClass.getName();
        String sqlSegment =
                "${@" + propertyHelper + "@getColumnByProperty(@java.lang.Class@forName(\"" + entityClassName + "\"),"
                        +   "@tk.mybatis.mapper.weekend.reflection.Reflections@fnToFieldName(fn))} in"
                        +   "<foreach open=\"(\" close=\")\" separator=\",\" collection=\"values\" item=\"obj\">\n"
                        +      "#{obj}\n"
                        +   "</foreach>\n";
        sql.append(sqlSegment);
        // 삭제 표시되지 않은 조회 조건
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 속성에 따라 삭제, 삭제 조건에 사용
     *
     * @param ms
     * @return
     */
    public String deleteBetweenByProperty(MappedStatement ms) {
        String propertyHelper = DeletePropertyProvider.class.getName();
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        // 삭제 표시 인 경우 수정하여 테이블을 업데이트하고 삭제 표시 필드의 값을 수정합니다.
        if (SqlHelper.hasLogicDeleteColumn(entityClass)) {
            sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
            sql.append("<set>");
            sql.append(SqlHelper.logicDeleteColumnEqualsValue(entityClass, true));
            sql.append("</set>");
            MetaObjectUtil.forObject(ms).setValue("sqlCommandType", SqlCommandType.UPDATE);
        } else {
            sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
        }
        sql.append("<where>\n");
        String entityClassName = entityClass.getName();
        String sqlSegment =
                "${@" + propertyHelper + "@getColumnByProperty(@java.lang.Class@forName(\"" + entityClassName + "\"),"
                        + "@tk.mybatis.mapper.weekend.reflection.Reflections@fnToFieldName(fn))} "
                        + "between #{begin} and #{end}";
        sql.append(sqlSegment);
        // 삭제 표시되지 않은 조회 조건
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 엔티티 클래스 및 속성 이름에 따라 해당 테이블 필드 이름을 얻습니다.
     * @param entityClass 엔티티 클래스 객체
     * @param property 속성 이름
     * @return
     */
    public static String getColumnByProperty(Class<?> entityClass, String property) {
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        EntityColumn entityColumn = entityTable.getPropertyMap().get(property);
        return entityColumn.getColumn();
    }

    public static boolean isNull(Object value, boolean safeDelete) {
        boolean isNull = false;
        if (safeDelete) {
            if (null == value) {
                throw new MapperException("안전하게 삭제模式下，不允许수행不带조회条件의 delete 方法");
            }
        } else {
            if (null == value) {
                isNull = true;
            }
        }
        return isNull;
    }
}
