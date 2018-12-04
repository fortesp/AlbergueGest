package controller;

import application.Helper;
import dao.ReportDao;
import dao.ServingDao;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import model.Meal;
import model.person.Employee;
import java.util.List;

public class OptionController extends Controller {

    private ReportDao reportDao = new ReportDao();
    private ServingDao servingDao = new ServingDao();

    private boolean changedMeals = false;

    @FXML
    private Button btNew;
    @FXML
    private Button btDel;
    @FXML
    private Button btNewMeal;
    @FXML
    private Button btDelMeal;
    @FXML
    private Button btCancel1;
    @FXML
    private Button btCancel2;
    @FXML
    private ListView lvListEmployees;
    @FXML
    private TableView tblMeals;
    @FXML
    private TabPane OptionsTab;


    @Override
    @FXML
    void initialize() {

        btCancel1.setOnAction(event -> {
            closeWindow(btCancel1);
        });

        btCancel2.setOnAction(event -> {
            closeWindow(btCancel2);
        });

        btNew.setOnAction(event -> {

            Employee newEmployee = reportDao.addEmployee("(Nuebo Responsible)");

            lvListEmployees.getItems().add(createTextField(newEmployee));
        });


        btDel.setOnAction(event -> {

            if (Helper.confirmMessage("", getLabel("message.areyousure"))) {

                TextField tf = (TextField) lvListEmployees.getSelectionModel().getSelectedItem();

                if(tf != null) {
                    Employee employee = reportDao.findEmployeeByName(tf.getText().trim());

                    if (reportDao.removeEmployee(employee)) {
                        lvListEmployees.getItems().remove(lvListEmployees.getSelectionModel().getSelectedItem());
                    } else {

                        Helper.showErrornMessage("Não pode ser eliminado. Existem registos dependentes.");
                    }
                } else {
                    Helper.showErrornMessage(getLabel("message.selectEmployee"));
                }
            }

        });

        // ---------------------

        btNewMeal.setOnAction(event -> {

            Meal newMeal = servingDao.addMeal("(Nuebo Tipo)");
            tblMeals.getItems().add(newMeal);
            changedMeals = true;
        });


        btDelMeal.setOnAction(event -> {

            if (Helper.confirmMessage("", getLabel("message.areyousure"))) {

                Meal selectedMeal = (Meal) tblMeals.getSelectionModel().getSelectedItem();

                if(selectedMeal != null) {
                    if (servingDao.removeMeal(selectedMeal)) {
                        tblMeals.getItems().remove(selectedMeal);
                        changedMeals = true;
                    } else {

                        Helper.showErrornMessage("Não pode ser eliminado. Existem registos dependentes.");

                    }
                } else {
                    Helper.showErrornMessage(getLabel("message.selectMeal"));
                }
            }

        });

        // ---------------------

        TableColumn<Meal, String> tc1 = new TableColumn(getLabel("name"));
        tc1.setSortable(false);
        tc1.setCellFactory(cellData -> {

            return new TextFieldTableCell(new StringConverter() {
                @Override
                public String toString(Object object) {
                    return object.toString();
                }

                @Override
                public Object fromString(String string) {
                    return string;
                }
            }) {


            };

        });
        tc1.setCellValueFactory(cellData -> {

            StringProperty value = new SimpleStringProperty(cellData.getValue().getName());

            value.addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                   servingDao.updateMeal(cellData.getValue(), newValue, cellData.getValue().getVisible());
                   changedMeals = true;
                }
            });

           return value;
        });


        TableColumn<Meal, Boolean> tc2 = new TableColumn(getLabel("optionStage.mealsVisible"));
        tc2.setSortable(false);
        tc2.setCellFactory(cellData -> {

            return new CheckBoxTableCell<>() {

                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                }
            };
        });

        tc2.setCellValueFactory(cellData -> {

            BooleanProperty value = new SimpleBooleanProperty(cellData.getValue().getVisible());

            value.addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                    servingDao.updateMeal(cellData.getValue(), cellData.getValue().getName(), newValue);
                }
            });

            return value;
        });

        tblMeals.getColumns().addAll(tc1, tc2);

        // -------------------

        populateEmployeesListView();

        populateMealsTableView();

    }


    public void onCloseRequest() {

        ((MainController) this.getController(MainController.class)).reloadData();
        ((ReportController) this.getController(ReportController.class)).populateEmployeesCombo();

        if(changedMeals) {
            Helper.showInformationMessage(getLabel("message.restartApp"));
            changedMeals = false;
        }
    }

    private void populateEmployeesListView() {

        lvListEmployees.getItems().clear();

        ObservableList<TextField> textfields = FXCollections.observableArrayList();

        List<Employee> employees = reportDao.getEmployees();

        for (Employee employee : employees) {
            textfields.add(createTextField(employee));
        }

        lvListEmployees.setItems(textfields);
    }


    private void populateMealsTableView() {

        tblMeals.getItems().clear();

        try {
            ObservableList<Meal> data = FXCollections.observableArrayList();

            List<Meal> meals = servingDao.getMeals(false);
            data.addAll(meals);

            tblMeals.setItems(data);

        } catch (Exception e) {

            Helper.showExceptionMessage(e);
        }

    }


    private TextField createTextField(Employee employee) {

        TextField tf = new TextField();

        tf.focusedProperty().addListener((observable, oldValue, newValue) -> {
            lvListEmployees.getSelectionModel().select(tf);
        });

        tf.textProperty().bindBidirectional(new SimpleStringProperty(employee.getName()));

        tf.textProperty().addListener((observable, oldValue, newValue) -> {

            if(!newValue.isEmpty()) {
                reportDao.updateEmployee(employee, Helper.getLimitedText(newValue, 50, "..."));
            }
        });

        return tf;
    }


    void selectTab(boolean employeesTab) {

        if(employeesTab) {
            OptionsTab.getSelectionModel().select(0);
            OptionsTab.getTabs().get(0).setDisable(false);
            OptionsTab.getTabs().get(1).setDisable(true);
        } else {
            OptionsTab.getSelectionModel().select(1);
            OptionsTab.getTabs().get(1).setDisable(false);
            OptionsTab.getTabs().get(0).setDisable(true);
        }

    }

    void reloadData() {

        populateEmployeesListView();

        ((MainController) this.getController(MainController.class)).populateEmployeesCombo();
        ((ReportController) this.getController(ReportController.class)).populateEmployeesCombo();

    }
}
