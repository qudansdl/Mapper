package tk.mybatis.mapper.additional.dialect.oracle;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

/**
 * @description: Oracle구현 클래스
 * @author: qrqhuangcy
 * @date: 2018-11-15
 **/
public class OracleProvider extends MapperTemplate {

    public OracleProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * <bind name="listNotEmptyCheck" value="@tk.mybatis.mapper.util.OGNL@notEmptyCollectionCheck(list, 'tk.mybatis.mapper.additional.dialect.oracle.DemoCountryMapper.insertList 메소드 매개 변수가 비어 있습니다.')"/>
     * INSERT ALL
     * <foreach collection="list" item="record">
     *     INTO demo_country
     *     <trim prefix="(" suffix=")" suffixOverrides=",">country_id,country_name,country_code,</trim>
     *     VALUES
     *     <trim prefix="(" suffix=")" suffixOverrides=",">
     *         <bind name="country_idGenIdBind"  value="@tk.mybatis.mapper.genid.GenIdUtil@genId(record, 'countryId', @tk.mybatis.mapper.additional.insertlist.UUIdGenId@class, 'demo_country', 'country_id')"/>
     *         #{record.countryId},#{record.countryName},#{record.countryCode},
     *     </trim>
     * </foreach>
     * SELECT 1 FROM DUAL
     * 
     * @param ms
     * @return
     */
    public String insertList(MappedStatement ms){
        final Class<?> entityClass = getEntityClass(ms);
        //맞춤법 SQL 시작
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"listNotEmptyCheck\" value=\"@tk.mybatis.mapper.util.OGNL@notEmptyCollectionCheck(list, '" + ms.getId() + " 메소드 매개 변수가 비어 있습니다.')\"/>\n");

        sql.append("INSERT ALL\n");
        sql.append("<foreach collection=\"list\" item=\"record\">\n");

        String tableName = SqlHelper.getDynamicTableName(entityClass, tableName(entityClass),"list[0]");
        String columns = SqlHelper.insertColumns(entityClass, false, false, false);
        sql.append(" INTO ").append(tableName).append(" ").append(columns).append("\n");
        sql.append(" VALUES ");

        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");

        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        //genId 메서드에 대한 지원을 별도로 추가
        for (EntityColumn column : columnList) {
            if(column.getGenIdClass() != null){
                sql.append("<bind name=\"").append(column.getColumn()).append("GenIdBind\" value=\"@tk.mybatis.mapper.genid.GenIdUtil@genId(");
                sql.append("record").append(", '").append(column.getProperty()).append("'");
                sql.append(", @").append(column.getGenIdClass().getCanonicalName()).append("@class");
                sql.append(", '").append(tableName(entityClass)).append("'");
                sql.append(", '").append(column.getColumn()).append("')");
                sql.append("\"/>");
            }
        }
        //열에 기본 키 전략이있는 경우 해당 속성이 비어 있는지 여부를 고려할 필요가 없습니다. 비어있는 경우 기본 키 전략에 따라 값이 생성되기 때문입니다.
        for (EntityColumn column : columnList) {
            if (column.isInsertable()) {
                sql.append(column.getColumnHolder("record") + ",");
            }
        }
        sql.append("</trim>\n");

        sql.append("</foreach>\n");
        sql.append("SELECT 1 FROM DUAL");

        //System.out.println("sql mapper: \n" + sql.toString());
        return sql.toString();
    }
}
