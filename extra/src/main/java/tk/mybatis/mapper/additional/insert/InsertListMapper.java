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

package tk.mybatis.mapper.additional.insert;

import org.apache.ibatis.annotations.InsertProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * 일반 Mapper 인터페이스, 특수 메소드, 배치 Insert, 배치 Insert을 지원하는 데이터베이스 (예 : mysql, h2 등)를 사용할 수 있습니다.
 *
 * @param <T> 필수
 * @author liuzh
 * @since 3.5.0
 */
@RegisterMapper
public interface InsertListMapper<T> {

    /**
     * 대량 Insert, MySQL, H2 등과 같이 대량 Insert을 지원하는 데이터베이스를 사용할 수 있습니다.
     * <p>
     * 기본 키 전략을 지원하지 않습니다. Insert하기 전에 기본 키의 값을 설정해야합니다.
     * <p>
     * 주의：2018-04-22 나중에이 메서드는 @KeySql 주석이 달린 genId 메서드를 지원합니다.
     *
     * @param recordList
     * @return
     */
    @InsertProvider(type = InsertListProvider.class, method = "dynamicSQL")
    int insertList(List<? extends T> recordList);
}