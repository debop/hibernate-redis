package org.hibernate.examples.mapping.associations.join;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.hibernate.examples.mapping.associations.join.repository.JoinCustomerRepository;
import org.hibernate.examples.mapping.associations.join.repository.JoinUserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.associations.join.JoinTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 11:20
 */
@Slf4j
@Transactional
public class JoinTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;
  @Autowired
  JoinUserRepository userRepository;
  @Autowired
  JoinCustomerRepository customerRepository;

  @Test
  public void configuration() throws Exception {
    assertThat(userRepository).isNotNull();
  }

  @Test
  public void joinUserTest() throws Exception {
    JoinUser user = new JoinUser();
    user.getNicknames().add("debop");
    user.getNicknames().add("sunghyouk");

    JoinAddressEntity home = new JoinAddressEntity();
    home.setCity("Seoul");
    home.setStreet("Jungreung");
    home.setZipcode("100-100");
    user.getAddresses().put("home", home);

    JoinAddressEntity office = new JoinAddressEntity();
    office.setCity("Seoul");
    office.setStreet("Ankook");
    office.setZipcode("200-200");
    user.getAddresses().put("office", office);

    userRepository.saveAndFlush(user);
    em.clear();

    JoinUser loaded = userRepository.findOne(user.getId());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getAddresses()).isNotNull();
    assertThat(loaded.getAddresses().size()).isEqualTo(2);
    assertThat(loaded.getNicknames().size()).isEqualTo(2);
  }

  @Test
  public void joinCustomerTest() throws Exception {
    JoinCustomer customer = new JoinCustomer();
    customer.setName("배성혁");
    customer.setEmail("sunghyouk.bae@gmail.com");

    JoinAddress addr = new JoinAddress();
    addr.setCity("Seoul");
    addr.setStreet("Jungreung");
    addr.setZipcode("100-100");

    // Embedded Class
    customer.setJoinAddress(addr);
    customerRepository.saveAndFlush(customer);
    em.clear();

    JoinCustomer loaded = customerRepository.findByName(customer.getName());

    assertThat(loaded).isNotNull();
    assertThat(loaded.getJoinAddress()).isNotNull();
    assertThat(loaded.getJoinAddress().getCity()).isEqualTo(addr.getCity());
  }
}
