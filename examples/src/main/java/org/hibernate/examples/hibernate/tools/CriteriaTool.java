package org.hibernate.examples.hibernate.tools;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Criteria 빌드를 위한 Helper class
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 4. 오후 11:31
 */
public final class CriteriaTool {

  public static Criteria addIn(Criteria criteria, String propertyName, Collection<?> ids) {
    return criteria.add(Restrictions.in(propertyName, ids));
  }

  public static Criteria addIn(Criteria criteria, String propertyName, Object[] ids) {
    return criteria.add(Restrictions.in(propertyName, ids));
  }

  public static DetachedCriteria addIn(DetachedCriteria dc, String propertyName, Collection<?> ids) {
    return dc.add(Restrictions.in(propertyName, ids));
  }

  public static DetachedCriteria addIn(DetachedCriteria dc, String propertyName, Object[] ids) {
    return dc.add(Restrictions.in(propertyName, ids));
  }


  public static List<Order> toOrders(Sort sort) {
    List<Order> orders = new ArrayList<Order>();

    for (org.springframework.data.domain.Sort.Order order : sort) {
      if (order.getDirection() == Sort.Direction.ASC)
        orders.add(Order.asc(order.getProperty()));
      else
        orders.add(Order.desc(order.getProperty()));
    }

    return orders;
  }
}
