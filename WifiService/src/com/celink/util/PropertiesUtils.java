package com.celink.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: lifaqiu
 * Date: 14-3-6
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
public class PropertiesUtils {
    private static Map<String, Properties> data = new HashMap<String, Properties>();
    private String propertyFile;
    private PropertiesUtils(){}
    public static PropertiesUtils getInstance(String propertyFile){
        PropertiesUtils instance = new PropertiesUtils();
        instance.propertyFile = propertyFile;
        tryToFillData(propertyFile);
        return instance;
    }

    private static void tryToFillData(String propertyFile){
        Properties properties = data.get(propertyFile);
        try {
            if (properties == null) {
                Resource resource = new ClassPathResource(propertyFile+".properties");
                properties = PropertiesLoaderUtils.loadProperties(resource);
                data.put(propertyFile, properties);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getValue(String key){
        return data.get(propertyFile).getProperty(key);
    }

    public String getValue(String key, Object... args){
        String value = data.get(propertyFile).getProperty(key);
        return MessageFormat.format(value, args);
    }
}
