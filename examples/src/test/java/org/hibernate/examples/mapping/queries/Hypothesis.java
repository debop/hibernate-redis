package org.hibernate.examples.mapping.queries;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * org.hibernate.examples.mapping.queries.Hypothesis
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 8:08
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Hypothesis extends AbstractHibernateEntity<String> {

  protected Hypothesis() {
  }

  public Hypothesis(final String id) {
    this.id = id;
  }

  @Id
  @Setter(AccessLevel.PROTECTED)
  private String id;

  private String description;

  private Integer position;

  @Override
  public int hashCode() {
    return HashTool.compute(description, position);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("description", description)
        .add("position", position);
  }

  private static final long serialVersionUID = 6827597657248806590L;
}
