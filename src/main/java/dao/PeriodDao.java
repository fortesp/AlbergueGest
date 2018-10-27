package dao;

import db.DBSession;
import model.Period;
import model.person.Companion;
import model.person.Patient;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class PeriodDao extends Dao {


    public Period checkin(Patient patient, Companion companion) throws Exception {

        EntityManager em = DBSession.getInstance().getEntitymanager();
        try {
            Period lastPeriod = getActivePeriod(patient);

            if (lastPeriod == null) {

                em.getTransaction().begin();
                Period newPeriod = new Period(patient, LocalDate.now());

                if (companion != null) {
                    em.persist(companion);
                    newPeriod.setCompanion(companion);
                }

                em.persist(newPeriod);
                em.getTransaction().commit();
                return newPeriod;
            }

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

        return null;
    }

    public Period checkin(String name, LocalDate dob, String idcard, String companionName) throws Exception {

        Patient patient = addPatient(name, dob, idcard);

        if (companionName != null && !companionName.isEmpty()) {
            return checkin(patient, new Companion(companionName));
        } else {
            return checkin(patient, null);
        }

    }


    public Period checkin(Patient patient, String companionName, boolean dummy) throws Exception {

        if (companionName != null && !companionName.isEmpty()) {
            return checkin(patient, new Companion(companionName));
        } else {
            return checkin(patient, null);
        }

    }

    public boolean checkout(Period period, String obs) {

        EntityManager em = DBSession.getInstance().getEntitymanager();
        try {

            if (period.getCheckout() == null) {
                em.getTransaction().begin();
                period.setCheckout(LocalDate.now());
                period.setObs(obs);
                em.merge(period);
                em.getTransaction().commit();
            }
            return true;

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }

        return false;
    }


    public boolean updatePatient(Patient patient, String name, LocalDate dob, String idcard) {

        EntityManager em = DBSession.getInstance().getEntitymanager();
        try {

            em.getTransaction().begin();

            patient.setName(name);
            patient.setDob(dob);
            patient.setIdcard(idcard);
            em.getTransaction().commit();

            return true;

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }

        return false;
    }

    public Patient addPatient(String name, LocalDate dob, String idcard) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {
            em.getTransaction().begin();

            Patient newPatient = new Patient(name.trim(), dob, idcard);
            em.persist(newPatient);
            em.getTransaction().commit();

            return newPatient;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateCompanion(Companion companion, String name) {

        EntityManager em = DBSession.getInstance().getEntitymanager();
        try {

            em.getTransaction().begin();
            companion.setName(name);
            em.getTransaction().commit();

            return true;

        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        }

        return false;
    }

    public Companion addCompanion(String name) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {
            em.getTransaction().begin();

            Companion newCompanion = new Companion();

            newCompanion.setName(name.trim());
            em.persist(newCompanion);

            em.getTransaction().commit();

            return newCompanion;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public Patient findPatientByIdCard(String idcard) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        List<Patient> list = em.createQuery("SELECT p FROM Patient p WHERE p.idcard = ?0", Patient.class)
                .setParameter(0, idcard.trim())
                .setMaxResults(1)
                .getResultList();

        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public Period getLastPeriod(Patient patient) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        List<Period> list = em.createQuery("SELECT p FROM Period p WHERE p.patient = ?0 ORDER BY p.id DESC", Period.class)
                .setParameter(0, patient)
                .getResultList();

        if (list.isEmpty()) return null;
        return list.get(0);
    }


    public Period getActivePeriod(Patient patient) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        List<Period> list = em.createQuery("SELECT p FROM Period p WHERE p.patient = ?0 AND p.checkout = NULL ORDER BY p.id DESC", Period.class)
                .setParameter(0, patient)
                .setMaxResults(1)
                .getResultList();

        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public List<Period> getActivePeriods(LocalDate date) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        List<Period> periods = em.createQuery("SELECT p FROM Period p WHERE (?0 BETWEEN p.checkin AND p.checkout) OR (p.checkin <= ?0 AND p.checkout IS NULL) ORDER BY p.checkin ASC, p.id ASC", Period.class)
                .setParameter(0, date)
                .getResultList();

        return periods;
    }


}
