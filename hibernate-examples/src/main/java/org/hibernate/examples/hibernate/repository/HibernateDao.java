package org.hibernate.examples.hibernate.repository;

import org.hibernate.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.examples.hibernate.HibernateParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Hibernate를 직접 사용할 때 사용하는 Data Access Object의 interface입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 1:58
 */
public interface HibernateDao {

  Session getSession();

  void flush();

  /**
   * 엔티티를 로드합니다. 실제로 로드하는 것이 아니라 id 값만 가지는 Proxy를 로드합니다.
   * 사용 시에 DB에 데이터가 없는 경우에는 예외가 발생합니다.
   * ManyToOne 등 실제로 Id 값만을 필요로 할 때에 사용하면 좋습니다.
   *
   * @param clazz 엔티티 수형
   * @param id    엔티티의 Id
   * @param <T>   엔티티 수형
   * @return 엔티티의 Proxy
   */
  <T> T load(Class<T> clazz, Serializable id);

  /**
   * 엔티티를 로드합니다. 실제로 로드하는 것이 아니라 id 값만 가지는 Proxy를 로드합니다.
   * 사용 시에 DB에 데이터가 없는 경우에는 예외가 발생합니다.
   * ManyToOne 등 실제로 Id 값만을 필요로 할 때에 사용하면 좋습니다.
   *
   * @param clazz       엔티티 수형
   * @param id          엔티티의 Id
   * @param lockOptions Lock 옵션
   * @param <T>         엔티티 수형
   * @return 엔티티의 Proxy
   */
  <T> T load(Class<T> clazz, Serializable id, LockOptions lockOptions);

  /**
   * 엔티티를 로드합니다.
   *
   * @param clazz 엔티티 수형
   * @param id    엔티티의 Id
   * @param <T>   엔티티 수형
   * @return 엔티티의 Proxy
   */
  <T> T get(Class<T> clazz, Serializable id);

  /**
   * 엔티티를 로드합니다.
   *
   * @param clazz       엔티티 수형
   * @param id          엔티티의 Id
   * @param lockOptions Lock 옵션
   * @param <T>         엔티티 수형
   * @return 엔티티의 Proxy
   */
  <T> T get(Class<T> clazz, Serializable id, LockOptions lockOptions);

  /**
   * 지정한 Id 컬렉션에 해당하는 Id를 가진 Entity 들을 로드합니다. SQL 문의 in 과 같습니다.
   *
   * @param clazz entity type
   * @param ids   로드할 엔티티의 Id 들
   * @param <T>   entity type
   * @return entity list
   */
  <T> List<T> getIn(Class<T> clazz, Collection<? extends Serializable> ids);

  /**
   * 지정한 Id 컬렉션에 해당하는 Id를 가진 Entity 들을 로드합니다. SQL 문의 in 과 같습니다.
   *
   * @param clazz entity type
   * @param ids   로드할 엔티티의 Id 들
   * @param <T>   entity type
   * @return entity list
   */
  <T> List<T> getIn(Class<T> clazz, Serializable[] ids);

  /**
   * 서버 커서를 사용하는 {@link org.hibernate.ScrollableResults} 를 반환합니다.
   * 대용량 데이터에 대해 scoll을 통해 작업을 수행할 때 필요합니다.
   *
   * @param clazz entity type
   * @return {@link org.hibernate.ScrollableResults} instance
   */
  ScrollableResults scroll(Class<?> clazz);

  /**
   * DetachedCriteria로 조회된 정보를 서버 커서를 사용하는 {@link org.hibernate.ScrollableResults} 를 반환합니다.
   * 대용량 데이터에 대해 scoll을 통해 작업을 수행할 때 필요합니다.
   *
   * @return {@link org.hibernate.ScrollableResults} instance
   */
  ScrollableResults scroll(DetachedCriteria dc);

  /**
   * DetachedCriteria로 조회된 정보를 서버 커서를 사용하는 {@link org.hibernate.ScrollableResults} 를 반환합니다.
   * 대용량 데이터에 대해 scoll을 통해 작업을 수행할 때 필요합니다.
   *
   * @param scrollMode scroll mode ({@link org.hibernate.ScrollMode}
   * @return {@link org.hibernate.ScrollableResults} instance
   */
  ScrollableResults scroll(DetachedCriteria dc, ScrollMode scrollMode);

