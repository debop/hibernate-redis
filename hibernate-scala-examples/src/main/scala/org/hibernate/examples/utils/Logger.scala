package org.hibernate.examples.utils

import language.implicitConversions
import org.slf4j.{Marker, Logger => Slf4jLogger}
import scala.annotation.varargs

/**
 * Scala를 위한 Logger 입니다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 9. 오후 9:37
 */
class Logger(val log: Slf4jLogger) {

    private implicit def _any2String(msg: Any): String = {
        msg match {
            case null => "<null>"
            case _ => msg.toString
        }
    }

    lazy val name = log.getName

    lazy val isTraceEnabled = log.isTraceEnabled

    @inline final def isTraceEnabled(marker: Marker) = log.isTraceEnabled(marker)

    @inline final
    def trace(msg: => String, t: Throwable) {
        log.trace(msg, t)
    }

    @inline final
    def trace(marker: Marker, msg: => String, t: Throwable) {
        if (isTraceEnabled(marker))
            log.trace(marker, msg, t)
    }

    @inline
    @varargs final
    def trace(format: => Any, arguments: Any*) {
        log.trace(format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }

    @inline
    @varargs final
    def trace(marker: Marker, format: => Any, arguments: Any*) {
        if (isTraceEnabled(marker))
            log.trace(marker, format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }

    lazy val isDebugEnabled = log.isDebugEnabled

    @inline final def isDebugEnabled(marker: Marker) = log.isDebugEnabled(marker)

    @inline final
    def debug(msg: => String, t: Throwable) {
        log.debug(msg, t)
    }

    @inline final
    def debug(marker: Marker, msg: => String, t: Throwable) {
        if (isDebugEnabled(marker))
            log.debug(marker, msg, t)
    }

    @inline
    @varargs final
    def debug(format: => Any, arguments: Any*) {
        log.debug(format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }

    @inline
    @varargs final
    def debug(marker: Marker, format: => Any, arguments: Any*) {
        if (isDebugEnabled(marker))
            log.debug(marker, format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }

    lazy val isInfoEnabled = log.isInfoEnabled

    @inline final def isInfoEnabled(marker: Marker) = log.isInfoEnabled(marker)

    @inline final
    def info(msg: => String, t: Throwable) {
        log.info(msg, t)
    }

    @inline final
    def info(marker: Marker, msg: => String, t: Throwable) {
        if (isInfoEnabled(marker))
            log.info(marker, msg, t)
    }

    @inline
    @varargs final
    def info(format: => Any, arguments: Any*) {
        log.info(format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }

    @inline
    @varargs final
    def info(marker: Marker, format: => Any, arguments: Any*) {
        if (isInfoEnabled(marker))
            log.info(marker, format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }

    lazy val isWarnEnabled = log.isWarnEnabled

    @inline final def isWarnEnabled(marker: Marker) = log.isWarnEnabled(marker)

    @inline final
    def warn(msg: => String, t: Throwable) {
        log.warn(msg, t)
    }

    @inline final
    def warn(marker: Marker, msg: => String, t: Throwable) {
        if (isWarnEnabled(marker))
            log.warn(marker, msg, t)
    }

    @inline
    @varargs final
    def warn(format: => Any, arguments: Any*) {
        log.warn(format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }

    @inline
    @varargs final
    def warn(marker: Marker, format: => Any, arguments: Any*) {
        if (isWarnEnabled(marker))
            log.warn(marker, format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }


    lazy val isErrorEnabled = log.isErrorEnabled

    @inline final def isErrorEnabled(marker: Marker) = log.isErrorEnabled(marker)

    @inline final
    def error(msg: => String, t: Throwable) {
        log.error(msg, t)
    }

    @inline final
    def error(marker: Marker, msg: => String, t: Throwable) {
        if (isErrorEnabled(marker))
            log.error(marker, msg, t)
    }

    @inline
    @varargs final
    def error(format: => Any, arguments: Any*) {
        log.error(format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }

    @inline
    @varargs final
    def error(marker: Marker, format: => Any, arguments: Any*) {
        if (isErrorEnabled(marker))
            log.error(marker, format.toString, arguments.map(_.asInstanceOf[AnyRef]): _*)
    }
}

/**
 * Logger
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  13. 6. 2. 오후 4:43
 */
object Logger {

    import reflect.{classTag, ClassTag}

    lazy val RootLoggerName = Slf4jLogger.ROOT_LOGGER_NAME

    /**
     * 지정된 이름을 Logger 이름으로 사용합니다. 예: Logger("LoggerName")
     */
    def apply(name: String): Logger = new Logger(org.slf4j.LoggerFactory.getLogger(name))

    /**
     * 지정한 클래스 수형에 맞는 Logger를 반환합니다.
     * {{{
     *     val log = Logger(classOf[MyClass])
     * }}}
     */
    def apply(cls: Class[_]): Logger = apply(cls.getName)

    /**
     * 특정 클래스에 맞는 Logger 를 반환합니다.
     * {{{
     *  val log = Logger[classOf[MyClass]]
     * }}}
     */
    def apply[C: ClassTag]: Logger = apply(classTag[C].runtimeClass.getName)

    /**
     * 특정 클래스에 맞는 Logger 를 반환합니다.
     * {{{
     *   val log = Logger[MyClass]
     * }}}
     */
    def get[T](implicit tag: reflect.ClassTag[T]): Logger = apply(tag.runtimeClass.getName)

    /**
     * Root Logger
     */
    def rootLogger = apply(RootLoggerName)
}
