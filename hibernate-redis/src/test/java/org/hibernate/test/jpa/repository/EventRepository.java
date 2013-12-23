package org.hibernate.test.jpa.repository;

import org.hibernate.test.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * org.hibernate.test.jpa.repository.AccountRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 21. 오후 5:59
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select evt from Event evt where evt.title=:title")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<Event> findByTitle(final String title);
}
