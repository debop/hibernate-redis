package org.hibernate.examples.utils

import org.hibernate.criterion.{Restrictions, DetachedCriteria}
import org.hibernate.examples.model.HibernateTreeEntity
import org.hibernate.examples.hibernate.repository.HibernateDao

/**
 * kr.debop4s.data.hibernate.tools.Entities
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 24. 오후 8:38
 */
object Entities {

    val GET_LIST_BY_META_KEY = "select distinct me from %s me where :key in indices(me.metaMap)"
    val GET_LIST_BY_META_VALUE = "select distinct me from %s me join me.metaMap meta where meta.value = :value"

    def updateTreeNodePosition[T <: HibernateTreeEntity[T]](entity: T) {
        assert(entity != null)
        val np = entity.getNodePosition
        if (entity.getParent != null) {
            np.level = entity.getParent.getNodePosition.level + 1
            if (!entity.getParent.getChildren.contains(entity)) {
                np.order = entity.getParent.getChildren.size
            }
        } else {
            np.level = 0
            np.order = 0
        }
    }

    def getChildCount[T <: HibernateTreeEntity[T]](dao: HibernateDao, entity: T) = {
        val dc = DetachedCriteria.forClass(entity.getClass)
        dc.add(Restrictions.eq("parent", entity))
        dao.count(dc)
    }

    def hasChildren[T <: HibernateTreeEntity[T]](dao: HibernateDao, entity: T): Boolean = {
        val dc = DetachedCriteria.forClass(entity.getClass)
        dc.add(Restrictions.eq("parent", entity))
        dao.exists(dc)
    }

    def setNodeOrder[T <: HibernateTreeEntity[T]](node: T, order: Int) {
        assert(node != null)
        if (node.getParent != null) {
            node.getParent.getChildren.foreach(child => {
                if (child.getNodePosition.order >= order)
                    child.getNodePosition.order = child.getNodePosition.order + 1
            })
        }
        node.getNodePosition.order = order
    }

    def setNodeOrder[T <: HibernateTreeEntity[T]](parent: T) {
        assert(parent != null)

        var order = 0
        parent.getChildren.toList
            .sortWith(_.getNodePosition.order < _.getNodePosition.order)
            .foreach(n => {
            n.getNodePosition.order = order
            order += 1
        })
    }

    def changeParent[T <: HibernateTreeEntity[T]](node: T, oldParent: T, newParent: T) {
        assert(node != null)

        if (oldParent != null)
            oldParent.removeChild(node)
        if (newParent != null)
            newParent.addChild(node)

        node.setParent(newParent)
        updateTreeNodePosition(node)
    }

    def setParent[T <: HibernateTreeEntity[T]](node: T, parent: T) {
        assert(node != null)
        changeParent(node, node.getParent, parent)
    }

    def insertChildNode[T <: HibernateTreeEntity[T]](parent: T, child: T, order: Int) {
        assert(parent != null)
        assert(child != null)

        val ord = Math.max(0, Math.min(order, parent.getChildren.size - 1))
        parent.addChild(child)
        setNodeOrder(child, ord)
    }

}
