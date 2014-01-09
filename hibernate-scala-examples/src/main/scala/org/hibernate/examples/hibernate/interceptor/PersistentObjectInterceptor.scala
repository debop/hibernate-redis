package org.hibernate.examples.interceptor

import java.io.Serializable
import org.hibernate.EmptyInterceptor
import org.hibernate.`type`.Type
import org.hibernate.examples.model.PersistentObject

/**
 * kr.debop4s.data.hibernate.interceptor.PersistentObjectInterceptor
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 2:38
 */
class PersistentObjectInterceptor extends EmptyInterceptor {

    def isPersisted(entity: AnyRef): Boolean = entity match {
        case p: PersistentObject => true
        case _ => false
    }

    override
    def onLoad(entity: Any, id: Serializable, state: Array[AnyRef], propertyNames: Array[String], types: Array[Type]): Boolean = {
        entity match {
            case p: PersistentObject => p.onLoad()
            case _ =>
        }
        false
    }

    override
    def onSave(entity: scala.Any, id: Serializable, state: Array[AnyRef], propertyNames: Array[String], types: Array[Type]): Boolean = {
        entity match {
            case p: PersistentObject => p.onPersist()
            case _ =>
        }
        false
    }
}
