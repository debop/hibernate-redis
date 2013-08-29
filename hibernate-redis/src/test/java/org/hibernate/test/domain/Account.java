package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * org.hibernate.test.domain.Account
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:53
 */
@Entity
@org.hibernate.annotations.Cache(region = "hibernate-redis", usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class Account implements Serializable {
    private static final long serialVersionUID = 6662300674854084326L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PersonId")
    private Person person;
}
