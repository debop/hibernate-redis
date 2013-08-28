package org.hibernate.test.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * org.hibernate.test.domain.Item
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오전 12:54
 */
@Entity
@org.hibernate.annotations.Cache(region = "hibernate-redis", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class Item implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String description;

	private static final long serialVersionUID = 5597936606448211014L;
}
