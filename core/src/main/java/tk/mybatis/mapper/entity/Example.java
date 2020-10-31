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

package tk.mybatis.mapper.entity;

import org.apache.ibatis.reflection.MetaObject;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.util.MetaObjectUtil;
import tk.mybatis.mapper.util.Sqls;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;

/**
 * 일반 예제 조회 개체
 *
 * @author liuzh
 */
public class Example implements IDynamicTableName {
    protected String orderByClause;

    protected boolean distinct;

    protected boolean exists;

    protected boolean notNull;

    protected boolean forUpdate;

    //조회 필드
    protected Set<String> selectColumns;

    //제외 된 조회 필드
    protected Set<String> excludeColumns;

    protected String countColumn;

    protected List<Criteria> oredCriteria;

    protected Class<?> entityClass;

    protected EntityTable               table;
    //속성 및 열 대응
    protected Map<String, EntityColumn> propertyMap;
    //동적 테이블 이름
    protected String                    tableName;

    protected OrderBy ORDERBY;

    /**
     * 기본값은 true입니다.
     *
     * @param entityClass
     */
    public Example(Class<?> entityClass) {
        this(entityClass, true);
    }

    /**
     * exist 매개 변수가있는 생성자, 기본 notNull은 false이며 비어있을 수 있습니다.
     *
     * @param entityClass
     * @param exists      - true필드가 존재하지 않는 경우 예외 발생, false 인 경우 필드가 존재하지 않는 경우 필드를 사용하지 않는 조건
     */
    public Example(Class<?> entityClass, boolean exists) {
        this(entityClass, exists, false);
    }

    /**
     * 존재 매개 변수로 메소드 생성
     *
     * @param entityClass
     * @param exists      - true필드가 존재하지 않는 경우 예외 발생, false 인 경우 필드가 존재하지 않는 경우 필드를 사용하지 않는 조건
     * @param notNull     - true값이 비어 있으면 예외가 발생하고, false이면 비어 있으면 필드 조건이 사용되지 않습니다.
     */
    public Example(Class<?> entityClass, boolean exists, boolean notNull) {
        this.exists = exists;
        this.notNull = notNull;
        oredCriteria = new ArrayList<Criteria>();
        this.entityClass = entityClass;
        table = EntityHelper.getEntityTable(entityClass);
        //Li Lingbei의 # 159 수정 제안에 따르면
        propertyMap = table.getPropertyMap();
        this.ORDERBY = new OrderBy(this, propertyMap);
    }


    private Example(Builder builder) {
        this.exists = builder.exists;
        this.notNull = builder.notNull;
        this.distinct = builder.distinct;
        this.entityClass = builder.entityClass;
        this.propertyMap = builder.propertyMap;
        this.selectColumns = builder.selectColumns;
        this.excludeColumns = builder.excludeColumns;
        this.oredCriteria = builder.exampleCriterias;
        this.forUpdate = builder.forUpdate;
        this.tableName = builder.tableName;

        if (!StringUtil.isEmpty(builder.orderByClause.toString())) {
            this.orderByClause = builder.orderByClause.toString();
        }
    }

    public static Builder builder(Class<?> entityClass) {
        return new Builder(entityClass);
    }

    public OrderBy orderBy(String property) {
        this.ORDERBY.orderBy(property);
        return this.ORDERBY;
    }

    /**
     * 조회 필드 제외, 우선 순위가 selectProperties보다 낮음
     *
     * @param properties 속성 이름의 가변 매개 변수
     * @return
     */
    public Example excludeProperties(String... properties) {
        if (properties != null && properties.length > 0) {
            if (this.excludeColumns == null) {
                this.excludeColumns = new LinkedHashSet<String>();
            }
            for (String property : properties) {
                if (propertyMap.containsKey(property)) {
                    this.excludeColumns.add(propertyMap.get(property).getColumn());
                } else {
                    throw new MapperException("수업 " + entityClass.getSimpleName() + " 속성을 포함하지 않음 \'" + property + "\'，또는 속성은 @Transient!");
                }
            }
        }
        return this;
    }

