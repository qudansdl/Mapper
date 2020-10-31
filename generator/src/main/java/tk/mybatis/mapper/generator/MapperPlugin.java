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

package tk.mybatis.mapper.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.*;

/**
 * 일반 매퍼 생성기 플러그인
 *
 * @author liuzh
 */
public class MapperPlugin extends FalseMethodPlugin {
    private Set<String> mappers = new HashSet<String>();
    private boolean caseSensitive = false;
    private boolean useMapperCommentGenerator = true;
    //예를 들어, mysql은`, sqlserver는[
    private String beginningDelimiter = "";
    //예를 들어, mysql은`, sqlserver는]
    private String endingDelimiter = "";
    //데이터베이스 스키마
    private String schema;
    //주석 생성기
    private CommentGeneratorConfiguration commentCfg;
    //강제 주석
    private boolean forceAnnotation;

    //데이터 주석을 생성해야합니까?
    private boolean needsData = false;
    //Getter 주석 생성 여부
    private boolean needsGetter = false;
    //Setter 주석을 생성해야합니까?
    private boolean needsSetter = false;
    //ToString 주석을 생성해야합니까?
    private boolean needsToString = false;
    //접근 자 (chain = true) 주석을 생성해야합니까?
    private boolean needsAccessors = false;
    //EqualsAndHashCode 주석 생성 여부
    private boolean needsEqualsAndHashCode = false;
    //EqualsAndHashCode 주석을 생성해야합니까?“callSuper = true”
    private boolean needsEqualsAndHashCodeAndCallSuper = false;
    //필드 이름 상수 생성 여부
    private boolean generateColumnConsts = false;
    //기본 속성의 정적 메서드 생성 여부
    private boolean generateDefaultInstanceMethod = false;
    //@ApiModel 및@ApiModelProperty
    private boolean needsSwagger = false;

    public String getDelimiterName(String name) {
        StringBuilder nameBuilder = new StringBuilder();
        if (StringUtility.stringHasValue(schema)) {
            nameBuilder.append(schema);
            nameBuilder.append(".");
        }
        nameBuilder.append(beginningDelimiter);
        nameBuilder.append(name);
        nameBuilder.append(endingDelimiter);
        return nameBuilder.toString();
    }

    /**
     * 생성 된 매퍼 인터페이스
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //엔티티 클래스 가져 오기
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        //import상호 작용
        for (String mapper : mappers) {
            interfaze.addImportedType(new FullyQualifiedJavaType(mapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(mapper + "<" + entityType.getShortName() + ">"));
        }
        //import엔티티 클래스
        interfaze.addImportedType(entityType);
        return true;
    }

    /**
     * 엔티티 클래스의 패키지 및 @Table 주석 처리
     *
     * @param topLevelClass
     * @param introspectedTable
     */
    private void processEntityClass(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        //JPA 주석 소개
        topLevelClass.addImportedType("javax.persistence.*");
        //lombok확장 시작
        //데이터가 필요한 경우 패키지를 가져오고 코드에 주석을 추가합니다.
        if (this.needsData) {
            topLevelClass.addImportedType("lombok.Data");
            topLevelClass.addAnnotation("@Data");
        }
        //Getter가 필요한 경우 패키지를 가져와 코드에 주석을 추가하십시오.
        if (this.needsGetter) {
            topLevelClass.addImportedType("lombok.Getter");
            topLevelClass.addAnnotation("@Getter");
        }
        //Setter가 필요한 경우 패키지를 가져와 코드에 주석을 추가하십시오.
        if (this.needsSetter) {
            topLevelClass.addImportedType("lombok.Setter");
            topLevelClass.addAnnotation("@Setter");
        }
        //ToString이 필요한 경우 패키지를 가져와 코드에 주석을 추가하십시오.
        if (this.needsToString) {
            topLevelClass.addImportedType("lombok.ToString");
            topLevelClass.addAnnotation("@ToString");
        }
        // 같은果需要EqualsAndHashCode，并且“callSuper = true”，引入包，代码댓글 추가
        if (this.needsEqualsAndHashCodeAndCallSuper) {
            topLevelClass.addImportedType("lombok.EqualsAndHashCode");
            topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = true)");
        } else {
            // EqualsAndHashCode가 필요한 경우 패키지를 가져오고 코드에 주석을 추가합니다.
            if (this.needsEqualsAndHashCode) {
                topLevelClass.addImportedType("lombok.EqualsAndHashCode");
                topLevelClass.addAnnotation("@EqualsAndHashCode");
            }
        }
        // 접근자가 필요한 경우 패키지를 가져오고 코드에 주석을 추가합니다.
        if (this.needsAccessors) {
            topLevelClass.addImportedType("lombok.experimental.Accessors");
            topLevelClass.addAnnotation("@Accessors(chain = true)");
        }
        // lombok확장 종료
        // 지역 스웨거 확장
        if (this.needsSwagger) {
            //가이드 패키지
            topLevelClass.addImportedType("io.swagger.annotations.ApiModel");
            topLevelClass.addImportedType("io.swagger.annotations.ApiModelProperty");
            //댓글 추가(주석에서 변환 문자 제거)
            String remarks = introspectedTable.getRemarks();
            if (remarks == null) {
                remarks = "";
            }
            topLevelClass.addAnnotation("@ApiModel(\"" + remarks.replaceAll("\r", "").replaceAll("\n", "") + "\")");
        }
        // endregion swagger 확장
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();

