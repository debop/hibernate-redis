package org.hibernate.cache.redis.jpa.repository;

import org.hibernate.cache.redis.jpa.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;


public interface EventRepository extends JpaRepository<Event, Integer> {

  @Query("select evt from Event evt where evt.title=:title")
  @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  List<Event> findByTitle(@Param("title") final String title);

  @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  List<Event> findByDate(Date date);
}