    /**
     * 조회 할 속성 열을 지정합니다. 여기서는 테이블 필드에 자동으로 매핑됩니다.
     *
     * @param properties
     * @return
     */
    public Example selectProperties(String... properties) {
        if (properties != null && properties.length > 0) {
            if (this.selectColumns == null) {
                this.selectColumns = new LinkedHashSet<String>();
            }
            for (String property : properties) {
                if (propertyMap.containsKey(property)) {
                    this.selectColumns.add(propertyMap.get(property).getColumn());
                } else {
                    throw new MapperException("수업 " + entityClass.getSimpleName() + " 속성을 포함하지 않음 \'" + property + "\'，또는 속성은 @Transient!");
                }
            }
        }
        return this;
    }

    public void or(Criteria criteria) {
        criteria.setAndOr("or");
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        criteria.setAndOr("or");
        oredCriteria.add(criteria);
        return criteria;
    }

    public void and(Criteria criteria) {
        criteria.setAndOr("and");
        oredCriteria.add(criteria);
    }

    public Criteria and() {
        Criteria criteria = createCriteriaInternal();
        criteria.setAndOr("and");
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            criteria.setAndOr("and");
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria(propertyMap, exists, notNull);
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    public Map<String, EntityColumn> getPropertyMap() {
        return propertyMap;
    }

    public static class OrderBy {
        //속성 및 열 대응
        protected Map<String, EntityColumn> propertyMap;
        private   Example                   example;
        private   Boolean                   isProperty;

        public OrderBy(Example example, Map<String, EntityColumn> propertyMap) {
            this.example = example;
            this.propertyMap = propertyMap;
        }

        private String property(String property) {
            if (StringUtil.isEmpty(property) || StringUtil.isEmpty(property.trim())) {
                throw new MapperException("받은 속성이 비어 있습니다!");
            }
            property = property.trim();
            if (!propertyMap.containsKey(property)) {
                throw new MapperException("현재 엔티티 클래스에 이름이 없습니다." + property + "속성!");
            }
            return propertyMap.get(property).getColumn();
        }

        public OrderBy orderBy(String property) {
            String column = property(property);
            if (column == null) {
                isProperty = false;
                return this;
            }
            if (StringUtil.isNotEmpty(example.getOrderByClause())) {
                example.setOrderByClause(example.getOrderByClause() + "," + column);
            } else {
                example.setOrderByClause(column);
            }
            isProperty = true;
            return this;
        }

        public OrderBy desc() {
            if (isProperty) {
                example.setOrderByClause(example.getOrderByClause() + " DESC");
                isProperty = false;
            }
            return this;
        }

        public OrderBy asc() {
            if (isProperty) {
                example.setOrderByClause(example.getOrderByClause() + " ASC");
                isProperty = false;
            }
            return this;
        }
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion>           criteria;
        //필드가 있어야하는지 여부
        protected boolean                   exists;
        //값을 비워 둘 수 없는지 여부
        protected boolean                   notNull;
        //연결 조건
        protected String                    andOr;
        //속성 및 열 대응
        protected Map<String, EntityColumn> propertyMap;

        protected GeneratedCriteria(Map<String, EntityColumn> propertyMap, boolean exists, boolean notNull) {
            super();
            this.exists = exists;
            this.notNull = notNull;
            criteria = new ArrayList<Criterion>();
            this.propertyMap = propertyMap;
        }

        private String column(String property) {
            if (propertyMap.containsKey(property)) {
                return propertyMap.get(property).getColumn();
            } else if (exists) {
                throw new MapperException("현재 엔티티 클래스에 이름이 없습니다." + property + "속성!");
            } else {
                return null;
            }
        }

        private String property(String property) {
            if (propertyMap.containsKey(property)) {
                return property;
            } else if (exists) {
                throw new MapperException("현재 엔티티 클래스에 이름이 없습니다." + property + "속성!");
            } else {
                return null;
            }
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new MapperException("Value for condition cannot be null");
            }
            if (condition.startsWith("null")) {
                return;
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                if (notNull) {
                    throw new MapperException("Value for " + property + " cannot be null");
                } else {
                    return;
                }
            }
            if (property == null) {
                return;
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                if (notNull) {
                    throw new MapperException("Between values for " + property + " cannot be null");
                } else {
                    return;
                }
            }
            if (property == null) {
                return;
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        protected void addOrCriterion(String condition) {
            if (condition == null) {
                throw new MapperException("Value for condition cannot be null");
            }
            if (condition.startsWith("null")) {
                return;
            }
            criteria.add(new Criterion(condition, true));
        }

        protected void addOrCriterion(String condition, Object value, String property) {
            if (value == null) {
                if (notNull) {
                    throw new MapperException("Value for " + property + " cannot be null");
                } else {
                    return;
                }
            }
            if (property == null) {
                return;
            }
            criteria.add(new Criterion(condition, value, true));
        }

        protected void addOrCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                if (notNull) {
                    throw new MapperException("Between values for " + property + " cannot be null");
                } else {
                    return;
                }
            }
            if (property == null) {
                return;
            }
            criteria.add(new Criterion(condition, value1, value2, true));
        }

        public Criteria andIsNull(String property) {
            addCriterion(column(property) + " is null");
            return (Criteria) this;
        }

        public Criteria andIsNotNull(String property) {
            addCriterion(column(property) + " is not null");
            return (Criteria) this;
        }

        public Criteria andEqualTo(String property, Object value) {
            addCriterion(column(property) + " =", value, property(property));
            return (Criteria) this;
        }

        public Criteria andNotEqualTo(String property, Object value) {
            addCriterion(column(property) + " <>", value, property(property));
            return (Criteria) this;
        }

        public Criteria andGreaterThan(String property, Object value) {
            addCriterion(column(property) + " >", value, property(property));
            return (Criteria) this;
        }

        public Criteria andGreaterThanOrEqualTo(String property, Object value) {
            addCriterion(column(property) + " >=", value, property(property));
            return (Criteria) this;
        }

        public Criteria andLessThan(String property, Object value) {
            addCriterion(column(property) + " <", value, property(property));
            return (Criteria) this;
        }

        public Criteria andLessThanOrEqualTo(String property, Object value) {
            addCriterion(column(property) + " <=", value, property(property));
            return (Criteria) this;
        }

        public Criteria andIn(String property, Iterable values) {
            addCriterion(column(property) + " in", values, property(property));
            return (Criteria) this;
        }

        public Criteria andNotIn(String property, Iterable values) {
            addCriterion(column(property) + " not in", values, property(property));
            return (Criteria) this;
        }

        public Criteria andBetween(String property, Object value1, Object value2) {
            addCriterion(column(property) + " between", value1, value2, property(property));
            return (Criteria) this;
        }

        public Criteria andNotBetween(String property, Object value1, Object value2) {
            addCriterion(column(property) + " not between", value1, value2, property(property));
            return (Criteria) this;
        }

        public Criteria andLike(String property, String value) {
            addCriterion(column(property) + "  like", value, property(property));
            return (Criteria) this;
        }

        public Criteria andNotLike(String property, String value) {
            addCriterion(column(property) + "  not like", value, property(property));
            return (Criteria) this;
        }

        /**
         * 필기 조건
         *
         * @param condition 예 : "length(countryname)<5"
         * @return
         */
        public Criteria andCondition(String condition) {
            addCriterion(condition);
            return (Criteria) this;
        }

        /**
         * 왼쪽 조건을 손으로 쓰고 오른쪽 값을 사용합니다.
         *
         * @param condition 예 : "length(countryname)="
         * @param value     예 : 5
         * @return
         */
        public Criteria andCondition(String condition, Object value) {
            criteria.add(new Criterion(condition, value));
            return (Criteria) this;
        }

        /**
         * 이 개체의 비어 있지 않은 필드 매개 변수를 동일한 조회 조건으로 사용하십시오.
         *
         * @param param 매개 변수 개체
         * @author Bob {@link}0haizhu0@gmail.com
         * @날짜 2015 년 7 월 17 일 오후 12시:48:08
         */
        public Criteria andEqualTo(Object param) {
            if(param == null){
                return (Criteria) this;
            }
            MetaObject metaObject = MetaObjectUtil.forObject(param);
            String[] properties = metaObject.getGetterNames();
            for (String property : properties) {
                //속성 및 열은 맵에서이 속성에 해당합니다.
                if (propertyMap.get(property) != null) {
                    Object value = metaObject.getValue(property);
                    //속성 값이 비어 있지 않습니다.
                    if (value != null) {
                        andEqualTo(property, value);
                    }
                }
            }
            return (Criteria) this;
        }

        /**
         * 이 개체의 모든 필드 매개 변수를 동일한 조회 조건으로 취급합니다. 필드가 null이면 null입니다.
         *
         * @param param 매개 변수 개체
         */
        public Criteria andAllEqualTo(Object param) {
            MetaObject metaObject = MetaObjectUtil.forObject(param);
            String[] properties = metaObject.getGetterNames();
            for (String property : properties) {
                //속성 및 열은 맵에서이 속성에 해당합니다.
                if (propertyMap.get(property) != null) {
                    Object value = metaObject.getValue(property);
                    //속성 값이 비어 있지 않습니다.
                    if (value != null) {
                        andEqualTo(property, value);
                    } else {
                        andIsNull(property);
                    }
                }
            }
            return (Criteria) this;
        }

        public Criteria orIsNull(String property) {
            addOrCriterion(column(property) + " is null");
            return (Criteria) this;
        }

        public Criteria orIsNotNull(String property) {
            addOrCriterion(column(property) + " is not null");
            return (Criteria) this;
        }

        public Criteria orEqualTo(String property, Object value) {
            addOrCriterion(column(property) + " =", value, property(property));
            return (Criteria) this;
        }

        public Criteria orNotEqualTo(String property, Object value) {
            addOrCriterion(column(property) + " <>", value, property(property));
            return (Criteria) this;
        }

        public Criteria orGreaterThan(String property, Object value) {
            addOrCriterion(column(property) + " >", value, property(property));
            return (Criteria) this;
        }

        public Criteria orGreaterThanOrEqualTo(String property, Object value) {
            addOrCriterion(column(property) + " >=", value, property(property));
            return (Criteria) this;
        }

        public Criteria orLessThan(String property, Object value) {
            addOrCriterion(column(property) + " <", value, property(property));
            return (Criteria) this;
        }

        public Criteria orLessThanOrEqualTo(String property, Object value) {
            addOrCriterion(column(property) + " <=", value, property(property));
            return (Criteria) this;
        }

        public Criteria orIn(String property, Iterable values) {
            addOrCriterion(column(property) + " in", values, property(property));
            return (Criteria) this;
        }

        public Criteria orNotIn(String property, Iterable values) {
            addOrCriterion(column(property) + " not in", values, property(property));
            return (Criteria) this;
        }

        public Criteria orBetween(String property, Object value1, Object value2) {
            addOrCriterion(column(property) + " between", value1, value2, property(property));
            return (Criteria) this;
        }

        public Criteria orNotBetween(String property, Object value1, Object value2) {
            addOrCriterion(column(property) + " not between", value1, value2, property(property));
            return (Criteria) this;
        }

        public Criteria orLike(String property, String value) {
            addOrCriterion(column(property) + "  like", value, property(property));
            return (Criteria) this;
        }

        public Criteria orNotLike(String property, String value) {
            addOrCriterion(column(property) + "  not like", value, property(property));
            return (Criteria) this;
        }

        /**
         * 필기 조건
         *
         * @param condition 예 : "length(countryname)<5"
         * @return
         */
        public Criteria orCondition(String condition) {
            addOrCriterion(condition);
            return (Criteria) this;
        }

        /**
         * 왼쪽 조건을 손으로 쓰고 오른쪽 값을 사용합니다.
         *
         * @param condition 예 : "length(countryname)="
         * @param value     예 : 5
         * @return
         */
        public Criteria orCondition(String condition, Object value) {
            criteria.add(new Criterion(condition, value, true));
            return (Criteria) this;
        }

        /**
         * 이 개체의 비어 있지 않은 필드 매개 변수를 동일한 조회 조건으로 사용하십시오.
         *
         * @param param 매개 변수 개체
         * @author Bob {@link}0haizhu0@gmail.com
         * @날짜 2015 년 7 월 17 일 오후 12시:48:08
         */
        public Criteria orEqualTo(Object param) {
            MetaObject metaObject = MetaObjectUtil.forObject(param);
            String[] properties = metaObject.getGetterNames();
            for (String property : properties) {
                //속성 및 열은 맵에서이 속성에 해당합니다.
                if (propertyMap.get(property) != null) {
                    Object value = metaObject.getValue(property);
                    //속성 값이 비어 있지 않습니다.
                    if (value != null) {
                        orEqualTo(property, value);
                    }
                }
            }
            return (Criteria) this;
        }

        /**
         * 이 개체의 모든 필드 매개 변수를 동일한 조회 조건으로 취급합니다. 필드가 null이면 null입니다.
         *
         * @param param 매개 변수 개체
         */
        public Criteria orAllEqualTo(Object param) {
            MetaObject metaObject = MetaObjectUtil.forObject(param);
            String[] properties = metaObject.getGetterNames();
            for (String property : properties) {
                //속성 및 열은 맵에서이 속성에 해당합니다.
                if (propertyMap.get(property) != null) {
                    Object value = metaObject.getValue(property);
                    //속성 값이 비어 있지 않습니다.
                    if (value != null) {
                        orEqualTo(property, value);
                    } else {
                        orIsNull(property);
                    }
                }
            }
            return (Criteria) this;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public String getAndOr() {
            return andOr;
        }

        public void setAndOr(String andOr) {
            this.andOr = andOr;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria(Map<String, EntityColumn> propertyMap, boolean exists, boolean notNull) {
            super(propertyMap, exists, notNull);
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private String andOr;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        protected Criterion(String condition) {
            this(condition, false);
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            this(condition, value, typeHandler, false);
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null, false);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            this(condition, value, secondValue, typeHandler, false);
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null, false);
        }

        protected Criterion(String condition, boolean isOr) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
            this.andOr = isOr ? "or" : "and";
        }

        protected Criterion(String condition, Object value, String typeHandler, boolean isOr) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            this.andOr = isOr ? "or" : "and";
            if (value instanceof Collection<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value, boolean isOr) {
            this(condition, value, null, isOr);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler, boolean isOr) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
            this.andOr = isOr ? "or" : "and";
        }

