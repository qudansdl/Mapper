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

package tk.mybatis.mapper.util;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.MapperException;

/**
 * @author liuzh
 * @since 2017/7/9.
 */
public class MsUtil {

    public static final Cache CLASS_CACHE = new SoftCache(new PerpetualCache("MAPPER_CLASS_CACHE"));

    /**
     * msId에 따라 인터페이스 클래스 가져 오기
     *
     * @param msId
     * @return
     */
    public static Class<?> getMapperClass(String msId) {
        if (msId.indexOf(".") == -1) {
            throw new MapperException("当前MappedStatement의id=" + msId + ", MappedStatement의 규칙을 충족하지 않습니다!");
        }
        String mapperClassStr = msId.substring(0, msId.lastIndexOf("."));
        //인터페이스의 각 메서드는 다음 작업을 수행하므로 캐시는
        Class<?> mapperClass = (Class<?>) CLASS_CACHE.getObject(mapperClassStr);
        if(mapperClass != null){
            return mapperClass;
        }
        ClassLoader[] classLoader = getClassLoaders();

        for (ClassLoader cl : classLoader) {
            if (null != cl) {
                try {
                    mapperClass = Class.forName(mapperClassStr, true, cl);
                    if (mapperClass != null) {
                        break;
                    }
                } catch (ClassNotFoundException e) {
                    // we'll ignore this until all class loaders fail to locate the class
                }
            }
        }
        if (mapperClass == null) {
            throw new MapperException("class loaders failed to locate the class " + mapperClassStr);
        }
        CLASS_CACHE.putObject(mapperClassStr, mapperClass);
        return mapperClass;
    }

    private static ClassLoader[] getClassLoaders() {
        return new ClassLoader[]{Thread.currentThread().getContextClassLoader(), MsUtil.class.getClassLoader()};
    }

    /**
     * 실행 된 메서드의 이름을 가져옵니다.
     *
     * @param ms
     * @return
     */
    public static String getMethodName(MappedStatement ms) {
        return getMethodName(ms.getId());
    }

    /**
     * 실행 된 메서드의 이름을 가져옵니다.
     *
     * @param msId
     * @return
     */
    public static String getMethodName(String msId) {
        return msId.substring(msId.lastIndexOf(".") + 1);
    }

}
