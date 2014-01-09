package org.hibernate.examples.utils

import scala.collection.mutable

/**
 * kr.debop4s.core.tools.ToStringHelper
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:31
 */
class ToStringHelper(val className: String) {

    val map = new mutable.LinkedHashMap[String, Any]()

    def add(name: String, value: Any): ToStringHelper = {
        addMap(name, value)
        this
    }

    override def toString: String = map.mkString("{", ", ", "}")

    private def addMap(name: String, value: Any) {
        map.put(name, value)
    }
}

object ToStringHelper {
    def apply(self: Any) = new ToStringHelper(self.getClass.getName)
}
