package org.hibernate.examples.mapping.simple;

import org.hibernate.Session;
import org.hibernate.examples.AbstractHibernateTest;
import org.hibernate.examples.utils.Serializers;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.simple.SimpleEntityTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 4:18
 */
@Transactional
public class SimpleEntityTest extends AbstractHibernateTest {

  public Session getSession() {
    return sessionFactory.getCurrentSession();
  }

  @Test
  public void lifecycle() throws Exception {
    LifecycleEntity entity = new LifecycleEntity();

    getSession().save(entity);
    getSession().flush();

    LifecycleEntity loaded = (LifecycleEntity) getSession().load(LifecycleEntity.class, entity.getId());
    assertThat(loaded).isNotNull();
    assertThat(loaded).isEqualTo(entity);
  }

  @Test
  public void transientObject() throws Exception {
    SimpleEntity transientObj = new SimpleEntity();
    transientObj.setName("transient");

    SimpleEntity transientObj2 = Serializers.copyObject(transientObj);

    transientObj2.setDescription("desc");
    assertThat(transientObj2).isEqualTo(transientObj);

    SimpleEntity savedObj = Serializers.copyObject(transientObj);
    getSession().save(savedObj);
    getSession().flush();
    getSession().clear();

    // Id를 발급받은 Persistent Object 와 Transient Object 와의 비교. hashCode에서
    assertThat(savedObj).isNotEqualTo(transientObj);

    SimpleEntity loaded = (SimpleEntity) getSession().get(SimpleEntity.class, savedObj.getId());

    assertThat(loaded).isNotNull();
    // Persistent Object 간의 비교
    assertThat(loaded).isEqualTo(savedObj);

    // Persistent Object 와 Transient Object 간의 비교
    assertThat(loaded).isNotEqualTo(transientObj);

    SimpleEntity savedObj2 = Serializers.copyObject(transientObj);
    getSession().save(savedObj2);
    getSession().flush();
    getSession().clear();

    SimpleEntity loaded2 = (SimpleEntity) getSession().get(SimpleEntity.class, savedObj2.getId());
    assertThat(loaded2).isNotNull();
    assertThat(loaded2).isEqualTo(savedObj2);
    assertThat(loaded2).isNotEqualTo(transientObj);
    assertThat(loaded2).isNotEqualTo(loaded);
  }
}
