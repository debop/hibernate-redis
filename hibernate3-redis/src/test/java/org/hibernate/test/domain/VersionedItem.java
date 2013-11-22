package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * org.hibernate.test.domain.VersionedItem
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:54
 */
@Entity
@org.hibernate.annotations.Cache(region = "common", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class VersionedItem implements Serializable {

    private static final long serialVersionUID = -1799457963599978471L;

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    private String name;

    private String description;
}
