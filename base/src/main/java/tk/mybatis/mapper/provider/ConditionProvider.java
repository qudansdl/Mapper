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

package tk.mybatis.mapper.provider;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;

/**
 * ConditionProvider구현 클래스, 기본 메소드 구현 클래스
 *
 * @author liuzh
 */
public class ConditionProvider extends MapperTemplate {

    private ExampleProvider exampleProvider;

    public ConditionProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
        exampleProvider = new ExampleProvider(mapperClass, mapperHelper);
    }

    /**
     * 조건에 따라 총 수 조회
     *
     * @param ms
     * @return
     */
    public String selectCountByCondition(MappedStatement ms) {
        return exampleProvider.selectCountByExample(ms);
    }

    /**
     * 조건에 따라 삭제
     *
     * @param ms
     * @return
     */
    public String deleteByCondition(MappedStatement ms) {
        return exampleProvider.deleteByExample(ms);
    }


    /**
     * 조건에 따른 조회
     *
     * @param ms
     * @return
     */
    public String selectByCondition(MappedStatement ms) {
        return exampleProvider.selectByExample(ms);
    }

    /**
     * 조건에 따른 조회
     *
     * @param ms
     * @return
     */
    public String selectByConditionAndRowBounds(MappedStatement ms) {
        return exampleProvider.selectByExample(ms);
    }

    /**
     * 예제에 따라 널이 아닌 필드 업데이트
     *
     * @param ms
     * @return
     */
    public String updateByConditionSelective(MappedStatement ms) {
        return exampleProvider.updateByExampleSelective(ms);
    }

    /**
     * 조건에 따라 업데이트
     *
     * @param ms
     * @return
     */
    public String updateByCondition(MappedStatement ms) {
        return exampleProvider.updateByExample(ms);
    }
}
