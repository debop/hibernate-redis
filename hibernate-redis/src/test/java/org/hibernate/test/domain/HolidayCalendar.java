package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * org.hibernate.test.domain.HolidayCalendar
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:54
 */
@Entity
@Getter
@Setter
public class HolidayCalendar implements Serializable {

	private static final long serialVersionUID = 1863327691656323002L;

	@Id
	@GeneratedValue
	private Long id;

	@CollectionTable(name = "HolidayMap", joinColumns = @JoinColumn(name = "CalendarId"))
	@MapKeyClass(Date.class) // Map의 Key 의 수형
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)  // Map의 Value의 수형
	private Map<Date, String> holidays = new HashMap<Date, String>();

	public HolidayCalendar init() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			holidays.clear();
			holidays.put(df.parse("2013-01-01"), "New Year's Day");
			holidays.put(df.parse("2013-05-05"), "Children's Day");
			holidays.put(df.parse("2013-10-14"), "My Birthday");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return this;
	}
}
