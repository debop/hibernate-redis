package org.hibernate.examples.mapping.tree;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.model.HibernateTreeEntity;
import org.hibernate.examples.model.TreeNodePosition;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * org.hibernate.examples.mapping.tree.TreeNode
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 6:42
 */
@Entity
@org.hibernate.annotations.Cache(region = "example", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class TreeNode extends AbstractHibernateEntity<Long> implements HibernateTreeEntity<TreeNode> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Setter(AccessLevel.PROTECTED)
  private Long id;

  private String title;
  private String data;
  private String description;

  /**
   * 부모 노드
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @LazyToOne(LazyToOneOption.PROXY)
  @JoinColumn(name = "ParentId")
  private TreeNode parent;

  /**
   * 자식 노드
   * mappedBy를 정의하여, child 마다 관리되도록 해야합니다.
   */
  @OneToMany(mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true)
  @LazyCollection(LazyCollectionOption.EXTRA)
  private Set<TreeNode> children = new LinkedHashSet<TreeNode>();

  /**
   * 노드의 TreeView 상에서의 위치
   */
  TreeNodePosition nodePosition = new TreeNodePosition();

  /**
   * 자식 노드를 추가합니다.
   */
  @Override
  public void addChild(TreeNode child) {
    child.setParent(this);
    children.add(child);
  }

  /**
   * 자식 노드를 삭제합니다.
   */
  @Override
  public void removeChild(TreeNode child) {
    if (children.contains(child)) {
      children.remove(child);
      child.setParent(null);
    }
  }

  @Override
  public int hashCode() {
    return HashTool.compute(title);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("id", id)
        .add("title", title)
        .add("description", description);
  }

  private static final long serialVersionUID = 6573065680420748563L;
}