        protected Criterion(String condition, Object value, Object secondValue, boolean isOr) {
            this(condition, value, secondValue, null, isOr);
        }

        public String getAndOr() {
            return andOr;
        }

        public void setAndOr(String andOr) {
            this.andOr = andOr;
        }

        public String getCondition() {
            return condition;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        public Object getValue() {
            return value;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }
    }

    public static class Builder {
        private final Class<?>                  entityClass;
        protected     EntityTable               table;
        //속성 및 열 대응
        protected     Map<String, EntityColumn> propertyMap;
        private       StringBuilder             orderByClause;
        private       boolean                   distinct;
        private       boolean                   exists;
        private       boolean                   notNull;
        private       boolean                   forUpdate;
        //조회 필드
        private       Set<String>               selectColumns;
        //제외 된 조회 필드
        private       Set<String>               excludeColumns;
        private       String                    countColumn;
        private       List<Sqls.Criteria>       sqlsCriteria;
        //동적 테이블 이름
        private       List<Example.Criteria>    exampleCriterias;
        //동적 테이블 이름
        private       String                    tableName;

        public Builder(Class<?> entityClass) {
            this(entityClass, true);
        }

        public Builder(Class<?> entityClass, boolean exists) {
            this(entityClass, exists, false);
        }

        public Builder(Class<?> entityClass, boolean exists, boolean notNull) {
            this.entityClass = entityClass;
            this.exists = exists;
            this.notNull = notNull;
            this.orderByClause = new StringBuilder();
            this.table = EntityHelper.getEntityTable(entityClass);
            this.propertyMap = table.getPropertyMap();
            this.sqlsCriteria = new ArrayList<Sqls.Criteria>(2);
        }

