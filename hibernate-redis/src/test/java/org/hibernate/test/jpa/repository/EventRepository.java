package org.hibernate.test.jpa.repository;

import org.hibernate.test.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

/**
 * org.hibernate.test.jpa.repository.AccountRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 21. 오후 5:59
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    // NOTE: JPQL 결과 셋을 2nd level cache 에 저장하기 위해 @QueryHints 를 사용합니다.
    @Query("select evt from Event evt where evt.title=:title")
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<Event> findByTitle(@Param("title") final String title);

    // NOTE: 일반 쿼리 결과도 @QueryHints를 이용해 2nd level cache에 저장합니다.
    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    List<Event> findByDate(Date date);
}
