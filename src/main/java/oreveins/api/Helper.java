package oreveins.api;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class Helper {

    /**
     * Gets an int value from an ore config with default value
     *
     * @param config       The ore config object
     * @param key          The key to check
     * @param defaultValue If not found, the default value
     * @return the value
     */
    public static int getValue(Config config, String key, int defaultValue) {
        int result;
        try {
            result = config.getInt(key);
        } catch (ConfigException e) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * Gets a boolean value from a ore config with default = true
     *
     * @param config the ore config object
     * @param key    the key to check
     * @return the value
     */
    public static boolean getBoolean(Config config, String key) {
        boolean result;
        try {
            result = config.getBoolean(key);
        } catch (ConfigException e) {
            result = true;
        }
        return result;
    }
}
