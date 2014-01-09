package org.hibernate.examples.interceptor

import java.util
import org.hibernate.EmptyInterceptor
import org.hibernate.examples.model.UpdatedTimestampEntity

/**
 * 엔티티가 변경된 최신 시각을 관리하는 Interceptor 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:27
 */
class UpdatedTimestampInterceptor extends EmptyInterceptor {

    override def preFlush(entities: util.Iterator[_]) {
        while (entities.hasNext) {
            val entity = entities.next()
            entity match {
                case ute: UpdatedTimestampEntity => ute.updateUpdatedTimestamp()
                case _ =>
            }
        }
    }
}
