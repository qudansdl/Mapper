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

package tk.mybatis.mapper.mapperhelper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.resolve.EntityResolve;
import tk.mybatis.mapper.provider.EmptyProvider;
import tk.mybatis.mapper.util.StringUtil;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static tk.mybatis.mapper.util.MsUtil.getMapperClass;

/**
 * 가장 중요한 클래스 인 메인 로직 처리
 * <p/>
 * <p> 프로젝트 주소 : <a href="https://github.com/abel533/Mapper" target="_blank">https://github.com/abel533/Mapper</a></p>
 *
 * @author liuzh
 */
public class MapperHelper {

    private static final Log log = LogFactory.getLog(MapperHelper.class);

    /**
     * 등록 된 인터페이스
     */
    private List<Class<?>> registerClass = new ArrayList<Class<?>>();

    /**
     * 등록 된 일반 매퍼 인터페이스
     */
    private Map<Class<?>, MapperTemplate> registerMapper = new ConcurrentHashMap<Class<?>, MapperTemplate>();

    /**
     * 일반 매퍼 구성
     */
    private Config config = new Config();

    /**
     * 기본 생성자
     */
    public MapperHelper() {
    }

    /**
     * 구성이있는 시공 방법
     *
     * @param properties
     */
    public MapperHelper(Properties properties) {
        this();
        setProperties(properties);
    }

