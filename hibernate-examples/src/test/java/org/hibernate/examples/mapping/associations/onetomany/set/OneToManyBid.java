package org.hibernate.examples.mapping.associations.onetomany.set;

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
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * org.hibernate.examples.mapping.associations.onetomany.set.OneToManyBid
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 29. 오후 2:04
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToManyBid extends AbstractHibernateEntity<Long> {

    protected OneToManyBid() {}

    public OneToManyBid(OneToManyBiddingItem item, BigDecimal amount) {
        this.item = item;
        this.item.getBids().add(this);
        this.amount = amount;
    }

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @ManyToOne
    @LazyToOne(LazyToOneOption.PROXY)
    private OneToManyBiddingItem item;

    @Column(nullable = false)
    private BigDecimal amount;

    @Transient
    private Timestamp timestamp;

    @Override
    public int hashCode() {
        return HashTool.compute(amount);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("amount", amount);
    }

    private static final long serialVersionUID = 6305076394221526269L;
}
