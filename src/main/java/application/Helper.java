package application;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

final public class Helper {


    public static void showInformationMessage(String msg) {

        showMessage(msg.trim(), Alert.AlertType.INFORMATION);
    }

    public static void showErrornMessage(String msg) {

        showMessage(msg, Alert.AlertType.ERROR);
    }

    public static void showExceptionMessage(Exception e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("An error has occurred.");
        alert.setContentText(e.getMessage());

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        e.printStackTrace();

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();

        System.exit(1);
    }

    public static void showMessage(String msg, Alert.AlertType alerttype) {

        Alert alert = new Alert(alerttype);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.setContentText(msg);

        alert.showAndWait();
    }

    public static boolean confirmMessage(String title, String msg) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(msg);

        Optional<ButtonType> result = alert.showAndWait();
        return (result.get() == ButtonType.OK);
    }

    // ---------------


    public static String formatDate(LocalDate date) {

        final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(Locale.getDefault());
        return date.format(formatter);
    }

    public static int getNumberOfDaysInMonth(int year, int month) {

        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }


    public static int getAge(LocalDate date) {
        return java.time.Period.between(date, LocalDate.now()).getYears();
    }

    public static String getFormattedFullName(String fullname) {

        String result = "";

        String[] names = fullname.split(" ");
        for(String name : names) {
           if(!name.isEmpty()) result += Character.toString(name.charAt(0)).toUpperCase() + name.substring(1, name.length()) + " ";
        }

        return result.trim();
    }

    public static String getLimitedText(String text, int limit, String overrun) {

        if(text != null && text.length() > limit)
            text = text.substring(0, limit - overrun.length()) + overrun;

        return text;
    }

}
