package org.hibernate.cache.redis.jpa.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.cache.redis.AbstractHibernateEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author sunghyouk.bae@gmail.com
 */
@Entity
@Getter
@Setter
public class Event extends AbstractHibernateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;


  private String title;

  @Temporal(TemporalType.TIMESTAMP)
  private Date date;

  @ManyToMany(cascade = {CascadeType.ALL})
  private Set<Person> participants = new HashSet<Person>();

  @ManyToOne
  @JoinColumn(name = "organizerId")
  private Person organizer;

  public void addPaticipant(Person person) {
    participants.add(person);
    person.getEvents().add(this);
  }

  public void removeParticipant(Person person) {
    participants.remove(person);
    person.getEvents().remove(this);
  }

  @Override
  public String toString() {
    return "Event# " + getTitle() + ": " + getDate();
  }

  private static final long serialVersionUID = 8201868945321506870L;
}
