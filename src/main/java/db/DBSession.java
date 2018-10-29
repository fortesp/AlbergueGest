package db;

import application.Helper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBSession {

    private EntityManager entitymanager;
    private static DBSession instance;

    protected DBSession() throws Exception {

        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("myJPA");
        entitymanager = emfactory.createEntityManager();
    }

    public static DBSession getInstance() {

        if(instance == null) { // Lazy instatiation
            try{

                instance = new DBSession();

            }catch(org.hibernate.service.spi.ServiceException e){
                Helper.showExceptionMessage(new Exception("Error connecting to the database."));
            }
            catch(Exception e) {
                Helper.showExceptionMessage(e);
            }
        }

        return instance;
    }

    public EntityManager getEntitymanager() {

        return entitymanager;
    }

}
