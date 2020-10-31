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

import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.code.IdentityDialect;
import tk.mybatis.mapper.code.Style;
import tk.mybatis.mapper.mapperhelper.resolve.EntityResolve;
import tk.mybatis.mapper.util.SimpleTypeUtil;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 일반 매퍼 속성 구성
 *
 * @author liuzh
 */
public class Config {
    public static final String PREFIX = "mapper";

    private List<Class> mappers = new ArrayList<Class>();
    private String  IDENTITY;
    private boolean BEFORE;
    private String  seqFormat;
    private String  catalog;
    private String  schema;
    //Example 메서드를 호출 할 때 Example (entityClass) 및 Mapper <EntityClass>가 동일한 지 확인합니다.
    private boolean checkExampleEntityClass;
    //간단한 유형 사용
    //3.5.기본값은 0 이후에 true로 변경됩니다.
    private boolean useSimpleType    = true;
    /**
     * @since 3.5.0
     */
    private boolean enumAsSimpleType;
    /**
     * 메소드에서 주석을 지원할지 여부, 기본값은 false입니다.
     */
    private boolean enableMethodAnnotation;
    /**
     * 일반 getAllIfColumnNode의 경우 판단 여부!=''，기본적으로 판단 없음
     */
    private boolean notEmpty;
    /**
     * 필드 변환 스타일, 기본 카멜 케이스를 밑줄로
     */
    private Style style;
    /**
     * 처리 키워드, 기본적으로 비어 있음, mysql은`{0}`로 설정 가능, sqlserver는 [{0}], {0}는 열 이름을 나타냄
     */
    private String wrapKeyword = "";
    /**
     * 파서 구성
     */
    private Class<? extends EntityResolve> resolveClass;
    /**
     * 안전하게 삭제, 개봉 후 테이블 전체 삭제는 허용되지 않습니다. delete from table
     */
    private boolean safeDelete;
    /**
     * 보안 업데이트는 개봉 후 다음과 같은 전체 테이블을 업데이트 할 수 없습니다. update table set xx=?
     */
    private boolean safeUpdate;
    /**
     * javaType 설정 여부
     */
    private boolean useJavaType;

    public String getCatalog() {
        return catalog;
    }

    /**
     * 글로벌 카탈로그를 설정합니다. 기본값은 비어 있고 값이 설정되면 테이블을 작동 할 때 SQL이 catalog.tablename이됩니다.
     *
     * @param catalog
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * 기본 키 자동 증가 쓰기 저장 SQL 가져 오기
     *
     * @return
     */
    public String getIDENTITY() {
        if (StringUtil.isNotEmpty(this.IDENTITY)) {
            return this.IDENTITY;
        }
        //mysql의 기본값
        return IdentityDialect.MYSQL.getIdentityRetrievalStatement();
    }

    /**
     * 기본 키 자동 증가 및 쓰기 되돌림 방법, 기본값은 MYSQL입니다. 자세한 내용은 문서를 참조하십시오.
     *
     * @param IDENTITY
     */
    public void setIDENTITY(String IDENTITY) {
        IdentityDialect identityDialect = IdentityDialect.getDatabaseDialect(IDENTITY);
        if (identityDialect != null) {
            this.IDENTITY = identityDialect.getIdentityRetrievalStatement();
        } else {
            this.IDENTITY = IDENTITY;
        }
    }

    /**
     * 카탈로그 또는 스키마를 사용하여 테이블 접두사 가져 오기
     *
     * @return
     */
    public String getPrefix() {
        if (StringUtil.isNotEmpty(this.catalog)) {
            return this.catalog;
        }
        if (StringUtil.isNotEmpty(this.schema)) {
            return this.schema;
        }
        return "";
    }

    public String getSchema() {
        return schema;
    }

    /**
     * 전역 스키마를 설정합니다. 기본값은 비어 있습니다. 값이 설정되면 테이블 작동시 SQL이schema.tablename
     * <br>카탈로그가 동시에 설정된 경우 먼저 사용catalog.tablename
     *
     * @param schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 시퀀스 서식 템플릿 가져 오기
     *
     * @return
     */
    public String getSeqFormat() {
        if (StringUtil.isNotEmpty(this.seqFormat)) {
            return this.seqFormat;
        }
        return "{0}.nextval";
    }

    /**
     * 시퀀스 획득 규칙, {num} 형식 지정 매개 변수 사용, Oracle의 경우 기본값은 {0} .nextval입니다.
     * <br>각각 0,1,2,3에 해당하는 3 개의 선택적 매개 변수가 있습니다.SequenceName，ColumnName, PropertyName，TableName
     *
     * @param seqFormat
     */
    public void setSeqFormat(String seqFormat) {
        this.seqFormat = seqFormat;
    }

