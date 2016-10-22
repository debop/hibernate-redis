package org.hibernate.stresser.persistence.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

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
    public Player save(Player object) {
        getSession().save(object);
        return object;
    }

    @Transactional
    public void update(Player object) {
        getSession().update(object);
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
}
