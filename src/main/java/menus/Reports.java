package menus;


import dbData.Appointment;
import dbData.Customer;
import dbData.DBData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static dbData.DBData.dbData;


public class Reports implements Initializable {

    @FXML
    private ListView<String> customerListView;
    @FXML
    private ListView<String> typesListView;
    @FXML
    private ComboBox<String> usersComboBox;
    @FXML
    private ListView<Appointment> usersListView;
    @FXML
    private Label reportTypeLbl;
    @FXML
    private AnchorPane mainPane;

    private Map<String, List<Appointment>> userSchedules;
    private final ArrayList<String> userNamesList = new ArrayList<String>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showCustomerReport();//Types report will be the default report to show up

        ArrayList<Appointment> allAppointmentsList = new ArrayList<>(DBData.getReportsAppointmentList());
        allAppointmentsList.addAll(dbData.getAppointmentList()); //Add remaining appointments from current user.

        //Setup types report
        Map<String, Integer> AppointmentTypesMap = allAppointmentsList.stream().
                collect(Collectors.groupingBy(Appointment::getType, Collectors.summingInt(num -> 1)));
        ObservableList<String> typesList = FXCollections.observableArrayList();
        AppointmentTypesMap.entrySet().forEach((typeMap -> typesList.add(typeMap.getKey() + ": "+typeMap.getValue())));
        typesListView.setItems(typesList);


        //Setup users report
        userSchedules = allAppointmentsList.stream()
                .filter((Appointment appointment) -> dbData.findUserByID(appointment.getUserID()).isPresent())
                .collect(Collectors.groupingBy( (Appointment appointment) ->
                        dbData.findUserByID(appointment.getUserID()).get().getUsername()
        ));

        userSchedules.forEach((key, val) -> userNamesList.add(key));
        usersComboBox.getItems().setAll(userNamesList);
        usersComboBox.getSelectionModel().selectFirst();
        if(!userSchedules.isEmpty())
            usersListView.setItems(FXCollections.observableList(userSchedules.get(usersComboBox.getSelectionModel()
                    .getSelectedItem())));

        //Setup customers report
        Map<String, Integer> customerMap = dbData.getCustomerList().stream().
                collect(Collectors.groupingBy(Customer::getCityName, Collectors.summingInt(num -> 1)));
        ObservableList<String> customerList = FXCollections.observableArrayList();
        customerMap.entrySet().forEach((typeMap -> customerList.add(typeMap.getKey() + ": "+typeMap.getValue())));
        customerListView.setItems(customerList);
    }

    /**
     * Displays the types report.
     * @param event The event that triggered the method.
     */
    @FXML
    private void showTypesReport(ActionEvent event) {
        reportTypeLbl.setText("Appointment Types Report");
        usersComboBox.setVisible(false);
        usersListView.setVisible(false);
        customerListView.setVisible(false);
        typesListView.setVisible(true);
    }

    /**
     * Displays the user report.
     * @param event
     */
    @FXML
    private void showUserReport(ActionEvent event) {
        reportTypeLbl.setText("Users Report");
        customerListView.setVisible(false);
        typesListView.setVisible(false);
        usersComboBox.setVisible(true);
        usersListView.setVisible(true);
        updateUsers(new ActionEvent());
    }

    /**
     * Displays the customer report.
     */
    @FXML
    private void showCustomerReport() {
        reportTypeLbl.setText("Customers Per City Report");
        usersComboBox.setVisible(false);
        usersListView.setVisible(false);
        typesListView.setVisible(false);
        customerListView.setVisible(true);
    }


    @FXML
    private void openMainMenu(ActionEvent actionEvent) {
        MainMenu.openMainMenu(mainPane);
    }

    /**
     * Updates the ListView with the newly selected user data.
     * @param event The event that triggered the method.
     */
    public void updateUsers(ActionEvent event) {
        if(!userNamesList.isEmpty() && !usersComboBox.getSelectionModel().isEmpty()) {

            usersListView.setItems(FXCollections.observableList(userSchedules.get(usersComboBox.getSelectionModel()
                    .getSelectedItem())));
        }
    }
}
