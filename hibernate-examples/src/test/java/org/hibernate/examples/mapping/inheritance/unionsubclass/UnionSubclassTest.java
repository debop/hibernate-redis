package org.hibernate.examples.mapping.inheritance.unionsubclass;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.inheritance.unionsubclass.UnionSubclassTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 30. 오후 12:53
 */
@Slf4j
@Transactional
public class UnionSubclassTest extends AbstractJpaTest {

  @PersistenceContext
  EntityManager em;

  @Test
  public void personTest() {
    BankAccount bankAccount = new BankAccount();
    bankAccount.setAccount("account");
    bankAccount.setBankname("은행이름");
    bankAccount.setOwner("배성혁");
    em.persist(bankAccount);

    CreditCard creditCard = new CreditCard();
    creditCard.setNumber("1111-1111-1111-1111");
    creditCard.setExpYear(2020);
    creditCard.setExpMonth(12);
    creditCard.setOwner("배성혁");
    em.persist(creditCard);

    em.flush();
    em.clear();

    BankAccount bankAccount1 = em.find(BankAccount.class, bankAccount.getId());
    assertThat(bankAccount1).isNotNull();
    assertThat(bankAccount1.getId()).isNotNull();
    assertThat(bankAccount1).isEqualTo(bankAccount);

    CreditCard creditCard1 = em.find(CreditCard.class, creditCard.getId());
    assertThat(creditCard1).isNotNull();
    assertThat(creditCard1.getId()).isNotNull();
    assertThat(creditCard1).isEqualTo(creditCard);
  }
}