        //지역 문서 참고
        String remarks = introspectedTable.getRemarks();
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * 테이블 이름：" + tableName);
        if (remarks != null) {
            remarks = remarks.trim();
        }
        if (remarks != null && remarks.trim().length() > 0) {
            String[] lines = remarks.split("\\r?\\n");
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (i == 0) {
                    topLevelClass.addJavaDocLine(" * 테이블 노트：" + line);
                } else {
                    topLevelClass.addJavaDocLine(" *         " + line);
                }
            }
        }
        topLevelClass.addJavaDocLine("*/");
        //endregion
        //공백이 포함되어 있거나 구분 기호가 필요한 경우 개선해야합니다.
        if (StringUtility.stringContainsSpace(tableName)) {
            tableName = context.getBeginningDelimiter()
                    + tableName
                    + context.getEndingDelimiter();
        }
        //대소 문자를 무시할지 여부는 대소 문자를 구분하는 데이터베이스에 유용합니다.
        if (caseSensitive && !topLevelClass.getType().getShortName().equals(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        } else if (!topLevelClass.getType().getShortName().equalsIgnoreCase(tableName)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        } else if (StringUtility.stringHasValue(schema)
                || StringUtility.stringHasValue(beginningDelimiter)
                || StringUtility.stringHasValue(endingDelimiter)) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        } else if (forceAnnotation) {
            topLevelClass.addAnnotation("@Table(name = \"" + getDelimiterName(tableName) + "\")");
        }
        if (generateColumnConsts) {
            for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                Field field = new Field();
                field.setVisibility(JavaVisibility.PUBLIC);
                field.setStatic(true);
                field.setFinal(true);
                field.setName(introspectedColumn.getActualColumnName().toUpperCase()); //$NON-NLS-1$
                field.setType(new FullyQualifiedJavaType(String.class.getName())); //$NON-NLS-1$
                field.setInitializationString("\"" + introspectedColumn.getJavaProperty() + "\"");
                context.getCommentGenerator().addClassComment(topLevelClass, introspectedTable);
                topLevelClass.addField(field);
                //pageHelper에 대한 필드 이름 상수 추가
                Field columnField = new Field();
                columnField.setVisibility(JavaVisibility.PUBLIC);
                columnField.setStatic(true);
                columnField.setFinal(true);
                columnField.setName("DB_" + introspectedColumn.getActualColumnName().toUpperCase()); //$NON-NLS-1$
                columnField.setType(new FullyQualifiedJavaType(String.class.getName())); //$NON-NLS-1$
                columnField.setInitializationString("\"" + introspectedColumn.getActualColumnName() + "\"");
                topLevelClass.addField(columnField);
            }
        }
        if (generateDefaultInstanceMethod) {
            //패키지의 기본 유형과 색인은 향후 사용을 용이하게하기 위해 일관성이 있어야합니다.
            List<String> baseClassName = Arrays.asList("byte", "short", "char", "int", "long", "float", "double", "boolean");
            List<String> wrapperClassName = Arrays.asList("Byte", "Short", "Character", "Integer", "Long", "Float", "Double", "Boolean");
            List<String> otherClassName = Arrays.asList("String", "BigDecimal", "BigInteger");
            Method defaultMethod = new Method();
            //메서드 메모 추가
            defaultMethod.addJavaDocLine("/**");
            defaultMethod.addJavaDocLine(" * 기본값이있는 예");
            defaultMethod.addJavaDocLine("*/");
            defaultMethod.setStatic(true);
            defaultMethod.setName("defaultInstance");
            defaultMethod.setVisibility(JavaVisibility.PUBLIC);
            defaultMethod.setReturnType(topLevelClass.getType());
            defaultMethod.addBodyLine(String.format("%s instance = new %s();", topLevelClass.getType().getShortName(), topLevelClass.getType().getShortName()));
            for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
                String shortName = introspectedColumn.getFullyQualifiedJavaType().getShortName();
                if (!baseClassName.contains(shortName) && !wrapperClassName.contains(shortName) && !otherClassName.contains(shortName)) {
                    continue;
                }
                if (introspectedColumn.getDefaultValue() != null) {
                    String defaultValue = introspectedColumn.getDefaultValue();
                    //postgresql과 같은 비고의 유형 설명을 처리하십시오. ''::character varying
                    if (defaultValue.matches("'\\.*'::\\w+(\\s\\w+)?")) {
                        //
                        defaultValue = defaultValue.substring(0, defaultValue.lastIndexOf("::"));
                    }
                    //제거 전후'',같은 '123456' -> 123456
                    if (defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
                        if (defaultValue.length() == 2) {
                            defaultValue = "";
                        } else {
                            defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
                        }
                    }
                    //시간 유형 기본값 인식은 일시적으로 지원되지 않으며 다른 데이터베이스 표현식이 다릅니다.
                    if ("Boolean".equals(shortName) || "boolean".equals(shortName)) {
                        if ("0".equals(defaultValue)) {
                            defaultValue = "false";
                        } else if ("1".equals(defaultValue)) {
                            defaultValue = "true";
                        }
                    }

                    if ("String".equals(shortName)) {
                        //문자열, 새 문자열로 생성되지 않음
                        // 사실 새로운 현에는 문제가 없지만 강박 장애, 아이디어가 촉발되므로 변화
                        defaultMethod.addBodyLine(String.format("instance.%s = \"%s\";", introspectedColumn.getJavaProperty(), defaultValue));
                    } else {
                        String javaProperty = introspectedColumn.getJavaProperty();
                        if (baseClassName.contains(shortName)) {
                            //기본 유형, 새로운 패키징 클래스 생성으로 변환
                            javaProperty = wrapperClassName.get(baseClassName.indexOf(shortName));
                        }
                        //새로운 방법으로 변환
                        defaultMethod.addBodyLine(String.format("instance.%s = new %s(\"%s\");", javaProperty, shortName, defaultValue));
                    }
                }

            }
            defaultMethod.addBodyLine("return instance;");
            topLevelClass.addMethod(defaultMethod);
        }
    }

    /**
     * Getter 주석을 생성해야하는 경우 관련 코드 가져 오기를 생성 할 필요가 없습니다.
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {

        return !(this.needsData || this.needsGetter);
    }

    /**
     * Setter 주석을 생성해야하는 경우 세트 관련 코드를 생성 할 필요가 없습니다.
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method,
                                              TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return !(this.needsData || this.needsSetter);
    }

    /**
     * 기본 엔티티 클래스 생성
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * 엔티티 클래스 주석 KEY 객체 생성
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return true;
    }

    /**
     * BLOB 필드로 개체 생성
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        processEntityClass(topLevelClass, introspectedTable);
        return false;
    }


    @Override
    public void setContext(Context context) {
        super.setContext(context);
        //기본 댓글 생성기 설정
        useMapperCommentGenerator = !"FALSE".equalsIgnoreCase(context.getProperty("useMapperCommentGenerator"));
        if (useMapperCommentGenerator) {
            commentCfg = new CommentGeneratorConfiguration();
            commentCfg.setConfigurationType(MapperCommentGenerator.class.getCanonicalName());
            context.setCommentGeneratorConfiguration(commentCfg);
        }
        //댓글을 얻기 위해 오라클 지원 # 114
        context.getJdbcConnectionConfiguration().addProperty("remarksReporting", "true");
        //대기하다mysql얻다논평
        context.getJdbcConnectionConfiguration().addProperty("useInformationSchema", "true");
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        String mappers = getProperty("mappers");
        if (StringUtility.stringHasValue(mappers)) {
            for (String mapper : mappers.split(",")) {
                this.mappers.add(mapper);
            }
        } else {
            throw new RuntimeException("Mapper插件缺少必要의mappers속성!");
        }
        this.caseSensitive = Boolean.parseBoolean(this.properties.getProperty("caseSensitive"));
        this.forceAnnotation = getPropertyAsBoolean("forceAnnotation");
        this.beginningDelimiter = getProperty("beginningDelimiter", "");
        this.endingDelimiter = getProperty("endingDelimiter", "");
        this.schema = getProperty("schema");
        //lombok넓히다
        String lombok = getProperty("lombok");
        if (lombok != null && !"".equals(lombok)) {
            this.needsData = lombok.contains("Data");
            //@Data 더 높은 우선 순위 @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode
            this.needsGetter = !this.needsData && lombok.contains("Getter");
            this.needsSetter = !this.needsData && lombok.contains("Setter");
            this.needsToString = !this.needsData && lombok.contains("ToString");
            this.needsEqualsAndHashCode = !this.needsData && lombok.contains("EqualsAndHashCode");
            // 롬복 확장 EqualsAndHashCode 주석 추가 여부 구성“callSuper = true”
            String lombokEqualsAndHashCodeCallSuper = getProperty("lombokEqualsAndHashCodeCallSuper", "false");
            this.needsEqualsAndHashCodeAndCallSuper = this.needsEqualsAndHashCode && "TRUE".equalsIgnoreCase(lombokEqualsAndHashCodeCallSuper);
            this.needsAccessors = lombok.contains("Accessors");
        }
        //swagger넓히다
        String swagger = getProperty("swagger", "false");
        if ("TRUE".equalsIgnoreCase(swagger)) {
            this.needsSwagger = true;
        }
        if (useMapperCommentGenerator) {
            commentCfg.addProperty("beginningDelimiter", this.beginningDelimiter);
            commentCfg.addProperty("endingDelimiter", this.endingDelimiter);
            String forceAnnotation = getProperty("forceAnnotation");
            if (StringUtility.stringHasValue(forceAnnotation)) {
                commentCfg.addProperty("forceAnnotation", forceAnnotation);
            }
            commentCfg.addProperty("needsSwagger", this.needsSwagger + "");
        }
        this.generateColumnConsts = getPropertyAsBoolean("generateColumnConsts");
        this.generateDefaultInstanceMethod = getPropertyAsBoolean("generateDefaultInstanceMethod");
    }

    protected String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    protected String getProperty(String key, String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    protected Boolean getPropertyAsBoolean(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

}
