package controller;

import application.Helper;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import model.*;
import model.person.Employee;
import model.person.Person;
import dao.PeriodDao;
import dao.ReportDao;
import dao.ServingDao;
import reporting.ReportBuilder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;


public class MainController extends Controller {

    private PeriodDao periodDao = new PeriodDao();
    private ServingDao servingDao = new ServingDao();
    private ReportDao reportDao = new ReportDao();

    @FXML
    TableView tblData;
    @FXML
    DatePicker dpDate;
    @FXML
    Button btPrev;
    @FXML
    Button btNext;
    @FXML
    Button btNew;
    @FXML
    ComboBox cbEmployees;
    @FXML
    Button btMonthlyCount;
    @FXML
    private MenuItem miClose;
    @FXML
    private GridPane gpLateralInfo;
    @FXML
    private Label lblPatientName;
    @FXML
    private Label lblAge;
    @FXML
    private Label lblIdCard;
    @FXML
    private Label lblWithCompanion;
    @FXML
    private Label lblCompanionName;
    @FXML
    private Label lblCheckin;
    @FXML
    private Label lblCheckout;
    @FXML
    private TextArea txtObs;
    @FXML
    private Button btCheckout;
    @FXML
    private Button btCheckin;
    @FXML
    private Button btGenerateReport;
    @FXML
    private MenuItem miOptions;
    @FXML
    private MenuItem miNewPatient;
    @FXML
    private MenuItem miMonthlyCount;
    @FXML
    private MenuItem miDailyReport;
    @FXML
    private MenuItem miMonthlyReport;
    @FXML
    private MenuItem miAnnualReport;
    @FXML
    private MenuItem miManageEmployees;
    @FXML
    private MenuItem miAbout;
    @FXML
    private MenuItem miOpenReportDirectory;

