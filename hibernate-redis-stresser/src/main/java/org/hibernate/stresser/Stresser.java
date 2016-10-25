package org.hibernate.stresser;

import com.google.common.util.concurrent.RateLimiter;
import org.hibernate.stresser.persistence.dao.*;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * WARNING: Running this application will execute destructive commands on the database it connects to.
 *
 * @author Johno Crawford (johno@sulake.com)
 */
@Configuration
@ComponentScan(basePackages = "org.hibernate.stresser")
public class Stresser {

    private static final int ITERATIONS = 10000;
    private static final int CONCURRENCY = 10;

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Stresser.class);
        context.registerShutdownHook();

        try {
            final RateLimiter limiter = RateLimiter.create(5000); // 5k rps
            final PlayerDao playerDao = context.getBean(PlayerDao.class);

            Thread[] threads = new Thread[CONCURRENCY];
            for (int i = 0; i < CONCURRENCY; i++) {
                threads[i] = new Thread() {
                    @Override
                    public void run() {
                        for (int i = 0; i < ITERATIONS; i++) {
                            limiter.acquire();
                            ThreadLocalRandom random = ThreadLocalRandom.current();
                            int playerId = random.nextInt(10000);
                            Player player = playerDao.get(playerId);
                            if (player == null) {
                                player = new Player();
                                player.setName("player" + playerId);
                                playerDao.save(player);
                            } else if (random.nextDouble() < 0.7) {
                                player.setUpdateTime(new Date());
                                player.setCount(i);
                                playerDao.update(player);
                            }
                        }
                    }
                };
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
        } finally {
            context.close();
        }
    }
}
