package org.hibernate.stresser.persistence.dao;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.stresser.persistence.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
@Entity
@Table(name = "player")
@Cache(region = "Player", usage = CacheConcurrencyStrategy.READ_WRITE)
public class Player extends AbstractEntity {

    private String name;
    private int[] array = new int[]{1024};
    private Date updateTime;
    private int count;

    public void setName(String name) {
        this.name = name;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
