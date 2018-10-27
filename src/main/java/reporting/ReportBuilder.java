package reporting;

import application.Helper;
import db.DBSession;
import model.Meal;
import model.Period;
import model.Report;
import model.Serving;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import dao.PeriodDao;
import dao.ServingDao;

import javax.persistence.EntityManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

final public class ReportBuilder {

    public static final String REPODIR = "repository/";
    private final String REPODIRXML = "repository/xml/";

    private String xslResourceDaily = "/reporting/xsl/report_daily_template.xsl";
    private String xslResourceMonthly = "/reporting/xsl/report_monthly_template.xsl";
    private String xslResourceAnnually = "/reporting/xsl/report_annually_template.xsl";

    private String xmlFilename = "report_test.xml";
    private String pdfFilename = "report_test.pdf";


    public ReportBuilder() {

        try {
            File repodirFile = new File(REPODIR);
            if (!repodirFile.exists()) {
                if (!repodirFile.mkdir()) throw new IOException("Could not create dir.");

                File repodirXmlFile = new File(REPODIRXML);
                if (!repodirXmlFile.exists()) {
                    if (!repodirXmlFile.mkdir()) throw new IOException("Could not create dir.");
                }
            }

        } catch (IOException e) {
            Helper.showErrornMessage(e.getMessage());
        }
    }

    public String getREPODIR() {
        return REPODIR;
    }

    public String getXslFilenameDaily() {
        return xslResourceDaily;
    }

    public void setXslFilenameDaily(String xslFilenameDaily) {
        this.xslResourceDaily = xslFilenameDaily;
    }

    public String getXslFilenameMonthly() {
        return xslResourceMonthly;
    }

    public void setXslFilenameMonthly(String xslFilenameMonthly) {
        this.xslResourceMonthly = xslFilenameMonthly;
    }

    public String getXmlFilename() {
        return xmlFilename;
    }

    public void setXmlFilename(String xmlFilename) {
        this.xmlFilename = xmlFilename;
    }

    public String getPdfFilename() {
        return pdfFilename;
    }

    public void setPdfFilename(String pdfFilename) {
        this.pdfFilename = pdfFilename;
    }

