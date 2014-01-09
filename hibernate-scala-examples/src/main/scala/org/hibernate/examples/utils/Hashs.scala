package org.hibernate.examples.utils

import scala.annotation.varargs


/**
 * Hash 관련 툴
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오후 4:57
 */
object Hashs {

    /** The constant NULL_VALUE. */
    val NULL_VALUE = 0
    /** The constant ONE_VALUE. */
    val ONE_VALUE = 1
    /** The constant FACTOR. */
    val FACTOR = 31

    /**
     * 해시코드를 생성합니다.
     *
     * @param x 해시코드를 생성할 객체
     * @return 해시코드
     */
    @inline
    private def computeInternal(x: Any) = if (x == null) NULL_VALUE else x.hashCode()


    /**
     * 지정된 객체들의 Hash Code를 조합한 Hash Code를 생성합니다.
     *
     * @param objs 해쉬코드를 생성할 객체 배열
     * @return 조합된 Hash code
     */
    @inline
    @varargs
    def compute(objs: Any*): Int = {
        if (objs == null || objs.size == 0)
            NULL_VALUE

        var hash = NULL_VALUE
        objs.foreach(x => hash = hash * FACTOR + computeInternal(x))
        hash
    }
}
