package com.scmt.generator.utils;

import org.apache.commons.configuration.*;

import java.io.InputStream;
import java.util.Collection;

/**
 * sql字段转java
 *
 * @author Zheng Jie
 * @date 2019-01-03
 */
public class ColUtil {

    /**
     * 转换mysql数据类型为java数据类型
     * @param type 数据库字段类型
     * @return String
     */
    static String cloToJava(String type){
        Configuration config = getConfig();
        assert config != null;
        return config.getString(type,"unknowType");
    }

    /**
     * 获取配置信息
     */
    public static PropertiesConfiguration getConfig() {
        try {
            String s = ColUtil.class.getClassLoader().getResource("generator.properties").toString();
            //InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("generator.properties");
            return new PropertiesConfiguration(s);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
