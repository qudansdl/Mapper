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

package tk.mybatis.mapper.additional.update.differ;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.annotation.Version;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.version.VersionException;

import java.util.Set;

/**
 * @author liuzh
 */
public class UpdateByDifferProvider extends MapperTemplate {
    public static final String OLD = "old";
    public static final String NEWER = "newer";

    public UpdateByDifferProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 차이 업데이트
     *
     * @param ms
     */
    public String updateByDiffer(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(updateSetColumnsByDiffer(entityClass));
        sql.append(wherePKColumns(entityClass, true));
        return sql.toString();
    }

    /**
     * 여기서 기본 키 조건
     *
     * @param entityClass
     * @return
     */
    public String wherePKColumns(Class<?> entityClass, boolean useVersion) {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        //모든 열 가져 오기
        Set<EntityColumn> columnSet = EntityHelper.getPKColumns(entityClass);
        //열에 기본 키 전략이있는 경우 해당 속성이 비어 있는지 여부를 고려할 필요가 없습니다. 비어있는 경우 기본 키 전략에 따라 값이 생성되기 때문입니다.
        for (EntityColumn column : columnSet) {
            sql.append(" AND " + column.getColumnEqualsHolder(NEWER));
        }
        if (useVersion) {
            sql.append(whereVersion(entityClass));
        }
        sql.append("</where>");
        return sql.toString();
    }


    /**
     * 낙관적 인 잠금 필드 조건
     *
     * @param entityClass
     * @return
     */
    public String whereVersion(Class<?> entityClass) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        boolean hasVersion = false;
        String result = "";
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (hasVersion) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解의들，一个수업中只能存在一个带有 @Version 注解의들!");
                }
                hasVersion = true;
                result = " AND " + column.getColumnEqualsHolder(NEWER);
            }
        }
        return result;
    }

    /**
     * update set기둥
     *
     * @param entityClass
     * @return
     */
    public String updateSetColumnsByDiffer(Class<?> entityClass) {
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
                } else {
                    //if old.xx != newer.xx
                    sql.append(getIfNotEqual(column, column.getColumnEqualsHolder(NEWER) + ","));
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * 자동 판단!=널 조건부 구조
     *
     * @param column
     * @param contents
     * @return
     */
    public String getIfNotEqual(EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"").append(OLD).append(".").append(column.getProperty());
        sql.append(" != ").append(NEWER).append(".").append(column.getProperty()).append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

}