        public Builder distinct() {
            return setDistinct(true);
        }

        public Builder forUpdate() {
            return setForUpdate(true);
        }

        public Builder selectDistinct(String... properties) {
            select(properties);
            this.distinct = true;
            return this;
        }

        public Builder select(String... properties) {
            if (properties != null && properties.length > 0) {
                if (this.selectColumns == null) {
                    this.selectColumns = new LinkedHashSet<String>();
                }
                for (String property : properties) {
                    if (this.propertyMap.containsKey(property)) {
                        this.selectColumns.add(propertyMap.get(property).getColumn());
                    } else {
                        throw new MapperException("현재 엔티티 클래스에 이름이 없습니다." + property + "속성!");
                    }
                }
            }
            return this;
        }

        public Builder notSelect(String... properties) {
            if (properties != null && properties.length > 0) {
                if (this.excludeColumns == null) {
                    this.excludeColumns = new LinkedHashSet<String>();
                }
                for (String property : properties) {
                    if (propertyMap.containsKey(property)) {
                        this.excludeColumns.add(propertyMap.get(property).getColumn());
                    } else {
                        throw new MapperException("현재 엔티티 클래스에 이름이 없습니다." + property + "속성!");
                    }
                }
            }
            return this;
        }

        public Builder from(String tableName) {
            return setTableName(tableName);
        }

