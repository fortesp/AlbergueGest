package controller;

import application.Helper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.util.Pair;
import model.Meal;
import model.Report;
import model.person.Employee;
import dao.ReportDao;
import dao.ServingDao;

import javax.persistence.NoResultException;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class ReportController extends Controller {

    private ReportDao reportDao = new ReportDao();
    private ServingDao servingDao = new ServingDao();

    @FXML
    Button btCancel;
    @FXML
    ComboBox cbEmployees;
    @FXML
    TableView tblData;
    @FXML
    ComboBox cbYear;
    @FXML
    ComboBox cbMonth;
    @FXML
    Button btGenerateReport;

    @Override
    @FXML
    void initialize() {

        try {
            btGenerateReport.setOnAction(event -> {

                generateMonthlyReport();
            });

            btCancel.setOnAction(event -> {
                closeWindow(btCancel);
            });


            // Years
            List<Integer> years = reportDao.getYears();

            ObservableList<Integer> ydata = FXCollections.observableArrayList();
            ydata.addAll(years);
            cbYear.setItems(ydata);
            cbYear.setValue(LocalDate.now().getYear());
            cbYear.valueProperty().addListener(new ChangeListener<Integer>() {
                @Override
                public void changed(ObservableValue ov, Integer ol, Integer ne) {
                    populateMonthsComboBox();
                }
            });

            // Months
            populateMonthsComboBox();

            cbMonth.setValue(new Pair(LocalDate.now().getMonthValue(), LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase()) {
                public String toString() {
                    return (String) getValue();
                }
            });
            cbMonth.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) tblData.getItems().clear();
                else reloadData();
            });


            // Data Table
            tblData.setSelectionModel(null);

            List<TableColumn<Object, String>> tc_list = new ArrayList<TableColumn<Object, String>>();

            TableColumn<Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>>, String> tc1 = new TableColumn(getLabel("day"));
            tc1.setSortable(false);
            tc1.setCellValueFactory(cell -> {

                return new SimpleStringProperty(cell.getValue().getKey().toString());
            });

            tblData.getColumns().add(tc1);


            List<TableColumn<Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>>, String>> tc_list_group = new ArrayList();

            TableColumn<Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>>, String> tc2 = new TableColumn(getLabel("patient"));
            tc_list_group.add(tc2);

            TableColumn<Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>>, String> tc3 = new TableColumn(getLabel("companion"));
            tc_list_group.add(tc3);

            List<Meal> meals = servingDao.getMeals();

            for (Meal meal : meals) {
                TableColumn<Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>>, String> tc = new TableColumn(meal.getName());
                tc_list_group.get(0).getColumns().addAll(tc);
                tc.setSortable(false);
                tc.setCellValueFactory(cellData -> {

                    int person_index = 1;
                    Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>> entry = cellData.getValue();

                    if (entry.getValue().get(person_index) != null && entry.getValue().get(person_index).get(meal.getId()) != null)
                        return new SimpleStringProperty(entry.getValue().get(person_index).get(meal.getId()).toString());
                    else
                        return new SimpleStringProperty("");
                });
            }

            for (Meal meal : meals) {
                TableColumn<Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>>, String> tc = new TableColumn(meal.getName());
                tc_list_group.get(1).getColumns().addAll(tc);
                tc.setSortable(false);
                tc.setCellValueFactory(cellData -> {

                    int person_index = 2;
                    Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>> entry = cellData.getValue();

                    if (entry.getValue().get(person_index) != null && entry.getValue().get(person_index).get(meal.getId()) != null)
                        return new SimpleStringProperty(entry.getValue().get(person_index).get(meal.getId()).toString());
                    else
                        return new SimpleStringProperty("");
                });
            }

            tblData.getColumns().addAll(tc_list_group);

            // -------------------------

            reloadData();

        } catch (Exception e) {
            Helper.showExceptionMessage(e);
        }
    }

    void generateAnnuallyReport() {

        btGenerateReport.setDisable(true);
        try {

            String employeeName = null;
            if (cbEmployees.getValue() != null) employeeName = ((String) cbEmployees.getValue().toString());

            if (employeeName == null || employeeName.isEmpty()) {

                cbEmployees.getStyleClass().add("error");
                Helper.showInformationMessage(getLabel("message.selectEmployee"));
                getStage(ReportController.class).showAndWait();

            } else {

                Employee employee = reportDao.findEmployeeByName(employeeName);
                if (employee == null) {
                    employee = reportDao.addEmployee(Helper.getLimitedText(Helper.getFormattedFullName((String) cbEmployees.getValue()), 50, "..."));
                    cbEmployees.getItems().add(employee);
                }

                cbEmployees.getStyleClass().remove("error");
                File fileReport = reportDao.generateAnnually(employee, (int) cbYear.getValue());

                openWindowsFile(fileReport);
            }
        } catch (Exception e) {
            Helper.showExceptionMessage(e);
        } finally {
            btGenerateReport.setDisable(false);
        }

    }

    void generateMonthlyReport() {

        btGenerateReport.setDisable(true);
        try {

            String employeeName = null;
            if (cbEmployees.getValue() != null) employeeName = ((String) cbEmployees.getValue().toString());

            if (employeeName == null || employeeName.isEmpty()) {

                cbEmployees.getStyleClass().add("error");
                Helper.showInformationMessage(getLabel("message.selectEmployee"));
                getStage(ReportController.class).showAndWait();

            } else {

                Employee employee = reportDao.findEmployeeByName(employeeName);
                if (employee == null) {
                    employee = reportDao.addEmployee(((String) cbEmployees.getValue()).trim());
                    cbEmployees.getItems().add(employee);
                }

                cbEmployees.getStyleClass().remove("error");
                File fileReport = reportDao.generateMonthly(employee, (int) cbYear.getValue(), ((Pair<Integer, String>) cbMonth.getValue()).getKey());

                openWindowsFile(fileReport);
            }
        } catch (Exception e) {
            Helper.showExceptionMessage(e);
        } finally {
            btGenerateReport.setDisable(false);
        }

    }


    void populateMonthsComboBox() {

        cbMonth.getItems().clear();

        List<Integer> months = reportDao.getMonths((int) cbYear.getValue());

        ObservableList<Object> mdata = FXCollections.observableArrayList();
        for (Integer v : months) {
            mdata.add(new Pair(v, Month.of(v).getDisplayName(TextStyle.FULL, Locale.getDefault()).toUpperCase()) {
                public String toString() {
                    return (String) getValue();
                }
            });

        }

        cbMonth.setItems(mdata);
    }

    void populateTableView() {

        tblData.getItems().clear();

        try {
            int month = ((Pair<Integer, String>) cbMonth.getValue()).getKey();

            Map<Integer, Map<Integer, Map<Integer, Integer>>> map = servingDao.getMonthlyCount(Integer.parseInt(cbYear.getValue().toString()), month);

            ObservableList<Map.Entry<Integer, Map<Integer, Map<Integer, Integer>>>> data = FXCollections.observableArrayList(map.entrySet());
            tblData.getItems().addAll(data);

        } catch (NoResultException e) {
        } catch (Exception e) {
            Helper.showExceptionMessage(e);
        }

    }


    void populateEmployeesCombo() {

        List<Employee> employees = reportDao.getEmployees();
        ObservableList<Employee> data = FXCollections.observableArrayList();
        data.addAll(employees);
        cbEmployees.setItems(data);

        // Select employee report
        Report report = reportDao.getReport((int) cbYear.getValue(), ((Pair<Integer, String>) cbMonth.getValue()).getKey(),false);
        if (report != null)
            cbEmployees.setValue(report.getEmployee());
        else
            cbEmployees.setValue(null);

    }


    void reloadData() {

        populateEmployeesCombo();
        populateTableView();

    }
}