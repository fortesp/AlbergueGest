package dao;

import db.DBSession;
import model.Meal;
import model.Serving;
import model.person.Person;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static application.Helper.getNumberOfDaysInMonth;

public class ServingDao extends Dao {


    public Serving add(Person person, LocalDate date, Meal meal) throws Exception {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {

            em.getTransaction().begin();

            Serving newServing = new Serving();
            newServing.setPerson(person);
            newServing.setDate(date);
            newServing.setMeal(meal);
            em.persist(newServing);

            em.getTransaction().commit();

            return newServing;

        } catch (Exception e) {

            throw e;
        }

    }

    public void remove(Serving serving) throws Exception {

        try {

            super.remove(serving);

        } catch (Exception e) {
        }

    }

    public Meal addMeal(String name) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {
            em.getTransaction().begin();

            Meal newMeal = new Meal();

            newMeal.setName(name);
            newMeal.setVisible(false);
            em.persist(newMeal);

            em.getTransaction().commit();

            return newMeal;

        } catch (Exception e) {
        }

        return null;
    }

    public boolean updateMeal(Meal meal, String name, boolean visible) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {
            em.getTransaction().begin();
            meal.setName(name);
            meal.setVisible(visible);
            em.getTransaction().commit();

            return true;

        } catch (Exception e) {
        }

        return false;
    }

    public boolean removeMeal(Meal meal) {

        try {

            super.remove(meal);
            return true;

        } catch (Exception e) {
        }

        return false;
    }

    public List<Meal> getMeals(boolean onlyVisible) throws Exception {


        try {

            EntityManager em = DBSession.getInstance().getEntitymanager();

            String query = "FROM Meal WHERE visible=true";
            if(!onlyVisible) query += " OR 1=1";

            List<Meal> meals = em.createQuery(query, Meal.class)
                    .getResultList();


            return meals;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;

    }

    public List<Meal> getMeals() throws Exception {

         return getMeals(true);
    }

    public Serving getSingle(LocalDate date, Person person, Meal meal) {

        try {
            EntityManager em = DBSession.getInstance().getEntitymanager();

            Serving serving = em.createQuery("SELECT s FROM Serving s WHERE s.date = ?0 AND s.person = ?1 AND s.meal = ?2", Serving.class)
                    .setParameter(0, date)
                    .setParameter(1, person)
                    .setParameter(2, meal)
                    .setMaxResults(1)
                    .getSingleResult();

            return serving;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;


    }


    public List<Serving> getAll(LocalDate date, Person person) throws Exception {

        try {
            EntityManager em = DBSession.getInstance().getEntitymanager();

            List<Serving> servings = em.createQuery("SELECT s FROM Serving s WHERE s.date = ?0 AND s.person = ?1")
                    .setParameter(0, date)
                    .setParameter(1, person)
                    .getResultList();


            return servings;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }


    public Map<Integer, Map<Integer, Map<Integer, Integer>>> getMonthlyCount(int year, int month) {

        try {
            EntityManager em = DBSession.getInstance().getEntitymanager();

            List<Object[]> countList = em.createNativeQuery(
                    "SELECT DAY(s.date)as day, p.id_persontype, s.id_meal, COUNT(*) as total FROM SERVINGS as s, PERSONS as p WHERE YEAR(s.date)=?0 AND MONTH(s.date)=?1 AND s.id_person = p.id GROUP BY MONTH(s.date), DAY(s.date), s.id_meal, p.id_persontype")
                    .setParameter(0, year)
                    .setParameter(1, month)
                    .getResultList();

            Map<Integer, Map<Integer, Map<Integer, Integer>>> map = new HashMap<>();

            for (int d = 1; d <= getNumberOfDaysInMonth(year, month); d++) {
                map.put(d, new HashMap<>());
            }

            for (Object[] iparams : countList) {

                map.get(iparams[0]).putIfAbsent((Integer) iparams[1], new HashMap<>());
                map.get(iparams[0]).get(iparams[1]).put((Integer) iparams[2], (Integer) iparams[3]);
            }

            return map;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }


    public Map<Integer, Map<Integer, Map<Integer, Integer>>> getAnnuallyCount(int year) {

        try {
            EntityManager em = DBSession.getInstance().getEntitymanager();

            List<Object[]> countList = em.createNativeQuery(
                    "SELECT MONTH(s.date)as month, p.id_persontype, s.id_meal, COUNT(*) as total FROM SERVINGS as s, PERSONS as p WHERE YEAR(s.date)=?0 AND s.id_person = p.id GROUP BY YEAR(s.date), MONTH(s.date), s.id_meal, p.id_persontype")
                    .setParameter(0, year)
                    .getResultList();

            Map<Integer, Map<Integer, Map<Integer, Integer>>> map = new HashMap<>();

            for (int m = 1; m <= 12; m++) {
                map.put(m, new HashMap<>());
            }

            for (Object[] iparams : countList) {

                map.get(iparams[0]).putIfAbsent((Integer) iparams[1], new HashMap<>());
                map.get(iparams[0]).get(iparams[1]).put((Integer) iparams[2], (Integer) iparams[3]);
            }

            return map;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }

}
