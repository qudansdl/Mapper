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

package tk.mybatis.mapper.mapperhelper;

import tk.mybatis.mapper.LogicDeleteException;
import tk.mybatis.mapper.annotation.LogicDelete;
import tk.mybatis.mapper.annotation.Version;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.IDynamicTableName;
import tk.mybatis.mapper.util.StringUtil;
import tk.mybatis.mapper.version.VersionException;

import java.util.Set;

/**
 * 일반적인 SQL 도구
 *
 * @author liuzh
 * @since 2015-11-03 22:40
 */
public class SqlHelper {

    /**
     * 테이블 이름 가져 오기-동적 테이블 이름 지원
     *
     * @param entityClass
     * @param tableName
     * @return
     */
    public static String getDynamicTableName(Class<?> entityClass, String tableName) {
        if (IDynamicTableName.class.isAssignableFrom(entityClass)) {
            StringBuilder sql = new StringBuilder();
            sql.append("<choose>");
            sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@isDynamicParameter(_parameter) and dynamicTableName != null and dynamicTableName != ''\">");
            sql.append("${dynamicTableName}\n");
            sql.append("</when>");
            //지정된 열이 지원되지 않는 경우 모든 열 조회
            sql.append("<otherwise>");
            sql.append(tableName);
            sql.append("</otherwise>");
            sql.append("</choose>");
            return sql.toString();
        } else {
            return tableName;
        }
    }

    /**
     * 테이블 이름 가져 오기-동적 테이블 이름 지원.이 메서드를 여러 입력 매개 변수에 사용하는 경우 parameterName을 통해 입력 매개 변수에 엔티티 클래스의 @Param 주석 값을 지정합니다.
     *
     * @param entityClass
     * @param tableName
     * @param parameterName
     * @return
     */
    public static String getDynamicTableName(Class<?> entityClass, String tableName, String parameterName) {
        if (IDynamicTableName.class.isAssignableFrom(entityClass)) {
            if (StringUtil.isNotEmpty(parameterName)) {
                StringBuilder sql = new StringBuilder();
                sql.append("<choose>");
                sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@isDynamicParameter(" + parameterName + ") and " + parameterName + ".dynamicTableName != null and " + parameterName + ".dynamicTableName != ''\">");
                sql.append("${" + parameterName + ".dynamicTableName}");
                sql.append("</when>");
                //지정된 열이 지원되지 않는 경우 모든 열 조회
                sql.append("<otherwise>");
                sql.append(tableName);
                sql.append("</otherwise>");
                sql.append("</choose>");
                return sql.toString();
            } else {
                return getDynamicTableName(entityClass, tableName);
            }

        } else {
            return tableName;
        }
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column
     * @return
     */
    public static String getBindCache(EntityColumn column) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"");
        sql.append(column.getProperty()).append("_cache\" ");
        sql.append("value=\"").append(column.getProperty()).append("\"/>");
        return sql.toString();
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column
     * @return
     */
    public static String getBindValue(EntityColumn column, String value) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"");
        sql.append(column.getProperty()).append("_bind\" ");
        sql.append("value='").append(value).append("'/>");
        return sql.toString();
    }

    /**
     * <bind name="pattern" value="'%' + _parameter.getTitle() + '%'" />
     *
     * @param column
     * @return
     */
    public static String getIfCacheNotNull(EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"").append(column.getProperty()).append("_cache != null\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * _cache 인 경우 == null
     *
     * @param column
     * @return
     */
    public static String getIfCacheIsNull(EntityColumn column, String contents) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"").append(column.getProperty()).append("_cache == null\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 자동 판단!=널 조건부 구조
     *
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    public static String getIfNotNull(EntityColumn column, String contents, boolean empty) {
        return getIfNotNull(null, column, contents, empty);
    }

    /**
     * 자동 == null의 조건 구조 판단
     *
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    public static String getIfIsNull(EntityColumn column, String contents, boolean empty) {
        return getIfIsNull(null, column, contents, empty);
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
    public static String getIfNotNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
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
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 자동 == null의 조건 구조 판단
     *
     * @param entityName
     * @param column
     * @param contents
     * @param empty
     * @return
     */
    public static String getIfIsNull(String entityName, EntityColumn column, String contents, boolean empty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"");
        if (StringUtil.isNotEmpty(entityName)) {
            sql.append(entityName).append(".");
        }
        sql.append(column.getProperty()).append(" == null");
        if (empty && column.getJavaType().equals(String.class)) {
            sql.append(" or ");
            if (StringUtil.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            sql.append(column.getProperty()).append(" == '' ");
        }
        sql.append("\">");
        sql.append(contents);
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * ID, 이름, 코드와 같은 모든 조회 열을 가져옵니다...
     *
     * @param entityClass
     * @return
     */
    public static String getAllColumns(Class<?> entityClass) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnSet) {
            sql.append(entityColumn.getColumn()).append(",");
        }
        return sql.substring(0, sql.length() - 1);
    }

    /**
     * select xxx,xxx...
     *
     * @param entityClass
     * @return
     */
    public static String selectAllColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(getAllColumns(entityClass));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * select count(x)
     *
     * @param entityClass
     * @return
     */
    public static String selectCount(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        Set<EntityColumn> pkColumns = EntityHelper.getPKColumns(entityClass);
        if (pkColumns.size() == 1) {
            sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
        } else {
            sql.append("COUNT(*) ");
        }
        return sql.toString();
    }

    /**
     * select case when count(x) > 0 then 1 else 0 end
     *
     * @param entityClass
     * @return
     */
    public static String selectCountExists(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CASE WHEN ");
        Set<EntityColumn> pkColumns = EntityHelper.getPKColumns(entityClass);
        if (pkColumns.size() == 1) {
            sql.append("COUNT(").append(pkColumns.iterator().next().getColumn()).append(") ");
        } else {
            sql.append("COUNT(*) ");
        }
        sql.append(" > 0 THEN 1 ELSE 0 END AS result ");
        return sql.toString();
    }

    /**
     * from tableName - 동적 테이블 이름
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String fromTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM ");
        sql.append(getDynamicTableName(entityClass, defaultTableName));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * update tableName - 동적 테이블 이름
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String updateTable(Class<?> entityClass, String defaultTableName) {
        return updateTable(entityClass, defaultTableName, null);
    }

    /**
     * update tableName - 동적 테이블 이름
     *
     * @param entityClass
     * @param defaultTableName 기본 테이블 이름
     * @param entityName       별명
     * @return
     */
    public static String updateTable(Class<?> entityClass, String defaultTableName, String entityName) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(getDynamicTableName(entityClass, defaultTableName, entityName));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * delete tableName - 동적 테이블 이름
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String deleteFromTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(getDynamicTableName(entityClass, defaultTableName));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * insert into tableName - 동적 테이블 이름
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String insertIntoTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(getDynamicTableName(entityClass, defaultTableName));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * insert into tableName - 동적 테이블 이름
     *
     * @param entityClass
     * @param defaultTableName
     * @param parameterName 동적 테이블 이름의 매개 변수 이름
     * @return
     */
    public static String insertIntoTable(Class<?> entityClass, String defaultTableName, String parameterName) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(getDynamicTableName(entityClass, defaultTableName, parameterName));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * insert table()기둥
     *
     * @param entityClass
     * @param skipId      컬럼에서 ID 유형 무시 여부
     * @param notNull     판단할지 여부!=null
     * @param notEmpty    String 타입 판단 여부!=''
     * @return
     */
    public static String insertColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        //모든 열 가져 오기
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //열에 기본 키 전략이있는 경우 해당 속성이 비어 있는지 여부를 고려할 필요가 없습니다. 비어있는 경우 기본 키 전략에 따라 값이 생성되기 때문입니다.
        for (EntityColumn column : columnSet) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(SqlHelper.getIfNotNull(column, column.getColumn() + ",", notEmpty));
            } else {
                sql.append(column.getColumn() + ",");
            }
        }
        sql.append("</trim>");
        return sql.toString();
    }

    /**
     * insert-values()기둥
     *
     * @param entityClass
     * @param skipId      컬럼에서 ID 유형 무시 여부
     * @param notNull     판단할지 여부!=null
     * @param notEmpty    String 타입 판단 여부!=''
     * @return
     */
    public static String insertValuesColumns(Class<?> entityClass, boolean skipId, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        //모든 열 가져 오기
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //열에 기본 키 전략이있는 경우 해당 속성이 비어 있는지 여부를 고려할 필요가 없습니다. 비어있는 경우 기본 키 전략에 따라 값이 생성되기 때문입니다.
        for (EntityColumn column : columnSet) {
            if (!column.isInsertable()) {
                continue;
            }
            if (skipId && column.isId()) {
                continue;
            }
            if (notNull) {
                sql.append(SqlHelper.getIfNotNull(column, column.getColumnHolder() + ",", notEmpty));
            } else {
                sql.append(column.getColumnHolder() + ",");
            }
        }
        sql.append("</trim>");
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
    public static String updateSetColumns(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //모든 열 가져 오기
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //낙관적 잠금 지원
        EntityColumn versionColumn = null;
        // 삭제 표시 열
        EntityColumn logicDeleteColumn = null;
        //열에 기본 키 전략이있는 경우 해당 속성이 비어 있는지 여부를 고려할 필요가 없습니다. 비어있는 경우 기본 키 전략에 따라 값이 생성되기 때문입니다.
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (versionColumn != null) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解의들，一个수업中只能存在一个带有 @Version 注解의들!");
                }
                versionColumn = column;
            }
            if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                if (logicDeleteColumn != null) {
                    throw new LogicDeleteException(entityClass.getCanonicalName() + " 中包含多个带有 @LogicDelete 注解의들，一个수업中只能存在一个带有 @LogicDelete 注解의들!");
                }
                logicDeleteColumn = column;
            }
            if (!column.isId() && column.isUpdatable()) {
                if (column == versionColumn) {
                    Version version = versionColumn.getEntityField().getAnnotation(Version.class);
                    String versionClass = version.nextVersion().getCanonicalName();
                    sql.append("<bind name=\"").append(column.getProperty()).append("Version\" value=\"");
                    //version = ${@tk.mybatis.mapper.version@nextVersionClass("versionClass", version)}
                    sql.append("@tk.mybatis.mapper.version.VersionUtil@nextVersion(")
                        .append("@").append(versionClass).append("@class, ");
                    if (StringUtil.isNotEmpty(entityName)) {
                        sql.append(entityName).append(".");
                    }
                    sql.append(column.getProperty()).append(")\"/>");
                    sql.append(column.getColumn()).append(" = #{").append(column.getProperty()).append("Version},");
                } else if (column == logicDeleteColumn) {
                    sql.append(logicDeleteColumnEqualsValue(column, false)).append(",");
                } else if (notNull) {
                    sql.append(SqlHelper.getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName) + ",");
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * 업데이트 세트 열, 낙관적 잠금 주석 @Version을 고려하지 않음
     *
     * @param entityClass
     * @param entityName  엔티티 매핑 이름
     * @param notNull     판단할지 여부!=null
     * @param notEmpty    String 타입 판단 여부!=''
     * @return
     */
    public static String updateSetColumnsIgnoreVersion(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //모든 열 가져 오기
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        // 삭제 표시 열
        EntityColumn logicDeleteColumn = null;
        //열에 기본 키 전략이있는 경우 해당 속성이 비어 있는지 여부를 고려할 필요가 없습니다. 비어있는 경우 기본 키 전략에 따라 값이 생성되기 때문입니다.
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                if (logicDeleteColumn != null) {
                    throw new LogicDeleteException(entityClass.getCanonicalName() + " 中包含多个带有 @LogicDelete 注解의들，一个수업中只能存在一个带有 @LogicDelete 注解의들!");
                }
                logicDeleteColumn = column;
            }
            if (!column.isId() && column.isUpdatable()) {
                if(column.getEntityField().isAnnotationPresent(Version.class)){
                    //ignore
                } else if (column == logicDeleteColumn) {
                    sql.append(logicDeleteColumnEqualsValue(column, false)).append(",");
                } else if (notNull) {
                    sql.append(SqlHelper.getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                } else {
                    sql.append(column.getColumnEqualsHolder(entityName) + ",");
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /**
     * 모든 매개 변수가 널이 아닌지 확인
     *
     * @param parameterName 매개 변수 이름
     * @param columnSet     확인할 열
     * @return
     */
    public static String notAllNullParameterCheck(String parameterName, Set<EntityColumn> columnSet) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"notAllNullParameterCheck\" value=\"@tk.mybatis.mapper.util.OGNL@notAllNullParameterCheck(");
        sql.append(parameterName).append(", '");
        StringBuilder fields = new StringBuilder();
        for (EntityColumn column : columnSet) {
            if (fields.length() > 0) {
                fields.append(",");
            }
            fields.append(column.getProperty());
        }
        sql.append(fields);
        sql.append("')\"/>");
        return sql.toString();
    }

    /**
     * Example 하나 이상의 조회 조건을 포함합니다.
     *
     * @param parameterName 매개 변수 이름
     * @return
     */
    public static String exampleHasAtLeastOneCriteriaCheck(String parameterName) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"exampleHasAtLeastOneCriteriaCheck\" value=\"@tk.mybatis.mapper.util.OGNL@exampleHasAtLeastOneCriteriaCheck(");
        sql.append(parameterName).append(")\"/>");
        return sql.toString();
    }

    /**
     * 여기서 기본 키 조건
     *
     * @param entityClass
     * @return
     */
    public static String wherePKColumns(Class<?> entityClass) {
        return wherePKColumns(entityClass, false);
    }

    /**
     * 여기서 기본 키 조건
     *
     * @param entityClass
     * @param useVersion
     * @return
     */
    public static String wherePKColumns(Class<?> entityClass, boolean useVersion) {
        return wherePKColumns(entityClass, null, useVersion);
    }

    /**
     * 여기서 기본 키 조건
     *
     * @param entityClass
     * @param entityName
     * @param useVersion
     * @return
     */
    public static String wherePKColumns(Class<?> entityClass, String entityName, boolean useVersion) {
        StringBuilder sql = new StringBuilder();
        boolean hasLogicDelete = hasLogicDeleteColumn(entityClass);

        sql.append("<where>");
        //모든 열 가져 오기
        Set<EntityColumn> columnSet = EntityHelper.getPKColumns(entityClass);
        //열에 기본 키 전략이있는 경우 해당 속성이 비어 있는지 여부를 고려할 필요가 없습니다. 비어있는 경우 기본 키 전략에 따라 값이 생성되기 때문입니다.
        for (EntityColumn column : columnSet) {
            sql.append(" AND ").append(column.getColumnEqualsHolder(entityName));
        }
        if (useVersion) {
            sql.append(whereVersion(entityClass));
        }

        if (hasLogicDelete) {
            sql.append(whereLogicDelete(entityClass, false));
        }

        sql.append("</where>");
        return sql.toString();
    }

    /**
     * 모든 열의 조건이 여부를 결정하는 곳!=null
     *
     * @param entityClass
     * @param empty
     * @return
     */
    public static String whereAllIfColumns(Class<?> entityClass, boolean empty) {
        return whereAllIfColumns(entityClass, empty, false);
    }

    /**
     * 모든 열의 조건이 여부를 결정하는 곳!=null
     *
     * @param entityClass
     * @param empty
     * @param useVersion
     * @return
     */
    public static String whereAllIfColumns(Class<?> entityClass, boolean empty, boolean useVersion) {
        StringBuilder sql = new StringBuilder();
        boolean hasLogicDelete = false;

        sql.append("<where>");
        //모든 열 가져 오기
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        EntityColumn logicDeleteColumn = SqlHelper.getLogicDeleteColumn(entityClass);
        //열에 기본 키 전략이있는 경우 해당 속성이 비어 있는지 여부를 고려할 필요가 없습니다. 비어있는 경우 기본 키 전략에 따라 값이 생성되기 때문입니다.
        for (EntityColumn column : columnSet) {
            if (!useVersion || !column.getEntityField().isAnnotationPresent(Version.class)) {
                // 삭제되지 않은 삭제되지 않은 삭제 표시 필드의 상태 인 삭제 표시는 나중에 연결됩니다.
                if (logicDeleteColumn != null && logicDeleteColumn == column) {
                    hasLogicDelete = true;
                    continue;
                }
                sql.append(getIfNotNull(column, " AND " + column.getColumnEqualsHolder(), empty));
            }
        }
        if (useVersion) {
            sql.append(whereVersion(entityClass));
        }
        if (hasLogicDelete) {
            sql.append(whereLogicDelete(entityClass, false));
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
    public static String whereVersion(Class<?> entityClass) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        boolean hasVersion = false;
        String result = "";
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (hasVersion) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解의들，一个수업中只能存在一个带有 @Version 注解의들!");
                }
                hasVersion = true;
                result = " AND " + column.getColumnEqualsHolder();
            }
        }
        return result;
    }

    /**
     * 삭제 표시의 where 조건, 삭제 표시 주석이없는 경우 빈 문자열이 반환됩니다.
     * <br>
     * AND column = value
     *
     * @param entityClass
     * @param isDeleted   true：삭제 표시，false：삭제 표시되지 않음
     * @return
     */
    public static String whereLogicDelete(Class<?> entityClass, boolean isDeleted) {
        String value = logicDeleteColumnEqualsValue(entityClass, isDeleted);
        return "".equals(value) ? "" : " AND " + value;
    }

    /**
     * 반환 형식: column = value
     * <br>
     * 기본isDeletedValue = 1  notDeletedValue = 0
     * <br>
     * 그런 다음 is_deleted = 1 또는 is_deleted = 0
     * <br>
     * 삭제 표시 주석이없는 경우 빈 문자열이 반환됩니다.
     *
     * @param entityClass
     * @param isDeleted   true 이미 삭제 표시됨 false 삭제 표시되지 않음
     */
    public static String logicDeleteColumnEqualsValue(Class<?> entityClass, boolean isDeleted) {
        EntityColumn logicDeleteColumn = SqlHelper.getLogicDeleteColumn(entityClass);

        if (logicDeleteColumn != null) {
            return logicDeleteColumnEqualsValue(logicDeleteColumn, isDeleted);
        }

        return "";
    }

    /**
     * 반환 형식: column = value
     * <br>
     * 기본isDeletedValue = 1  notDeletedValue = 0
     * <br>
     * 그런 다음 is_deleted = 1 또는 is_deleted = 0
     * <br>
     * 삭제 표시 주석이없는 경우 빈 문자열이 반환됩니다.
     *
     * @param column
     * @param isDeleted true 이미 삭제 표시됨 false 삭제 표시되지 않음
     */
    public static String logicDeleteColumnEqualsValue(EntityColumn column, boolean isDeleted) {
        String result = "";
        if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
            result = column.getColumn() + " = " + getLogicDeletedValue(column, isDeleted);
        }
        return result;
    }

    /**
     * 삭제 표시 주석의 매개 변수 값 가져 오기
     *
     * @param column
     * @param isDeleted true：삭제 표시 값，false：삭제 표시되지 않은 값
     * @return
     */
    public static int getLogicDeletedValue(EntityColumn column, boolean isDeleted) {
        if (!column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
            throw new LogicDeleteException(column.getColumn() + " 没有 @LogicDelete 注解!");
        }
        LogicDelete logicDelete = column.getEntityField().getAnnotation(LogicDelete.class);
        if (isDeleted) {
            return logicDelete.isDeletedValue();
        }
        return logicDelete.notDeletedValue();
    }

    /**
     * 삭제 표시 주석이 있습니까
     *
     * @param entityClass
     * @return
     */
    public static boolean hasLogicDeleteColumn(Class<?> entityClass) {
        return getLogicDeleteColumn(entityClass) != null;
    }

    /**
     * null을 반환하지 않는 경우 삭제 표시 주석 열을 가져옵니다.
     *
     * @param entityClass
     * @return
     */
    public static EntityColumn getLogicDeleteColumn(Class<?> entityClass) {
        EntityColumn logicDeleteColumn = null;
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        boolean hasLogicDelete = false;
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                if (hasLogicDelete) {
                    throw new LogicDeleteException(entityClass.getCanonicalName() + " 中包含多个带有 @LogicDelete 注解의들，一个수업中只能存在一个带有 @LogicDelete 注解의들!");
                }
                hasLogicDelete = true;
                logicDeleteColumn = column;
            }
        }
        return logicDeleteColumn;
    }

    /**
     * 주석으로 설정된 기본 orderBy 가져 오기
     *
     * @param entityClass
     * @return
     */
    public static String orderByDefault(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        String orderByClause = EntityHelper.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append(" ORDER BY ");
            sql.append(orderByClause);
        }
        return sql.toString();
    }

    /**
     * 예제는 지정된 열 조회를 지원합니다.
     *
     * @return
     */
    public static String exampleSelectColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@hasSelectColumns(_parameter)\">");
        sql.append("<foreach collection=\"_parameter.selectColumns\" item=\"selectColumn\" separator=\",\">");
        sql.append("${selectColumn}");
        sql.append("</foreach>");
        sql.append("</when>");
        //지정된 열이 지원되지 않는 경우 모든 열 조회
        sql.append("<otherwise>");
        sql.append(getAllColumns(entityClass));
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * 예제는 지정된 열 조회를 지원합니다.
     *
     * @return
     */
    public static String exampleCountColumn(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<choose>");
        sql.append("<when test=\"@tk.mybatis.mapper.util.OGNL@hasCountColumn(_parameter)\">");
        sql.append("COUNT(<if test=\"distinct\">distinct </if>${countColumn})");
        sql.append("</when>");
        sql.append("<otherwise>");
        sql.append("COUNT(*)");
        sql.append("</otherwise>");
        sql.append("</choose>");
        return sql.toString();
    }

    /**
     * 예제 조회의 orderBy 조건은 기본 orderBy를 결정합니다.
     *
     * @return
     */
    public static String exampleOrderBy(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"orderByClause != null\">");
        sql.append("order by ${orderByClause}");
        sql.append("</if>");
        String orderByClause = EntityHelper.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append("<if test=\"orderByClause == null\">");
            sql.append("ORDER BY " + orderByClause);
            sql.append("</if>");
        }
        return sql.toString();
    }

    /**
     * 예제 조회의 orderBy 조건은 기본 orderBy를 결정합니다.
     *
     * @return
     */
    public static String exampleOrderBy(String entityName, Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"").append(entityName).append(".orderByClause != null\">");
        sql.append("order by ${").append(entityName).append(".orderByClause}");
        sql.append("</if>");
        String orderByClause = EntityHelper.getOrderByClause(entityClass);
        if (orderByClause.length() > 0) {
            sql.append("<if test=\"").append(entityName).append(".orderByClause == null\">");
            sql.append("ORDER BY " + orderByClause);
            sql.append("</if>");
        }
        return sql.toString();
    }

    /**
     * 업데이트 지원 예
     *
     * @return
     */
    public static String exampleForUpdate() {
        StringBuilder sql = new StringBuilder();
        sql.append("<if test=\"@tk.mybatis.mapper.util.OGNL@hasForUpdate(_parameter)\">");
        sql.append("FOR UPDATE");
        sql.append("</if>");
        return sql.toString();
    }

    /**
     * 업데이트 지원 예
     *
     * @return
     */
    public static String exampleCheck(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("<bind name=\"checkExampleEntityClass\" value=\"@tk.mybatis.mapper.util.OGNL@checkExampleEntityClass(_parameter, '");
        sql.append(entityClass.getCanonicalName());
        sql.append("')\"/>");
        return sql.toString();
    }

    /**
     * Example 조회의 where 구조는 Example 매개 변수가 하나만있을 때 사용됩니다.
     *
     * @return
     */
    public static String exampleWhereClause() {
        return "<if test=\"_parameter != null\">" +
                "<where>\n" +
                " <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "  <foreach collection=\"oredCriteria\" item=\"criteria\">\n" +
                "    <if test=\"criteria.valid\">\n" +
                "      ${@tk.mybatis.mapper.util.OGNL@andOr(criteria)}" +
                "      <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
                "          <choose>\n" +
                "            <when test=\"criterion.noValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.singleValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.betweenValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.listValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "              <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\n" +
                "                #{listItem}\n" +
                "              </foreach>\n" +
                "            </when>\n" +
                "          </choose>\n" +
                "        </foreach>\n" +
                "      </trim>\n" +
                "    </if>\n" +
                "  </foreach>\n" +
                " </trim>\n" +
                " ${@tk.mybatis.mapper.util.OGNL@andNotLogicDelete(_parameter)}" +
                "</where>" +
                "</if>";
    }

    /**
     * Example-Updatewhere 구조, 여러 매개 변수에 사용되는 경우@Param("example")댓글을 달 때
     *
     * @return
     */
    public static String updateByExampleWhereClause() {
        return "<where>\n" +
                " <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "  <foreach collection=\"example.oredCriteria\" item=\"criteria\">\n" +
                "    <if test=\"criteria.valid\">\n" +
                "      ${@tk.mybatis.mapper.util.OGNL@andOr(criteria)}" +
                "      <trim prefix=\"(\" prefixOverrides=\"and |or \" suffix=\")\">\n" +
                "        <foreach collection=\"criteria.criteria\" item=\"criterion\">\n" +
                "          <choose>\n" +
                "            <when test=\"criterion.noValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.singleValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.betweenValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\n" +
                "            </when>\n" +
                "            <when test=\"criterion.listValue\">\n" +
                "              ${@tk.mybatis.mapper.util.OGNL@andOr(criterion)} ${criterion.condition}\n" +
                "              <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\n" +
                "                #{listItem}\n" +
                "              </foreach>\n" +
                "            </when>\n" +
                "          </choose>\n" +
                "        </foreach>\n" +
                "      </trim>\n" +
                "    </if>\n" +
                "  </foreach>\n" +
                " </trim>\n" +
                " ${@tk.mybatis.mapper.util.OGNL@andNotLogicDelete(example)}" +
                "</where>";
    }

}
