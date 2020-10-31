package tk.mybatis.mapper.additional.aggregation;

import tk.mybatis.mapper.util.Assert;
import tk.mybatis.mapper.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 집계 조회 조건
 *
 * @author liuchan
 * @author liuzh
 */
public class AggregateCondition implements Serializable {
    private static final long          serialVersionUID = 1L;
    // 집계 속성
    private              String        aggregateProperty;
    private              String        aggregateAliasName;
    // groupBy 조회 열
    private              List<String>  groupByProperties;
    // 집계 함수
    private              AggregateType aggregateType;

    public AggregateCondition() {
        this(null, AggregateType.COUNT, null);
    }

    /**
     * 기본 조회 수, 그룹화 없음
     *
     * @param aggregateProperty 집계 조회 속성은 필수.반환 된 결과 키가 전달 된 값과 동일한 지 확인하기 위해 메서드는 선행 및 후행 공백을 제거하지 않습니다.
     */
    public AggregateCondition(String aggregateProperty) {
        this(aggregateProperty, AggregateType.COUNT, null);
    }

    /**
     * 기본 조회 수
     *
     * @param aggregateProperty 집계 조회 속성은 필수.반환 된 결과 키가 전달 된 값과 동일한 지 확인하기 위해 메서드는 선행 및 후행 공백을 제거하지 않습니다.
     * @param groupByProperties 반환 된 결과 키가 전달 된 값과 동일한 지 확인하기 위해 메서드는 각 항목 앞뒤의 공백을 제거하지 않습니다.
     */
    public AggregateCondition(String aggregateProperty, String[] groupByProperties) {
        this(aggregateProperty, AggregateType.COUNT, groupByProperties);
    }

    /**
     * 그룹화없이 지정된 집계 방법으로 조회
     *
     * @param aggregateProperty
     * @param aggregateType
     */
    public AggregateCondition(String aggregateProperty, AggregateType aggregateType) {
        this(aggregateProperty, aggregateType, null);
    }

    /**
     * @param aggregateProperty는 필수. 반환 된 결과 키가 전달 된 값과 동일한 지 확인하기 위해 메서드는 선행 및 후행 공백을 제거하지 않습니다.
     * @param aggregateType
     * @param groupByProperties 반환 된 결과 키가 전달 된 값과 동일한 지 확인하기 위해 메서드는 각 항목 앞뒤의 공백을 제거하지 않습니다.
     */
    public AggregateCondition(String aggregateProperty, AggregateType aggregateType,
                              String[] groupByProperties) {
        this.groupByProperties = new ArrayList<String>();
        // propertyMap 초기화가 완료된 후 실행해야합니다.
        aggregateType(aggregateType);
        if (StringUtil.isNotEmpty(aggregateProperty)) {
            aggregateBy(aggregateProperty);
        }
        groupBy(groupByProperties);
    }

    public static AggregateCondition builder() {
        return new AggregateCondition();
    }

    public AggregateCondition groupBy(String... groupByProperties) {
        if (groupByProperties != null && groupByProperties.length > 0) {
            this.groupByProperties.addAll(Arrays.asList(groupByProperties));
        }
        return this;
    }

    public AggregateCondition aggregateBy(String aggregateProperty) {
        Assert.notEmpty(aggregateProperty,
                "aggregateProperty must have length; it must not be null or empty");
        this.aggregateProperty = aggregateProperty;
        return this;
    }

    public AggregateCondition aliasName(String aggregateAliasName) {
        this.aggregateAliasName = aggregateAliasName;
        return this;
    }

    public AggregateCondition aggregateType(AggregateType aggregateType) {
        Assert.notNull(aggregateType,
                "aggregateType is required; it must not be null");
        this.aggregateType = aggregateType;
        return this;
    }

    public String getAggregateProperty() {
        return aggregateProperty;
    }

    public String getAggregateAliasName() {
        return aggregateAliasName;
    }

    public List<String> getGroupByProperties() {
        return groupByProperties;
    }

    public AggregateType getAggregateType() {
        return aggregateType;
    }
}
