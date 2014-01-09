package org.hibernate.examples.hibernate.repository

import org.hibernate._
import org.hibernate.criterion._
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.{Page, PageImpl, Pageable}
import org.springframework.stereotype.Repository
import scala.collection.JavaConversions._
import org.hibernate.examples.hibernate.HibernateParameter
import org.hibernate.examples.hibernate.tools.{CriteriaTool, HibernateTool}
import org.hibernate.examples.utils.Arrays

/**
 * kr.debop4s.data.hibernate.repository.HibernateDao
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 5:54
 */
@Repository
class HibernateDao {

    @Autowired
    var sessionFactory: SessionFactory = _

    def this(sessionFactory: SessionFactory) {
        this()
        this.sessionFactory = sessionFactory
    }

    def session: Session = sessionFactory.getCurrentSession

    def flush() {
        session.flush()
    }

    def load[T](clazz: Class[T], id: java.io.Serializable): T = session.load(clazz, id).asInstanceOf[T]

    def load[T](clazz: Class[T], id: java.io.Serializable, lockOptions: LockOptions): T =
        session.load(clazz, id, lockOptions).asInstanceOf[T]

    def get[T](clazz: Class[T], id: java.io.Serializable): T = session.get(clazz, id).asInstanceOf[T]

    def get[T](clazz: Class[T], id: java.io.Serializable, lockOptions: LockOptions): T =
        session.get(clazz, id, lockOptions).asInstanceOf[T]

    def getIn[T](clazz: Class[T], ids: java.io.Serializable*): List[_] = {
        val dc = CriteriaTool.addIn(DetachedCriteria.forClass(clazz), "id", ids)
        find(dc)
    }

    def scroll(clazz: Class[_]): ScrollableResults = scroll(DetachedCriteria.forClass(clazz))

    def scroll(dc: DetachedCriteria): ScrollableResults = scroll(dc, ScrollMode.FORWARD_ONLY)

    def scroll(dc: DetachedCriteria, scrollMode: ScrollMode): ScrollableResults =
        dc.getExecutableCriteria(session).scroll(scrollMode)

    def scroll(criteria: Criteria): ScrollableResults = scroll(criteria, ScrollMode.FORWARD_ONLY)

    def scroll(criteria: Criteria, scrollMode: ScrollMode = ScrollMode.FORWARD_ONLY): ScrollableResults =
        criteria.scroll(scrollMode)

    def scroll(query: Query, parameters: HibernateParameter*): ScrollableResults =
        HibernateTool.setParameters(query, parameters: _*).scroll()

    def scroll(query: Query, scrollMode: ScrollMode, parameters: HibernateParameter*): ScrollableResults =
        HibernateTool.setParameters(query, parameters: _*).scroll()

    def findAll[T](clazz: Class[T], orders: Order*): List[T] = {
        val criteria = session.createCriteria(clazz)
        if (Arrays.isEmpty(orders)) {
            criteria.list().toList.asInstanceOf[List[T]]
        } else {
            HibernateTool.addOrders(criteria, orders: _*).list().toList.asInstanceOf[List[T]]
        }
    }

    def findAll[T](clazz: Class[T], firstResult: Int, maxResults: Int, orders: Order*): List[T] = {
        val criteria = session.createCriteria(clazz)
        if (Arrays.isEmpty(orders)) {
            HibernateTool.setPaging(criteria, firstResult, maxResults).list().toList.asInstanceOf[List[T]]
        } else {
            val crit = HibernateTool.addOrders(criteria, orders: _*)
            HibernateTool.setPaging(crit, firstResult, maxResults)
                .list().toList.asInstanceOf[List[T]]
        }
    }

    def find(criteria: Criteria, orders: Order*): List[_] =
        HibernateTool.addOrders(criteria, orders: _*).list().toList

    def find(criteria: Criteria, firstResult: Int, maxResults: Int, orders: Order*): List[_] = {
        val crit = HibernateTool.addOrders(criteria, orders: _*)
        HibernateTool.setPaging(crit, firstResult, maxResults).list().toList
    }

