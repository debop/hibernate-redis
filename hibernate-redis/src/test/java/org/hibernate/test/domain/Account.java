package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * org.hibernate.test.domain.Account
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:53
 */
@Entity
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PersonId")
    private Person person;
}
