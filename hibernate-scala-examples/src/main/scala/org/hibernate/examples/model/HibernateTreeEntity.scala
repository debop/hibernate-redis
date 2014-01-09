package org.hibernate.examples.model

/**
 * Tree Node 를 표현하는 Entity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:13
 */
trait HibernateTreeEntity[T <: HibernateTreeEntity[T]] extends PersistentObject {

    def getParent: T

    def setParent(parent: T)

    def getChildren: Set[T]

    def getNodePosition: TreeNodePosition

    def addChild(child: T)

    def removeChild(child: T)
}
