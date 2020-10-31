package tk.mybatis.mapper.additional.aggregation;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * 일반 집계 조회 인터페이스, 특수 방법
 *
 * @author liuchan
 */
@tk.mybatis.mapper.annotation.RegisterMapper
public interface AggregationMapper<T> {

    /**
     * 예제 및 aggregateCondition을 기반으로하는 집계 조회
     * 그룹화는 조건 필터링을 지원하지 않습니다. 필요한 경우 xml 파일을 사용하는 것이 좋습니다.
     *
     * @param example
     * @param aggregateCondition 집계 조회의 속성 및 그룹화 속성을 설정할 수 있습니다.
     * @return 집계 조회 속성 및 그룹화 속성의 값을 반환합니다.
     */
    @SelectProvider(type = AggregationProvider.class, method = "dynamicSQL")
    List<T> selectAggregationByExample(@Param("example") Object example, @Param("aggregateCondition") AggregateCondition aggregateCondition);

}
