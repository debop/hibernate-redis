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

package org.hibernate.cache.redis.jpa.models;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cache.redis.AbstractHibernateEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@org.hibernate.annotations.Cache(region = "common", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Person extends AbstractHibernateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;


  private Integer age;
  private String firstname;
  private String lastname;

  private Float weight = 77.7F;
  private Double height = 188.8D;

  @ManyToMany(mappedBy = "participants")
  private List<Event> events = new ArrayList<Event>();

  @CollectionTable(name = "EmailAddressSet", joinColumns = @JoinColumn(name = "personId"))
  @ElementCollection(targetClass = String.class)
  @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
  private Set<String> emailAddresses = new HashSet<String>();

  @CollectionTable(name = "PhoneNumberSet", joinColumns = @JoinColumn(name = "productItemId"))
  @ElementCollection(targetClass = PhoneNumber.class)
  @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
  private Set<PhoneNumber> phoneNumbers = new HashSet<PhoneNumber>();

  @CollectionTable(name = "TailsManList", joinColumns = @JoinColumn(name = "personId"))
  @ElementCollection(targetClass = String.class)
  @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
  private List<String> tailsmans = new ArrayList<String>();


  @Override
  public int hashCode() {
    return Objects.hashCode(firstname, lastname);
  }

  private static final long serialVersionUID = 5899077467654859662L;
}