  /**
   * Criteria로 조회된 정보를 서버 커서를 사용하는 {@link org.hibernate.ScrollableResults} 를 반환합니다.
   * 대용량 데이터에 대해 scoll을 통해 작업을 수행할 때 필요합니다.
   *
   * @return {@link org.hibernate.ScrollableResults} instance
   */
  ScrollableResults scroll(Criteria criteria);

  /**
   * {@link org.hibernate.Criteria} 로 조회된 정보를 서버 커서를 사용하는 {@link org.hibernate.ScrollableResults} 를 반환합니다.
   * 대용량 데이터에 대해 scoll을 통해 작업을 수행할 때 필요합니다.
   *
   * @param scrollMode scroll mode ({@link org.hibernate.ScrollMode}
   * @return {@link org.hibernate.ScrollableResults} instance
   */
  ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode);

  /**
   * {@link org.hibernate.Query}로 조회된 정보를 서버 커서를 사용하는 {@link org.hibernate.ScrollableResults} 를 반환합니다.
   * 대용량 데이터에 대해 scoll을 통해 작업을 수행할 때 필요합니다.
   *
   * @return {@link org.hibernate.ScrollableResults} instance
   */
  ScrollableResults scroll(Query query, HibernateParameter... parameters);

  /**
   * {@link org.hibernate.Query} 로 조회된 정보를 서버 커서를 사용하는 {@link org.hibernate.ScrollableResults} 를 반환합니다.
   * 대용량 데이터에 대해 scoll을 통해 작업을 수행할 때 필요합니다.
   *
   * @param scrollMode scroll mode ({@link org.hibernate.ScrollMode}
   * @return {@link org.hibernate.ScrollableResults} instance
   */
  ScrollableResults scroll(Query query, ScrollMode scrollMode, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티의 모든 레코드를 조회합니다.
   *
   * @param clazz entity type
   * @param <T>   entity type
   * @return entity list
   */
  <T> List<T> findAll(Class<T> clazz);

  /**
   * 해당 수형의 엔티티들을 지정된 정렬 방식에 따라 조회합니다.
   *
   * @param clazz  entity type
   * @param orders ordering
   * @param <T>    entity type
   * @return entity list
   */
  <T> List<T> findAll(Class<T> clazz, Order... orders);

  /**
   * 해당 수형의 엔티티 중 정렬 후 지정된 범위에 해당하는 엔티티만 조회합니다.
   *
   * @param clazz       entity type
   * @param firstResult first result index (start from 0)
   * @param maxResult   max result size
   * @param orders      ordering
   * @param <T>         entity type
   * @return entity list
   */
  <T> List<T> findAll(Class<T> clazz, int firstResult, int maxResult, Order... orders);

  /**
   * 해당 수형의 엔티티 중 질의 {@link org.hibernate.Criteria}로 필터링된 엔티티들을 조회합니다.
   *
   * @param clazz    entity type
   * @param criteria 질의 조건
   * @param orders   ordering
   * @param <T>      entity type
   * @return entity list
   */
  <T> List<T> find(Class<T> clazz, Criteria criteria, Order... orders);

  /**
   * 해당 수형의 엔티티 중 질의 {@link org.hibernate.Criteria}로 필터링된 엔티티 중 범위에 해당하는 엔티티만 조회합니다.
   *
   * @param clazz       entity type
   * @param criteria    질의 조건
   * @param firstResult first result index (start from 0)
   * @param maxResults  max result size
   * @param orders      ordering
   * @param <T>         entity type
   * @return entity list
   */
  <T> List<T> find(Class<T> clazz, Criteria criteria, int firstResult, int maxResults, Order... orders);

  /**
   * 해당 수형의 엔티티 중 질의 {@link DetachedCriteria}로 필터링된 엔티티들을 조회합니다.
   *
   * @param clazz  entity type
   * @param dc     질의 조건
   * @param orders ordering
   * @param <T>    entity type
   * @return entity list
   */
  <T> List<T> find(Class<T> clazz, DetachedCriteria dc, Order... orders);

  /**
   * 해당 수형의 엔티티 중 질의 {@link DetachedCriteria}로 필터링된 엔티티 중 범위에 해당하는 엔티티만 조회합니다.
   *
   * @param clazz       entity type
   * @param dc          질의 조건
   * @param firstResult first result index (start from 0)
   * @param maxResults  max result size
   * @param orders      ordering
   * @param <T>         entity type
   * @return entity list
   */
  <T> List<T> find(Class<T> clazz, DetachedCriteria dc, int firstResult, int maxResults, Order... orders);

  /**
   * 해당 수형의 엔티티 중 질의 {@link org.hibernate.Query}로 필터링된 엔티티들을 조회합니다.
   *
   * @param clazz      entity type
   * @param query      질의
   * @param parameters parameters for query
   * @param <T>        entity type
   * @return entity list
   */
  <T> List<T> find(Class<T> clazz, Query query, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티 중 질의 {@link org.hibernate.Query}로 필터링된 엔티티 중 범위에 해당하는 엔티티만 조회합니다.
   *
   * @param clazz       entity type
   * @param query       질의
   * @param firstResult first result index (start from 0)
   * @param maxResults  max result size
   * @param parameters  parameters for query
   * @param <T>         entity type
   * @return entity list
   */
  <T> List<T> find(Class<T> clazz, Query query, int firstResult, int maxResults, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티 중 HQL 질의로 필터링된 엔티티들을 조회합니다.
   *
   * @param clazz      entity type
   * @param hql        hibernate query languate
   * @param parameters parameters for query
   * @param <T>        entity type
   * @return entity list
   */
  <T> List<T> findByHql(Class<T> clazz, final String hql, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티 중 HQL 질의로 필터링된 엔티티 중 범위에 해당하는 엔티티만 조회합니다.
   *
   * @param clazz       entity type
   * @param hql         hibernate query languate
   * @param firstResult first result index (start from 0)
   * @param maxResults  max result size
   * @param parameters  parameters for query
   * @param <T>         entity type
   * @return entity list
   */
  <T> List<T> findByHql(Class<T> clazz, final String hql, int firstResult, int maxResults, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티 중 NamedQuery 질의로 필터링된 엔티티들을 조회합니다.
   *
   * @param clazz      entity type
   * @param queryName  name of NamedQuery
   * @param parameters parameters for query
   * @param <T>        entity type
   * @return entity list
   */
  <T> List<T> findByNamedQuery(Class<T> clazz, String queryName, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티 중 NamedQuery 질의로 필터링된 엔티티 중 범위에 해당하는 엔티티만 조회합니다.
   *
   * @param clazz       entity type
   * @param queryName   name of NamedQuery
   * @param firstResult first result index (start from 0)
   * @param maxResults  max result size
   * @param parameters  parameters for query
   * @param <T>         entity type
   * @return entity list
   */
  <T> List<T> findByNamedQuery(Class<T> clazz, final String queryName, int firstResult, int maxResults, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티 중 native query string 질의로 필터링된 엔티티들을 조회합니다.
   *
   * @param clazz      entity type
   * @param sqlString  native query String
   * @param parameters parameters for query
   * @param <T>        entity type
   * @return entity list
   */
  <T> List<T> findBySQLString(Class<T> clazz, final String sqlString, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티 중 native query string 질의로 필터링된 엔티티 중 범위에 해당하는 엔티티만 조회합니다.
   *
   * @param clazz       entity type
   * @param sqlString   native query String
   * @param firstResult first result index (start from 0)
   * @param maxResults  max result size
   * @param parameters  parameters for query
   * @param <T>         entity type
   * @return entity list
   */
  <T> List<T> findBySQLString(Class<T> clazz, final String sqlString, int firstResult, int maxResults, HibernateParameter... parameters);

  /**
   * 해당 수헝을 {@link Example} 로 질의를 수행합니다.
   *
   * @param clazz   entity type
   * @param example example instance for query
   * @param <T>     entity type
   * @return entity list
   */
  <T> List<T> findByExample(Class<T> clazz, Example example);

  /**
   * 해당 엔티티 수형의 정보 중 해당 페이지 범위만 조회를 수행합니다.
   *
   * @param clazz    entity type
   * @param criteria criteria
   * @param pageable Pageable 정보
   * @param <T>      entity type
   * @return Paging list
   */
  <T> Page<T> getPage(Class<T> clazz, Criteria criteria, Pageable pageable);

  /**
   * 해당 엔티티 수형의 정보 중 해당 페이지 범위만 조회를 수행합니다.
   *
   * @param clazz    entity type
   * @param dc       DetachedCriteria instance
   * @param pageable Pageable 정보
   * @param <T>      entity type
   * @return Paging list
   */
  <T> Page<T> getPage(Class<T> clazz, DetachedCriteria dc, Pageable pageable);

  /**
   * 해당 엔티티 수형의 정보 중 해당 페이지 범위만 조회를 수행합니다.
   *
   * @param clazz    entity type
   * @param query    Query instance
   * @param pageable Pageable 정보
   * @param <T>      entity type
   * @return Paging list
   */
  <T> Page<T> getPage(Class<T> clazz, Query query, Pageable pageable, HibernateParameter... parameters);


  /**
   * 해당 엔티티 수형의 정보 중 해당 페이지 범위만 조회를 수행합니다.
   *
   * @param clazz      entity type
   * @param hql        query string by Hibernate Query Language
   * @param pageable   Pagable 정보
   * @param parameters parameters for Query
   * @param <T>        entity type
   * @return Paging list
   */
  <T> Page<T> getPageByHql(Class<T> clazz, String hql, Pageable pageable, HibernateParameter... parameters);

  /**
   * 해당 엔티티 수형의 정보 중 해당 페이지 범위만 조회를 수행합니다.
   *
   * @param clazz      entity type
   * @param queryName  name of NamedQuery
   * @param pageable   Pageable 정보
   * @param parameters parameters for Query
   * @param <T>        entity type
   * @return Paging list
   */
  <T> Page<T> getPageByNamedQuery(Class<T> clazz, final String queryName, Pageable pageable, HibernateParameter... parameters);

  /**
   * 해당 엔티티 수형의 정보 중 해당 페이지 범위만 조회를 수행합니다.
   *
   * @param clazz      entity type
   * @param sqlString  native query string
   * @param pageable   Pageable 정보
   * @param parameters parameters for Query
   * @param <T>        entity type
   * @return Paging list
   */
  <T> Page<T> getPageBySQLString(Class<T> clazz, final String sqlString, Pageable pageable, HibernateParameter... parameters);

  /**
   * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
   *
   * @param criteria 조회 조건
   * @return 조회된 엔티티
   */
  <T> T findUnique(Class<T> clazz, Criteria criteria);

  /**
   * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
   *
   * @param dc 조회 조건
   * @return 조회된 엔티티
   */
  <T> T findUnique(Class<T> clazz, DetachedCriteria dc);

  /**
   * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
   *
   * @param query 조회 조건
   * @return 조회된 엔티티
   */
  <T> T findUnique(Class<T> clazz, Query query, HibernateParameter... parameters);

  /**
   * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
   *
   * @param hql 조회 조건
   * @return 조회된 엔티티
   */
  <T> T findUniqueByHql(Class<T> clazz, String hql, HibernateParameter... parameters);

  /**
   * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
   *
   * @param queryName 쿼리 명
   * @return 조회된 엔티티
   */
  <T> T findUniqueByNamedQuery(Class<T> clazz, final String queryName, HibernateParameter... parameters);

  /**
   * 지정한 엔티티에 대한 유일한 결과를 조회합니다. (결과가 없거나, 복수이면 예외가 발생합니다.
   *
   * @param sqlString 쿼리 명
   * @return 조회된 엔티티
   */
  <T> T findUniqueBySQLString(Class<T> clazz, final String sqlString, HibernateParameter... parameters);

  /**
   * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
   *
   * @param criteria 조회 조건
   * @return 조회된 엔티티
   */
  <T> T findFirst(Class<T> clazz, Criteria criteria, Order... orders);

  /**
   * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
   *
   * @param dc 조회 조건
   * @return 조회된 엔티티
   */
  <T> T findFirst(Class<T> clazz, DetachedCriteria dc, Order... orders);

  /**
   * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
   *
   * @param query 조회 조건
   * @return 조회된 엔티티
   */
  <T> T findFirst(Class<T> clazz, Query query, HibernateParameter... parameters);

  /**
   * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
   *
   * @param hql 조회 조건
   * @return 조회된 엔티티
   */
  <T> T findFirstByHql(Class<T> clazz, String hql, HibernateParameter... parameters);

  /**
   * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
   *
   * @param queryName 쿼리 명
   * @return 조회된 엔티티
   */
  <T> T findFirstByNamedQuery(Class<T> clazz, final String queryName, HibernateParameter... parameters);

  /**
   * 질의 조건에 만족하는 첫번째 엔티티를 반환합니다.
   *
   * @param sqlString 쿼리 명
   * @return 조회된 엔티티
   */
  <T> T findFirstBySQLString(Class<T> clazz, final String sqlString, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티가 존재하는지 여부를 반환합니다.
   *
   * @param clazz entity type
   * @return 엔티티가 있으면 true, 없으면 false
   */
  boolean exists(Class<?> clazz);

  /**
   * 해당 수형의 엔티티가 존재하는지 여부를 반환합니다.
   *
   * @param clazz    entity type
   * @param criteria Criteria
   * @return 엔티티가 있으면 true, 없으면 false
   */
  boolean exists(Class<?> clazz, Criteria criteria);

  /**
   * 해당 수형의 엔티티가 존재하는지 여부를 반환합니다.
   *
   * @param clazz entity type
   * @param dc    DetachedCriteria
   * @return 엔티티가 있으면 true, 없으면 false
   */
  boolean exists(Class<?> clazz, DetachedCriteria dc);

  /**
   * 해당 수형의 엔티티가 존재하는지 여부를 반환합니다.
   *
   * @param query      Query
   * @param parameters Parameter for query
   * @return 엔티티가 있으면 true, 없으면 false
   */
  boolean exists(Class<?> clazz, Query query, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티가 존재하는지 여부를 반환합니다.
   *
   * @param hql        Hibernate Query Language
   * @param parameters Parameter for query
   * @return 엔티티가 있으면 true, 없으면 false
   */
  boolean existsByHql(Class<?> clazz, String hql, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티가 존재하는지 여부를 반환합니다.
   *
   * @param queryName  Name of NamedQuery
   * @param parameters Parameter for query
   * @return 엔티티가 있으면 true, 없으면 false
   */
  boolean existsByNamedQuery(Class<?> clazz, final String queryName, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티가 존재하는지 여부를 반환합니다.
   *
   * @param sqlString  native query string
   * @param parameters Parameter for query
   * @return 엔티티가 있으면 true, 없으면 false
   */
  boolean existsBySQLString(Class<?> clazz, final String sqlString, HibernateParameter... parameters);

  /**
   * 해당 수형의 엔티티의 갯수
   *
   * @param clazz entity type
   * @return enitty count
   */
  long count(Class<?> clazz);

  /**
   * 해당 질의에 해당하는 엔티티의 갯수
   *
   * @param criteria Criteria
   * @return enitty count
   */
  long count(Criteria criteria);

  long count(DetachedCriteria dc);

  long count(Query query, HibernateParameter... parameters);

  long countByHql(String hql, HibernateParameter... parameters);

  long countByNamedQuery(final String queryName, HibernateParameter... parameters);

  long countBySQLString(final String sqlString, HibernateParameter... parameters);


  Object merge(Object entity);

  @Transactional
  void persist(Object entity);

  @Transactional
  Serializable save(Object entity);

  @Transactional
  void saveOrUpdate(Object entity);

  @Transactional
  void update(Object entity);

  @Transactional
  void delete(Object entity);

  @Transactional
  void deleteById(Class<?> clazz, Serializable id);

  @Transactional
  void deleteAll(Class<?> clazz);

  @Transactional
  void deleteAll(Collection<?> entities);

  @Transactional
  void deleteAll(Class<?> clazz, Criteria criteria);

  @Transactional
  void deleteAll(Class<?> clazz, DetachedCriteria dc);


  /**
   * Cascade 적용 없이 엔티티들을 모두 삭제합니다.
   */
  @Transactional
  int deleteAllWithoutCascade(Class<?> clazz);

  /**
   * 쿼리를 실행합니다.
   *
   * @param query      실행할 Query
   * @param parameters 인자 정보
   * @return 실행에 영향 받은 행의 수
   */
  @Transactional
  int executeUpdate(Query query, HibernateParameter... parameters);

  /**
   * 지정한 HQL 구문 (insert, update, del) 을 수행합니다.
   *
   * @param hql        수행할 HQL 구문
   * @param parameters 인자 정보
   * @return 실행에 영향 받은 행의 수
   */
  @Transactional
  int executeUpdateByHql(String hql, HibernateParameter... parameters);

  /**
   * 지정한 쿼리 구문 (insert, update, del) 을 수행합니다.
   *
   * @param queryName  수행할 Query 명
   * @param parameters 인자 정보
   * @return 실행에 영향 받은 행의 수
   */
  @Transactional
  int executeUpdateByNamedQuery(final String queryName, HibernateParameter... parameters);

  /**
   * 지정한 쿼리 구문 (insert, update, del) 을 수행합니다.
   *
   * @param sqlString  수행할 Query
   * @param parameters 인자 정보
   * @return 실행에 영향 받은 행의 수
   */
  @Transactional
  int executeUpdateBySQLString(final String sqlString, HibernateParameter... parameters);

  /**
   * 질의어에 해당하는 엔티티를 로드한 후 Projection을 거쳐 원하는 수형의 객체로 변환합니다.
   * 예: Distinct count, Id만 조회 등등
   *
   * @param projectClass   최종 변환된 수형
   * @param projectionList projection 작업 목록
   * @param criteria       질의
   * @param <P>            대상 수형
   * @return Projection된 수형
   */
  <P> P reportOne(Class<P> projectClass, ProjectionList projectionList, Criteria criteria);

  /**
   * 질의어에 해당하는 엔티티를 로드한 후 Projection을 거쳐 원하는 수형의 객체로 변환합니다.
   * 예: Distinct count, Id만 조회 등등
   *
   * @param projectClass   최종 변환된 수형
   * @param projectionList projection 작업 목록
   * @param dc             질의
   * @param <P>            대상 수형
   * @return Projection된 수형
   */
  <P> P reportOne(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc);

  /**
   * 질의어에 해당하는 엔티티를 로드한 후 Projection을 거쳐 원하는 수형의 객체로 변환합니다.
   * 예: Distinct count, Id만 조회 등등
   *
   * @param projectClass   최종 변환된 수형
   * @param projectionList projection 작업 목록
   * @param criteria       질의
   * @param <P>            대상 수형
   * @return Projection된 수형
   */
  <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, Criteria criteria);

  /**
   * 질의어에 해당하는 엔티티를 로드한 후 Projection을 거쳐 원하는 수형의 객체로 변환합니다.
   * 예: Distinct count, Id만 조회 등등
   *
   * @param projectClass   최종 변환된 수형
   * @param projectionList projection 작업 목록
   * @param criteria       질의
   * @param firstResult    반환 레코드의 첫번째 인덱스 (0부터 시작)
   * @param maxResults     반환 레코드의 최대 갯수
   * @param <P>            대상 수형
   * @return Projection된 수형
   */
  <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, Criteria criteria, int firstResult, int maxResults);

  /**
   * 질의어에 해당하는 엔티티를 로드한 후 Projection을 거쳐 원하는 수형의 객체로 변환합니다.
   * 예: Distinct count, Id만 조회 등등
   *
   * @param projectClass   최종 변환된 수형
   * @param projectionList projection 작업 목록
   * @param dc             질의
   * @param <P>            대상 수형
   * @return Projection된 수형
   */
  <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc);

  /**
   * 질의어에 해당하는 엔티티를 로드한 후 Projection을 거쳐 원하는 수형의 객체로 변환합니다.
   * 예: Distinct count, Id만 조회 등등
   *
   * @param projectClass   최종 변환된 수형
   * @param projectionList projection 작업 목록
   * @param dc             질의
   * @param firstResult    반환 레코드의 첫번째 인덱스 (0부터 시작)
   * @param maxResults     반환 레코드의 최대 갯수
   * @param <P>            대상 수형
   * @return Projection된 수형
   */
  <P> List<P> reportList(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc, int firstResult, int maxResults);

  /**
   * 질의어에 해당하는 엔티티를 로드한 후 Projection을 거쳐 원하는 수형의 객체로 변환합니다.
   * 예: Distinct count, Id만 조회 등등
   *
   * @param projectClass   최종 변환된 수형
   * @param projectionList projection 작업 목록
   * @param criteria       질의
   * @param pageable       Pageable 정보
   * @param <P>            대상 수형
   * @return Projection된 수형
   */
  <P> Page<P> reportPage(Class<P> projectClass, ProjectionList projectionList, Criteria criteria, Pageable pageable);

  /**
   * 질의어에 해당하는 엔티티를 로드한 후 Projection을 거쳐 원하는 수형의 객체로 변환합니다.
   * 예: Distinct count, Id만 조회 등등
   *
   * @param projectClass   최종 변환된 수형
   * @param projectionList projection 작업 목록
   * @param dc             질의
   * @param pageable       Pageable 정보
   * @param <P>            대상 수형
   * @return Projection된 수형
   */
  <P> Page<P> reportPage(Class<P> projectClass, ProjectionList projectionList, DetachedCriteria dc, Pageable pageable);

}
