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

package org.hibernate.examples.hibernate.repository;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.examples.hibernate.HibernateParameter;
import org.hibernate.examples.hibernate.tools.CriteriaTool;
import org.hibernate.examples.hibernate.tools.HibernateTool;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Hibernate Data Access Object 의 기본 구현체입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 4. 오후 11:01
 */
@Slf4j
@SuppressWarnings("unchecked")
@Repository
public class HibernateDaoImpl implements HibernateDao {

  @Autowired
  SessionFactory sessionFactory;

  public HibernateDaoImpl() {
  }

  public HibernateDaoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public Session getSession() {
    return sessionFactory.getCurrentSession();
  }

  @Override
  public void flush() {
    getSession().flush();
  }

  @Override
  public <T> T load(Class<T> clazz, Serializable id) {
    return (T) getSession().load(clazz, id);
  }

  @Override
  public <T> T load(Class<T> clazz, Serializable id, LockOptions lockOptions) {
    return (T) getSession().load(clazz, id, lockOptions);
  }

  @Override
  public <T> T get(Class<T> clazz, Serializable id) {
    return (T) getSession().get(clazz, id);
  }

  @Override
  public <T> T get(Class<T> clazz, Serializable id, LockOptions lockOptions) {
    return (T) getSession().get(clazz, id, lockOptions);
  }

  @Override
  public <T> List<T> getIn(Class<T> clazz, Collection<? extends Serializable> ids) {
    DetachedCriteria dc = CriteriaTool.addIn(DetachedCriteria.forClass(clazz), "id", ids);
    return find(clazz, dc);
  }

  @Override
  public <T> List<T> getIn(Class<T> clazz, Serializable[] ids) {
    DetachedCriteria dc = CriteriaTool.addIn(DetachedCriteria.forClass(clazz), "id", ids);
    return find(clazz, dc);
  }

  @Override
  public ScrollableResults scroll(Class<?> clazz) {
    return scroll(DetachedCriteria.forClass(clazz));
  }

  @Override
  public ScrollableResults scroll(DetachedCriteria dc) {
    return dc.getExecutableCriteria(getSession()).scroll();
  }

  @Override
  public ScrollableResults scroll(DetachedCriteria dc, ScrollMode scrollMode) {
    return dc.getExecutableCriteria(getSession()).scroll(scrollMode);
  }

  @Override
  public ScrollableResults scroll(Criteria criteria) {
    return criteria.scroll();
  }

