package org.hibernate.test.cache.redis;

import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class Item implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String description;
}
