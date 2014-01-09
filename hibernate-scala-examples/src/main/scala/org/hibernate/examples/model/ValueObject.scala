package org.hibernate.examples.model

import org.hibernate.examples.utils.ToStringHelper

/**
 * DDD 의 Value Object를 표현합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:30
 */
trait ValueObject extends AnyRef with Serializable {

    override
    def equals(obj: scala.Any): Boolean = {
        obj match {
            case vo: ValueObject => hashCode() == obj.hashCode()
            case _ => false
        }
    }

    override def hashCode(): Int = System.identityHashCode(this)

    override def toString: String = buildStringHelper.toString

    protected def buildStringHelper: ToStringHelper = ToStringHelper(this)
}

abstract class AbstractValueObject extends ValueObject {

}