    def find(dc: DetachedCriteria, orders: Order*): List[_] =
        HibernateTool.addOrders(dc, orders: _*)
            .getExecutableCriteria(session)
            .list().toList

    def find(dc: DetachedCriteria, firstResult: Int, maxResults: Int, orders: Order*): List[_] = {
        val crit = HibernateTool.addOrders(dc, orders: _*).getExecutableCriteria(session)
        HibernateTool.setPaging(crit, firstResult, maxResults)
            .list().toList
    }


    def find(query: Query, parameters: HibernateParameter*): List[_] =
        HibernateTool.setParameters(query, parameters: _*).list().toList

    def find(query: Query, firstResult: Int, maxResults: Int, parameters: HibernateParameter*): List[_] = {
        val q = HibernateTool.setParameters(query, parameters: _*)
        HibernateTool.setPaging(q, firstResult, maxResults).list().toList
    }

    def findByHql(hql: String, parameters: HibernateParameter*): List[_] =
        find(session.createQuery(hql), parameters: _*)

    def findByHql(hql: String, firstResult: Int, maxResults: Int, parameters: HibernateParameter*): List[_] =
        find(session.createQuery(hql), firstResult, maxResults, parameters: _*)

    def findByNamedQuery(queryName: String, parameters: HibernateParameter*): List[_] =
        find(session.getNamedQuery(queryName), parameters: _*)

    def findByNamedQuery(queryName: String, firstResult: Int, maxResults: Int, parameters: HibernateParameter*): List[_] =
        find(session.getNamedQuery(queryName), firstResult, maxResults, parameters: _*)

    def findBySQLString(sqlString: String, parameters: HibernateParameter*): List[_] =
        find(session.createSQLQuery(sqlString), parameters: _*)

    def findBySQLString(sqlString: String, firstResult: Int, maxResults: Int, parameters: HibernateParameter*): List[_] =
        find(session.createSQLQuery(sqlString), firstResult, maxResults, parameters: _*)

    def findByExample[T](clazz: Class[T], example: Example): List[T] =
        session.createCriteria(clazz).add(example).list.toList.asInstanceOf[List[T]]

    def getPage(criteria: Criteria, page: Pageable): Page[_] = {
        val countCriteria = HibernateTool.copyCriteria(criteria)
        val totalCount = count(countCriteria)

        val items = find(HibernateTool.setPaging(criteria, page), CriteriaTool.toOrders(page.getSort): _*)
        new PageImpl(items, page, totalCount)
    }

    def getPage(dc: DetachedCriteria, page: Pageable): Page[_] =
        getPage(dc.getExecutableCriteria(session), page)

    def getPage(page: Pageable, query: Query, parameters: HibernateParameter*): Page[_] = {
        val totalCount = count(query, parameters: _*)
        val items = find(HibernateTool.setPaging(query, page), parameters: _*)
        new PageImpl(items, page, totalCount)
    }

    def getPageByHql(page: Pageable, hql: String, parameters: HibernateParameter*): Page[_] =
        getPage(page, session.createQuery(hql), parameters: _*)

    def getPageByNamedQuery(page: Pageable, queryName: String, parameters: HibernateParameter*): Page[_] =
        getPage(page, session.getNamedQuery(queryName), parameters: _*)

    def getPageBySQLString(page: Pageable, sqlString: String, parameters: HibernateParameter*): Page[_] =
        getPage(page, session.createSQLQuery(sqlString), parameters: _*)

    def findUnique[T](criteria: Criteria): T =
        criteria.uniqueResult().asInstanceOf[T]

    def findUnique[T](dc: DetachedCriteria): T =
        dc.getExecutableCriteria(session).uniqueResult().asInstanceOf[T]

    def findUnique[T](query: Query, parameters: HibernateParameter*): T =
        HibernateTool.setParameters(query, parameters: _*).uniqueResult().asInstanceOf[T]