    @FXML
    void initialize() {

        try {

            miManageEmployees.setOnAction(event -> {
                ((OptionController) this.getController(OptionController.class)).reloadData();
                getStage(OptionController.class).showAndWait();
            });

            miOpenReportDirectory.setOnAction(event -> {
                openWindowsFile(new File(ReportBuilder.REPODIR));
            });


            txtObs.setTextFormatter(new TextFormatter<String>(change ->
                    change.getControlNewText().length() <= 499 ? change : null));

            gpLateralInfo.setVisible(false);

            miAbout.setOnAction(event -> {
                getStage(AboutController.class).showAndWait();
            });

            miClose.setOnAction(event -> {
                System.exit(0);
            });

            dpDate.setEditable(false);

            btPrev.setOnAction(event -> {

                dpDate.setValue(dpDate.getValue().minusDays(1));
                btNew.setDisable(true);
                miNewPatient.setDisable(true);
            });

            dpDate.setValue(LocalDate.now());

            dpDate.setOnAction(event -> {

                btNext.setDisable(!(dpDate.getValue().compareTo(LocalDate.now()) < 0));

                if (dpDate.getValue().compareTo(LocalDate.now()) > 0) {
                    tblData.setDisable(true);
                    btGenerateReport.setDisable(true);
                    btNew.setDisable(true);
                } else {
                    tblData.setDisable(false);
                    btGenerateReport.setDisable(false);
                    btNew.setDisable(false);
                    miNewPatient.setDisable(false);

                    reloadData();
                }
            });


            btNext.setDisable(true);
            btNext.setOnAction(event -> {
                dpDate.setValue(dpDate.getValue().plusDays(1));
                btNew.setDisable(false);
            });

            btMonthlyCount.setOnAction(event -> {
                ((ReportController) this.getController(ReportController.class)).populateTableView();
                getStage(ReportController.class).showAndWait();
            });

            btNew.setOnAction(event -> {
                ((PeriodController) this.getController(PeriodController.class)).resetFieldValues();
                getStage(PeriodController.class).showAndWait();
            });

            miNewPatient.setOnAction(event -> {
                getStage(PeriodController.class).showAndWait();
            });
            miDailyReport.setOnAction(event -> {
                generateDailyReport();
            });
            miMonthlyReport.setOnAction(event -> {
                ((ReportController) this.getController(ReportController.class)).generateMonthlyReport();
            });
            miAnnualReport.setOnAction(event -> {
                ((ReportController) this.getController(ReportController.class)).generateAnnuallyReport();
            });
            miMonthlyCount.setOnAction(event -> {
                ((ReportController) this.getController(ReportController.class)).populateTableView();
                getStage(ReportController.class).showAndWait();
            });
            miOptions.setOnAction(event -> {
                ((OptionController) this.getController(OptionController.class)).reloadData();
                getStage(OptionController.class).showAndWait();
            });


            btCheckin.setOnAction(event -> {

                Period period = null;
                Map.Entry<Period, Map<Person, List<Serving>>> item = (Map.Entry<Period, Map<Person, List<Serving>>>) tblData.getSelectionModel().getSelectedItem();
                if (item != null) {
                    period = item.getKey();

                    try {
                        if (Helper.confirmMessage(btCheckout.getText(), getLabel("message.areyousure"))) { // Are you sure you want to recheckin?

                            Period newPeriod = periodDao.checkin(period.getPatient(), period.getCompanion());

                            if(newPeriod != null) {
                                Helper.showInformationMessage(getLabel("message.successCheckin"));

                                txtObs.setDisable(true);
                                btCheckout.setDisable(true);
                                btCheckin.setDisable(true);

                                dpDate.setValue(newPeriod.getCheckin());
                                populateTableView();

                                tblData.getSelectionModel().selectLast();
                                tblData.scrollTo(tblData.getItems().size());

                            } else {

                                Helper.showErrornMessage(getLabel("message.checkinDoneAlready"));
                            }
                        }

                    } catch (Exception e) {
                        Helper.showExceptionMessage(e);
                    }
                }
            });

            btCheckout.setOnAction(event -> {

                Period period = null;
                Map.Entry<Period, Map<Person, List<Serving>>> item = (Map.Entry<Period, Map<Person, List<Serving>>>) tblData.getSelectionModel().getSelectedItem();
                if (item != null) {
                    period = item.getKey();

                    try {
                        if (Helper.confirmMessage(btCheckout.getText(), getLabel("message.areyousure"))) { // Are you sure you want to checkout?

                                periodDao.checkout(period, txtObs.getText());

                                //Helper.showInformationMessage(Start.labels.getString("message.successCheckout"));
                                tblData.refresh();

                                lblCheckout.setText(Helper.formatDate(period.getCheckout()));
                                txtObs.setDisable(true);
                                btCheckout.setDisable(true);
                                btCheckin.setDisable(false);
                        }
                    } catch (Exception e) {
                        Helper.showExceptionMessage(e);
                    }

                }
            });

            btGenerateReport.setOnAction(event -> {

                generateDailyReport();
            });


            // Table View -----------------
            TableColumn<Map.Entry<Period, Map<Person, List<Serving>>>, String> tc2 = new TableColumn(getLabel("name"));
            tc2.setCellValueFactory(cellData -> {

                Period period = cellData.getValue().getKey();
                String str = Helper.getLimitedText(period.getPatient().getName(), 50, "...") + "\r\n";

                if (period.getCompanion() != null) {
                    str += " + " + Helper.getLimitedText(period.getCompanion().getName(), 50, "...") + "\r\n";
                }
                return new SimpleStringProperty(str);
            });

            TableColumn<Map.Entry<Period, Map.Entry<Person, List<Serving>>>, String> tc3 = new TableColumn(getLabel("age"));

            tc3.setCellValueFactory(cellData -> {

                LocalDate dt = cellData.getValue().getKey().getPatient().getDob();
                if (dt != null)
                    return new SimpleStringProperty(Helper.getAge(dt) + "");
                return new SimpleStringProperty("");
            });

            TableColumn<Map.Entry<Period, Map<Person, List<Serving>>>, String> tc4 = new TableColumn(getLabel("idcard"));
            tc4.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey().getPatient().getIdcard()));

            tblData.getColumns().addAll(tc2, tc3, tc4);


            List<TableColumn<Map.Entry<Period, Map<Person, List<Serving>>>, String>> tc_list_group = new ArrayList();
            TableColumn<Map.Entry<Period, Map<Person, List<Serving>>>, String> tc5 = new TableColumn(getLabel("patient"));
            tc_list_group.add(tc5);
            TableColumn<Map.Entry<Period, Map<Person, List<Serving>>>, String> tc6 = new TableColumn(getLabel("companion"));
            tc_list_group.add(tc6);

            // Populate Meals
            List<Meal> meals = servingDao.getMeals();

