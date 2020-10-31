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

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import tk.mybatis.mapper.code.ORDER;
import tk.mybatis.mapper.genid.GenId;
import tk.mybatis.mapper.util.StringUtil;

/**
 * 데이터베이스 테이블의 해당 열
 *
 * @author liuzh
 */
public class EntityColumn {
    private EntityTable table;
    private String property;
    private String column;
    private Class<?> javaType;
    private JdbcType jdbcType;
    private Class<? extends TypeHandler<?>> typeHandler;
    private boolean id = false;
    private boolean identity = false;
    private Class<? extends GenId> genIdClass;
    //필드가 Blob인지 여부
    private boolean blob;
    private String generator;
    //종류
    private String orderBy;
    private int orderPriority;
    //플러그 가능
    private boolean insertable = true;
    //업데이트 가능
    private boolean updatable = true;
    private ORDER order = ORDER.DEFAULT;
    //javaType 설정 여부
    private boolean useJavaType;
    /**
     * 해당 필드 정보
     *
     * @since 3.5.0
     */
    private EntityField entityField;

    public EntityColumn() {
    }

    public EntityColumn(EntityTable table) {
        this.table = table;
    }

    /**
     * 반환 형식은 다음과 같습니다.:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName
     * @return
     */
    public String getColumnEqualsHolder(String entityName) {
        return this.column + " = " + getColumnHolder(entityName);
    }

    /**
     * 반환 형식은 다음과 같습니다.:#{entityName.age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName
     * @return
     */
    public String getColumnHolder(String entityName) {
        return getColumnHolder(entityName, null);
    }

    /**
     * 반환 형식은 다음과 같습니다.:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName
     * @param suffix
     * @return
     */
    public String getColumnHolder(String entityName, String suffix) {
        return getColumnHolder(entityName, null, null);
    }

    /**
     * 반환 형식은 다음과 같습니다.:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler},
     *
     * @param entityName
     * @param suffix
     * @return
     */
    public String getColumnHolderWithComma(String entityName, String suffix) {
        return getColumnHolder(entityName, suffix, ",");
    }

    /**
     * 반환 형식은 다음과 같습니다.:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}+separator
     *
     * @param entityName
     * @param suffix
     * @param separator
     * @return
     */
    public String getColumnHolder(String entityName, String suffix, String separator) {
        StringBuffer sb = new StringBuffer("#{");
        if (StringUtil.isNotEmpty(entityName)) {
            sb.append(entityName);
            sb.append(".");
        }
        sb.append(this.property);
        if (StringUtil.isNotEmpty(suffix)) {
            sb.append(suffix);
        }
        //null이 값으로 전달되면 null 일 수있는 모든 열에 JDBC 유형이 필요합니다.
        if (this.jdbcType != null) {
            sb.append(", jdbcType=");
            sb.append(this.jdbcType.toString());
        }
        //나중에 유형 처리를 사용자 정의하기 위해 열거와 같은 특수 유형 프로세서 클래스를 지정할 수도 있습니다.
        if (this.typeHandler != null) {
            sb.append(", typeHandler=");
            sb.append(this.typeHandler.getCanonicalName());
        }
        //3.4.0 이전 mybatis는 상위 클래스에서 일반 javaType을 가져올 수 없었으므로 더 낮은 버전을 사용하는 경우 다음을 설정해야합니다. useJavaType = true
        //useJavaType 기본값은 false이며, javaType 제한이없는 경우 ByPrimaryKey 메소드의 매개 변수 검증이 완화되어 자동으로 변환됩니다.
        if (useJavaType && !this.javaType.isArray()) {//유형이 배열 인 경우 javaType # 103이 설정되지 않습니다.
            sb.append(", javaType=");
            sb.append(javaType.getCanonicalName());
        }
        sb.append("}");
        if (StringUtil.isNotEmpty(separator)) {
            sb.append(separator);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityColumn that = (EntityColumn) o;

        if (id != that.id) return false;
        if (identity != that.identity) return false;
        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        if (property != null ? !property.equals(that.property) : that.property != null) return false;
        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (javaType != null ? !javaType.equals(that.javaType) : that.javaType != null) return false;
        if (jdbcType != that.jdbcType) return false;
        if (typeHandler != null ? !typeHandler.equals(that.typeHandler) : that.typeHandler != null) return false;
        if (generator != null ? !generator.equals(that.generator) : that.generator != null) return false;
        return !(orderBy != null ? !orderBy.equals(that.orderBy) : that.orderBy != null);

    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        result = 31 * result + (jdbcType != null ? jdbcType.hashCode() : 0);
        result = 31 * result + (typeHandler != null ? typeHandler.hashCode() : 0);
        result = 31 * result + (id ? 1 : 0);
        result = 31 * result + (identity ? 1 : 0);
        result = 31 * result + (generator != null ? generator.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        return result;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    /**
     * 반환 형식은 다음과 같습니다.:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @return
     */
    public String getColumnEqualsHolder() {
        return getColumnEqualsHolder(null);
    }

    /**
     * 반환 형식은 다음과 같습니다.:#{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @return
     */
    public String getColumnHolder() {
        return getColumnHolder(null);
    }

    public EntityField getEntityField() {
        return entityField;
    }

    public void setEntityField(EntityField entityField) {
        this.entityField = entityField;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public EntityTable getTable() {
        return table;
    }

    public void setTable(EntityTable table) {
        this.table = table;
    }

    public Class<? extends TypeHandler<?>> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public boolean isId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public boolean isIdentity() {
        return identity;
    }

    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    public Class<? extends GenId> getGenIdClass() {
        return genIdClass;
    }

    public void setGenIdClass(Class<? extends GenId> genIdClass) {
        this.genIdClass = genIdClass;
    }

    public boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public ORDER getOrder() {
        return order;
    }

    public void setOrder(ORDER order) {
        this.order = order;
    }

    public boolean isBlob() {
        return blob;
    }

    public void setBlob(boolean blob) {
        this.blob = blob;
    }

    public boolean isUseJavaType() {
        return useJavaType;
    }

    public void setUseJavaType(boolean useJavaType) {
        this.useJavaType = useJavaType;
    }

    public int getOrderPriority() {
        return orderPriority;
    }

    public void setOrderPriority(int orderPriority) {
        this.orderPriority = orderPriority;
    }

    @Override
    public String toString() {
        return "EntityColumn{" +
                "table=" + table.getName() +
                ", property='" + property + '\'' +
                ", column='" + column + '\'' +
                ", javaType=" + javaType +
                ", jdbcType=" + jdbcType +
                ", typeHandler=" + typeHandler +
                ", id=" + id +
                ", identity=" + identity +
                ", blob=" + blob +
                ", generator='" + generator + '\'' +
                ", orderBy='" + orderBy + '\'' +
                ", orderPriority='" + orderPriority + '\'' +
                ", insertable=" + insertable +
                ", updatable=" + updatable +
                ", order=" + order +
                '}';
    }
}
