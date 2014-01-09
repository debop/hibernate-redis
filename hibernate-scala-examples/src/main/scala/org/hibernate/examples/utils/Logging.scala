package org.hibernate.examples.utils

import org.slf4j.Marker
import scala.annotation.varargs

/**
 * kr.debop4s.core.logging.Logging
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 9:37
 */
trait Logging {

    lazy val log = Logger[Logging]

    def loggerName = log.name

    lazy val isTraceEnabled = log.isTraceEnabled

    protected def trace(msg: => String, t: Throwable) {
        log.trace(msg, t)
    }

    protected def trace(marker: Marker, msg: => String, t: Throwable) {
        log.trace(marker, msg, t)
    }

    @varargs
    private def trace(format: => Any, arguments: Any*) {
        log.trace(format, arguments)
    }

    @varargs
    protected def trace(marker: Marker, format: => Any, arguments: Any*) {
        log.trace(marker, format, arguments)
    }


    lazy val isDebugEnabled = log.isDebugEnabled

    protected def debug(msg: => String, t: Throwable) {
        log.debug(msg, t)
    }

    protected def debug(marker: Marker, msg: => String, t: Throwable) {
        log.debug(marker, msg, t)
    }

    @varargs
    protected def debug(format: => Any, arguments: Any*) {
        log.debug(format, arguments)
    }

    @varargs
    protected def debug(marker: Marker, format: => Any, arguments: Any*) {
        log.debug(marker, format, arguments)
    }

    lazy val isInfoEnabled = log.isInfoEnabled

    protected def info(msg: => String, t: Throwable) {
        log.info(msg, t)
    }

    protected def info(marker: Marker, msg: => String, t: Throwable) {
        log.info(marker, msg, t)
    }

    @varargs
    protected def info(format: Any, arguments: Any*) {
        log.info(format, arguments)
    }

    @varargs
    protected def info(marker: Marker, format: Any, arguments: Any*) {
        log.info(marker, format, arguments)
    }

    lazy val isWarnEnabled = log.isWarnEnabled

    protected def warn(msg: => String, t: Throwable) {
        log.warn(msg, t)
    }

    protected def warn(marker: Marker, msg: => String, t: Throwable) {
        log.warn(marker, msg, t)
    }

    @varargs
    protected def warn(format: Any, arguments: Any*) {
        log.warn(format, arguments)
    }

    @varargs
    protected def warn(marker: Marker, format: Any, arguments: Any*) {
        log.warn(marker, format, arguments)
    }

    lazy val isErrorEnabled = log.isErrorEnabled

    protected def error(msg: => String, t: Throwable) {
        log.error(msg, t)
    }

    protected def error(marker: Marker, msg: => String, t: Throwable) {
        log.error(marker, msg, t)
    }

    @varargs
    protected def error(format: Any, arguments: Any*) {
        log.error(format, arguments)
    }

    @varargs
    protected def error(marker: Marker, format: Any, arguments: Any*) {
        log.error(marker, format, arguments)
    }
}
