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

package tk.mybatis.mapper.util;

import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.annotation.LogicDelete;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.IDynamicTableName;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.lang.reflect.Method;
import java.util.*;

/**
 * OGNL 정적 메서드
 *
 * @author liuzh
 */
public abstract class OGNL {
    public static final String SAFE_DELETE_ERROR = "만능인 Mapper 安全检查: 对조회条件参数进行检查时出错!";
    public static final String SAFE_DELETE_EXCEPTION = "만능인 Mapper 安全检查: 当前操作의方法没有指定조회条件，不允许수행该操作!";

    /**
     * 일반 예제의 entityClass가 현재 메소드와 일치하는지 확인
     *
     * @param parameter
     * @param entityFullName
     * @return
     */
    public static boolean checkExampleEntityClass(Object parameter, String entityFullName) {
        if (parameter != null && parameter instanceof Example && StringUtil.isNotEmpty(entityFullName)) {
            Example example = (Example) parameter;
            Class<?> entityClass = example.getEntityClass();
            if (!entityClass.getCanonicalName().equals(entityFullName)) {
                throw new MapperException("当前 Example 方法对应实体为:" + entityFullName
                        + ", 그러나 Example 매개 변수의 entityClass는:" + entityClass.getCanonicalName());
            }
        }
        return true;
    }

    /**
     * 매개 변수 개체에 지정된 필드가 모두 null인지 확인하고, 그렇다면 예외를 throw합니다.
     *
     * @param parameter
     * @param fields
     * @return
     */
    public static boolean notAllNullParameterCheck(Object parameter, String fields) {
        if (parameter != null) {
            try {
                Set<EntityColumn> columns = EntityHelper.getColumns(parameter.getClass());
                Set<String> fieldSet = new HashSet<String>(Arrays.asList(fields.split(",")));
                for (EntityColumn column : columns) {
                    if (fieldSet.contains(column.getProperty())) {
                        Object value = column.getEntityField().getValue(parameter);
                        if (value != null) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                throw new MapperException(SAFE_DELETE_ERROR, e);
            }
        }
        throw new MapperException(SAFE_DELETE_EXCEPTION);
    }

    /**
     * 유효성 검사 컬렉션 유형 매개 변수는 필수.
     *
     * @param parameter
     * @param error
     * @return
     */
    public static boolean notEmptyCollectionCheck(Object parameter, String error){
        if(parameter == null || (parameter instanceof Collection && ((Collection) parameter).size() == 0)){
            throw new IllegalArgumentException(error);
        }
        return true;
    }

    /**
     * 매개 변수 개체에 지정된 필드가 모두 null인지 확인하고, 그렇다면 예외를 throw합니다.
     *
     * @param parameter
     * @return
     */
    public static boolean exampleHasAtLeastOneCriteriaCheck(Object parameter) {
        if (parameter != null) {
            try {
                if (parameter instanceof Example) {
                    List<Example.Criteria> criteriaList = ((Example) parameter).getOredCriteria();
                    if (criteriaList != null && criteriaList.size() > 0) {
                        return true;
                    }
                } else {
                    Method getter = parameter.getClass().getDeclaredMethod("getOredCriteria");
                    Object list = getter.invoke(parameter);
                    if(list != null && list instanceof List && ((List) list).size() > 0){
                        return true;
                    }
                }
            } catch (Exception e) {
                throw new MapperException(SAFE_DELETE_ERROR, e);
            }
        }
        throw new MapperException(SAFE_DELETE_EXCEPTION);
    }

    /**
     * 사용자 지정 조회 열 포함 여부
     *
     * @param parameter
     * @return
     */
    public static boolean hasSelectColumns(Object parameter) {
        if (parameter != null && parameter instanceof Example) {
            Example example = (Example) parameter;
            if (example.getSelectColumns() != null && example.getSelectColumns().size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 사용자 정의 개수 열 포함 여부
     *
     * @param parameter
     * @return
     */
    public static boolean hasCountColumn(Object parameter) {
        if (parameter != null && parameter instanceof Example) {
            Example example = (Example) parameter;
            return StringUtil.isNotEmpty(example.getCountColumn());
        }
        return false;
    }

    /**
     * forUpdate 포함 여부
     *
     * @param parameter
     * @return
     */
    public static boolean hasForUpdate(Object parameter) {
        if (parameter != null && parameter instanceof Example) {
            Example example = (Example) parameter;
            return example.isForUpdate();
        }
        return false;
    }

    /**
     * 사용자 지정 조회 열을 포함하지 않습니다.
     *
     * @param parameter
     * @return
     */
    public static boolean hasNoSelectColumns(Object parameter) {
        return !hasSelectColumns(parameter);
    }

    /**
     * 매개 변수가 동적 테이블 이름을 지원하는지 여부 확인
     *
     * @param parameter
     * @return 진정한 지원, 거짓 지원 안 함
     */
    public static boolean isDynamicParameter(Object parameter) {
        if (parameter != null && parameter instanceof IDynamicTableName) {
            return true;
        }
        return false;
    }

    /**
     * 매개 변수 b가 동적 테이블 이름을 지원하는지 확인
     *
     * @param parameter
     * @return true는 지원하지 않음, false는 지원함
     */
    public static boolean isNotDynamicParameter(Object parameter) {
        return !isDynamicParameter(parameter);
    }

    /**
     * 조건이 및 또는인지 확인 or
     *
     * @param parameter
     * @return
     */
    public static String andOr(Object parameter) {
        if (parameter instanceof Example.Criteria) {
            return ((Example.Criteria) parameter).getAndOr();
        } else if (parameter instanceof Example.Criterion) {
            return ((Example.Criterion) parameter).getAndOr();
        } else if (parameter.getClass().getCanonicalName().endsWith("Criteria")) {
            return "or";
        } else {
            return "and";
        }
    }

    /**
     * 논리적으로 삭제 된 필드를 연결하기위한 삭제되지 않은 조회 조건
     *
     * @param parameter
     * @return
     */
    public static String andNotLogicDelete(Object parameter) {
        String result = "";
        if (parameter instanceof Example) {
            Example example = (Example) parameter;
            Map<String, EntityColumn> propertyMap = example.getPropertyMap();

            for (Map.Entry<String, EntityColumn> entry: propertyMap.entrySet()) {
                EntityColumn column = entry.getValue();
                if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                    // 삭제 표시되지 않는 조건
                    result = "and " + column.getColumn() + " = " + SqlHelper.getLogicDeletedValue(column, false);
                }
            }
        }
        return result;
    }

}
