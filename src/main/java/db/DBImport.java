package db;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.*;

import java.sql.SQLException;


public class DBImport {

    private InputStream resource;

    public DBImport(String resourceName) {
        this.resource = getClass().getResourceAsStream(resourceName);
    }

    public void fire() throws SQLException, IOException {

        EntityManager em = DBSession.getInstance().getEntitymanager();

        StringBuffer sb = new StringBuffer();

        try {

            BufferedReader buffer = new BufferedReader(new InputStreamReader(resource));

            String str = "";
            while ((str = buffer.readLine()) != null) {
                sb.append(str.trim());
            }


            String[] instructions = sb.toString().split(";");

            em.getTransaction().begin();
            for (String instruction : instructions) {
                if (!instruction.isEmpty()) {

                    System.out.println(">> " + instruction.trim());

                    Query query = em.createNativeQuery(instruction);
                    query.executeUpdate();
                }
            }
            em.getTransaction().commit();

        } catch (Exception e) {

            em.getTransaction().rollback();

            e.printStackTrace();
            throw e;
        }

    }
}