package org.hibernate.stresser.persistence.dao;

import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
@Repository
public class PlayerDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    public Player get(Serializable objectId) {
        return getSession().get(Player.class, objectId);
    }

    @Transactional
    public void saveAll(int amount) {
        for (int i = 0; i <= amount; i++) {
            Player player = new Player();
            player.setName("player" + i);
            player.setUpdateTime(new Date());
            getSession().save(player);
        }
    }

    @Transactional
    public void update(int playerId, int count) {
        Player player = getSession().load(Player.class, playerId, LockOptions.UPGRADE);
        player.setUpdateTime(new Date());
        player.setCount(count);
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
