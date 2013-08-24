package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * org.hibernate.test.domain.Person
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:54
 */
@Entity
@Getter
@Setter
public class Person implements Serializable {

	private static final long serialVersionUID = -8245742950718661800L;

	@Id
	@GeneratedValue
	private Long id;

	private int age;
	private String firstname;
	private String lastname;

	private List<Event> events = new ArrayList<Event>();
	private Set<String> emailAddresses = new HashSet<String>();
	private Set<PhoneNumber> phoneNumbers = new HashSet<PhoneNumber>();
	private List<String> tailsmans = new ArrayList<String>();

	public String toString() {
		return getFirstname() + " " + getLastname();
	}
}
