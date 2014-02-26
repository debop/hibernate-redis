package org.hibernate.test.jpa.repository;

import org.hibernate.test.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * org.hibernate.test.jpa.repository.ItemRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 23. 오후 6:43
 */
public interface ItemRepository extends JpaRepository<Item, Long> {

    // NOTE: 일반 쿼리 결과도 @QueryHints를 이용해 2nd level cache에 저장합니다.
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<Item> findByName(final String name);
}
