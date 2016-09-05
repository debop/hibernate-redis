/*
 * Copyright (c) 2016. Sunghyouk Bae <sunghyouk.bae@gmail.com>
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

package org.hibernate.cache.redis.jpa.repository;

import org.hibernate.cache.redis.jpa.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * @author sunghyouk.bae@gmail.com
 */
public interface ItemRepository extends JpaRepository<Item, Integer> {


  // NOTE: 일반 쿼리 결과도 @QueryHints를 이용해 2nd level cache에 저장합니다.
  @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  List<Item> findByName(final String name);
}
