/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 abel533@gmail.com
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

package tk.mybatis.spring.mapper;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author liuzh
 * @since 1.2.1
 */
public abstract class SpringBootBindUtil {

    public static final IBind BIND;

    static {
        IBind bind;
        try {
            //boot2
            Class.forName("org.springframework.boot.context.properties.bind.Binder");
            bind = new SpringBoot2Bind();
        } catch (Exception e) {
            //boot1
            bind = new SpringBoot1Bind();
        }
        BIND = bind;
    }

    public static <T> T bind(Environment environment, Class<T> targetClass, String prefix) {
        return BIND.bind(environment, targetClass, prefix);
    }

    public interface IBind {
        <T> T bind(Environment environment, Class<T> targetClass, String prefix);
    }

    /**
     * Spring Boot 1.x를 사용하여 바인딩
     */
    public static class SpringBoot1Bind implements IBind {
        @Override
        public <T> T bind(Environment environment, Class<T> targetClass, String prefix) {
            /**
             코드를 변경하지 않고 Spring Boot 2.x에 대한 향후 직접적인 의존성을 용이하게하기 위해 여기서 리플렉션도 사용됩니다.
             try {
             RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(environment);
             Map<String, Object> properties = resolver.getSubProperties("");
             T target = targetClass.newInstance();
             RelaxedDataBinder binder = new RelaxedDataBinder(target, prefix);
             binder.bind(new MutablePropertyValues(properties));
             return target;
             } catch (Exception e) {
             throw new RuntimeException(e);
             }
             다음은이 코드의 반영 구현입니다.
             */
            try {
                //구성 정보 추출 반영
                Class<?> resolverClass = Class.forName("org.springframework.boot.bind.RelaxedPropertyResolver");
                Constructor<?> resolverConstructor = resolverClass.getDeclaredConstructor(PropertyResolver.class);
                Method getSubPropertiesMethod = resolverClass.getDeclaredMethod("getSubProperties", String.class);
                Object resolver = resolverConstructor.newInstance(environment);
                Map<String, Object> properties = (Map<String, Object>) getSubPropertiesMethod.invoke(resolver, "");
                //결과 클래스 만들기
                T target = targetClass.newInstance();
                //반사 사용 org.springframework.boot.bind.RelaxedDataBinder
                Class<?> binderClass = Class.forName("org.springframework.boot.bind.RelaxedDataBinder");
                Constructor<?> binderConstructor = binderClass.getDeclaredConstructor(Object.class, String.class);
                Method bindMethod = binderClass.getMethod("bind", PropertyValues.class);
                //바인더 생성 및 데이터 바인딩
                Object binder = binderConstructor.newInstance(target, prefix);
                bindMethod.invoke(binder, new MutablePropertyValues(properties));
                return target;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Spring Boot 2.x를 사용하여 바인딩
     */
    public static class SpringBoot2Bind implements IBind {
        @Override
        public <T> T bind(Environment environment, Class<T> targetClass, String prefix) {
            /**
             동시에 두 개의 다른 버전에 의존 할 수 없으므로 리플렉션을 사용하여 다음 코드를 구현하십시오.
             Binder binder = Binder.get(environment);
             return binder.bind(prefix, targetClass).get();
             아래는이 두 줄의 코드를 완전히 반영한 버전입니다.
             */
            try {
                Class<?> bindClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
                Method getMethod = bindClass.getDeclaredMethod("get", Environment.class);
                Method bindMethod = bindClass.getDeclaredMethod("bind", String.class, Class.class);
                Object bind = getMethod.invoke(null, environment);
                Object bindResult = bindMethod.invoke(bind, prefix, targetClass);
                Method resultGetMethod = bindResult.getClass().getDeclaredMethod("get");
                Method isBoundMethod = bindResult.getClass().getDeclaredMethod("isBound");
                if ((Boolean) isBoundMethod.invoke(bindResult)) {
                    return (T) resultGetMethod.invoke(bindResult);
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
