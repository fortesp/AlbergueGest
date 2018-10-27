package controller;

import application.Helper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import model.Period;
import model.person.Companion;
import model.person.Patient;
import dao.PeriodDao;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class PeriodController extends Controller {

    private PeriodDao periodDao = new PeriodDao();

    private Period editPeriod;

    enum Mode {
        CHECKINPATIENT,
        UPDATEFIELDS
    }

    private Mode mode = Mode.CHECKINPATIENT; // Default

    @FXML
    private GridPane gpMain;
    @FXML
    private TextField txtPatientName;
    @FXML
    private TextField txtCompanionName;
    @FXML
    private DatePicker dpDob;
    @FXML
    private TextField txtIdCard;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;
    @FXML
    private CheckBox chkCompanion;
    @FXML
    private Button btDel;
    @FXML
    private Button btSearch;

    @FXML
    void initialize() {


        btSearch.setOnKeyPressed(event -> {

            txtIdCard.getStyleClass().remove("error");
        });

        btSearch.setOnAction(event -> {

            if (txtIdCard.getText().isEmpty()) {

                txtIdCard.getStyleClass().add("error");
                Helper.showInformationMessage(getLabel("message.idcardRequired"));

            } else {

                Patient foundPatient = periodDao.findPatientByIdCard(txtIdCard.getText());
                if (foundPatient != null) { // Found

                    Period lastPeriod = periodDao.getLastPeriod(foundPatient);

                    if (lastPeriod != null && lastPeriod.getCheckout() == null) {
                        txtIdCard.getStyleClass().remove("error");
                        Helper.showInformationMessage(getLabel("message.checkoutNotDone"));
                        loadFieldValues(lastPeriod);
                    } else {

                        txtIdCard.getStyleClass().remove("error");
                        loadFieldValues(lastPeriod);
                        chkCompanion.setDisable(false);

                    }
                } else { // Not found
                    txtIdCard.getStyleClass().add("error");
                    Helper.showErrornMessage(getLabel("message.patientNotfound"));
                }

            }
        });


        chkCompanion.setOnAction(event -> {
            txtCompanionName.setDisable(!chkCompanion.isSelected());
            if (!chkCompanion.isSelected()) txtCompanionName.setText("");
        });

        btCancel.setOnAction(event -> {
            closeWindow(btCancel);
        });

        btSave.setOnAction(event -> {

            //  Field validations ---
            boolean error = false;
            Patient foundPatient = null;

            if (txtPatientName.getText().isEmpty()) {
                txtPatientName.getStyleClass().add("error");
                error = true;
            } else txtPatientName.getStyleClass().remove("error");

            if (dpDob.getValue() == null) {
                dpDob.getStyleClass().add("error");
                error = true;
            } else if(Helper.getAge(((LocalDate) dpDob.getValue())) < 11) {
                Helper.showInformationMessage(getLabel("personStage.minimumDobBirth"));
                dpDob.getStyleClass().add("error");
                error = true;
            } else dpDob.getStyleClass().remove("error");

            if (txtIdCard.getText().isEmpty() || !Pattern.matches(getLabel("regex.idcard").trim(), txtIdCard.getText().trim().toUpperCase())) {

                txtIdCard.getStyleClass().add("error");
                error = true;

            } else {

                foundPatient = periodDao.findPatientByIdCard(txtIdCard.getText());

                if ((foundPatient != null && editPeriod != null) && foundPatient != editPeriod.getPatient()) {
                    Helper.showErrornMessage(getLabel("message.idcardExists") + "\r\n(" + foundPatient + ")");
                    txtIdCard.getStyleClass().add("error");
                    error = true;
                } else txtIdCard.getStyleClass().remove("error");
            }

            if (chkCompanion.isSelected() && txtCompanionName.getText().isEmpty()) {
                txtCompanionName.getStyleClass().add("error");
                error = true;
            } else txtCompanionName.getStyleClass().remove("error");


            if (error) return;

            // Limit texts
            txtPatientName.setText(Helper.getLimitedText(Helper.getFormattedFullName(txtPatientName.getText()), 80, "..."));

            if(!txtCompanionName.isDisable())
                txtCompanionName.setText(Helper.getLimitedText(Helper.getFormattedFullName(txtCompanionName.getText()), 80, "..."));

            // --------------

            MainController mainController = ((MainController) this.getController(MainController.class));

            try {

                if (foundPatient != null) { //  Update

                    Period period = editPeriod;

                    periodDao.updatePatient(period.getPatient(), txtPatientName.getText(), dpDob.getValue(), txtIdCard.getText());

                    if (chkCompanion.isSelected()) {
                        if (period.getCompanion() != null) {
                            if (mode == Mode.UPDATEFIELDS)
                                periodDao.updateCompanion(period.getCompanion(), txtCompanionName.getText());
                        } else {
                            Companion newCompanion = periodDao.addCompanion(txtCompanionName.getText());
                            period.setCompanion(newCompanion);
                        }
                    }

                    if (mode == Mode.CHECKINPATIENT) {

                        periodDao.checkin(editPeriod.getPatient(), txtCompanionName.getText(), false);
                    }

                    mainController.reloadData();

                } else { // New checkin

                    periodDao.checkin(txtPatientName.getText(), dpDob.getValue(), txtIdCard.getText(), txtCompanionName.getText());

                    mainController.reloadData();
                    mainController.tblData.scrollTo(mainController.tblData.getItems().size());
                }


            } catch (Exception e) {
                Helper.showExceptionMessage(e);
            } finally {
                closeWindow(btCancel);
            }

        });


    }

    void setMode(Mode mode) {
        this.mode = mode;
    }

    void resetFieldValues() {

        txtPatientName.setText("");
        txtCompanionName.setText("");
        txtIdCard.setText("");
        chkCompanion.setSelected(false);
        chkCompanion.setDisable(false);
        txtCompanionName.setDisable(true);
        dpDob.setValue(null);
        btSearch.setDisable(false);

        txtPatientName.getStyleClass().remove("error");
        txtCompanionName.getStyleClass().remove("error");
        txtIdCard.getStyleClass().remove("error");
        dpDob.getStyleClass().remove("error");
    }


    void loadFieldValues(Period period) {

        resetFieldValues();

        editPeriod = period;

        txtPatientName.setText(period.getPatient().getName());
        txtIdCard.setText(period.getPatient().getIdcard());
        dpDob.setValue(period.getPatient().getDob());
        btSearch.setDisable(true);

        if (period.getCompanion() != null) {
            txtCompanionName.setText(period.getCompanion().getName());
            chkCompanion.setSelected(true);
            chkCompanion.setDisable(true);
            txtCompanionName.setDisable(false);
        }

    }


}