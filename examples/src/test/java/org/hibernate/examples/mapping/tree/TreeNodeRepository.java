package org.hibernate.examples.mapping.tree;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import javax.persistence.QueryHint;
import java.util.List;

/**
 * org.hibernate.examples.mapping.tree.TreeNodeRepository
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 6:45
 */
public interface TreeNodeRepository extends JpaRepository<TreeNode, Long>, QueryDslPredicateExecutor<TreeNode> {

  /**
   * Tree 상에서 Root 노드 (부모가 없는 노드) 를 조회합니다.
   *
   * @return
   */
  @Query("select node from TreeNode node where node.parent is null")
  @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  List<TreeNode> findRoots();

  /**
   * Tree 상에서 Root 노드 (부모가 없는 노드) 를 조회합니다.
   * #findRoots() 와 같다.
   *
   * @return
   */
  @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
  List<TreeNode> findByParentIsNull();
}
