package com.almashines.qa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigManager reads settings from the external config.properties configuration files.
 */
public class ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static Properties properties;

    static {
        properties = new Properties();
        String path = "./src/test/resources/config.properties";
        try (FileInputStream inputStream = new FileInputStream(path)) {
            properties.load(inputStream);
            logger.info("Loaded configuration properties from path: {}", path);
        } catch (IOException e) {
            logger.error("Failed to load configuration properties at: {}", path, e);
            throw new RuntimeException("Could not initialize configuration properties.", e);
        }
    }

    /**
     * Gets a property string by its key.
     * @param key Config key name.
     * @return String property value.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Gets a property string with a fallback default.
     * @param key Config key name.
     * @param defaultValue Fallback value if key is not found.
     * @return String property value.
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Gets an integer configuration value.
     * @param key Config key name.
     * @param defaultValue Default integer value.
     * @return int config value.
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Key '{}' has invalid integer format: '{}'. Defaulting to {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Gets a boolean configuration value.
     * @param key Config key name.
     * @param defaultValue Default boolean value.
     * @return boolean config value.
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }
}