            populateTableViewMeals(meals, tc_list_group, 0); // Patient
            populateTableViewMeals(meals, tc_list_group, 1); // Companion
            // ---

            TableColumn<Map.Entry<Period, Map<Person, List<Serving>>>, String> tc7 = new TableColumn(getLabel("observations"));
            tc7.setCellValueFactory(cellData -> new SimpleStringProperty(Helper.getLimitedText(cellData.getValue().getKey().getObs(), 40, "...")));

            tc_list_group.add(tc7);

            tblData.getColumns().addAll(tc_list_group);


            tblData.setOnMouseClicked(event -> {

                Period period = null;
                Map.Entry<Period, Map<Person, List<Serving>>> item = (Map.Entry<Period, Map<Person, List<Serving>>>) tblData.getSelectionModel().getSelectedItem();

                if (item != null) {
                    period = item.getKey();

                    if (event.getClickCount() == 2) { // Mouse clicked twice

                        if (period != null) {

                            PeriodController periodController = ((PeriodController) this.getController(PeriodController.class));
                            periodController.loadFieldValues(period);
                            periodController.setMode(PeriodController.Mode.UPDATEFIELDS);
                            getStage(PeriodController.class).showAndWait();
                        }
                    }
                }
            });


            tblData.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

                Period period = null;
                Map.Entry<Period, Map<Person, List<Serving>>> item = (Map.Entry<Period, Map<Person, List<Serving>>>) tblData.getSelectionModel().getSelectedItem();

