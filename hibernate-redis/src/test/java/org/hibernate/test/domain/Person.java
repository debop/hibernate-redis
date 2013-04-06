package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
}
