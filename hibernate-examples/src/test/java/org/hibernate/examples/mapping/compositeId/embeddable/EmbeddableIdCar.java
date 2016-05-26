package org.hibernate.examples.mapping.compositeId.embeddable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * org.hibernate.examples.mapping.compositeId.embeddable.EmbeddableIdCar
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 4:41
 */
@Entity
//@Cache(region = "composite", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class EmbeddableIdCar extends AbstractHibernateEntity<EmbeddableCarIdentifier> {

  protected EmbeddableIdCar() { }

  public EmbeddableIdCar(EmbeddableCarIdentifier id) {
    this.id = id;
  }

  @EmbeddedId
  @Column(name = "carId")
  @Setter(AccessLevel.PROTECTED)
  private EmbeddableCarIdentifier id;

  private String serialNo;

  @Override
  public int hashCode() {
    return HashTool.compute(id, serialNo);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
                .add("serialNo", serialNo);
  }

  private static final long serialVersionUID = 3596634438044379341L;
}
