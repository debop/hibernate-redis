package org.hibernate.examples.usertype;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;
import java.sql.*;
import java.util.Objects;

/**
 * Joda-Time 의 DateTime의 정보를 시각과 {@link org.joda.time.DateTimeZone} 정보로 분리하여 저장하도록 합니다.
 * 로드 시에는 해당 TimeZone으로 설정된 {@link org.joda.time.DateTime} 을 반환합니다.
 * <p/>
 * <pre>
 *     // UTC DateTime 과 TimeZone 으로 분리해서 저장하고, 로드 시에는 통합합니다.
 *   @Columns(columns = { @Column(name = "startTime"), @Column(name = "startTimeZone") })
 *   @Type(type = "kr.hconnect.data.hibernate.usertype.JodaDateTimeTZUserType")
 *   private DateTime startTZ;
 *
 *   @Columns(columns = { @Column(name = "endTime"), @Column(name = "endTimeZone") })
 *   @Type(type = "kr.hconnect.data.hibernate.usertype.JodaDateTimeTZUserType")
 *   private DateTime endTZ;
 * </pre>
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 6. 오후 2:18
 */
public class JodaDateTimeTZUserType implements UserType {

    @Override
    public int[] sqlTypes() {
        return new int[]{ Types.TIMESTAMP, Types.VARCHAR };
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
        Timestamp timestamp = (Timestamp) StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names[0], session, owner);
        String timezone = (String) StandardBasicTypes.STRING.nullSafeGet(rs, names[1], session, owner);

        if (timestamp == null || timezone == null)
            return null;
        else
            return new DateTime(timestamp, DateTimeZone.forID(timezone));
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        DateTime dt = (DateTime) value;
        if (dt == null) {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index, session);
            StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, session);
        } else {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, dt.toDate(), index, session);
            StandardBasicTypes.STRING.nullSafeSet(st, dt.getZone().getID(), index + 1, session);
        }
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
