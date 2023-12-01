package menus;


import dbData.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import static dataIO.DBConnector.getLoggedInUser;
import static dbData.DBData.dbData;

public class MainMenu implements Initializable {

    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> customerIDColumn;
    @FXML
    private TableColumn<Appointment, String> customerNameColumn;
    @FXML
    private TableColumn<Appointment, String> startDateColumn;
    @FXML
    private TableColumn<Appointment, String> endDateColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumn;
    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private AnchorPane mainPane;

    private final Predicate<Appointment> checkMonthly = (appointment) ->
            appointment.getStartDate().toLocalDateTime().isBefore(LocalDateTime.now().plusMonths(1));
    private final Predicate<Appointment> checkWeekly = (appointment) ->
            appointment.getStartDate().toLocalDateTime().isBefore(LocalDateTime.now().plusWeeks(1));

    private ObservableList<Appointment> monthlyList;
    private ObservableList<Appointment> weeklyList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthlyList = FXCollections.observableList(dbData.getAppointmentList().stream().filter(checkMonthly)
                .collect(Collectors.toCollection(ArrayList::new)));

        weeklyList = FXCollections.observableList(dbData.getAppointmentList().stream().filter(checkWeekly)
                .collect(Collectors.toCollection(ArrayList::new)));


        appointmentTableView.setItems(dbData.getAppointmentList());
        customerIDColumn.setCellValueFactory(cellData -> cellData.getValue().customerIDProperty());
        customerNameColumn.setCellValueFactory(cellData -> dbData.getCustomerList().stream().filter(customer ->
                        customer.getCustomerID() == cellData.getValue().getCustomerID())
                .findFirst().get().customerNameProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        endDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    }

    public void openCustomerRecords() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menusFXML/CustomerRecords.fxml"));
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene( new Scene(loader.load()));
            CustomerRecords controller = loader.getController();
            loader.setController(controller);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void openAppointmentsMenu() {
        if(!dbData.getCustomerList().isEmpty()){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/menusFXML/Appointments.fxml"));
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.setScene( new Scene(loader.load()));
                Appointments controller = loader.getController();
                loader.setController(controller);
                stage.show();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please add a customer before setting an " +
                    "appointment.");
            alert.setTitle("No Customers");
            alert.setHeaderText("There are no customers to schedule for.");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (okayPressed.isEmpty());
        }
    }

    public void openReportsMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menusFXML/Reports.fxml"));
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene( new Scene(loader.load()));
            Reports controller = loader.getController();
            loader.setController(controller);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void openLoginMenu() {
        try {
            getLoggedInUser().reset();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Lang", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menusFXML/LoginScreen.fxml"), resourceBundle);
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene( new Scene(loader.load()));
            Login controller = loader.getController();
            loader.setController(controller);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAllAppointments() {
        appointmentTableView.setItems(dbData.getAppointmentList());
    }

    @FXML
    public void showMonthlyAppointments() {
        appointmentTableView.setItems(monthlyList);
    }

    @FXML
    public void showWeeklyAppointments() {
        appointmentTableView.setItems(weeklyList);
    }

    public static void openMainMenu(Pane mainPane){
        try {
            FXMLLoader loader = new FXMLLoader(MainMenu.class.getResource("/menusFXML/MainMenu.fxml"));
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene( new Scene(loader.load()));
            MainMenu controller = loader.getController();
            loader.setController(controller);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
