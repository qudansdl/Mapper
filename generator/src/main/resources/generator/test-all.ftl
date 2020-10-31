대상 패키지: ${package}

<!-- http://freemarker.apache.org/docs/ref_builtins_date.html -->
현재 시간：
<#assign dateTime = .now>
${dateTime?date}
${dateTime?time}
${dateTime?string["yyyy-MM-dd HH:mm:ss"]}

구성된 모든 속성 정보:
<#list props?keys as key>
${key} - ${props[key]}
</#list>

<#list tableClassSet as tableClass>
****************************************************************************************
엔터티 및 테이블에 대한 정보：
테이블 이름：${tableClass.tableName}
변수 이름：${tableClass.variableName}
소문자 이름：${tableClass.lowerCaseName}
클래스 이름：${tableClass.shortClassName}
성명：${tableClass.fullClassName}
패키지 이름：${tableClass.packageName}

컬럼 정보：
=====================================
    <#if tableClass.pkFields??>
    기본 키：
        <#list tableClass.pkFields as field>
        -------------------------------------
        열 이름：${field.columnName}
        컬럼 유형：${field.jdbcType}
        분야 명：${field.fieldName}
        <#if field.remarks??>
        논평：${field.remarks}
        </#if>
        패키지 이름 입력：${field.typePackage}
        짧은 이름 입력：${field.shortTypeName}
        성명 입력：${field.fullTypeName}
        기본 키：${field.identity?c}
        사용할 수 있습니까?：${field.nullable?c}
        BLOB 컬럼입니까：${field.blobColumn?c}
        문자열 열입니까?：${field.stringColumn?c}
        문자열 열입니까?：${field.jdbcCharacterColumn?c}
        날짜 열입니까?：${field.jdbcDateColumn?c}
        시간 열인가요?：${field.jdbcTimeColumn?c}
        시퀀스 열입니까?：${field.sequenceColumn?c}
        기둥 길이：${field.length?c}
        컬럼 정밀도：${field.scale}
        </#list>
    </#if>

    <#if tableClass.baseFields??>
    베이스 컬럼：
        <#list tableClass.baseFields as field>
        -------------------------------------
        열 이름：${field.columnName}
        컬럼 유형：${field.jdbcType}
        분야 명：${field.fieldName}
        <#if field.remarks??>
        논평：${field.remarks}
        </#if>
        패키지 이름 입력：${field.typePackage}
        짧은 이름 입력：${field.shortTypeName}
        성명 입력：${field.fullTypeName}
        기본 키：${field.identity?c}
        사용할 수 있습니까?：${field.nullable?c}
        BLOB 컬럼입니까：${field.blobColumn?c}
        문자열 열입니까?：${field.stringColumn?c}
        문자열 열입니까?：${field.jdbcCharacterColumn?c}
        날짜 열입니까?：${field.jdbcDateColumn?c}
        시간 열인가요?：${field.jdbcTimeColumn?c}
        시퀀스 열입니까?：${field.sequenceColumn?c}
        기둥 길이：${field.length?c}
        컬럼 정밀도：${field.scale}
        </#list>
    </#if>

    <#if tableClass.blobFields??>
    Blob 열：
        <#list tableClass.blobFields as field>
        -------------------------------------
        열 이름：${field.columnName}
        컬럼 유형：${field.jdbcType}
        분야 명：${field.fieldName}
        <#if field.remarks??>
        논평：${field.remarks}
        </#if>
        패키지 이름 입력：${field.typePackage}
        짧은 이름 입력：${field.shortTypeName}
        성명 입력：${field.fullTypeName}
        기본 키：${field.identity?c}
        사용할 수 있습니까?：${field.nullable?c}
        BLOB 컬럼입니까：${field.blobColumn?c}
        문자열 열입니까?：${field.stringColumn?c}
        문자열 열입니까?：${field.jdbcCharacterColumn?c}
        날짜 열입니까?：${field.jdbcDateColumn?c}
        시간 열인가요?：${field.jdbcTimeColumn?c}
        시퀀스 열입니까?：${field.sequenceColumn?c}
        기둥 길이：${field.length?c}
        컬럼 정밀도：${field.scale}
        </#list>
    </#if>
=====================================
모든 열：
    <#if tableClass.allFields??>
    열 이름 - 분야 명
        <#list tableClass.allFields as field>
        ${field.columnName} - ${field.fieldName}
        </#list>
    </#if>
</#list>