package tk.mybatis.mapper.additional.select;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Objects;

/**
 * @author jingkaihui
 * @date 2019/10/11
 */
public class SelectPropertyProvider extends MapperTemplate {

    private static final Log log = LogFactory.getLog(SelectPropertyProvider.class);

    public SelectPropertyProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**Ba
     * 속성 질의에 따르면 반환 값은 하나만있을 수 있으며, 결과가 여러 개인 경우 예외가 발생합니다. 질의 조건은 등호를 사용합니다.
     *
     * @param ms
     * @return
     */
    public String selectOneByProperty(MappedStatement ms) {
        String propertyHelper = SelectPropertyProvider.class.getName();
        Class<?> entityClass = getEntityClass(ms);
        //반환 값 유형을 엔티티 유형으로 수정
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append("<where>\n");
        sql.append("<if test=\"@");
        sql.append(propertyHelper);
        sql.append("@existsWhereCondition(value, ");
        sql.append(isNotEmpty());
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
        // 삭제 표시되지 않은 조회 조건
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 속성 조회에 따르면 조회 조건은 등호를 사용합니다.
     *
     * @param ms
     * @return
     */
    public String selectByProperty(MappedStatement ms) {
        String propertyHelper = SelectPropertyProvider.class.getName();
        Class<?> entityClass = getEntityClass(ms);
        //반환 값 유형을 엔티티 유형으로 수정
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append("<where>\n");
        sql.append("<if test=\"@");
        sql.append(propertyHelper);
        sql.append("@existsWhereCondition(value, ");
        sql.append(isNotEmpty());
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
        // 삭제 표시되지 않은 조회 조건
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 속성 조회에 따라 조회 조건 사용 in
     *
     * @param ms
     * @return
     */
    public String selectInByProperty(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //반환 값 유형을 엔티티 유형으로 수정
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append("<where>\n");
        String entityClassName = entityClass.getName();
        String propertyHelper = SelectPropertyProvider.class.getName();
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
     * 속성 조회에 따르면 조회 조건은
     *
     * @param ms
     * @return
     */
    public String selectBetweenByProperty(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //반환 값 유형을 엔티티 유형으로 수정
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append("<where>\n");
        String entityClassName = entityClass.getName();
        String propertyHelper = SelectPropertyProvider.class.getName();
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
     * 총 속성 조회 수에 따라 조회 조건은 등호를 사용합니다.
     *
     * @param ms
     * @return
     */
    public String existsWithProperty(MappedStatement ms) {
        String propertyHelper = SelectPropertyProvider.class.getName();
        Class<?> entityClass = getEntityClass(ms);

        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectCountExists(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append("<where>\n");
        sql.append("<if test=\"@");
        sql.append(propertyHelper);
        sql.append("@existsWhereCondition(value, ");
        sql.append(isNotEmpty());
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
        // 삭제 표시되지 않은 조회 조건
        sql.append(SqlHelper.whereLogicDelete(entityClass, false));
        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 총 속성 조회 수에 따라 조회 조건은 등호를 사용합니다.
     *
     * @param ms
     * @return
     */
    public String selectCountByProperty(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        String propertyHelper = SelectPropertyProvider.class.getName();

        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectCount(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append("<where>\n");
        sql.append("<if test=\"@");
        sql.append(propertyHelper);
        sql.append("@existsWhereCondition(value, ");
        sql.append(isNotEmpty());
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

    /**
     * 조건에 접합할지 여부 결정
     * @param value
     * @param notEmpty
     * @return
     */
    public static boolean existsWhereCondition(Object value, boolean notEmpty) {
        boolean appendWhereCondition = true;
        if (Objects.isNull(value)) {
            log.warn("value is null! this will case no conditions after where keyword");
        } else {
            if (String.class.equals(value.getClass()) && notEmpty && StringUtil.isEmpty(value.toString())) {
                // 값이 문자열 유형이면 조건을 연결할지 여부를 결정하기 위해 추가 검사가 수행됩니다.
                appendWhereCondition = false;
            }
        }
        return appendWhereCondition;
    }
}
