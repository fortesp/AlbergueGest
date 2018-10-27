package dao;

import db.DBSession;
import model.Report;
import model.person.Employee;
import reporting.ReportBuilder;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public class ReportDao extends Dao {

    public List<Employee> getEmployees() {

        try {

            EntityManager em = DBSession.getInstance().getEntitymanager();

            List<Employee> employees = em.createQuery("SELECT e FROM Employee e ORDER BY name ASC", Employee.class)
                    .getResultList();

            return employees;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }

    public Employee findEmployeeByName(String name) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        List<Employee> list = em.createQuery("SELECT e FROM Employee e WHERE UPPER(e.name) = ?0", Employee.class)
                .setParameter(0, name.trim().toUpperCase())
                .setMaxResults(1)
                .getResultList();

        if (list.isEmpty()) return null;
        return list.get(0);
    }

    public Employee addEmployee(String name) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {
            em.getTransaction().begin();

            Employee newEmployee = new Employee();

            newEmployee.setName(name);
            em.persist(newEmployee);

            em.getTransaction().commit();

            return newEmployee;

        } catch (Exception e) {
            em.getTransaction().rollback();
        }

        return null;
    }

    public boolean updateEmployee(Employee employee, String name) {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {
            em.getTransaction().begin();
            employee.setName(name);
            em.getTransaction().commit();

            return true;

        } catch (Exception e) {
        }

        return false;
    }

    public boolean removeEmployee(Employee employee) {

        try {
            super.remove(employee);
            return true;

        } catch (Exception e) {
        }

        return false;
    }



    public List<Integer> getYears() {

        try {
            EntityManager em = DBSession.getInstance().getEntitymanager();

            List<Integer> years = em.createQuery("SELECT DISTINCT YEAR(s.date) FROM Serving s GROUP BY s.date", Integer.class)
                    .getResultList();

            return years;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }

    public List<Integer> getMonths(int year) {

        try {
            EntityManager em = DBSession.getInstance().getEntitymanager();

            List<Integer> months = em.createQuery("SELECT DISTINCT MONTH(s.date) FROM Serving s WHERE YEAR(s.date) = ?0 GROUP BY s.date", Integer.class)
                    .setParameter(0, year)
                    .getResultList();

            return months;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }


    public List<Report> getReports(LocalDate date, boolean daily) {

        try {

            EntityManager em = DBSession.getInstance().getEntitymanager();

            List<Report> reports = em.createQuery("FROM Report r WHERE r.date = ?0 AND r.detailed=?1 ORDER BY r.id DESC", Report.class)
                    .setParameter(0, date)
                    .setParameter(1, daily)
                    .getResultList();

            return reports;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }

    public Report getReport(int year, int month, boolean daily) {

        try {
            EntityManager em = DBSession.getInstance().getEntitymanager();

            Report report = em.createQuery("FROM Report r WHERE YEAR(r.date) = ?0 AND MONTH(r.date) = ?1 AND r.detailed=?2 ORDER BY r.id DESC", Report.class)
                    .setParameter(0, year)
                    .setParameter(1, month)
                    .setParameter(2, daily)
                    .setMaxResults(1).getSingleResult();

            return report;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }

    public Report getReport(LocalDate date, boolean daily) {

        try {

            EntityManager em = DBSession.getInstance().getEntitymanager();

            Report report = em.createQuery("FROM Report r WHERE r.date = ?0 AND r.detailed=?1 ORDER BY r.id DESC", Report.class)
                    .setParameter(0, date)
                    .setParameter(1, daily)
                    .setMaxResults(1).getSingleResult();

            return report;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }

    public Report getReport(LocalDate date, Employee employee) throws Exception {

        try {
            EntityManager em = DBSession.getInstance().getEntitymanager();

            Report report = em.createQuery("SELECT r FROM Report r WHERE r.date = ?0 AND r.employee = ?1", Report.class)
                    .setParameter(0, date)
                    .setParameter(1, employee)
                    .getSingleResult();

            return report;

        } catch (NoResultException e) {
        } catch (Exception e) {
            throw e;
        }

        return null;
    }

    public File generateDaily(Employee employee, LocalDate date) throws Exception {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {
            Report report = getReport(date, true);

            em.getTransaction().begin();
            if (report == null) {

                report = new Report(employee, date);
                report.setDetailed(true);

                em.persist(report);

            } else {
                report.setEmployee(employee);
            }
            em.getTransaction().commit();

            ReportBuilder rb = new ReportBuilder();
            rb.setXmlFilename("Relatorio_Diario_(" + date.toString() + ").xml");
            rb.setPdfFilename("Relatorio_Diario_(" + date.toString() + ").pdf");
            rb.generateDaily(date);

            return new File(rb.getREPODIR() + rb.getPdfFilename());

        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

    }


    public File generateMonthly(Employee employee, int year, int month) throws Exception {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {

            LocalDate date = LocalDate.now();

            Report report = getReport(date, employee);

            if (report == null) {
                em.getTransaction().begin();

                report = new Report(employee, date);
                report.setDetailed(false);

                em.persist(report);
                em.getTransaction().commit();
            }

            ReportBuilder rb = new ReportBuilder();
            rb.setXmlFilename("Relatorio_Mensal_" + month + "_(" + date.toString() + ").xml");
            rb.setPdfFilename("Relatorio_Mensal_" + month + "_(" + date.toString() + ").pdf");
            rb.generateMonthly(year, month);

            Desktop.getDesktop().open(new File(rb.getREPODIR() + rb.getPdfFilename()));

            return new File(rb.getREPODIR() + rb.getPdfFilename());
        }
        catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }

    }


    public File generateAnnually(Employee employee, int year) throws Exception {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        try {

            LocalDate date = LocalDate.now();

            Report report = getReport(date, employee);

            if (report == null) {
                em.getTransaction().begin();

                report = new Report(employee, date);
                report.setDetailed(false);

                em.persist(report);
                em.getTransaction().commit();
            }

            ReportBuilder rb = new ReportBuilder();
            rb.setXmlFilename("Relatorio_Anual_" + year + "_(" + date.toString() + ").xml");
            rb.setPdfFilename("Relatorio_Anual_" + year + "_(" + date.toString() + ").pdf");
            rb.generateAnnually(year);

            return new File(rb.getREPODIR() + rb.getPdfFilename());

           // Desktop.getDesktop().open(new File(rb.getREPODIR() + rb.getPdfFilename()));
        }
        catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

}
