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
