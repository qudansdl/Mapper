package tk.mybatis.mapper.additional.delete;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.weekend.Fn;

import java.util.List;

/**
 * @param <T> 필수
 * @author jingkaihui
 * @date 2020/3/30
 */
@RegisterMapper
public interface DeleteByPropertyMapper<T> {

    /**
     * 엔티티의 속성에 따라 삭제하고 조건에 등호를 사용하십시오.
     *
     * @param fn 속성
     * @param value    속성 값
     * @return
     */
    @DeleteProvider(type = DeletePropertyProvider.class, method = "dynamicSQL")
    int deleteByProperty(@Param("fn") Fn<T, ?> fn, @Param("value") Object value);

    /**
     * 엔티티의 속성에 따라 삭제, 조건에서 사용
     *
     * @param fn 속성
     * @param value    속성 값
     * @return
     */
    @DeleteProvider(type = DeletePropertyProvider.class, method = "dynamicSQL")
    int deleteInByProperty(@Param("fn") Fn<T, ?> fn, @Param("values") Object value);

    /**
     * 속성 및 해당 값에 따라 삭제, 삭제 조건 사용 between
     *
     * @param fn 속성
     * @param begin 시작 값
     * @param end 시작 값
     * @return
     */
    @SelectProvider(type = DeletePropertyProvider.class, method = "dynamicSQL")
    int deleteBetweenByProperty(@Param("fn") Fn<T, ?> fn, @Param("begin") Object begin, @Param("end") Object end);
}
