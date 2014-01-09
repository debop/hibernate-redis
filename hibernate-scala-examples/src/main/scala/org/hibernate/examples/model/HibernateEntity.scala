package org.hibernate.examples.model

import javax.persistence._
import org.hibernate.examples.utils.{ToStringHelper, Hashs}

/**
 * Hibernate, JPA 의 모든 엔티티의 기본 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 2:41
 */
@MappedSuperclass
trait HibernateEntity[TId <: java.io.Serializable] extends PersistentObject {

    def getId: TId

    @PostPersist
    override def onPersist() {
        super.onPersist()
    }

    @PostLoad
    override def onLoad() {
        super.onLoad()
    }

    override def equals(obj: Any): Boolean = {
        val isSameType = (obj != null) && getClass.equals(obj.getClass)

        if (isSameType) {
            val entity = obj.asInstanceOf[HibernateEntity[TId]]
            return hasSameNonDefaultIds(entity) ||
                   ((!isPersisted || !entity.isPersisted) && hasSameBusinessSignature(entity))
        }
        false
    }

    override def hashCode(): Int =
        if (getId == null) System.identityHashCode(this) else Hashs.compute(getId)


    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
            .add("id", getId)

    private def hasSameNonDefaultIds(entity: HibernateEntity[TId]): Boolean = {
        if (entity == null)
            false

        val id = getId
        val entityId = entity.getId
        (id != null) && (entityId != null) && id.equals(entityId)
    }

    private def hasSameBusinessSignature(entity: HibernateEntity[TId]): Boolean = {
        val notNull = (entity != null)
        val hash = if (getId != null) Hashs.compute(getId) else hashCode()
        if (notNull) {
            val entityHash = if (entity.getId != null) Hashs.compute(entity.getId) else entity.hashCode()
            hash == entityHash
        }
        false
    }
}
