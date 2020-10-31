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

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.util.MetaObjectUtil;
import tk.mybatis.mapper.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static tk.mybatis.mapper.util.MsUtil.getMapperClass;
import static tk.mybatis.mapper.util.MsUtil.getMethodName;

/**
 * Generic Mapper를 확장 할 때 상속해야하는 Generic Mapper 템플릿 클래스
 *
 * @author liuzh
 */
public abstract class MapperTemplate {
    private static final XMLLanguageDriver     languageDriver = new XMLLanguageDriver();
    protected            Map<String, Method>   methodMap      = new ConcurrentHashMap<String, Method>();
    protected            Map<String, Class<?>> entityClassMap = new ConcurrentHashMap<String, Class<?>>();
    protected            Class<?>              mapperClass;
    protected            MapperHelper          mapperHelper;

    public MapperTemplate(Class<?> mapperClass, MapperHelper mapperHelper) {
        this.mapperClass = mapperClass;
        this.mapperHelper = mapperHelper;
    }

    /**
     * 이 메서드는 ProviderSqlSource를 초기화하는 데만 사용됩니다.
     *
     * @param record
     * @return
     */
    public String dynamicSQL(Object record) {
        return "dynamicSQL";
    }

    /**
     * 매핑 방법 추가
     *
     * @param methodName
     * @param method
     */
    public void addMethodMap(String methodName, Method method) {
        methodMap.put(methodName, method);
    }

    /**
     * IDENTITY 값을 얻기위한 표현식
     *
     * @param column
     * @return
     */
    public String getIDENTITY(EntityColumn column) {
        return MessageFormat.format(mapperHelper.getConfig().getIDENTITY(), column.getColumn(), column.getProperty(), column.getTable().getName());
    }

    /**
     * 일반적인 방법 지원 여부
     *
     * @param msId
     * @return
     */
    public boolean supportMethod(String msId) {
        Class<?> mapperClass = getMapperClass(msId);
        if (mapperClass != null && this.mapperClass.isAssignableFrom(mapperClass)) {
            String methodName = getMethodName(msId);
            return methodMap.get(methodName) != null;
        }
        return false;
    }

    /**
     * 반환 값 유형 설정-선택시 typeHandler를 적용하려면 대신 resultMap을 설정하십시오.
     *
     * @param ms
     * @param entityClass
     */
    protected void setResultType(MappedStatement ms, Class<?> entityClass) {
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        List<ResultMap> resultMaps = new ArrayList<ResultMap>();
        resultMaps.add(entityTable.getResultMap(ms.getConfiguration()));
        MetaObject metaObject = MetaObjectUtil.forObject(ms);
        metaObject.setValue("resultMaps", Collections.unmodifiableList(resultMaps));
    }

    /**
     * 초기화SqlSource
     *
     * @param ms
     * @param sqlSource
     */
    protected void setSqlSource(MappedStatement ms, SqlSource sqlSource) {
        MetaObject msObject = MetaObjectUtil.forObject(ms);
        msObject.setValue("sqlSource", sqlSource);
    }

    /**
     * xmlSql을 통해 sqlSource 만들기
     *
     * @param ms
     * @param xmlSql
     * @return
     */
    public SqlSource createSqlSource(MappedStatement ms, String xmlSql) {
        return languageDriver.createSqlSource(ms.getConfiguration(), "<script>\n\t" + xmlSql + "</script>", null);
    }

    /**
     * 반환 값 유형-엔티티 유형 가져 오기
     *
     * @param ms
     * @return
     */
    public Class<?> getEntityClass(MappedStatement ms) {
        String msId = ms.getId();
        if (entityClassMap.containsKey(msId)) {
            return entityClassMap.get(msId);
        } else {
            Class<?> mapperClass = getMapperClass(msId);
            Type[] types = mapperClass.getGenericInterfaces();
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    ParameterizedType t = (ParameterizedType) type;
                    if (t.getRawType() == this.mapperClass || this.mapperClass.isAssignableFrom((Class<?>) t.getRawType())) {
                        Class<?> returnType = (Class<?>) t.getActualTypeArguments()[0];
                        //타입 획득 후 처음으로 타입 초기화
                        EntityHelper.initEntityNameMap(returnType, mapperHelper.getConfig());
                        entityClassMap.put(msId, returnType);
                        return returnType;
                    }
                }
            }
        }
        throw new MapperException("없는 " + msId + " 방법에 대한 일반 정보!");
    }

    /**
     * 엔티티 클래스의 테이블 이름 가져 오기
     *
     * @param entityClass
     * @return
     */
    protected String tableName(Class<?> entityClass) {
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        String prefix = entityTable.getPrefix();
        if (StringUtil.isEmpty(prefix)) {
            //전역 구성 사용
            prefix = mapperHelper.getConfig().getPrefix();
        }
        if (StringUtil.isNotEmpty(prefix)) {
            return prefix + "." + entityTable.getName();
        }
        return entityTable.getName();
    }

    public Config getConfig() {
        return mapperHelper.getConfig();
    }

    public String getIDENTITY() {
        return getConfig().getIDENTITY();
    }

    public boolean isBEFORE() {
        return getConfig().isBEFORE();
    }

    public boolean isCheckExampleEntityClass() {
        return getConfig().isCheckExampleEntityClass();
    }

    public boolean isNotEmpty() {
        return getConfig().isNotEmpty();
    }

    /**
     * 초기화SqlSource
     *
     * @param ms
     * @throws java.lang.reflect.InvocationTargetException
     * @throws IllegalAccessException
     */
    public void setSqlSource(MappedStatement ms) throws Exception {
        if (this.mapperClass == getMapperClass(ms.getId())) {
            throw new MapperException("请不要配置或扫描만능인Mapper상호 작용수업：" + this.mapperClass);
        }
        Method method = methodMap.get(getMethodName(ms));
        try {
            //첫 번째 유형은 값을 반환하지 않고 ms를 직접 조작하는 것입니다.
            if (method.getReturnType() == Void.TYPE) {
                method.invoke(this, ms);
            }
            //두 번째, SqlNode로 돌아 가기
            else if (SqlNode.class.isAssignableFrom(method.getReturnType())) {
                SqlNode sqlNode = (SqlNode) method.invoke(this, ms);
                DynamicSqlSource dynamicSqlSource = new DynamicSqlSource(ms.getConfiguration(), sqlNode);
                setSqlSource(ms, dynamicSqlSource);
            }
            //세 번째는 xml 형식으로 SQL 문자열을 반환하는 것입니다.
            else if (String.class.equals(method.getReturnType())) {
                String xmlSql = (String) method.invoke(this, ms);
                SqlSource sqlSource = createSqlSource(ms, xmlSql);
                //원래 SqlSource 바꾸기
                setSqlSource(ms, sqlSource);
            } else {
                throw new MapperException("自定义Mapper方法返回수업型错误,可选의返回수업型为void,SqlNode,String三种!");
            }
        } catch (IllegalAccessException e) {
            throw new MapperException(e);
        } catch (InvocationTargetException e) {
            throw new MapperException(e.getTargetException() != null ? e.getTargetException() : e);
        }
    }

}
