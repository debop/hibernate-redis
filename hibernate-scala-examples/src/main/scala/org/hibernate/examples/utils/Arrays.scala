package org.hibernate.examples.utils

/**
 * org.hibernate.examples.utils.Arrays 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 9. 오후 3:57
 */
object Arrays {

    val EMPTY_BYTE_ARRAY = Array[Byte](0)

    def isEmpty[T](array: Array[T]): Boolean =
        (array eq null) || (array.length == 0)

    def isEmpty[T](iterable: Iterable[T]): Boolean =
        (iterable eq null) || (!iterable.iterator.hasNext)
}