    public Style getStyle() {
        return this.style == null ? Style.camelhump : this.style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public String getWrapKeyword() {
        return wrapKeyword;
    }

    public void setWrapKeyword(String wrapKeyword) {
        this.wrapKeyword = wrapKeyword;
    }

    /**
     * SelectKey 순서 가져 오기
     *
     * @return
     */
    public boolean isBEFORE() {
        return BEFORE;
    }

    public void setBEFORE(boolean BEFORE) {
        this.BEFORE = BEFORE;
    }

    public boolean isCheckExampleEntityClass() {
        return checkExampleEntityClass;
    }

    public void setCheckExampleEntityClass(boolean checkExampleEntityClass) {
        this.checkExampleEntityClass = checkExampleEntityClass;
    }

    public boolean isEnableMethodAnnotation() {
        return enableMethodAnnotation;
    }

    public void setEnableMethodAnnotation(boolean enableMethodAnnotation) {
        this.enableMethodAnnotation = enableMethodAnnotation;
    }

    public boolean isEnumAsSimpleType() {
        return enumAsSimpleType;
    }

    public void setEnumAsSimpleType(boolean enumAsSimpleType) {
        this.enumAsSimpleType = enumAsSimpleType;
    }

    public boolean isNotEmpty() {
        return notEmpty;
    }

    public void setNotEmpty(boolean notEmpty) {
        this.notEmpty = notEmpty;
    }

    public boolean isUseSimpleType() {
        return useSimpleType;
    }

    public void setUseSimpleType(boolean useSimpleType) {
        this.useSimpleType = useSimpleType;
    }

    /**
     * 기본 키 자동 증가 및 쓰기 되돌림 방법의 실행 순서, 기본값은 AFTER, 선택적 값(BEFORE|AFTER)
     *
     * @param order
     */
    public void setOrder(String order) {
        this.BEFORE = "BEFORE".equalsIgnoreCase(order);
    }

    public String getIdentity() {
        return getIDENTITY();
    }

    public void setIdentity(String identity) {
        setIDENTITY(identity);
    }

    public List<Class> getMappers() {
        return mappers;
    }

    public void setMappers(List<Class> mappers) {
        this.mappers = mappers;
    }

    public boolean isBefore() {
        return isBEFORE();
    }

    public void setBefore(boolean before) {
        setBEFORE(before);
    }

    public Class<? extends EntityResolve> getResolveClass() {
        return resolveClass;
    }

    public void setResolveClass(Class<? extends EntityResolve> resolveClass) {
        this.resolveClass = resolveClass;
    }

    public boolean isSafeDelete() {
        return safeDelete;
    }

    public void setSafeDelete(boolean safeDelete) {
        this.safeDelete = safeDelete;
    }

    public boolean isSafeUpdate() {
        return safeUpdate;
    }

    public void setSafeUpdate(boolean safeUpdate) {
        this.safeUpdate = safeUpdate;
    }

    public boolean isUseJavaType() {
        return useJavaType;
    }

    public void setUseJavaType(boolean useJavaType) {
        this.useJavaType = useJavaType;
    }

    /**
     * 구성 속성
     *
     * @param properties
     */
    public void setProperties(Properties properties) {
        if (properties == null) {
            //기본 혹
            this.style = Style.camelhump;
            return;
        }
        String IDENTITY = properties.getProperty("IDENTITY");
        if (StringUtil.isNotEmpty(IDENTITY)) {
            setIDENTITY(IDENTITY);
        }
        String seqFormat = properties.getProperty("seqFormat");
        if (StringUtil.isNotEmpty(seqFormat)) {
            setSeqFormat(seqFormat);
        }
        String catalog = properties.getProperty("catalog");
        if (StringUtil.isNotEmpty(catalog)) {
            setCatalog(catalog);
        }
        String schema = properties.getProperty("schema");
        if (StringUtil.isNotEmpty(schema)) {
            setSchema(schema);
        }

        //ORDER에는 구성 할 수있는 세 가지 속성 이름이 있습니다.
        String ORDER = properties.getProperty("ORDER");
        if (StringUtil.isNotEmpty(ORDER)) {
            setOrder(ORDER);
        }
        ORDER = properties.getProperty("order");
        if (StringUtil.isNotEmpty(ORDER)) {
            setOrder(ORDER);
        }
        ORDER = properties.getProperty("before");
        if (StringUtil.isNotEmpty(ORDER)) {
            setBefore(Boolean.valueOf(ORDER));
        }


        this.notEmpty = Boolean.valueOf(properties.getProperty("notEmpty"));
        this.enableMethodAnnotation = Boolean.valueOf(properties.getProperty("enableMethodAnnotation"));
        this.checkExampleEntityClass = Boolean.valueOf(properties.getProperty("checkExampleEntityClass"));
        //默인정 된 값이 사실이므로 특별한 판단이 필요합니다.
        String useSimpleTypeStr = properties.getProperty("useSimpleType");
        if (StringUtil.isNotEmpty(useSimpleTypeStr)) {
            this.useSimpleType = Boolean.valueOf(useSimpleTypeStr);
        }
        this.enumAsSimpleType = Boolean.valueOf(properties.getProperty("enumAsSimpleType"));
        //注정규화 된 클래스 이름을 사용하여 쉼표로 구분 된 새로운 기본 유형
        String simpleTypes = properties.getProperty("simpleTypes");
        if (StringUtil.isNotEmpty(simpleTypes)) {
            SimpleTypeUtil.registerSimpleType(simpleTypes);
        }
        //8 가지 기본 유형 사용
        if (Boolean.valueOf(properties.getProperty("usePrimitiveType"))) {
            SimpleTypeUtil.registerPrimitiveTypes();
        }
        String styleStr = properties.getProperty("style");
        if (StringUtil.isNotEmpty(styleStr)) {
            try {
                this.style = Style.valueOf(styleStr);
            } catch (IllegalArgumentException e) {
                throw new MapperException(styleStr + "법적 스타일 값이 아닙니다!");
            }
        } else {
            //기본 혹
            this.style = Style.camelhump;
        }
        //처리 키워드
        String wrapKeyword = properties.getProperty("wrapKeyword");
        if (StringUtil.isNotEmpty(wrapKeyword)) {
            this.wrapKeyword = wrapKeyword;
        }
        //안전하게 삭제
        this.safeDelete = Boolean.valueOf(properties.getProperty("safeDelete"));
        //보안 업데이트
        this.safeUpdate = Boolean.valueOf(properties.getProperty("safeUpdate"));
        //다음과 같은 javaType을 설정할지 여부 {id, javaType=java.lang.Long}
        this.useJavaType = Boolean.valueOf(properties.getProperty("useJavaType"));
    }
}
