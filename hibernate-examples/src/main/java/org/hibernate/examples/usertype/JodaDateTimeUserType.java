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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Joda-Time 라이브러리의 {@link DateTime} 수형을 표현하는 UserType 입니다.
 * 저장 시에는 Timestamp 값이 저장되고, 로드 시에는 {@link DateTime}으로 변환됩니다.
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
    return new int[]{StandardBasicTypes.TIMESTAMP.sqlType()};
  }

  @Override
  public Class returnedClass() {
    return DateTime.class;
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    return Objects.equal(x, y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return Objects.hashCode(x);
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sharedSessionContractImplementor, Object owner) throws HibernateException, SQLException {
    Object value = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names[0], sharedSessionContractImplementor, owner);
    return asDateTime(value);
  }

  @Override
  public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
    Date date = (value == null) ? null : ((DateTime) value).toDate();
    StandardBasicTypes.TIMESTAMP.nullSafeSet(st, date, index, sharedSessionContractImplementor);
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

