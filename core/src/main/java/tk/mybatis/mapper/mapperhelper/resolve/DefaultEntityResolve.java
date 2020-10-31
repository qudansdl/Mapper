package tk.mybatis.mapper.mapperhelper.resolve;

import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.annotation.ColumnType;
import tk.mybatis.mapper.annotation.KeySql;
import tk.mybatis.mapper.annotation.NameStyle;
import tk.mybatis.mapper.annotation.Order;
import tk.mybatis.mapper.code.IdentityDialect;
import tk.mybatis.mapper.code.ORDER;
import tk.mybatis.mapper.code.Style;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.genid.GenId;
import tk.mybatis.mapper.gensql.GenSql;
import tk.mybatis.mapper.mapperhelper.FieldHelper;
import tk.mybatis.mapper.util.SimpleTypeUtil;
import tk.mybatis.mapper.util.SqlReservedWords;
import tk.mybatis.mapper.util.StringUtil;

import javax.persistence.*;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author liuzh
 */
public class DefaultEntityResolve implements EntityResolve {
    private final Log log = LogFactory.getLog(DefaultEntityResolve.class);

    @Override
    public EntityTable resolveEntity(Class<?> entityClass, Config config) {
        Style style = config.getStyle();
        //스타일,이 주석은 전역 구성보다 우선합니다.
        if (entityClass.isAnnotationPresent(NameStyle.class)) {
            NameStyle nameStyle = entityClass.getAnnotation(NameStyle.class);
            style = nameStyle.value();
        }

        //EntityTable 생성 및 캐시
        EntityTable entityTable = null;
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            if (!"".equals(table.name())) {
                entityTable = new EntityTable(entityClass);
                entityTable.setTable(table);
            }
        }
        if (entityTable == null) {
            entityTable = new EntityTable(entityClass);
            //다래끼로 제어 가능
            String tableName = StringUtil.convertByStyle(entityClass.getSimpleName(), style);
            //키워드 자동 처리
            if (StringUtil.isNotEmpty(config.getWrapKeyword()) && SqlReservedWords.containsWord(tableName)) {
                tableName = MessageFormat.format(config.getWrapKeyword(), tableName);
            }
            entityTable.setName(tableName);
        }
        entityTable.setEntityClassColumns(new LinkedHashSet<EntityColumn>());
        entityTable.setEntityClassPKColumns(new LinkedHashSet<EntityColumn>());
        //모든 열 처리
        List<EntityField> fields = null;
        if (config.isEnableMethodAnnotation()) {
            fields = FieldHelper.getAll(entityClass);
        } else {
            fields = FieldHelper.getFields(entityClass);
        }
        for (EntityField field : fields) {
            //단순형이 활성화 된 경우 단순형 검증을 수행하고 단순형이 아닌 경우 직접 건너 뛰기
            //3.5.0 열거가 단순 유형으로 활성화 된 경우 열거 유형이 자동으로 무시되지 않습니다.
            //4.0 Column 또는 ColumnType 주석으로 표시된 경우 무시하지 마십시오.
            if (config.isUseSimpleType()
                    && !field.isAnnotationPresent(Column.class)
                    && !field.isAnnotationPresent(ColumnType.class)
                    && !(SimpleTypeUtil.isSimpleType(field.getJavaType())
                    ||
                    (config.isEnumAsSimpleType() && Enum.class.isAssignableFrom(field.getJavaType())))) {
                continue;
            }
            processField(entityTable, field, config, style);
        }
        //pk.size = 0이면 모든 열을 기본 키로 사용
        if (entityTable.getEntityClassPKColumns().size() == 0) {
            entityTable.setEntityClassPKColumns(entityTable.getEntityClassColumns());
        }
        entityTable.initPropertyMap();
        return entityTable;
    }

    /**
     * 가공 분야
     *
     * @param entityTable
     * @param field
     * @param config
     * @param style
     */
    protected void processField(EntityTable entityTable, EntityField field, Config config, Style style) {
        //필드 제외
        if (field.isAnnotationPresent(Transient.class)) {
            return;
        }
        //Id
        EntityColumn entityColumn = new EntityColumn(entityTable);
        //사용 여부 {xx, javaType=xxx}
        entityColumn.setUseJavaType(config.isUseJavaType());
        //후속 확장 사용을 위해 필드 정보 기록
        entityColumn.setEntityField(field);
        if (field.isAnnotationPresent(Id.class)) {
            entityColumn.setId(true);
        }
        //Column
        String columnName = null;
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.name();
            entityColumn.setUpdatable(column.updatable());
            entityColumn.setInsertable(column.insertable());
        }
        //ColumnType
        if (field.isAnnotationPresent(ColumnType.class)) {
            ColumnType columnType = field.getAnnotation(ColumnType.class);
            //BLOB 필드입니까?
            entityColumn.setBlob(columnType.isBlob());
            //열은 별칭으로 작동 할 수 있습니다.
            if (StringUtil.isEmpty(columnName) && StringUtil.isNotEmpty(columnType.column())) {
                columnName = columnType.column();
            }
            if (columnType.jdbcType() != JdbcType.UNDEFINED) {
                entityColumn.setJdbcType(columnType.jdbcType());
            }
            if (columnType.typeHandler() != UnknownTypeHandler.class) {
                entityColumn.setTypeHandler(columnType.typeHandler());
            }
        }
        //열 이름
        if (StringUtil.isEmpty(columnName)) {
            columnName = StringUtil.convertByStyle(field.getName(), style);
        }
        //키워드 자동 처리
        if (StringUtil.isNotEmpty(config.getWrapKeyword()) && SqlReservedWords.containsWord(columnName)) {
            columnName = MessageFormat.format(config.getWrapKeyword(), columnName);
        }
        entityColumn.setProperty(field.getName());
        entityColumn.setColumn(columnName);
        entityColumn.setJavaType(field.getJavaType());
        if (field.getJavaType().isPrimitive()) {
            log.warn("만능인 Mapper 警告信息: <[" + entityColumn + "]> 사용하다了基本수업型，基本수업型在动态 SQL 中由于存在기본값，因此任何时候都不等于 null，建议修改基本수업型为对应의包装수업型!");
        }
        //OrderBy
        processOrderBy(entityTable, field, entityColumn);
        //기본 키 전략 처리
        processKeyGenerator(entityTable, field, entityColumn);
        entityTable.getEntityClassColumns().add(entityColumn);
        if (entityColumn.isId()) {
            entityTable.getEntityClassPKColumns().add(entityColumn);
        }
    }

    /**
     * 처리 정렬
     *
     * @param entityTable
     * @param field
     * @param entityColumn
     */
    protected void processOrderBy(EntityTable entityTable, EntityField field, EntityColumn entityColumn) {
        String orderBy = "";
        if(field.isAnnotationPresent(OrderBy.class)){
            orderBy = field.getAnnotation(OrderBy.class).value();
            if ("".equals(orderBy)) {
                orderBy = "ASC";
            }
            log.warn(OrderBy.class + " is outdated, use " + Order.class + " instead!");
        }
        if (field.isAnnotationPresent(Order.class)) {
            Order order = field.getAnnotation(Order.class);
            if ("".equals(order.value()) && "".equals(orderBy)) {
                orderBy = "ASC";
            } else {
                orderBy = order.value();
            }
            entityColumn.setOrderPriority(order.priority());
        }
        if (StringUtil.isNotEmpty(orderBy)) {
            entityColumn.setOrderBy(orderBy);
        }
    }

    /**
     * 기본 키 전략 처리
     *
     * @param entityTable
     * @param field
     * @param entityColumn
     */
    protected void processKeyGenerator(EntityTable entityTable, EntityField field, EntityColumn entityColumn) {
        //KeySql이 가장 높은 우선 순위를 가짐
        if (field.isAnnotationPresent(KeySql.class)) {
            processKeySql(entityTable, entityColumn, field.getAnnotation(KeySql.class));
        } else if (field.isAnnotationPresent(GeneratedValue.class)) {
            //수행 sql - selectKey
            processGeneratedValue(entityTable, entityColumn, field.getAnnotation(GeneratedValue.class));
        }
    }

    /**
     * GeneratedValue 주석 처리
     *
     * @param entityTable
     * @param entityColumn
     * @param generatedValue
     */
    protected void processGeneratedValue(EntityTable entityTable, EntityColumn entityColumn, GeneratedValue generatedValue) {
        if ("JDBC".equals(generatedValue.generator())) {
            entityColumn.setIdentity(true);
            entityColumn.setGenerator("JDBC");
            entityTable.setKeyProperties(entityColumn.getProperty());
            entityTable.setKeyColumns(entityColumn.getColumn());
        } else {
            //생성기가 ID를 가져 오는 SQL을 설정할 수 있도록합니다. 예를 들면mysql=CALL IDENTITY(),hsqldb=SELECT SCOPE_IDENTITY()
            //인터셉터 매개 변수를 통한 공용 생성기 설정 허용
            if (generatedValue.strategy() == GenerationType.IDENTITY) {
                //mysql의 자동 성장
                entityColumn.setIdentity(true);
                if (!"".equals(generatedValue.generator())) {
                    String generator = null;
                    IdentityDialect identityDialect = IdentityDialect.getDatabaseDialect(generatedValue.generator());
                    if (identityDialect != null) {
                        generator = identityDialect.getIdentityRetrievalStatement();
                    } else {
                        generator = generatedValue.generator();
                    }
                    entityColumn.setGenerator(generator);
                }
            } else {
                throw new MapperException(entityColumn.getProperty()
                        + " - 이 필드 @GeneratedValue 구성은 다음 양식 만 허용합니다.:" +
                        "\n1.useGeneratedKeys의@GeneratedValue(generator=\\\"JDBC\\\")  " +
                        "\n2.수업mysql 데이터베이스처럼@GeneratedValue(strategy=GenerationType.IDENTITY[,generator=\"Mysql\"])");
            }
        }
    }

    /**
     * KeySql 주석 처리
     *
     * @param entityTable
     * @param entityColumn
     * @param keySql
     */
    protected void processKeySql(EntityTable entityTable, EntityColumn entityColumn, KeySql keySql) {
        if (keySql.useGeneratedKeys()) {
            entityColumn.setIdentity(true);
            entityColumn.setGenerator("JDBC");
            entityTable.setKeyProperties(entityColumn.getProperty());
            entityTable.setKeyColumns(entityColumn.getColumn());
        } else if (keySql.dialect() == IdentityDialect.DEFAULT) {
            entityColumn.setIdentity(true);
            entityColumn.setOrder(ORDER.AFTER);
        }  else if (keySql.dialect() != IdentityDialect.NULL) {
            //자동 성장
            entityColumn.setIdentity(true);
            entityColumn.setOrder(ORDER.AFTER);
            entityColumn.setGenerator(keySql.dialect().getIdentityRetrievalStatement());
        } else if (StringUtil.isNotEmpty(keySql.sql())){

            entityColumn.setIdentity(true);
            entityColumn.setOrder(keySql.order());
            entityColumn.setGenerator(keySql.sql());
        } else if (keySql.genSql() != GenSql.NULL.class){
            entityColumn.setIdentity(true);
            entityColumn.setOrder(keySql.order());
            try {
                GenSql genSql = keySql.genSql().newInstance();
                entityColumn.setGenerator(genSql.genSql(entityTable, entityColumn));
            } catch (Exception e) {
                log.error("GenSql을 인스턴스화하지 못했습니다.: " + e, e);
                throw new MapperException("GenSql을 인스턴스화하지 못했습니다.: " + e, e);
            }
        } else if(keySql.genId() != GenId.NULL.class){
            entityColumn.setIdentity(false);
            entityColumn.setGenIdClass(keySql.genId());
        } else {
            throw new MapperException(entityTable.getEntityClass().getCanonicalName()
                    + " 클래스의 @KeySql 주석 구성이 잘못되었습니다!");
        }
    }

}
