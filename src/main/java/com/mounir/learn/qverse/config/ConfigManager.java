package com.mounir.learn.qverse.config;

import org.aeonbits.owner.ConfigFactory;

/**
 * <b>Singleton pattern</b> - one immutable, thread-safe configuration instance per JVM.
 * Double-checked locking with a volatile field keeps first access cheap under parallel execution.
 */
public final class ConfigManager {

    private static volatile QVerseConfig instance;

    private ConfigManager() {
    }

    public static QVerseConfig config() {
        QVerseConfig local = instance;
        if (local == null) {
            synchronized (ConfigManager.class) {
                local = instance;
                if (local == null) {
                    if (System.getProperty("env") == null) {
                        System.setProperty("env", "qa");
                    }
                    local = ConfigFactory.create(QVerseConfig.class);
                    instance = local;
                }
            }
        }
        return local;
    }
}
