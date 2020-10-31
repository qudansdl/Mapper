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

package tk.mybatis.mapper.annotation;

import tk.mybatis.mapper.code.IdentityDialect;
import tk.mybatis.mapper.code.ORDER;
import tk.mybatis.mapper.genid.GenId;
import tk.mybatis.mapper.gensql.GenSql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JPA의 복잡한 사용을 대체하는 데 사용되는 기본 키 전략
 *
 * @author liuzh
 * @since 2015-10-29 22:00
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface KeySql {

    /**
     * JDBC 메소드를 사용하여 기본 키를 가져올 지 여부, 우선 순위가 가장 높으며 true로 설정되면 다른 구성이 확인되지 않습니다.
     *
     * @return
     */
    boolean useGeneratedKeys() default false;

    /**
     * 두 번째 우선 순위는 구성된 데이터베이스 유형에 따라 기본 키를 검색하고 다른 구성을 무시하는 것입니다.
     *
     * @return
     */
    IdentityDialect dialect() default IdentityDialect.NULL;

    /**
     * 기본 키를 가져 오는 SQL
     *
     * @return
     */
    String sql() default "";

    /**
     * SQL 생성, 초기화 중 실행, 우선 순위가 다음보다 낮음 sql
     *
     * @return
     */
    Class<? extends GenSql> genSql() default GenSql.NULL.class;

    /**
     * SQL과 함께 사용할 수 있으며 전역 구성의 ORDER가 기본적으로 사용됩니다.
     *
     * @return
     */
    ORDER order() default ORDER.DEFAULT;

    /**
     * Java 발급 기관과 같은 서비스에서 사용할 수있는 기본 키 생성 방법
     *
     * @return
     */
    Class<? extends GenId> genId() default GenId.NULL.class;

}
