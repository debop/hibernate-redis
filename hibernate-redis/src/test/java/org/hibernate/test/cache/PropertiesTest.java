package org.hibernate.test.cache;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.test.PropertiesTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 16. 오후 3:38
 */
@Slf4j
public class PropertiesTest {

    @Test
    public void loadProperties() {
        Properties cacheProps = new Properties();
        String cachePath = "hibernate-redis.properties";
        try {
            log.info("Loading cache properties... path=[{}]", cachePath);

            // NOTE: getClass().getResourceAsStream() 과 getClassLoader().getResourceAsStream() 의 경로 해석이 다르다.
            // NOTE: getClass() 는 "/" 를 넣어야 하고, getClassLoader() 는 "/" 이 없어야 한다.
            cachePath = "/hibernate-redis.properties";
            InputStream is1 = getClass().getResourceAsStream(cachePath);
            assertThat(is1).isNotNull();
            cacheProps.load(is1);

            System.out.println("properties... " + cacheProps.toString());

            cachePath = "hibernate-redis.properties";
            InputStream is2 = PropertiesTest.class.getClassLoader().getResourceAsStream(cachePath);
            assertThat(is2).isNotNull();
            cacheProps.load(is2);

            System.out.println("properties... " + cacheProps.toString());

        } catch (Exception e) {
            log.warn("Cache용 환경설정 정보를 로드하는데 실패했습니다. cachePath=" + cachePath, e);
        }
    }
}