    /**
     * 일반 Mapper 인터페이스를 통해 해당 MapperTemplate 얻기
     *
     * @param mapperClass
     * @return
     * @throws Exception
     */
    private MapperTemplate fromMapperClass(Class<?> mapperClass) {
        Method[] methods = mapperClass.getDeclaredMethods();
        Class<?> templateClass = null;
        Class<?> tempClass = null;
        Set<String> methodSet = new HashSet<String>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(SelectProvider.class)) {
                SelectProvider provider = method.getAnnotation(SelectProvider.class);
                tempClass = provider.type();
                methodSet.add(method.getName());
            } else if (method.isAnnotationPresent(InsertProvider.class)) {
                InsertProvider provider = method.getAnnotation(InsertProvider.class);
                tempClass = provider.type();
                methodSet.add(method.getName());
            } else if (method.isAnnotationPresent(DeleteProvider.class)) {
                DeleteProvider provider = method.getAnnotation(DeleteProvider.class);
                tempClass = provider.type();
                methodSet.add(method.getName());
            } else if (method.isAnnotationPresent(UpdateProvider.class)) {
                UpdateProvider provider = method.getAnnotation(UpdateProvider.class);
                tempClass = provider.type();
                methodSet.add(method.getName());
            }
            if (templateClass == null) {
                templateClass = tempClass;
            } else if (templateClass != tempClass) {
                log.error("一个만능인Mapper中只允许存在一个MapperTemplate子수업!");
                throw new MapperException("一个만능인Mapper中只允许存在一个MapperTemplate子수업!");
            }
        }
        if (templateClass == null || !MapperTemplate.class.isAssignableFrom(templateClass)) {
            templateClass = EmptyProvider.class;
        }
        MapperTemplate mapperTemplate = null;
        try {
            mapperTemplate = (MapperTemplate) templateClass.getConstructor(Class.class, MapperHelper.class).newInstance(mapperClass, this);
        } catch (Exception e) {
            log.error("实例化MapperTemplate对象失败:" + e, e);
            throw new MapperException("实例化MapperTemplate对象失败:" + e.getMessage());
        }
        //등록 방법
        for (String methodName : methodSet) {
            try {
                mapperTemplate.addMethodMap(methodName, templateClass.getMethod(methodName, MappedStatement.class));
            } catch (NoSuchMethodException e) {
                log.error(templateClass.getCanonicalName() + "잃어버린" + methodName + "방법!", e);
                throw new MapperException(templateClass.getCanonicalName() + "잃어버린" + methodName + "방법!");
            }
        }
        return mapperTemplate;
    }

    /**
     * 공통 매퍼 인터페이스 등록
     *
     * @param mapperClass
     */
    public void registerMapper(Class<?> mapperClass) {
        if (!registerMapper.containsKey(mapperClass)) {
            registerClass.add(mapperClass);
            registerMapper.put(mapperClass, fromMapperClass(mapperClass));
        }
        //상속 된 인터페이스의 자동 등록
        Class<?>[] interfaces = mapperClass.getInterfaces();
        if (interfaces != null && interfaces.length > 0) {
            for (Class<?> anInterface : interfaces) {
                registerMapper(anInterface);
            }
        }
    }

    /**
     * 공통 매퍼 인터페이스 등록
     *
     * @param mapperClass
     */
    public void registerMapper(String mapperClass) {
        try {
            registerMapper(Class.forName(mapperClass));
        } catch (ClassNotFoundException e) {
            log.error("일반 등록Mapper[" + mapperClass + "]失败，找不到该만능인Mapper!", e);
            throw new MapperException("일반 등록Mapper[" + mapperClass + "]失败，找不到该만능인Mapper!");
        }
    }

    /**
     * 현재 인터페이스 메서드를 가로 채야하는지 여부 확인
     *
     * @param msId
     * @return
     */
    public MapperTemplate isMapperMethod(String msId) {
        MapperTemplate mapperTemplate = getMapperTemplateByMsId(msId);
        if(mapperTemplate == null){
            //@RegisterMapper 주석을 통한 자동 등록 기능
            try {
                Class<?> mapperClass = getMapperClass(msId);
                if(mapperClass.isInterface() && hasRegisterMapper(mapperClass)){
                    mapperTemplate = getMapperTemplateByMsId(msId);
                }
            } catch (Exception e){
                log.warn("특별한 상황: " + e);
            }
        }
        return mapperTemplate;
    }

    /**
     * msId를 기반으로 MapperTemplate 가져 오기
     *
     * @param msId
     * @return
     */
    public MapperTemplate getMapperTemplateByMsId(String msId){
        for (Map.Entry<Class<?>, MapperTemplate> entry : registerMapper.entrySet()) {
            if (entry.getValue().supportMethod(msId)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 인터페이스에 일반 인터페이스가 포함되어 있는지 확인합니다.
     *
     * @param mapperInterface
     * @return
     */
    public boolean isExtendCommonMapper(Class<?> mapperInterface) {
        for (Class<?> mapperClass : registerClass) {
            if (mapperClass.isAssignableFrom(mapperInterface)) {
                return true;
            }
        }
        //@RegisterMapper 주석을 통한 자동 등록 기능
        return hasRegisterMapper(mapperInterface);
    }

    /**
     * @RegisterMapper 주석을 통해 자동 등록 기능 추가
     *
     * @param mapperInterface
     * @return
     */
    private boolean hasRegisterMapper(Class<?> mapperInterface){
        //일치하는 항목이 없으면 매퍼가 아직 등록되지 않았을 가능성이 있습니다. 이때 @RegisterMapper 주석을 사용하여
        Class<?>[] interfaces = mapperInterface.getInterfaces();
        boolean hasRegisterMapper = false;
        if (interfaces != null && interfaces.length > 0) {
            for (Class<?> anInterface : interfaces) {
                //@RegisterMapper로 표시된 인터페이스 자동 등록
                if(anInterface.isAnnotationPresent(RegisterMapper.class)){
                    hasRegisterMapper = true;
                    //이미 등록한 경우 다음 반복 메서드를 반복적으로 호출하지 마십시오.
                    if (!registerMapper.containsKey(anInterface)) {
                        registerMapper(anInterface);
                    }
                }
                //상위 인터페이스의 상위 인터페이스에 주석이있는 경우 등록 할 수도 있습니다.
                else if(hasRegisterMapper(anInterface)){
                    hasRegisterMapper = true;
                }
            }
        }
        return hasRegisterMapper;
    }

    /**
     * 구성이 완료된 후 다음 작업을 수행하십시오.
     * <br>구성에서 모두 처리MappedStatement
     *
     * @param configuration
     */
    public void processConfiguration(Configuration configuration) {
        processConfiguration(configuration, null);
    }

    /**
     * 지정된 인터페이스 구성
     *
     * @param configuration
     * @param mapperInterface
     */
    public void processConfiguration(Configuration configuration, Class<?> mapperInterface) {
        String prefix;
        if (mapperInterface != null) {
            prefix = mapperInterface.getCanonicalName();
        } else {
            prefix = "";
        }
        for (Object object : new ArrayList<Object>(configuration.getMappedStatements())) {
            if (object instanceof MappedStatement) {
                MappedStatement ms = (MappedStatement) object;
                if (ms.getId().startsWith(prefix)) {
                    processMappedStatement(ms);
                }
            }
        }
    }

    /**
     * 다루다 MappedStatement
     *
     * @param ms
     */
    public void processMappedStatement(MappedStatement ms){
        MapperTemplate mapperTemplate = isMapperMethod(ms.getId());
        if(mapperTemplate != null && ms.getSqlSource() instanceof ProviderSqlSource) {
            setSqlSource(ms, mapperTemplate);
        }
    }

    /**
     * 일반 매퍼 구성 가져 오기
     *
     * @return
     */
    public Config getConfig() {
        return config;
    }

    /**
     * 일반 매퍼 구성 설정
     *
     * @param config
     */
    public void setConfig(Config config) {
        this.config = config;
        if(config.getResolveClass() != null){
            try {
                EntityHelper.setResolve(config.getResolveClass().newInstance());
            } catch (Exception e) {
                log.error("창조하다 " + config.getResolveClass().getCanonicalName()
                    + " 인스턴스가 실패했습니다. 클래스에 기본 생성자가 있는지 확인하십시오!", e);
                throw new MapperException("창조하다 " + config.getResolveClass().getCanonicalName()
                        + " 인스턴스가 실패했습니다. 클래스에 기본 생성자가 있는지 확인하십시오!", e);
            }
        }
        if(config.getMappers() != null && config.getMappers().size() > 0){
            for (Class mapperClass : config.getMappers()) {
                registerMapper(mapperClass);
            }
        }
    }

    /**
     * 구성 속성
     *
     * @param properties
     */
    public void setProperties(Properties properties) {
        config.setProperties(properties);
        //리졸버 등록
        if (properties != null) {
            String resolveClass = properties.getProperty("resolveClass");
            if (StringUtil.isNotEmpty(resolveClass)) {
                try {
                    EntityHelper.setResolve((EntityResolve) Class.forName(resolveClass).newInstance());
                } catch (Exception e) {
                    log.error("창조하다 " + resolveClass + " 인스턴스가 실패했습니다!", e);
                    throw new MapperException("창조하다 " + resolveClass + " 인스턴스가 실패했습니다!", e);
                }
            }
        }
        //공통 인터페이스 등록
        if (properties != null) {
            String mapper = properties.getProperty("mappers");
            if (StringUtil.isNotEmpty(mapper)) {
                String[] mappers = mapper.split(",");
                for (String mapperClass : mappers) {
                    if (mapperClass.length() > 0) {
                        registerMapper(mapperClass);
                    }
                }
            }
        }
    }

    /**
     * 초기화SqlSource
     * <p/>
     * 이 메서드를 실행하기 전에 isMapperMethod를 사용하여 판단해야합니다. 그렇지 않으면 msIdCache가 비어 있습니다.
     *
     * @param ms
     * @param mapperTemplate
     */
    public void setSqlSource(MappedStatement ms, MapperTemplate mapperTemplate) {
        try {
            if (mapperTemplate != null) {
                mapperTemplate.setSqlSource(ms);
            }
        } catch (Exception e) {
            throw new MapperException(e);
        }
    }

}