                if (item != null) {
                    period = item.getKey();

                    // Update layout
                    lblPatientName.setText(period.getPatient().getName());
                    lblAge.setText(Helper.getAge(period.getPatient().getDob()) + " " + getLabel("years"));
                    lblIdCard.setText(getLabel("idcard") + " " + period.getPatient().getIdcard());

                    if (period.getCompanion() != null) {
                        lblWithCompanion.setText(getLabel("withCompanion"));
                        lblCompanionName.setText(period.getCompanion().getName());
                        lblCompanionName.setVisible(true);
                    } else {
                        lblWithCompanion.setText(getLabel("withoutCompanion"));
                        lblCompanionName.setVisible(false);
                    }

                    lblCheckin.setText(Helper.formatDate(period.getCheckin()));

                    if (period.getCheckout() != null) { // Re-checkin button active

                        Period activePeriod = periodDao.getActivePeriod(period.getPatient());

                        lblCheckout.setText(Helper.formatDate(period.getCheckout()));
                        txtObs.setDisable(true);

                        if (activePeriod == null) { // Activate Re-checkin button
                            btCheckout.setDisable(true);
                            btCheckin.setDisable(false);
                        } else { // Disable both

                            btCheckout.setDisable(true);
                            btCheckin.setDisable(true);
                        }

                    } else {    // Checkout button active

                        lblCheckout.setText("-");
                        txtObs.setDisable(false);
                        btCheckout.setDisable(false);
                        btCheckin.setDisable(true);
                    }

                    txtObs.setText(period.getObs());

                    gpLateralInfo.setVisible(true);
                }
            });


            reloadData();
            // ---------------



        } catch (Exception e) {
            Helper.showExceptionMessage(e);
        }
    }
    // ----------------------------------------


    void populateEmployeesCombo() {

        List<Employee> employees = reportDao.getEmployees();
        ObservableList<Employee> data = FXCollections.observableArrayList();
        data.addAll(employees);
        cbEmployees.setItems(data);

        // Select employee report
        Report report = reportDao.getReport(dpDate.getValue(), true);
        if (report != null)
            cbEmployees.setValue(report.getEmployee());
        else
            cbEmployees.setValue(null);
    }



    private void generateDailyReport() {

        btGenerateReport.setDisable(true);
        try {

            String employeeName = null;
            if(cbEmployees.getValue() != null)  employeeName = ((String)cbEmployees.getValue().toString()).trim();

            if (employeeName == null || employeeName.isEmpty()) {

                cbEmployees.getStyleClass().add("error");
                Helper.showInformationMessage(getLabel("message.selectEmployee"));

            } else {

                Employee employee = reportDao.findEmployeeByName(employeeName);

                if(employee == null) {
                    employee = reportDao.addEmployee(Helper.getLimitedText(Helper.getFormattedFullName((String)cbEmployees.getValue()), 50 ,"..."));
                    cbEmployees.getItems().add(employee);
                }

                cbEmployees.getStyleClass().remove("error");
                File fileReport = reportDao.generateDaily(employee, dpDate.getValue());

                openWindowsFile(fileReport);
            }

        } catch (Exception e) {
            Helper.showExceptionMessage(e);
        } finally {
            btGenerateReport.setDisable(false);
        }
    }


    // ----------------------------------------
    private void populateTableViewMeals(List<Meal> meals, List<TableColumn<Map.Entry<Period, Map<Person, List<Serving>>>, String>> tc_list_group, int index) {

        for (Meal meal : meals) {

            TableColumn<Map.Entry<Period, Map<Person, List<Serving>>>, Boolean> tc = new TableColumn(meal.getName());
            tc_list_group.get(index).getColumns().addAll(tc);

            tc.setCellFactory(cellData -> {

                CheckBoxTableCell chkcell = new CheckBoxTableCell<Map.Entry<Period, Map<Person, List<Serving>>>, Boolean>() {
                    @Override
                    public void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);

                        TableRow<Map.Entry<Period, Map<Person, List<Serving>>>> currentRow = getTableRow();

                        if (currentRow != null && currentRow.getItem() != null) {

                            if (currentRow.getItem().getKey().getCheckout() != null) { // Checkout

                                currentRow.setOpacity(0.5);
                                this.setDisable(true);

                            } else { // Not checkout

                                currentRow.setOpacity(1.0);
                                this.setDisable(false);

                                if (index == 1) { // Companion cell group
                                    if (currentRow.getItem().getKey().getCompanion() == null) {
                                        this.setDisable(true);
                                    } else {
                                        this.setDisable(false);
                                    }
                                }
                            }


                        }
                    }
                };

                return chkcell;
            });


            tc.setCellValueFactory(cellData -> {

                Boolean flg = false;
                BooleanProperty selected = new SimpleBooleanProperty(flg);

                Period period = cellData.getValue().getKey();

                Person person;
                if (index == 0)
                    person = period.getPatient();
                else
                    person = period.getCompanion();


                if (person != null) {
                    List<Serving> servings = cellData.getValue().getValue().get(person);

                    for (Serving serving : servings) {
                        if (serving.getMeal() == meal) {
                            flg = true;
                            break;
                        }
                    }

                    selected = new SimpleBooleanProperty(flg);

                    selected.addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> obs, Boolean remove, Boolean isSelected) {

                            try {
                                if (period.getCheckout() == null) {
                                    if (remove) {

                                        Serving selectedServing = null;
                                        for (Serving serving : servings) {
                                            if (serving.getMeal() == meal) {
                                                selectedServing = serving;
                                                break;
                                            }
                                        }

                                        selectedServing.setMeal(null);
                                        // Serving serving = entitymanager.find(Serving.class, selectedServing.getId());
                                        servingDao.remove(selectedServing);
                                        //servings.remove(selectedServing);
                                    } else {
                                        Serving newServing = servingDao.add(person, dpDate.getValue(), meal);
                                        servings.add(newServing);
                                    }
                                }

                            } catch (Exception e) {
                                Helper.showExceptionMessage(e);
                            }
                        }
                    });

                }
                return selected;
            });


        }

    }

    // Populate Table View ----------------------------------------
    private void populateTableView() {

        tblData.getItems().clear();
        gpLateralInfo.setVisible(false);

        List<Period> periods = periodDao.getActivePeriods(dpDate.getValue());

        tblData.setDisable(periods.isEmpty());

        try {

            for (Period period : periods) {

                List<Serving> servings = servingDao.getAll(dpDate.getValue(), period.getPatient());

                Map<Period, Map<Person, List<Serving>>> map = new HashMap();

                map.put(period, new HashMap());
                map.get(period).put(period.getPatient(), servings);

                if (period.getCompanion() != null) {

                    List<Serving> servings2 = servingDao.getAll(dpDate.getValue(), period.getCompanion());
                    map.get(period).put(period.getCompanion(), servings2);
                }

                tblData.getItems().addAll(map.entrySet());
            }

        } catch (Exception e) {
            Helper.showExceptionMessage(e);
        }


    }
    // ----------------------------------------

    void reloadData() {

        populateEmployeesCombo();
        populateTableView();

    }


}
