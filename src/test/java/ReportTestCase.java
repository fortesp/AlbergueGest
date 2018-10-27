import org.junit.Test;
import reporting.ReportBuilder;

import java.io.File;
import java.time.LocalDate;
import java.util.Locale;

import static junit.framework.TestCase.assertTrue;

public class ReportTestCase {


    @Test
    public void testDailyReport() throws Exception {

        try {
            ReportBuilder rp = new ReportBuilder();
            rp.generateDaily(LocalDate.now());

            File file = new File(rp.getREPODIR() + rp.getPdfFilename());
            assertTrue(file.exists());
            file.delete();

        } catch (Exception e) {

            assertTrue(false);
        }
    }

    @Test
    public void testMonthlyReport() throws Exception {

        try {
            Locale.setDefault(new Locale("es", "PA"));

            ReportBuilder rp = new ReportBuilder();
            rp.generateMonthly(LocalDate.now().getYear(), LocalDate.now().getMonth().getValue());

            File file = new File(rp.getREPODIR() + rp.getPdfFilename());
            assertTrue(file.exists());
            file.delete();

        } catch (Exception e) {

            assertTrue(false);
        }
    }

    @Test
    public void testAnnuallyReport() throws Exception {

        try {
            Locale.setDefault(new Locale("es", "PA"));

            ReportBuilder rp = new ReportBuilder();
            rp.generateAnnually(LocalDate.now().getYear());

            File file = new File(rp.getREPODIR() + rp.getPdfFilename());
            assertTrue(file.exists());
            file.delete();

        } catch (Exception e) {

            assertTrue(false);
        }
    }


}
