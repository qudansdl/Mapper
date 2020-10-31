package tk.mybatis.mapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 필드 정렬
 * @author: qrqhuangcy
 * @date: 2018-11-11
 **/
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {
    /**
     * 오름차순
     * @return
     */
    String value() default "ASC";

    /**
     * 우선 순위, 낮은 값 우선
     * @return
     */
    int priority() default 1;
}
