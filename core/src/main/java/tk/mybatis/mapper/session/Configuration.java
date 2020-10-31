package tk.mybatis.mapper.session;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

import java.util.Properties;

/**
 * 제공된 구성은 순수 Java 또는 Spring(mybatis-spring-1.3.0+) 모드에서 사용
 *
 * @author liuzh
 */
public class Configuration extends org.apache.ibatis.session.Configuration {

    private MapperHelper mapperHelper;

    /**
     * 직접 주입 mapperHelper
     *
     * @param mapperHelper
     */
    public void setMapperHelper(MapperHelper mapperHelper) {
        this.mapperHelper = mapperHelper;
    }

    /**
     * 속성 모드 구성 사용
     *
     * @param properties
     */
    public void setMapperProperties(Properties properties) {
        if (this.mapperHelper == null) {
            this.mapperHelper = new MapperHelper();
        }
        this.mapperHelper.setProperties(properties);
    }

    /**
     * Config를 사용하여 구성
     *
     * @param config
     */
    public void setConfig(Config config) {
        if (mapperHelper == null) {
            mapperHelper = new MapperHelper();
        }
        mapperHelper.setConfig(config);
    }

    @Override
    public void addMappedStatement(MappedStatement ms) {
        try {
            super.addMappedStatement(ms);
            //구성이없는 경우 기본 구성 사용
            if (this.mapperHelper == null) {
                this.mapperHelper = new MapperHelper();
            }
            this.mapperHelper.processMappedStatement(ms);
        } catch (IllegalArgumentException e) {
            //여기서 예외는 Spring이 무한 루프를 시작하게하는 핵심 위치입니다. 후속 삼키는 예외를 방지하려면 여기에 직접 출력하십시오.
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