        public Builder where(Sqls sqls) {
            Sqls.Criteria criteria = sqls.getCriteria();
            criteria.setAndOr("and");
            this.sqlsCriteria.add(criteria);
            return this;
        }

        public Builder where(SqlsCriteria sqls) {
            Sqls.Criteria criteria = sqls.getCriteria();
            criteria.setAndOr("and");
            this.sqlsCriteria.add(criteria);
            return this;
        }

        public Builder andWhere(Sqls sqls) {
            Sqls.Criteria criteria = sqls.getCriteria();
            criteria.setAndOr("and");
            this.sqlsCriteria.add(criteria);
            return this;
        }

        public Builder andWhere(SqlsCriteria sqls) {
            Sqls.Criteria criteria = sqls.getCriteria();
            criteria.setAndOr("and");
            this.sqlsCriteria.add(criteria);
            return this;
        }

        public Builder orWhere(Sqls sqls) {
            Sqls.Criteria criteria = sqls.getCriteria();
            criteria.setAndOr("or");
            this.sqlsCriteria.add(criteria);
            return this;
        }

        public Builder orWhere(SqlsCriteria sqls) {
            Sqls.Criteria criteria = sqls.getCriteria();
            criteria.setAndOr("or");
            this.sqlsCriteria.add(criteria);
            return this;
        }

