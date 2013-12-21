package org.hibernate.test.jpa.repository;

import org.hibernate.test.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * org.hibernate.test.jpa.repository.AccountRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 21. 오후 5:59
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByTitle(final String title);
}
