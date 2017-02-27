/*
 * Copyright (c) 2017. Sunghyouk Bae <sunghyouk.bae@gmail.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hibernate.stresser;

import com.google.common.util.concurrent.RateLimiter;
import org.hibernate.stresser.persistence.dao.PlayerDao;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

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
  private static final int ENTITIES = 5000;
  private static final int CONCURRENCY = 10;

  public static void main(String[] args) throws InterruptedException {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Stresser.class);
    context.registerShutdownHook();

    try {
      final RateLimiter limiter = RateLimiter.create(5000); // 5k rps
      final PlayerDao playerDao = context.getBean(PlayerDao.class);

      playerDao.saveAll(ENTITIES);

      Thread[] threads = new Thread[CONCURRENCY];
      for (int i = 0; i < CONCURRENCY; i++) {
        threads[i] = new Thread() {
          @Override
          public void run() {
            for (int i = 0; i < ITERATIONS; i++) {
              limiter.acquire();
              ThreadLocalRandom random = ThreadLocalRandom.current();
              int playerId = random.nextInt(ENTITIES);
              if (random.nextDouble() < 0.7) {
                playerDao.update(playerId, i);
              } else {
                playerDao.get(playerId);
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