        public Builder orderBy(String... properties) {
            return orderByAsc(properties);
        }

        public Builder orderByAsc(String... properties) {
            contactOrderByClause(" Asc", properties);
            return this;
        }

        public Builder orderByDesc(String... properties) {
            contactOrderByClause(" Desc", properties);
            return this;
        }

        private void contactOrderByClause(String order, String... properties) {
            StringBuilder columns = new StringBuilder();
            for (String property : properties) {
                String column;
                if ((column = propertyforOderBy(property)) != null) {
                    columns.append(",").append(column);
                }
            }
            columns.append(order);
            if (columns.length() > 0) {
                orderByClause.append(columns);
            }
        }

        public Example build() {
            this.exampleCriterias = new ArrayList<Criteria>();
            for (Sqls.Criteria criteria : sqlsCriteria) {
                Example.Criteria exampleCriteria = new Example.Criteria(this.propertyMap, this.exists, this.notNull);
                exampleCriteria.setAndOr(criteria.getAndOr());
                for (Sqls.Criterion criterion : criteria.getCriterions()) {
                    String condition = criterion.getCondition();
                    String andOr = criterion.getAndOr();
                    String property = criterion.getProperty();
                    Object[] values = criterion.getValues();
                    transformCriterion(exampleCriteria, condition, property, values, andOr);
                }
                exampleCriterias.add(exampleCriteria);
            }

            if (this.orderByClause.length() > 0) {
                this.orderByClause = new StringBuilder(this.orderByClause.substring(1, this.orderByClause.length()));
            }

            return new Example(this);
        }

