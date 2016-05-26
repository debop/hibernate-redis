package org.hibernate.examples.hibernate.config;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * org.hibernate.examples.hibernate.config.OracleNamingStrategyTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오전 9:44
 */
public class OracleNamingStrategyTest {

  private final String[] classNames = new String[]{"User", "UserLog", "GroupMember", "EventLog"};
  private final String[] tableNames = new String[]{"user", "user_log", "group_member", "event_log"};

  private final String[] propertyNames = new String[]{"username", "email", "companyName", "eventLog"};
  private final String[] columnNames = new String[]{"username", "email", "company_name", "event_log"};

  @Test
  public void improvedNaming_ClassToTableName() {
    ImprovedNamingStrategy namingStrategy = new ImprovedNamingStrategy();

    for (int i = 0; i < classNames.length; i++) {
      String tableName = namingStrategy.classToTableName(classNames[i]);
      assertThat(tableName).isEqualTo(tableNames[i]);
    }
  }

  @Test
  public void improvedNaming_PropertyToColumnName() {
    ImprovedNamingStrategy namingStrategy = new ImprovedNamingStrategy();

    for (int i = 0; i < propertyNames.length; i++) {
      final String columnName = namingStrategy.propertyToColumnName(propertyNames[i]);
      assertThat(columnName).isEqualTo(columnNames[i]);
    }
  }

  @Test
  public void oracleNaming_ClassToTableName() {
    OracleNamingStrategy namingStrategy = new OracleNamingStrategy();

    for (int i = 0; i < classNames.length; i++) {
      String tableName = namingStrategy.classToTableName(classNames[i]);
      assertThat(tableName).isEqualTo(tableNames[i].toUpperCase());
    }
  }

  @Test
  public void oracleNaming_PropertyToColumnName() {
    OracleNamingStrategy namingStrategy = new OracleNamingStrategy();

    for (int i = 0; i < propertyNames.length; i++) {
      final String columnName = namingStrategy.propertyToColumnName(propertyNames[i]);
      assertThat(columnName).isEqualTo(columnNames[i].toUpperCase());
    }
  }
}
