package org.hibernate.examples.mapping.associations.onetomany.list;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.examples.model.AbstractHibernateEntity;
import org.hibernate.examples.utils.HashTool;
import org.hibernate.examples.utils.ToStringHelper;

import javax.persistence.*;

/**
 * org.hibernate.examples.mapping.associations.onetomany.list.OneToManyOrderItem
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 1:18
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToManyOrderItem extends AbstractHibernateEntity<Long> {


    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String name;

    @ManyToOne
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JoinColumn(name = "orderId")
    private OneToManyOrder order;

    @Override
    public int hashCode() {
        return HashTool.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name);
    }

    private static final long serialVersionUID = -4968710913293508239L;
}
