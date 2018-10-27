package dao;

import db.DBSession;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

abstract public class Dao {


    protected void remove(Object obj) throws Exception {

        EntityManager em = DBSession.getInstance().getEntitymanager();
        EntityTransaction tr = em.getTransaction();

        try {

            tr.begin();

            em.remove(em.contains(obj) ? obj : em.merge(obj));
            tr.commit();

        } catch (Exception e) {

            tr.rollback();
            throw e;
        }


    }

}
