package tk.mybatis.mapper.additional.select;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.weekend.Fn;

import java.util.List;

/**
 * 속성 기반 조회 인터페이스
 *
 * @param <T> 필수
 *
 * @author jingkaihui
 * @date 2019/10/11
 */
@RegisterMapper
public interface SelectByPropertyMapper<T> {

    /**
     * 속성 및 해당 값을 기반으로하는 조회입니다. 반환 값은 하나만있을 수 있습니다. 결과가 여러 개인 경우 예외가 발생합니다. 조회 조건은 등호를 사용합니다.
     *
     * @param fn 조회 속성
     * @param value    속성 값
     * @return
     */
    @SelectProvider(type = SelectPropertyProvider.class, method = "dynamicSQL")
    T selectOneByProperty(@Param("fn") Fn<T, ?> fn, @Param("value") Object value);

    /**
     * 속성 및 해당 값을 기반으로하는 조회, 여러 반환 값이 있으며 조회 조건은 등호를 사용합니다.
     *
     * @param fn 조회 속성
     * @param value 속성 값
     * @return
     */
    @SelectProvider(type = SelectPropertyProvider.class, method = "dynamicSQL")
    List<T> selectByProperty(@Param("fn") Fn<T, ?> fn, @Param("value") Object value);

    /**
     * 속성 및 해당 값을 기반으로하는 조회, 조회 조건에 사용
     *
     * @param fn 조회 속성
     * @param values 속성 값 컬렉션, 컬렉션은 필수.
     * @return
     */
    @SelectProvider(type = SelectPropertyProvider.class, method = "dynamicSQL")
    List<T> selectInByProperty(@Param("fn") Fn<T, ?> fn, @Param("values") List<?> values);

    /**
     * 속성 및 해당 값을 기반으로 조회하고 조회 조건 사용 between
     *
     * @param fn 조회 속성
     * @param begin 시작 값
     * @param end 시작 값
     * @return
     */
    @SelectProvider(type = SelectPropertyProvider.class, method = "dynamicSQL")
    List<T> selectBetweenByProperty(@Param("fn") Fn<T, ?> fn, @Param("begin") Object begin, @Param("end") Object end);

    /**
     * 속성 및 해당 값을 기반으로 조회하고 해당 레코드가 있는지 확인하고 조회 조건에 등호 사용
     *
     * @param fn 조회 속성
     * @param value 속성 값
     * @return
     */
    @SelectProvider(type = SelectPropertyProvider.class, method = "dynamicSQL")
    boolean existsWithProperty(@Param("fn") Fn<T, ?> fn, @Param("value") Object value);

    /**
     * 속성 및 해당 값을 기반으로 조회하고, 조건을 충족하는 레코드 수를 계산하고, 조회 조건에 등호를 사용합니다.
     *
     * @param fn 조회 속성
     * @param value    속성 값
     * @return
     */
    @SelectProvider(type = SelectPropertyProvider.class, method = "dynamicSQL")
    int selectCountByProperty(@Param("fn") Fn<T, ?> fn, @Param("value") Object value);
}
