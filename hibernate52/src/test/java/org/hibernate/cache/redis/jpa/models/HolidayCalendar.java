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

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.cache.redis.AbstractHibernateEntity;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@org.hibernate.annotations.Cache(region = "common", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class HolidayCalendar extends AbstractHibernateEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @CollectionTable(name = "HolidayMap", joinColumns = @JoinColumn(name = "calendarId"))
  @MapKeyClass(Date.class) // Map의 Key 의 수형
  @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)  // Map의 Value의 수형
  private Map<Date, String> holidays = new HashMap<Date, String>();

  public HolidayCalendar init() {

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    try {
      holidays.clear();
      holidays.put(df.parse("2016-01-01"), "New Year's Day");
      holidays.put(df.parse("2016-05-05"), "Children's Day");
      holidays.put(df.parse("2016-10-14"), "My Birthday");
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  private static final long serialVersionUID = 562790120727800606L;
}
