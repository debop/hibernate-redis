package org.hibernate.test.cache.redis;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * org.hibernate.test.cache.redis.Item
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:52
 */
@Entity
@Cache(region = "common", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Item implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    private static final long serialVersionUID = -281066218676472922L;
}
