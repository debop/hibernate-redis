/*
 * Copyright (c) 2017. Sunghyouk Bae <sunghyouk.bae@gmail.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.hibernate.examples.usertype;

import com.google.common.base.Objects;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;
import java.sql.*;

/**
 * Joda-Time 의 DateTime의 정보를 시각과 {@link DateTimeZone} 정보로 분리하여 저장하도록 합니다.
 * 로드 시에는 해당 TimeZone으로 설정된 {@link DateTime} 을 반환합니다.
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
    return new int[]{Types.TIMESTAMP, Types.VARCHAR};
  }

  @Override
  public Class returnedClass() {
    return DateTime.class;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    return x == y || (x != null && x.equals(y));
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return Objects.hashCode(x);
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sharedSessionContractImplementor, Object owner) throws HibernateException, SQLException {
    Timestamp timestamp = (Timestamp) StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names[0], sharedSessionContractImplementor, owner);
    String timezone = (String) StandardBasicTypes.STRING.nullSafeGet(rs, names[1], sharedSessionContractImplementor, owner);

    if (timestamp == null || timezone == null)
      return null;
    else
      return new DateTime(timestamp, DateTimeZone.forID(timezone));
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
    DateTime dt = (DateTime) value;
    if (dt == null) {
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index, sharedSessionContractImplementor);
      StandardBasicTypes.STRING.nullSafeSet(st, null, index + 1, sharedSessionContractImplementor);
    } else {
      StandardBasicTypes.TIMESTAMP.nullSafeSet(st, dt.toDate(), index, sharedSessionContractImplementor);
      StandardBasicTypes.STRING.nullSafeSet(st, dt.getZone().getID(), index + 1, sharedSessionContractImplementor);
    }
  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    return value;
  }

  @Override
  public boolean isMutable() {
    return false;
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
