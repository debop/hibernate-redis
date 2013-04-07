package org.hibernate.test.domain;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * org.hibernate.test.domain.Event
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:53
 */
@Entity
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private Set<Person> participants = Sets.newHashSet();

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
