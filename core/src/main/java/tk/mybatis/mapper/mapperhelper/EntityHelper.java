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
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.resolve.DefaultEntityResolve;
import tk.mybatis.mapper.mapperhelper.resolve.EntityResolve;
import tk.mybatis.mapper.util.MetaObjectUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 엔티티 클래스 도구 클래스-주요 엔티티 및 데이터베이스 테이블 및 필드를 처리하는 클래스
 * <p/>
 * <p> 프로젝트 주소 : <a href="https://github.com/abel533/Mapper" target="_blank">https://github.com/abel533/Mapper</a></p>
 *
 * @author liuzh
 */
public class EntityHelper {

    /**
     * 엔티티 클래스 => 테이블 객체
     */
    private static final Map<Class<?>, EntityTable> entityTableMap = new ConcurrentHashMap<Class<?>, EntityTable>();

    private static final EntityResolve DEFAULT = new DefaultEntityResolve();

    /**
     * 엔티티 클래스 파서
     */
    private static EntityResolve resolve = DEFAULT;

    /**
     * 테이블 개체 가져 오기
     *
     * @param entityClass
     * @return
     */
    public static EntityTable getEntityTable(Class<?> entityClass) {
        EntityTable entityTable = entityTableMap.get(entityClass);
        if (entityTable == null) {
            throw new MapperException("엔티티 클래스를 가져올 수 없습니다." + entityClass.getCanonicalName() + "해당 테이블 이름!");
        }
        return entityTable;
    }

    /**
     * 기본 orderby 문 가져 오기
     *
     * @param entityClass
     * @return
     */
    public static String getOrderByClause(Class<?> entityClass) {
        EntityTable table = getEntityTable(entityClass);
        if (table.getOrderByClause() != null) {
            return table.getOrderByClause();
        }

        List<EntityColumn> orderEntityColumns = new ArrayList<EntityColumn>();
        for (EntityColumn column : table.getEntityClassColumns()) {
            if (column.getOrderBy() != null) {
                orderEntityColumns.add(column);
            }
        }

        Collections.sort(orderEntityColumns, new Comparator<EntityColumn>() {
            @Override
            public int compare(EntityColumn o1, EntityColumn o2) {
                return o1.getOrderPriority() - o2.getOrderPriority();
            }
        });

        StringBuilder orderBy = new StringBuilder();
        for (EntityColumn column : orderEntityColumns) {
            if (orderBy.length() != 0) {
                orderBy.append(",");
            }
            orderBy.append(column.getColumn()).append(" ").append(column.getOrderBy());
        }
        table.setOrderByClause(orderBy.toString());
        return table.getOrderByClause();
    }

    /**
     * 모든 열 가져 오기
     *
     * @param entityClass
     * @return
     */
    public static Set<EntityColumn> getColumns(Class<?> entityClass) {
        return getEntityTable(entityClass).getEntityClassColumns();
    }

    /**
     * 기본 키 정보 얻기
     *
     * @param entityClass
     * @return
     */
    public static Set<EntityColumn> getPKColumns(Class<?> entityClass) {
        return getEntityTable(entityClass).getEntityClassPKColumns();
    }

    /**
     * 조회 선택 가져 오기
     *
     * @param entityClass
     * @return
     */
    public static String getSelectColumns(Class<?> entityClass) {
        EntityTable entityTable = getEntityTable(entityClass);
        if (entityTable.getBaseSelect() != null) {
            return entityTable.getBaseSelect();
        }
        Set<EntityColumn> columnList = getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();
        boolean skipAlias = Map.class.isAssignableFrom(entityClass);
        for (EntityColumn entityColumn : columnList) {
            selectBuilder.append(entityColumn.getColumn());
            if (!skipAlias && !entityColumn.getColumn().equalsIgnoreCase(entityColumn.getProperty())) {
                //'DESC'와 같이 기다리지 않는 몇 가지 상황이 있습니다.
                if (entityColumn.getColumn().substring(1, entityColumn.getColumn().length() - 1).equalsIgnoreCase(entityColumn.getProperty())) {
                    selectBuilder.append(",");
                } else {
                    selectBuilder.append(" AS ").append(entityColumn.getProperty()).append(",");
                }
            } else {
                selectBuilder.append(",");
            }
        }
        entityTable.setBaseSelect(selectBuilder.substring(0, selectBuilder.length() - 1));
        return entityTable.getBaseSelect();
    }

    /**
     * 엔터티 속성 초기화
     *
     * @param entityClass
     * @param config
     */
    public static synchronized void initEntityNameMap(Class<?> entityClass, Config config) {
        if (entityTableMap.get(entityClass) != null) {
            return;
        }
        //EntityTable 생성 및 캐시
        EntityTable entityTable = resolve.resolveEntity(entityClass, config);
        entityTableMap.put(entityClass, entityTable);
    }

    /**
     * 엔티티 클래스 파서 설정
     *
     * @param resolve
     */
    static void setResolve(EntityResolve resolve) {
        EntityHelper.resolve = resolve;
    }

    /**
     * 리플렉션을 통해 MappedStatement의 keyProperties 필드 값 설정
     *
     * @param pkColumns 모든 기본 키 필드
     * @param ms        MappedStatement
     */
    public static void setKeyProperties(Set<EntityColumn> pkColumns, MappedStatement ms) {
        if (pkColumns == null || pkColumns.isEmpty()) {
            return;
        }

        List<String> keyProperties = new ArrayList<String>(pkColumns.size());
        for (EntityColumn column : pkColumns) {
            keyProperties.add(column.getProperty());
        }

        MetaObjectUtil.forObject(ms).setValue("keyProperties", keyProperties.toArray(new String[]{}));
    }
}