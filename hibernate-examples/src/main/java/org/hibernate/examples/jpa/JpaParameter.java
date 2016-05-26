package org.hibernate.examples.jpa;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractNamedParameter;
import org.hibernate.examples.utils.ToStringHelper;
import org.hibernate.type.StandardBasicTypes;

/**
 * org.hibernate.examples.jpa.JpaParameter
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:15
 */
@Slf4j
public class JpaParameter extends AbstractNamedParameter {

  private org.hibernate.type.Type paramType = StandardBasicTypes.SERIALIZABLE;

  public JpaParameter(String name, Object value) {
    super(name, value);
  }

  public JpaParameter(String name, Object value, org.hibernate.type.Type paramType) {
    super(name, value);
    this.paramType = paramType;
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("paramType", paramType);
  }

  private static final long serialVersionUID = -5022556957583530472L;
}
