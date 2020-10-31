package tk.mybatis.mapper.additional.update.differ;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

/**
 * 차이 업데이트
 *
 * @param <T> 필수
 * @author liuzh
 * @since 4.0.4
 */
@RegisterMapper
public interface UpdateByDifferMapper<T> {

    /**
     * 이전과 최신에 따라 차이를 업데이트하고 해당 필드 값이 다른 경우에만 업데이트
     *
     * @param old
     * @param newer
     * @return
     */
    @UpdateProvider(type = UpdateByDifferProvider.class, method = "dynamicSQL")
    int updateByDiffer(@Param("old") T old, @Param("newer") T newer);
}
