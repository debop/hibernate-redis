package org.hibernate.examples.mapping.tree;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.examples.AbstractJpaTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * org.hibernate.examples.mapping.tree.TreeNodeTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 3. 오후 6:48
 */
@Slf4j
@Transactional
public class TreeNodeTest extends AbstractJpaTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    TreeNodeRepository repository;

    @Test
    public void buildTreeTest() {

        TreeNode root = new TreeNode();
        root.setTitle("root");

        TreeNode child1 = new TreeNode();
        child1.setTitle("child1");

        TreeNode child2 = new TreeNode();
        child1.setTitle("child2");

        root.addChild(child1);
        root.addChild(child2);

        TreeNode child11 = new TreeNode();
        child11.setTitle("child11");

        TreeNode child12 = new TreeNode();
        child12.setTitle("child12");

        child1.addChild(child11);
        child1.addChild(child12);

        repository.saveAndFlush(root);
        em.clear();

        TreeNode node = repository.findOne(child1.getId());

        assertThat(node.getChildren().size()).isEqualTo(2);
        assertThat(node.getParent()).isEqualTo(root);


        // JPQL 로 정의된 메소드
        List<TreeNode> roots = repository.findRoots();
        assertThat(roots).hasSize(1);
        assertThat(roots.get(0)).isEqualTo(root);

        // 동적 생성
        roots = repository.findByParentIsNull();
        assertThat(roots).hasSize(1);
        assertThat(roots.get(0)).isEqualTo(root);
    }

    @Test
    public void buildTreeAndDelete() throws Exception {
        TreeNode root = new TreeNode();
        root.setTitle("root");

        TreeNode child1 = new TreeNode();
        child1.setTitle("child1");

        TreeNode child2 = new TreeNode();
        child1.setTitle("child2");

        root.addChild(child1);
        root.addChild(child2);

        TreeNode child11 = new TreeNode();
        child11.setTitle("child11");

        TreeNode child12 = new TreeNode();
        child12.setTitle("child12");

        child1.addChild(child11);
        child1.addChild(child12);

        repository.saveAndFlush(root);
        em.clear();

        TreeNode node = repository.findOne(child1.getId());
        repository.delete(node);
        em.flush();
        em.clear();

        List<TreeNode> roots = repository.findRoots();
        assertThat(roots).hasSize(1);
        assertThat(roots.get(0).getChildren()).hasSize(1);
    }
}