        private void transformCriterion(Example.Criteria exampleCriteria, String condition, String property, Object[] values, String andOr) {
            if (values.length == 0) {
                if ("and".equals(andOr)) {
                    exampleCriteria.addCriterion(column(property) + " " + condition);
                } else {
                    exampleCriteria.addOrCriterion(column(property) + " " + condition);
                }
            } else if (values.length == 1) {
                if ("and".equals(andOr)) {
                    exampleCriteria.addCriterion(column(property) + " " + condition, values[0], property(property));
                } else {
                    exampleCriteria.addOrCriterion(column(property) + " " + condition, values[0], property(property));
                }
            } else if (values.length == 2) {
                if ("and".equals(andOr)) {
                    exampleCriteria.addCriterion(column(property) + " " + condition, values[0], values[1], property(property));
                } else {
                    exampleCriteria.addOrCriterion(column(property) + " " + condition, values[0], values[1], property(property));
                }
            }
        }

        private String column(String property) {
            if (propertyMap.containsKey(property)) {
                return propertyMap.get(property).getColumn();
            } else if (exists) {
                throw new MapperException("현재 엔티티 클래스에 이름이 없습니다." + property + "속성!");
            } else {
                return null;
            }
        }

        private String property(String property) {
            if (propertyMap.containsKey(property)) {
                return property;
            } else if (exists) {
                throw new MapperException("현재 엔티티 클래스에 이름이 없습니다." + property + "속성!");
            } else {
                return null;
            }
        }

        private String propertyforOderBy(String property) {
            if (StringUtil.isEmpty(property) || StringUtil.isEmpty(property.trim())) {
                throw new MapperException("받은 속성이 비어 있습니다!");
            }
            property = property.trim();
            if (!propertyMap.containsKey(property)) {
                throw new MapperException("현재 엔티티 클래스에 이름이 없습니다." + property + "속성!");
            }
            return propertyMap.get(property).getColumn();
        }

        public Builder setDistinct(boolean distinct) {
            this.distinct = distinct;
            return this;
        }

        public Builder setForUpdate(boolean forUpdate) {
            this.forUpdate = forUpdate;
            return this;
        }

        public Builder setTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }
    }

    public String getCountColumn() {
        return countColumn;
    }

    @Override
    public String getDynamicTableName() {
        return tableName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public Set<String> getSelectColumns() {
        if (selectColumns != null && selectColumns.size() > 0) {
            //다룰 필요가 없습니다
        } else if (excludeColumns != null && excludeColumns.size() > 0) {
            Collection<EntityColumn> entityColumns = propertyMap.values();
            selectColumns = new LinkedHashSet<String>(entityColumns.size() - excludeColumns.size());
            for (EntityColumn column : entityColumns) {
                if (!excludeColumns.contains(column.getColumn())) {
                    selectColumns.add(column.getColumn());
                }
            }
        }
        return selectColumns;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isForUpdate() {
        return forUpdate;
    }

    public void setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    /**
     * 속성을 조회 할 수 (property)를 지정합니다.
     *
     * @param property
     */
    public void setCountProperty(String property) {
        if (propertyMap.containsKey(property)) {
            this.countColumn = propertyMap.get(property).getColumn();
        }
    }

    /**
     * 테이블 이름 설정
     *
     * @param tableName
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}