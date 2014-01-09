package org.hibernate.examples

import org.hibernate.examples.utils.Hashs
import org.hibernate.examples.model.ValueObject

/**
 * org.hibernate.examples.NamedParameter 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 3:31
 */
trait NamedParameter extends ValueObject {

    def name: String

    def value: Any
}

abstract class AbstractNamedParameter(val name: String, val value: Any) extends NamedParameter {

    override def hashCode(): Int = Hashs.compute(name)
}