    def findUniqueByHql[T](hql: String, parameters: HibernateParameter*): T =
        findUnique[T](session.createQuery(hql), parameters: _*)

    def findUniqueByNamedQuery[T](queryName: String, parameters: HibernateParameter*): T =
        findUnique[T](session.getNamedQuery(queryName), parameters: _*)

    def findUniqueBySQLString[T](sqlString: String, parameters: HibernateParameter*): T =
        findUnique[T](session.createSQLQuery(sqlString), parameters: _*)


    def findFirst[T](criteria: Criteria, orders: Order*): T = {
        val items = find(criteria, 0, 1, orders: _*)

        if (Arrays.isEmpty(items)) null.asInstanceOf[T]
        else items(0).asInstanceOf[T]
    }

    def findFirst[T](dc: DetachedCriteria, orders: Order*): T =
        findFirst[T](dc.getExecutableCriteria(session), orders: _*)

    def findFirst[T](query: Query, parameters: HibernateParameter*): T = {
        val items = find(query, 0, 1, parameters: _*)

        if (Arrays.isEmpty(items)) null.asInstanceOf[T]
        else items(0).asInstanceOf[T]
    }

    def findFirstByHql[T](hql: String, parameters: HibernateParameter*): T =
        findUnique[T](session.createQuery(hql), parameters: _*)

    def findFirstByNamedQuery[T](queryName: String, parameters: HibernateParameter*): T =
        findUnique[T](session.getNamedQuery(queryName), parameters: _*)

    def findFirstBySQLString[T](sqlString: String, parameters: HibernateParameter*): T =
        findUnique[T](session.createSQLQuery(sqlString), parameters: _*)

    def exists(clazz: Class[_]): Boolean = findFirst(session.createCriteria(clazz)) != null

    def exists(criteria: Criteria): Boolean = findFirst(criteria) != null

    def exists(dc: DetachedCriteria): Boolean = findFirst(dc) != null

    def exists(query: Query, parameters: HibernateParameter*): Boolean =
        findFirst(query, parameters: _*) != null

    def existsByHql(hql: String, parameters: HibernateParameter*): Boolean =
        findFirstByHql(hql, parameters: _*) != null

    def existsByNamedQuery(queryName: String, parameters: HibernateParameter*): Boolean =
        findFirstByNamedQuery(queryName, parameters: _*) != null

    def existsBySQLString(sqlString: String, parameters: HibernateParameter*): Boolean =
        findFirstBySQLString(sqlString, parameters: _*) != null

    def count(clazz: Class[_]): Long = count(session.createCriteria(clazz))

    def count(criteria: Criteria): Long =
        criteria.setProjection(Projections.rowCount).uniqueResult().asInstanceOf[Long]

    def count(dc: DetachedCriteria): Long = count(dc.getExecutableCriteria(session))

    def count(query: Query, parameters: HibernateParameter*): Long =
        HibernateTool.setParameters(query, parameters: _*)
            .setResultTransformer(CriteriaSpecification.PROJECTION)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .uniqueResult()
            .asInstanceOf[Long]

    def countByHql(hql: String, parameters: HibernateParameter*): Long =
        count(session.createQuery(hql), parameters: _*)

    def countByNamedQuery(queryName: String, parameters: HibernateParameter*): Long =
        count(session.getNamedQuery(queryName), parameters: _*)

    def countBySQLString(queryString: String, parameters: HibernateParameter*): Long =
        count(session.createSQLQuery(queryString), parameters: _*)

    def merge(entity: AnyRef) {
        session.merge(entity)
    }

    def persist(entity: AnyRef) {
        session.persist(entity)
    }

    def save(entity: AnyRef): java.io.Serializable = session.save(entity)

    def saveOrUpdate(entity: AnyRef) {
        session.saveOrUpdate(entity)
    }

    def update(entity: AnyRef) {
        session.update(entity)
    }

    def delete(entity: AnyRef) {
        session.delete(entity)
    }

    def deleteById(clazz: Class[_], id: java.io.Serializable) {
        session.delete(load(clazz, id))
    }

