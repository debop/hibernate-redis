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

  @Column(length = 255)
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
