package org.hibernate.examples.utils;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.examples.hibernate.repository.HibernateDao;
import org.hibernate.examples.model.HibernateTreeEntity;
import org.hibernate.examples.model.TreeNodePosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * org.hibernate.examples.utils.EntityTool
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 28. 오후 2:09
 */
@Slf4j
public class EntityTool {
  private EntityTool() {
  }

  public static final String GET_LIST_BY_META_KEY = "select distinct me from %s me where :key in indices(me.metaMap)";

  public static final String GET_LIST_BY_META_VALUE = "select distinct me from %s me join me.metaMap meta where meta.value = :value";


  public static <T extends HibernateTreeEntity<T>> void updateTreeNodePosition(T entity) {
    assert (entity != null);

    TreeNodePosition np = entity.getNodePosition();
    if (entity.getParent() != null) {
      np.setLevel(entity.getParent().getNodePosition().getLevel() + 1);
      if (!entity.getParent().getChildren().contains(entity)) {
        np.setOrder(entity.getParent().getChildren().size());
      }
    } else {
      np.setPosition(0, 0);
    }
  }

  public static <T extends HibernateTreeEntity<T>> long getChildCount(HibernateDao dao, T entity) {
    DetachedCriteria dc = DetachedCriteria.forClass(entity.getClass());
    dc.add(Restrictions.eq("parent", entity));
    return dao.count(dc);
  }

  public static <T extends HibernateTreeEntity<T>> boolean hasChildren(HibernateDao dao, T entity) {
    DetachedCriteria dc = DetachedCriteria.forClass(entity.getClass());
    dc.add(Restrictions.eq("parent", entity));

    return dao.exists(entity.getClass(), dc);
  }

  public static <T extends HibernateTreeEntity<T>> void setNodeOrder(T node, int order) {
    assert (node != null);

    if (node.getParent() != null) {
      for (T child : node.getParent().getChildren()) {
        if (child.getNodePosition().getOrder() >= order) {
          child.getNodePosition().setOrder(child.getNodePosition().getOrder() + 1);
        }
      }
    }
    node.getNodePosition().setOrder(order);
  }

  public static <T extends HibernateTreeEntity<T>> void adjustChildOrders(T parent) {
    assert (parent != null);

    List<T> children = new ArrayList<T>(parent.getChildren());
    Collections.sort(children, new Comparator<T>() {
      @Override
      public int compare(T o1, T o2) {
        return o1.getNodePosition().getOrder() - o2.getNodePosition().getOrder();
      }
    });
    int order = 0;
    for (T node : children) {
      node.getNodePosition().setOrder(order);
      order++;
    }
  }

  public static <T extends HibernateTreeEntity<T>> void changeParent(T node, T oldParent, T newParent) {
    assert (node != null);

    if (oldParent != null) {
      oldParent.getChildren().remove(node);
    }
    if (newParent != null) {
      newParent.getChildren().add(node);
    }
    node.setParent(newParent);
    updateTreeNodePosition(node);
  }

  public static <T extends HibernateTreeEntity<T>> void setParent(T node, T parent) {
    assert (node != null);
    changeParent(node, node.getParent(), parent);
  }

  public static <T extends HibernateTreeEntity<T>> void insertChildNode(T parent, T child, int order) {
    assert (parent != null);
    assert (child != null);

    int ord = Math.max(0, Math.min(order, parent.getChildren().size() - 1));
    parent.addChild(child);
    setNodeOrder(child, ord);
  }

  public static <T extends HibernateTreeEntity<T>> List<T> getAncestors(T current) {
    List<T> ancestors = new ArrayList<T>();
    if (current != null) {
      T parent = current;
      while (parent != null) {
        ancestors.add(parent);
        parent = parent.getParent();
      }
    }
    return ancestors;
  }

  public static <T extends HibernateTreeEntity<T>> T getRoot(T current) {
    if (current == null)
      return current;

    T root = current;
    T parent = current.getParent();
    while (parent != null) {
      root = parent;
      parent = parent.getParent();
    }
    return root;
  }

}