  @Override
  public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
    return criteria.scroll(scrollMode);
  }

  @Override
  public ScrollableResults scroll(Query query, HibernateParameter... parameters) {
    return HibernateTool.setParameters(query, parameters).scroll();
  }

  @Override
  public ScrollableResults scroll(Query query, ScrollMode scrollMode, HibernateParameter... parameters) {
    return HibernateTool.setParameters(query, parameters).scroll(scrollMode);
  }

  @Override
  public <T> List<T> findAll(Class<T> clazz) {
    return findAll(clazz, new Order[0]);
  }

  @Override
  public <T> List<T> findAll(Class<T> clazz, Order... orders) {
    if (orders == null || orders.length == 0) {
      return (List<T>) getSession().createCriteria(clazz).list();
    } else {
      Criteria criteria = getSession().createCriteria(clazz);
      return (List<T>) HibernateTool.addOrders(criteria, orders).list();
    }
  }

  @Override
  public <T> List<T> findAll(Class<T> clazz, int firstResult, int maxResults, Order... orders) {
    Criteria criteria = getSession().createCriteria(clazz);
    if (orders == null || orders.length == 0) {
      return (List<T>) HibernateTool.setPaging(criteria, firstResult, maxResults).list();
    } else {
      criteria = HibernateTool.addOrders(criteria, orders);
      criteria = HibernateTool.setPaging(criteria, firstResult, maxResults);
      return (List<T>) criteria.list();
    }
  }

  @Override
  public <T> List<T> find(Class<T> clazz, Criteria criteria, Order... orders) {
    return (List<T>) HibernateTool.addOrders(criteria, orders).list();
  }

  @Override
  public <T> List<T> find(Class<T> clazz, Criteria criteria, int firstResult, int maxResults, Order... orders) {
    Criteria cri = HibernateTool.addOrders(criteria, orders);
    return (List<T>) HibernateTool.setPaging(cri, firstResult, maxResults).list();
  }

  @Override
  public <T> List<T> find(Class<T> clazz, DetachedCriteria dc, Order... orders) {
    return find(clazz, dc.getExecutableCriteria(getSession()), orders);
  }

  @Override
  public <T> List<T> find(Class<T> clazz, DetachedCriteria dc, int firstResult, int maxResults, Order... orders) {
    return find(clazz, dc.getExecutableCriteria(getSession()), firstResult, maxResults, orders);
  }

  @Override
  public <T> List<T> find(Class<T> clazz, Query query, HibernateParameter... parameters) {
    return (List<T>) HibernateTool.setParameters(query, parameters).list();
  }

  @Override
  public <T> List<T> find(Class<T> clazz, Query query, int firstResult, int maxResults, HibernateParameter... parameters) {
    Query q = HibernateTool.setParameters(query, parameters);
    return (List<T>) HibernateTool.setPaging(q, firstResult, maxResults).list();
  }

  @Override
  public <T> List<T> findByHql(Class<T> clazz, String hql, HibernateParameter... parameters) {
    return find(clazz, getSession().createQuery(hql), parameters);
  }

  @Override
  public <T> List<T> findByHql(Class<T> clazz, String hql, int firstResult, int maxResults, HibernateParameter... parameters) {
    return find(clazz, getSession().createQuery(hql), firstResult, maxResults, parameters);
  }

  @Override
  public <T> List<T> findByNamedQuery(Class<T> clazz, String queryName, HibernateParameter... parameters) {
    return find(clazz, getSession().getNamedQuery(queryName), parameters);
  }

  @Override
  public <T> List<T> findByNamedQuery(Class<T> clazz, String queryName, int firstResult, int maxResults, HibernateParameter... parameters) {
    return find(clazz, getSession().getNamedQuery(queryName), firstResult, maxResults, parameters);
  }

  @Override
  public <T> List<T> findBySQLString(Class<T> clazz, String sqlString, HibernateParameter... parameters) {
    return find(clazz, getSession().createSQLQuery(sqlString), parameters);
  }

  @Override
  public <T> List<T> findBySQLString(Class<T> clazz, String sqlString, int firstResult, int maxResults, HibernateParameter... parameters) {
    return find(clazz, getSession().createSQLQuery(sqlString), parameters);
  }

  @Override
  public <T> List<T> findByExample(Class<T> clazz, Example example) {
    return (List<T>) getSession().createCriteria(clazz).add(example).list();
  }

  @Override
  public <T> Page<T> getPage(Class<T> clazz, Criteria criteria, Pageable pageable) {
    Criteria countCriteria = HibernateTool.copyCriteria(criteria);
    long totalItemCount = count(countCriteria);

    List<T> items = find(clazz, criteria,
                         pageable.getPageNumber() * pageable.getPageSize(),
                         pageable.getPageSize(),
                         CriteriaTool.toOrders(pageable.getSort()).toArray(new Order[0]));
    return new PageImpl<T>(items, pageable, totalItemCount);
  }

  @Override
  public <T> Page<T> getPage(Class<T> clazz, DetachedCriteria dc, Pageable pageable) {
    return getPage(clazz, dc.getExecutableCriteria(getSession()), pageable);
  }

  @Override
  public <T> Page<T> getPage(Class<T> clazz, Query query, Pageable pageable, HibernateParameter... parameters) {
    long totalItemCount = count(query, parameters);
    List<T> items = find(clazz, query, pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize(), parameters);
    return new PageImpl<T>(items, pageable, totalItemCount);
  }

  @Override
  public <T> Page<T> getPageByHql(Class<T> clazz, String hql, Pageable pageable, HibernateParameter... parameters) {
    return getPage(clazz, getSession().createQuery(hql), pageable, parameters);
  }

  @Override
  public <T> Page<T> getPageByNamedQuery(Class<T> clazz, String queryName, Pageable pageable, HibernateParameter... parameters) {
    return getPage(clazz, getSession().getNamedQuery(queryName), pageable, parameters);
  }

  @Override
  public <T> Page<T> getPageBySQLString(Class<T> clazz, String sqlString, Pageable pageable, HibernateParameter... parameters) {
    return getPage(clazz, getSession().createSQLQuery(sqlString), pageable, parameters);
  }

  @Override
  public <T> T findUnique(Class<T> clazz, Criteria criteria) {
    return (T) criteria.uniqueResult();
  }

  @Override
  public <T> T findUnique(Class<T> clazz, DetachedCriteria dc) {
    return findUnique(clazz, dc.getExecutableCriteria(getSession()));
  }

  @Override
  public <T> T findUnique(Class<T> clazz, Query query, HibernateParameter... parameters) {
    return (T) HibernateTool.setParameters(query, parameters).uniqueResult();
  }

  @Override
  public <T> T findUniqueByHql(Class<T> clazz, String hql, HibernateParameter... parameters) {
    return findUnique(clazz, getSession().createQuery(hql), parameters);
  }

  @Override
  public <T> T findUniqueByNamedQuery(Class<T> clazz, String queryName, HibernateParameter... parameters) {
    return findUnique(clazz, getSession().getNamedQuery(queryName), parameters);
  }

  @Override
  public <T> T findUniqueBySQLString(Class<T> clazz, String sqlString, HibernateParameter... parameters) {
    return findUnique(clazz, getSession().createSQLQuery(sqlString), parameters);
  }

  @Override
  public <T> T findFirst(Class<T> clazz, Criteria criteria, Order... orders) {
    List<T> items = find(clazz, criteria, 0, 1, orders);
    return (items != null && items.size() > 0) ? items.get(0) : null;
  }

  @Override
  public <T> T findFirst(Class<T> clazz, DetachedCriteria dc, Order... orders) {
    return findFirst(clazz, dc.getExecutableCriteria(getSession()), orders);
  }

  @Override
  public <T> T findFirst(Class<T> clazz, Query query, HibernateParameter... parameters) {
    List<T> items = find(clazz, query, 0, 1, parameters);
    return (items != null && items.size() > 0) ? items.get(0) : null;
  }

  @Override
  public <T> T findFirstByHql(Class<T> clazz, String hql, HibernateParameter... parameters) {
    return findFirst(clazz, getSession().createQuery(hql), parameters);
  }

  @Override
  public <T> T findFirstByNamedQuery(Class<T> clazz, String queryName, HibernateParameter... parameters) {
    return findFirst(clazz, getSession().getNamedQuery(queryName), parameters);
  }

  @Override
  public <T> T findFirstBySQLString(Class<T> clazz, String sqlString, HibernateParameter... parameters) {
    return findFirst(clazz, getSession().createSQLQuery(sqlString), parameters);
  }

  @Override
  public boolean exists(Class<?> clazz) {
    return findFirstByHql(clazz, "from " + clazz.getName()) != null;
  }

  @Override
  public boolean exists(Class<?> clazz, Criteria criteria) {
    return findFirst(clazz, criteria) != null;
  }

  @Override
  public boolean exists(Class<?> clazz, DetachedCriteria dc) {
    return exists(clazz, dc.getExecutableCriteria(getSession()));
  }

  @Override
  public boolean exists(Class<?> clazz, Query query, HibernateParameter... parameters) {
    return findFirst(clazz, query, parameters) != null;
  }

  @Override
  public boolean existsByHql(Class<?> clazz, String hql, HibernateParameter... parameters) {
    return findFirstByHql(clazz, hql, parameters) != null;
  }

  @Override
  public boolean existsByNamedQuery(Class<?> clazz, String queryName, HibernateParameter... parameters) {
    return findFirstByNamedQuery(clazz, queryName, parameters) != null;
  }

  @Override
  public boolean existsBySQLString(Class<?> clazz, String sqlString, HibernateParameter... parameters) {
    return findFirstBySQLString(clazz, sqlString, parameters) != null;
  }

  @Override
  public long count(Class<?> clazz) {
    return count(getSession().createCriteria(clazz));
  }

  @Override
  public long count(Criteria criteria) {
    return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
  }

  @Override
  public long count(DetachedCriteria dc) {
    return count(dc.getExecutableCriteria(getSession()));
  }

  @Override
  public long count(Query query, HibernateParameter... parameters) {
    return (Long) HibernateTool.setParameters(query, parameters)
                               .setResultTransformer(CriteriaSpecification.PROJECTION)
                               .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                               .uniqueResult();
  }

  @Override
  public long countByHql(String hql, HibernateParameter... parameters) {
    return count(getSession().createQuery(hql), parameters);
  }

  @Override
  public long countByNamedQuery(String queryName, HibernateParameter... parameters) {
    return count(getSession().getNamedQuery(queryName), parameters);
  }

  @Override
  public long countBySQLString(String sqlString, HibernateParameter... parameters) {
    return count(getSession().createSQLQuery(sqlString), parameters);
  }

  @Override
  public Object merge(Object entity) {
    return getSession().merge(entity);
  }


  @Override
  public void persist(Object entity) {
    getSession().persist(entity);
  }


  @Override
  public Serializable save(Object entity) {
    return getSession().save(entity);
  }


  @Override
  public void saveOrUpdate(Object entity) {
    getSession().saveOrUpdate(entity);
  }


  @Override
  public void update(Object entity) {
    getSession().update(entity);
  }


  @Override
  public void delete(Object entity) {
    getSession().delete(entity);
  }


  @Override
  public void deleteById(Class<?> clazz, Serializable id) {
    getSession().delete(load(clazz, id));
  }


  @Override
  public void deleteAll(Class<?> clazz) {
    deleteAll(findAll(clazz));
  }


  @Override
  public void deleteAll(Collection<?> entities) {
    final Session s = getSession();
    for (Object entity : entities) {
      s.delete(entity);
    }
  }


  @Override
  public void deleteAll(Class<?> clazz, Criteria criteria) {
    deleteAll(find(clazz, criteria));
  }


  @Override
  public void deleteAll(Class<?> clazz, DetachedCriteria dc) {
    deleteAll(clazz, dc.getExecutableCriteria(getSession()));
  }


  @Override
  public int deleteAllWithoutCascade(Class<?> clazz) {
    return getSession().createQuery("delete from " + clazz.getName()).executeUpdate();
  }


  @Override
  public int executeUpdate(Query query, HibernateParameter... parameters) {
    return HibernateTool.setParameters(query, parameters).executeUpdate();
  }


  @Override
  public int executeUpdateByHql(String hql, HibernateParameter... parameters) {
    return executeUpdate(getSession().createQuery(hql), parameters);
  }


  @Override
  public int executeUpdateByNamedQuery(String queryName, HibernateParameter... parameters) {
    return executeUpdate(getSession().getNamedQuery(queryName), parameters);
  }


  @Override
  public int executeUpdateBySQLString(String sqlString, HibernateParameter... parameters) {
    return executeUpdate(getSession().createSQLQuery(sqlString), parameters);
  }

  private <P> Criteria buildProjectionCriteria(Class<P> projectClass,
                                               Criteria criteria,
                                               Projection projections,
                                               boolean distinctResult) {
    if (distinctResult) {
      criteria.setProjection(Projections.distinct(projections));
    } else {
      criteria.setProjection(projections);
    }

    return criteria.setResultTransformer(Transformers.aliasToBean(projectClass));
  }

  @Override
  public <P> P reportOne(Class<P> projectClass, ProjectionList projectionList, Criteria criteria) {
    Criteria report = buildProjectionCriteria(projectClass, criteria, projectionList, true);
    return (P) report.uniqueResult();
  }

  @Override
  public <P> P reportOne(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc) {
    return reportOne(projectClass, projectionList, dc.getExecutableCriteria(getSession()));
  }

  @Override
  public <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, Criteria criteria) {
    Criteria report = buildProjectionCriteria(projectClass, criteria, projectionList, false);
    return (List<P>) report.uniqueResult();
  }

  @Override
  public <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, Criteria criteria, int firstResult, int maxResults) {
    Criteria report = buildProjectionCriteria(projectClass, criteria, projectionList, false);
    return (List<P>) HibernateTool.setPaging(report, firstResult, maxResults).list();
  }

  @Override
  public <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc) {
    return reportList(projectClass, projectionList, dc.getExecutableCriteria(getSession()));
  }

  @Override
  public <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc, int firstResult, int maxResults) {
    return reportList(projectClass, projectionList, dc.getExecutableCriteria(getSession()), firstResult, maxResults);
  }

  @Override
  public <P> Page<P> reportPage(Class<P> projectClass, ProjectionList projectionList, Criteria criteria, Pageable pageable) {
    Criteria report = buildProjectionCriteria(projectClass, criteria, projectionList, false);
    long totalCount = count(report);
    List<P> items = HibernateTool.setPaging(report, pageable).list();
    return new PageImpl<P>(items, pageable, totalCount);
  }

  @Override
  public <P> Page<P> reportPage(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc, Pageable pageable) {
    return reportPage(projectClass, projectionList, dc.getExecutableCriteria(getSession()), pageable);
  }

}
