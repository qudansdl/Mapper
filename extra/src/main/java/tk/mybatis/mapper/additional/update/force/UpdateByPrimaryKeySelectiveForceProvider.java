/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package tk.mybatis.mapper.additional.update.force;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.annotation.Version;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;
import tk.mybatis.mapper.version.VersionException;

import java.util.Set;

/**
 * @Description:  일반 매퍼 인터페이스, 업데이트, 필수, 구현
 * @author qrqhuangcy
 * @date 2018-06-26
 */
public class UpdateByPrimaryKeySelectiveForceProvider extends MapperTemplate {

    public static final String FORCE_UPDATE_PROPERTIES = "forceUpdateProperties";

    public UpdateByPrimaryKeySelectiveForceProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }


    public String updateByPrimaryKeySelectiveForce(MappedStatement ms) {
        Class entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass), "record"));
        sql.append(this.updateSetColumnsForce(entityClass, "record", true, isNotEmpty()));
        sql.append(SqlHelper.wherePKColumns(entityClass, "record", true));

        return sql.toString();
    }

    /**
     * update set기둥
     *
     * @param entityClass
     * @param entityName  엔티티 매핑 이름
     * @param notNull     판단할지 여부!=null
     * @param notEmpty    String 타입 판단 여부!=''
     * @return
     */
    public String updateSetColumnsForce(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //모든 열 가져 오기
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //낙관적 잠금 지원
        EntityColumn versionColumn = null;
        //열에 기본 키 전략이있는 경우 해당 속성이 비어 있는지 여부를 고려할 필요가 없습니다. 비어있는 경우 기본 키 전략에 따라 값이 생성되기 때문입니다.
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (versionColumn != null) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解의들，一个수업中只能存在一个带有 @Version 注解의들!");
                }
                versionColumn = column;
            }
            if (!column.isId() && column.isUpdatable()) {
                if (column == versionColumn) {
                    Version version = versionColumn.getEntityField().getAnnotation(Version.class);
                    String versionClass = version.nextVersion().getCanonicalName();
                    //version = ${@tk.mybatis.mapper.version@nextVersionClass("versionClass", version)}
                    sql.append(column.getColumn())
                        .append(" = ${@tk.mybatis.mapper.version.VersionUtil@nextVersion(")
                        .append("@").append(versionClass).append("@class, ")
                        .append(column.getProperty()).append(")},");
                } else if (notNull) {
                    sql.append(this.getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName) + ",");
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * 자동 판단!=널 조건부 구조
     *
     * @param entityName
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    public String getIfNotNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"");
        if (StringUtil.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" != null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" and ");
            if (StringUtil.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" != '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</when>");

        //지정된 필드가 강제로 업데이트됩니다.
        sql.append("<when test=\"");
        sql.append(FORCE_UPDATE_PROPERTIES).append(" != null and ").append(FORCE_UPDATE_PROPERTIES).append(".contains('");
        sql.append(column.getProperty());
        sql.append("')\">");
        sql.append(contents);
        sql.append("</when>");

        sql.append("<otherwise></otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

}
