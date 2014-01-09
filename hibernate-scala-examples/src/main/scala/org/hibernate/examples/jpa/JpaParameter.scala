package org.hibernate.examples.jpa

import org.hibernate.`type`.StandardBasicTypes
import org.hibernate.examples.AbstractNamedParameter

/**
 * kr.debop4s.data.jpa.JpaParameter
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 19. 오후 9:56
 */
@SerialVersionUID(2672746608417519438L)
class JpaParameter(override val name: String,
                   override val value: Any,
                   val paramType: org.hibernate.`type`.Type = StandardBasicTypes.SERIALIZABLE)
    extends AbstractNamedParameter(name, value) {

    def this(name: String, value: Any) {
        this(name, value, StandardBasicTypes.SERIALIZABLE)
    }

    override protected def buildStringHelper =
        super.buildStringHelper.add("paramType", paramType)
}
