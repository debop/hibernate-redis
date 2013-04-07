package org.hibernate.test.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private int age;
    private String firstname;
    private String lastname;

    private List<Event> events = Lists.newArrayList();
    private Set<String> emailAddresses = Sets.newHashSet();
    private Set<PhoneNumber> phoneNumbers = Sets.newHashSet();
    private List<String> tailsmans = Lists.newArrayList();

    public String toString() {
        return getFirstname() + " " + getLastname();
    }
}
