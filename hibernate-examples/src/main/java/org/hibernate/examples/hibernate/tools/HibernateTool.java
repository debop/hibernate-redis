package org.hibernate.examples.hibernate.tools;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.examples.hibernate.HibernateParameter;
import org.hibernate.examples.utils.Serializers;
import org.hibernate.internal.CriteriaImpl;
import org.springframework.data.domain.Pageable;

/**
 * Hibernate 용 Helper class
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 4. 오후 11:16
 */
public final class HibernateTool {

  public static DetachedCriteria copyDetachedCriteria(DetachedCriteria src) {
    return Serializers.copyObject(src);
  }

  public static Criteria copyCriteria(Criteria src) {
    return (CriteriaImpl) Serializers.copyObject((CriteriaImpl) src);
  }

  public static Criteria getExecutableCriteria(Session session, DetachedCriteria dc) {
    return dc.getExecutableCriteria(session);
  }

  public static Criteria getExecutableCriteria(DetachedCriteria dc, Session session, Order... orders) {
    Criteria criteria = getExecutableCriteria(session, dc);
    for (Order o : orders) {
      criteria.addOrder(o);
    }
    return criteria;
  }

  public static Criteria getExecutableCriteria(DetachedCriteria dc, Session session, Iterable<Order> orders) {
    Criteria criteria = getExecutableCriteria(session, dc);
    for (Order o : orders) {
      criteria.addOrder(o);
    }
    return criteria;
  }


  public static DetachedCriteria addOrders(DetachedCriteria dc, Order... orders) {
    for (Order o : orders) {
      dc.addOrder(o);
    }
    return dc;
  }

  public static DetachedCriteria addOrders(DetachedCriteria dc, Iterable<Order> orders) {
    for (Order o : orders) {
      dc.addOrder(o);
    }
    return dc;
  }

  public static Criteria addOrders(Criteria criteria, Order... orders) {
    for (Order o : orders) {
      criteria.addOrder(o);
    }
    return criteria;
  }

  public static Criteria addOrders(Criteria criteria, Iterable<Order> orders) {
    for (Order o : orders) {
      criteria.addOrder(o);
    }
    return criteria;
  }

  public static Query setParameters(Query query, HibernateParameter... parameters) {
    for (HibernateParameter p : parameters) {
      query.setParameter(p.getName(), p.getValue());
    }
    return query;
  }

  public static Query setParameters(Query query, Iterable<HibernateParameter> parameters) {
    for (HibernateParameter p : parameters) {
      query.setParameter(p.getName(), p.getValue());
    }
    return query;
  }


  public static Criteria setPaging(Criteria criteria, Pageable pageable) {
    return setPaging(criteria, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
  }

  public static Criteria setPaging(Criteria criteria, int firstResult, int maxResults) {
    if (firstResult >= 0)
      criteria.setFirstResult(firstResult);
    if (maxResults > 0)
      criteria.setMaxResults(maxResults);

    return criteria;
  }

  public static Query setPaging(Query query, Pageable pageable) {
    return setPaging(query, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());
  }

  public static Query setPaging(Query query, int firstResult, int maxResults) {
    if (firstResult >= 0)
      query.setFirstResult(firstResult);
    if (maxResults > 0)
      query.setMaxResults(maxResults);

    return query;
  }
}
