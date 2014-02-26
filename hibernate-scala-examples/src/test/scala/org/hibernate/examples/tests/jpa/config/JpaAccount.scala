package org.hibernate.examples.tests.jpa.config

import javax.persistence._
import org.hibernate.examples.model.HibernateEntity
import org.hibernate.examples.utils.{ToStringHelper, Hashs}

/**
 * org.hibernate.examples.tests.jpa.config.JpaAccount 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 4:23
 */
@Entity
@Access(AccessType.FIELD)
@NamedQuery(name = "JpaAccount.findByName", query = "select ja from JpaAccount ja where ja.name=?1")
@SerialVersionUID(8986275418970766284L)
class JpaAccount extends HibernateEntity[java.lang.Long] {

    @Id
    @GeneratedValue
    var id: java.lang.Long = _

    def getId = id

    var cashBalance: java.lang.Double = _

    @Column(name = "AccountName", nullable = false, length = 32)
    var name: String = _

    override def hashCode(): Int = Hashs.compute(name)

    override protected def buildStringHelper: ToStringHelper = super.buildStringHelper.add("name", name)
}
