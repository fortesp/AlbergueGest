import application.Helper;
import db.DBImport;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertTrue;

public class HelperTestCase {

    @Test
    public void formattedFullNameTest() {

        String result = Helper.getFormattedFullName("pedro   daniel fortes goncalves");
        Assert.assertEquals("Pedro Daniel Fortes Goncalves", result);
    }

    @Test
    public void limitedTextTest() {

        String result = Helper.getLimitedText("pedrodanielfortesgoncalvespedrodanielfortesgoncalvespedrodanielfortesgoncalvespedrodanielfortesgoncalves", 50, "...");
        Assert.assertEquals("pedrodanielfortesgoncalvespedrodanielfortesgonc...", result);
    }

    @Test
    public void resetDataBaseTest() {

        String dir = "db";

        try {
            File filedir = new File(dir);
            File filedirbkp = new File(dir + "_bkp");

            deleteFile(filedirbkp);
            try {
                FileUtils.copyDirectory(filedir, filedirbkp);
            } catch (Exception e) {
            }
            deleteFile(filedir);

            // Import
            DBImport imp = new DBImport("/db/schema.sql");
            imp.fire();

        } catch (Exception e) {

            assertTrue(false);
        }
    }


    private static void deleteFile(File file) {

        if (file.isDirectory()) {

            if (file.list().length == 0) {
                file.delete();
            } else {

                String files[] = file.list();
                for (String temp : files) {
                    File f = new File(file, temp);
                    deleteFile(f);
                }

                if (file.list().length == 0) {
                    if (file.delete())
                        System.out.println("File is deleted : " + file.getAbsolutePath());
                }
            }
        } else {

            if (file.delete())
                System.out.println("File is deleted : " + file.getAbsolutePath());
        }
    }


}