    public void generateAnnually(int year) {

        ServingDao servingDao = new ServingDao();

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Root element
            Element rootElement = doc.createElement("report");
            doc.appendChild(rootElement);

            Node pi = doc.createProcessingInstruction
                    ("xml-stylesheet", "type=\"text/xsl\" href=\"" + xslResourceAnnually + "\"");
            doc.insertBefore(pi, rootElement);


            createElement(doc, rootElement, "year", "" + year);


            Element mealsNode = createElement(doc, rootElement, "meals");

            List<Meal> meals = servingDao.getMeals();
            for (Meal meal : meals) {
                createElement(doc, mealsNode, "meal", meal.getName());
            }


            Element countsNode = createElement(doc, rootElement, "counts");


            Map<Integer, Map<Integer, Map<Integer, Integer>>> map = servingDao.getAnnuallyCount(year);


            for (Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>> item : map.entrySet()) {

                Element countNode = createElement(doc, countsNode, "count");

                createElement(doc, countNode, "month", item.getKey().toString());

                Element personsNode = createElement(doc, countNode, "persons");


                // Patient
                Element personNode = createElement(doc, personsNode, "person");
                Element personMealsNode = createElement(doc, personNode, "meals");

                Map<Integer, Integer> mealsMap1 = item.getValue().get(1);
                for (Meal meal : meals) {
                    if (mealsMap1 != null && mealsMap1.get(meal.getId()) != null)
                        createElement(doc, personMealsNode, "meal", mealsMap1.get(meal.getId()).toString());
                    else
                        createElement(doc, personMealsNode, "meal", "0");
                }

                // Companion
                personNode = createElement(doc, personsNode, "person");
                personMealsNode = createElement(doc, personNode, "meals");

                Map<Integer, Integer> mealsMap2 = item.getValue().get(2);
                for (Meal meal : meals) {
                    if (mealsMap2 != null && mealsMap2.get(meal.getId()) != null)
                        createElement(doc, personMealsNode, "meal", mealsMap2.get(meal.getId()).toString());
                    else
                        createElement(doc, personMealsNode, "meal", "0");
                }

            }


            createFile(doc, xslResourceAnnually);


        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void generateMonthly(int year, int month) {

        ServingDao servingDao = new ServingDao();

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Root element
            Element rootElement = doc.createElement("report");
            doc.appendChild(rootElement);

            Node pi = doc.createProcessingInstruction
                    ("xml-stylesheet", "type=\"text/xsl\" href=\"" + xslResourceMonthly + "\"");
            doc.insertBefore(pi, rootElement);


            createElement(doc, rootElement, "month", Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase());


            Element mealsNode = createElement(doc, rootElement, "meals");
            List<Meal> meals = servingDao.getMeals();
            for (Meal meal : meals) {
                createElement(doc, mealsNode, "meal", meal.getName());
            }


            Element countsNode = createElement(doc, rootElement, "counts");


            Map<Integer, Map<Integer, Map<Integer, Integer>>> map = servingDao.getMonthlyCount(year, month);


            for (Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>> item : map.entrySet()) {

                Element countNode = createElement(doc, countsNode, "count");

                createElement(doc, countNode, "day", item.getKey().toString());

                Element personsNode = createElement(doc, countNode, "persons");


                // Patient
                Element personNode = createElement(doc, personsNode, "person");
                Element personMealsNode = createElement(doc, personNode, "meals");

                Map<Integer, Integer> mealsMap1 = item.getValue().get(1);
                for (Meal meal : meals) {
                    if (mealsMap1 != null && mealsMap1.get(meal.getId()) != null)
                        createElement(doc, personMealsNode, "meal", mealsMap1.get(meal.getId()).toString());
                    else
                        createElement(doc, personMealsNode, "meal", "0");
                }

                // Companion
                personNode = createElement(doc, personsNode, "person");
                personMealsNode = createElement(doc, personNode, "meals");

                Map<Integer, Integer> mealsMap2 = item.getValue().get(2);
                for (Meal meal : meals) {
                    if (mealsMap2 != null && mealsMap2.get(meal.getId()) != null)
                        createElement(doc, personMealsNode, "meal", mealsMap2.get(meal.getId()).toString());
                    else
                        createElement(doc, personMealsNode, "meal", "0");
                }

            }


            createFile(doc, xslResourceMonthly);


        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void generateDaily(LocalDate date) {

        PeriodDao periodDao = new PeriodDao();
        ServingDao servingDao = new ServingDao();

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Root element
            Element rootElement = doc.createElement("report");
            doc.appendChild(rootElement);

            Node pi = doc.createProcessingInstruction
                    ("xml-stylesheet", "type=\"text/xsl\" href=\"" + xslResourceDaily + "\"");
            doc.insertBefore(pi, rootElement);


            createElement(doc, rootElement, "date", Helper.formatDate(date));

            Element mealsNode = createElement(doc, rootElement, "meals");

            List<Meal> meals = servingDao.getMeals();
            for (Meal meal : meals) {
                createElement(doc, mealsNode, "meal", meal.getName().toUpperCase());
            }


            Element patientsNode = createElement(doc, rootElement, "patients");


            List<Period> periods = periodDao.getActivePeriods(date);

            for (Period period : periods) {

                Element patientNode = createElement(doc, patientsNode, "patient");

                createElement(doc, patientNode, "name", period.getPatient().getName());
                createElement(doc, patientNode, "dob", Helper.getAge(period.getPatient().getDob()) + "");
                createElement(doc, patientNode, "idcard", period.getPatient().getIdcard());


                Element servingsNode = createElement(doc, patientNode, "servings");
                for (Meal meal : meals) {

                    Serving servingPatient = servingDao.getSingle(date, period.getPatient(), meal);

                    Element servingNode = createElement(doc, servingsNode, "serving");

                    if (servingPatient != null) {
                        createElement(doc, servingNode, "e", "1");
                    } else {
                        createElement(doc, servingNode, "e", "0");
                    }

                    if (period.getCompanion() != null) {

                        Serving servingCompanion = servingDao.getSingle(date, period.getCompanion(), meal);

                        if (servingCompanion != null) {
                            createElement(doc, servingNode, "a", "1");
                        } else {
                            createElement(doc, servingNode, "a", "0");
                        }

                    } else {

                        createElement(doc, servingNode, "a", "0");

                    }
                }

            }

            createFile(doc, xslResourceDaily);

        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    private void createFile(Document doc, String xslResourceName) throws Exception {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(REPODIRXML + xmlFilename));

        transformer.transform(source, result);

        PDFConverter converter = new PDFConverter();
        converter.setFontDir("reporting/fonts/");

        converter.XmlToPDF(xslResourceName, REPODIRXML + xmlFilename, REPODIR + pdfFilename);

    }

    private Element createElement(Document doc, Element parent, String key, String value) {

        Element child = doc.createElement(key);
        child.appendChild(doc.createTextNode(value));
        parent.appendChild(child);

        return child;
    }

    private Element createElement(Document doc, Element parent, String key) {

        Element child = doc.createElement(key);
        parent.appendChild(child);

        return child;
    }
}
