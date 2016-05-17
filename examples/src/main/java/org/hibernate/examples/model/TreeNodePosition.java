package org.hibernate.examples.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * 계층형 구조 엔티티의 TREE 상에서의 위치를 나타냅니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 27. 오후 4:30
 */
@Embeddable
@Getter
@Setter
public class TreeNodePosition extends AbstractValueObject {

  @Column(name = "treeLevel")
  private Integer level;

  @Column(name = "treeOrder")
  private Integer order;

  public TreeNodePosition() {
    this(0, 0);
  }

  public TreeNodePosition(int level, int order) {
    this.level = level;
    this.order = order;
  }

  public TreeNodePosition(TreeNodePosition src) {
    this(src.level, src.order);
  }

  public void setPosition(int level, int order) {
    this.level = level;
    this.order = order;
  }

  @Override
  public int hashCode() {
    return HashTool.compute(level, order);
  }

  @Override
  public ToStringHelper buildStringHelper() {
    return super.buildStringHelper()
        .add("level", level)
        .add("order", order);
  }

  private static final long serialVersionUID = -5552929284039433528L;
}
