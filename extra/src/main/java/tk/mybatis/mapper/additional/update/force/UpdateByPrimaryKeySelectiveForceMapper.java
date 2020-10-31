package tk.mybatis.mapper.additional.update.force;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * @Description:  일반 매퍼 인터페이스, 비어 있지 않은 필드의 필수 업데이트
 * @author qrqhuangcy
 * @date 2018-06-26
 */
@RegisterMapper
public interface UpdateByPrimaryKeySelectiveForceMapper<T> {

    /**
     * null이 아닌 속성 값을 업데이트하는 기본 키에 따라 지정된 속성 (null 값)이 강제로 업데이트됩니다.
     * @param record
     * @param forceUpdateProperties
     * @return
     */
    @UpdateProvider(type = UpdateByPrimaryKeySelectiveForceProvider.class, method = "dynamicSQL")
    int updateByPrimaryKeySelectiveForce(@Param("record") T record, @Param("forceUpdateProperties") List<String> forceUpdateProperties);
}
