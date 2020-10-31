package tk.mybatis.mapper.autoconfigure;

import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;

/**
 * 초기화가 완료된 후 클래스 정보 캐시를 지 웁니다.
 *
 * @author liuzh
 */
public class MapperCacheDisabler implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MapperCacheDisabler.class);

    @Override
    public void afterPropertiesSet() {
        disableCaching();
    }

    private void disableCaching() {
        try {
            //jar 패키지의 클래스는 모두 AppClassLoader에 의해로드되기 때문에 여기서 얻은 것은 다음과 같습니다. AppClassLoader
            ClassLoader appClassLoader = getClass().getClassLoader();
            removeStaticCache(ClassUtils.forName("tk.mybatis.mapper.util.MsUtil", appClassLoader), "CLASS_CACHE");
            removeStaticCache(ClassUtils.forName("tk.mybatis.mapper.genid.GenIdUtil", appClassLoader));
            removeStaticCache(ClassUtils.forName("tk.mybatis.mapper.version.VersionUtil", appClassLoader));

            removeEntityHelperCache(ClassUtils.forName("tk.mybatis.mapper.mapperhelper.EntityHelper", appClassLoader));
        } catch (Exception ex) {
        }
    }


    private void removeStaticCache(Class<?> utilClass) {
        removeStaticCache(utilClass, "CACHE");
    }

    private void removeStaticCache(Class<?> utilClass, String fieldName) {
        try {
            Field cacheField = ReflectionUtils.findField(utilClass, fieldName);
            if (cacheField != null) {
                ReflectionUtils.makeAccessible(cacheField);
                Object cache = ReflectionUtils.getField(cacheField, null);
                if (cache instanceof Map) {
                    ((Map) cache).clear();
                } else if (cache instanceof Cache) {
                    ((Cache) cache).clear();
                } else {
                    throw new UnsupportedOperationException("cache field must be a java.util.Map " +
                            "or org.apache.ibatis.cache.Cache instance");
                }
                logger.info("Clear " + utilClass.getCanonicalName() + " " + fieldName + " cache.");
            }
        } catch (Exception ex) {
            logger.warn("Failed to disable " + utilClass.getCanonicalName() + " "
                    + fieldName + " cache. ClassCastExceptions may occur", ex);
        }
    }

    private void removeEntityHelperCache(Class<?> entityHelper) {
        try {
            Field cacheField = ReflectionUtils.findField(entityHelper, "entityTableMap");
            if (cacheField != null) {
                ReflectionUtils.makeAccessible(cacheField);
                Map cache = (Map) ReflectionUtils.getField(cacheField, null);
                //Devtools를 사용하는 경우 여기에서 얻는 것은 현재 RestartClassLoader
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                for (Object key : new ArrayList(cache.keySet())) {
                    Class entityClass = (Class) key;
                    //테스트 환경의 오버플로를 방지하기 위해 이전 ClassLoader에 의해 캐시 된 데이터를 정리합니다.
                    if (!(entityClass.getClassLoader().equals(classLoader) || entityClass.getClassLoader().equals(classLoader.getParent()))) {
                        cache.remove(entityClass);
                    }
                }
                logger.info("Clear EntityHelper entityTableMap cache.");
            }
        } catch (Exception ex) {
            logger.warn("Failed to disable Mapper MsUtil cache. ClassCastExceptions may occur", ex);
        }
    }

}