    def deleteAll(entities: AnyRef*) {
        val s = session
        entities.foreach(entity => s.delete(entity))
    }

    def deleteAll[T](clazz: Class[T]) {
        // TODO: 삭제 시에는 id 값만 로드하여, 삭제하도록 합니다.
        deleteAll(findAll(clazz))
    }

    def deleteAll(clazz: Class[_], criteria: Criteria) {
        deleteAll(find(criteria))
    }

    def deleteAll(clazz: Class[_], dc: DetachedCriteria) {
        deleteAll(find(dc))
    }

    def deleteAllWithCascade(clazz: Class[_]) {
        session.createQuery(s"delete from ${clazz.getName}").executeUpdate()
    }

    def executeUpdate(query: Query, parameters: HibernateParameter*) {
        HibernateTool.setParameters(query, parameters: _*).executeUpdate()
    }

    def executeUpdateByHql(hql: String, parameters: HibernateParameter*) {
        executeUpdate(session.createQuery(hql), parameters: _*)
    }

    def executeUpdateByNamedQuery(queryName: String, parameters: HibernateParameter*) {
        executeUpdate(session.getNamedQuery(queryName), parameters: _*)
    }

    def executeUpdateBySQLString(sqlString: String, parameters: HibernateParameter*) {
        executeUpdate(session.createSQLQuery(sqlString), parameters: _*)
    }


    private def buildProjectionCriteria[P](projectClass: Class[P],
                                           criteria: Criteria,
                                           projections: Projection,
                                           distinctResult: Boolean = false) = {
        assert(criteria != null)

        if (distinctResult) {
            criteria.setProjection(Projections.distinct(projections))
        } else {
            criteria.setProjection(projections)
        }
        criteria.setResultTransformer(Transformers.aliasToBean(projectClass))
    }

    def reportOne[P](projectClass: Class[P], projectionList: ProjectionList, criteria: Criteria): P =
        buildProjectionCriteria(projectClass, criteria, projectionList, true)
            .uniqueResult()
            .asInstanceOf[P]

    def reportOne[P](projectClass: Class[P], projectionList: ProjectionList, dc: DetachedCriteria): P =
        reportOne(projectClass, projectionList, dc.getExecutableCriteria(session))

    def report[P](projectClass: Class[P], projectionList: ProjectionList, criteria: Criteria): List[P] =
        buildProjectionCriteria(projectClass, criteria, projectionList)
            .list()
            .toList
            .asInstanceOf[List[P]]

    def report[P](projectClass: Class[P], projectionList: ProjectionList, criteria: Criteria, firstResult: Int, maxResults: Int): List[P] = {
        val crit = buildProjectionCriteria(projectClass, criteria, projectionList)
        HibernateTool.setPaging(crit, firstResult, maxResults)
            .list()
            .toList
            .asInstanceOf[List[P]]
    }

    def report[P](projectClass: Class[P], projectionList: ProjectionList, dc: DetachedCriteria): List[P] =
        report(projectClass, projectionList, dc.getExecutableCriteria(session))

    def report[P](projectClass: Class[P], projectionList: ProjectionList, dc: DetachedCriteria, firstResult: Int, maxResults: Int): List[P] = {
        report(projectClass, projectionList, dc.getExecutableCriteria(session), firstResult, maxResults)
    }

    def reportPage[P](projectClass: Class[P], projectionList: ProjectionList, criteria: Criteria, page: Pageable): Page[P] = {
        val crit = buildProjectionCriteria(projectClass, criteria, projectionList)
        val totalCount = count(crit)
        val items = HibernateTool.setPaging(crit, page).list.toList.asInstanceOf[List[P]]

        new PageImpl[P](items, page, totalCount)
    }

    def reportPage[P](projectClass: Class[P], projectionList: ProjectionList, dc: DetachedCriteria, page: Pageable): Page[P] = {
        reportPage(projectClass, projectionList, dc.getExecutableCriteria(session), page)
    }
}
