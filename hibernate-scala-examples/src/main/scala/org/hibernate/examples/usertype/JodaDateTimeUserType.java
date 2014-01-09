package org.hibernate.examples.usertype;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

/**
 * Joda-Time 라이브러리의 {@link org.joda.time.DateTime} 수형을 표현하는 UserType 입니다.
 * 저장 시에는 Timestamp 값이 저장되고, 로드 시에는 {@link org.joda.time.DateTime}으로 변환됩니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 3:52
 */
public class JodaDateTimeUserType implements UserType {

    public DateTime asDateTime(Object value) {
        if (value instanceof Long)
            return new DateTime(value);
        else if (value instanceof Date)
            return new DateTime(value);
        else if (value instanceof DateTime)
            return (DateTime) value;
        else
            return null;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{ StandardBasicTypes.TIMESTAMP.sqlType() };
    }

    @Override
    public Class returnedClass() {
        return DateTime.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        Object value = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names[0], session, owner);
        return asDateTime(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        Date date = (value == null) ? null : ((DateTime) value).toDate();
        StandardBasicTypes.TIMESTAMP.nullSafeSet(st, date, index, session);
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
}

