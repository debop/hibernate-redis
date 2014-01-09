package org.hibernate.examples.model

import javax.persistence.{Embeddable, Column}
import org.hibernate.examples.utils.{ToStringHelper, Hashs}

/**
 * kr.debop4s.data.model.TreeNodePosition
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:03
 */
@SerialVersionUID(3455568346636164669L)
@Embeddable
case class TreeNodePosition(var level: Int = 0, var order: Int = 0) extends ValueObject {

    @Column(name = "treeLevel")
    def getLevel: Int = level

    @Column(name = "treeOrder")
    def getOrder: Int = order

    def setPosition(level: Int, order: Int) {
        this.level = level
        this.order = order
    }

    override def hashCode(): Int = Hashs.compute(level, order)

    override protected def buildStringHelper: ToStringHelper =
        super.buildStringHelper
            .add("level", level)
            .add("order", order)
}
