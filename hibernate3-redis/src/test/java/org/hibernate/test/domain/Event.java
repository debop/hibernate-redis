package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * org.hibernate.test.domain.Event
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:53
 */
@Entity
@org.hibernate.annotations.Cache(region = "account", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Event implements Serializable {

    private static final long serialVersionUID = 4714760453860670689L;

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToMany(cascade = { CascadeType.ALL })
    private Set<Person> participants = new HashSet<Person>();

    @ManyToOne
    @JoinColumn(name = "Organizer_Id")
    private Person organizer;

    public void addParticipant(Person person) {
        participants.add(person);
        person.getEvents().add(this);
    }

    public void removeParticipant(Person person) {
        participants.remove(person);
        person.getEvents().remove(this);
    }

    public String toString() {
        return getTitle() + ": " + getDate();
    }
}
