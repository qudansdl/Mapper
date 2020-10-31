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

package tk.mybatis.mapper.entity;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 필드와 메서드를 캡슐화하고 특정 메서드를 균일하게 호출
 *
 * @author liuzh
 */
public class EntityField {
    private String name;
    private Field field;
    private Class<?> javaType;
    private Method setter;
    private Method getter;

    /**
     * 시공 방법
     *
     * @param field              들
     * @param propertyDescriptor 필드 이름에 해당하는 속성
     */
    public EntityField(Field field, PropertyDescriptor propertyDescriptor) {
        if (field != null) {
            this.field = field;
            this.name = field.getName();
            this.javaType = field.getType();
        }
        if (propertyDescriptor != null) {
            this.name = propertyDescriptor.getName();
            this.setter = propertyDescriptor.getWriteMethod();
            this.getter = propertyDescriptor.getReadMethod();
            this.javaType = propertyDescriptor.getPropertyType();
        }
    }

    /**
     * 먼저 필드를 만든 다음이 메서드를 통해 속성 및 기타 속성을 가져올 수 있습니다.
     *
     * @param other
     */
    public void copyFromPropertyDescriptor(EntityField other) {
        this.setter = other.setter;
        this.getter = other.getter;
        this.javaType = other.javaType;
        this.name = other.name;
    }

    /**
     * 그런 주석이 있습니까
     *
     * @param annotationClass
     * @return
     */
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        boolean result = false;
        if (field != null) {
            result = field.isAnnotationPresent(annotationClass);
        }
        if (!result && setter != null) {
            result = setter.isAnnotationPresent(annotationClass);
        }
        if (!result && getter != null) {
            result = getter.isAnnotationPresent(annotationClass);
        }
        return result;
    }

    /**
     * 지정된 주석 가져 오기
     *
     * @param annotationClass
     * @param <T>
     * @return
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        T result = null;
        if (field != null) {
            result = field.getAnnotation(annotationClass);
        }
        if (result == null && setter != null) {
            result = setter.getAnnotation(annotationClass);
        }
        if (result == null && getter != null) {
            result = getter.getAnnotation(annotationClass);
        }
        return result;
    }

    /**
     * 가치를 얻기위한 반성
     *
     * @param object
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public Object getValue(Object object) throws IllegalAccessException, InvocationTargetException {
        Object result = null;
        if (getter != null) {
            result = getter.invoke(object);
        } else if (field != null) {
            if(!field.isAccessible()){
                field.setAccessible(true);
            }
            result = field.get(object);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityField that = (EntityField) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    /**
     * javaType 가져 오기
     *
     * @return
     */
    public Class<?> getJavaType() {
        return javaType;
    }

    /**
     * javaType 설정
     *
     * @param javaType
     */
    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    /**
     * 필드 속성 이름
     *
     * @return
     */
    public String getName() {
        return name;
    }
}
