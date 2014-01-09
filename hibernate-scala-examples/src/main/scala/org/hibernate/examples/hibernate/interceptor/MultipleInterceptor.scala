package org.hibernate.examples.interceptor

import java.io.Serializable
import java.util
import org.hibernate.`type`.Type
import org.hibernate.{Interceptor, EmptyInterceptor}
import scala.collection.mutable.ArrayBuffer

/**
 * kr.debop4s.data.hibernate.interceptor.MultipleInterceptor
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:43
 */
class MultipleInterceptor extends EmptyInterceptor {

    val interceptors = ArrayBuffer[Interceptor]()

    def this(interceptors: Interceptor*) {
        this()
        this.interceptors ++= interceptors
    }

    def addInterceptor(interceptor: Interceptor) {
        this.interceptors += interceptor
    }

    def removeInterceptor(interceptor: Interceptor) {
        this.interceptors -= interceptor
    }

    def exists: Boolean = interceptors != null && interceptors.size > 0

    override def onDelete(entity: Any, id: Serializable, state: Array[AnyRef], propertyNames: Array[String], types: Array[Type]) {
        if (exists) {
            interceptors.foreach(x => x.onDelete(entity, id, state, propertyNames, types))
        }
    }

    override def onFlushDirty(entity: Any, id: Serializable, currentState: Array[AnyRef], previousState: Array[AnyRef], propertyNames: Array[String], types: Array[Type]): Boolean = {
        if (exists) {
            interceptors.foreach(x => x.onFlushDirty(entity, id, currentState, previousState, propertyNames, types))
        }
        false
    }

    override def onSave(entity: scala.Any, id: Serializable, state: Array[AnyRef], propertyNames: Array[String], types: Array[Type]): Boolean = {
        if (exists) {
            interceptors.foreach(x => x.onSave(entity, id, state, propertyNames, types))
        }
        false
    }

    override def onLoad(entity: scala.Any, id: Serializable, state: Array[AnyRef], propertyNames: Array[String], types: Array[Type]): Boolean = {
        if (exists) {
            interceptors.foreach(x => x.onLoad(entity, id, state, propertyNames, types))
        }
        false
    }

    override def postFlush(entities: util.Iterator[_]) {
        if (exists) {
            interceptors.foreach(x => x.postFlush(entities))
        }
    }

    override def preFlush(entities: util.Iterator[_]) {
        if (exists) {
            interceptors.foreach(x => x.preFlush(entities))
        }
    }
}
