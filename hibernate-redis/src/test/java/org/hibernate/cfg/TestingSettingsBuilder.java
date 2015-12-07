package org.hibernate.cfg;

import java.lang.reflect.Field;

/**
 * {@link org.hibernate.cfg.Settings} builder for testing
 * @author KwonNam Son (kwon37xi@gmail.com)
 */
public class TestingSettingsBuilder {

    private Settings settings;

    public TestingSettingsBuilder() {
        settings = new Settings();
    }

    public TestingSettingsBuilder setField(String fieldName, Object value) {
        try {
            Field field = Settings.class.getDeclaredField(fieldName);
            field.setAccessible(true);

            field.set(settings, value);

            field.setAccessible(false);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        return this;
    }

    public Settings build() {
        return settings;
    }
}
