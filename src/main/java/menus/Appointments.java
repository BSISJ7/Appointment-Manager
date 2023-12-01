package menus;

import dataIO.DBConnector;
import dbData.Appointment;
import dbData.Customer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

import static dataIO.DBConnector.dbConnector;
import static dbData.DBData.dbData;
import static javafx.scene.control.SpinnerValueFactory.*;

public class Appointments implements Initializable {

    @FXML
    private HBox updateHBox;
    @FXML
    private Spinner<Integer> endHourSpin;
    @FXML
    private Spinner<Integer> endMinSpin;
    @FXML
    private HBox addHBox;
    @FXML
    private Spinner<Integer> startHourSpin;
    @FXML
    private Spinner<Integer> startMinSpin;
    @FXML
    private Label appointmentTimeLbl;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField typeTextField;
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> customerNameColumn;
    @FXML
    private TableColumn<Appointment, String> customerIDColumn;
    @FXML
    private TableColumn<Appointment, String> startDateColumn;
    @FXML
    private TableColumn<Appointment, String> endDateColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumn;
    @FXML
    private TableView<Appointment> appointmentTableView;

    private static final LocalTime START_BUSINESS_HOURS = LocalTime.of(8, 0);
    private static final LocalTime END_BUSINESS_HOURS = LocalTime.of(16, 30);

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        customerComboBox.setItems(dbData.getCustomerList());
        if(!customerComboBox.getItems().isEmpty()) {
            customerComboBox.getSelectionModel().select(0);
            selectCustomer();
        }

        datePicker.setDayCellFactory(Appointments::getDatePicker);
        datePicker.setValue(LocalDate.now());
        appointmentTimeLbl.setText("00:00");

        updateFields();

        //Setup date spinners
        startHourSpin.valueFactoryProperty().set(new IntegerSpinnerValueFactory(0, 23, LocalDateTime.now()
                .getHour()));
        startMinSpin.valueFactoryProperty().set(new IntegerSpinnerValueFactory(0, 59, LocalDateTime
                .now().getMinute()+1));
        endHourSpin.valueFactoryProperty().set(new IntegerSpinnerValueFactory(0, 3, 0));
        endMinSpin.valueFactoryProperty().set(new IntegerSpinnerValueFactory(0, 59, 0));

        //Add action listener to update the end time label to all spinners
        startHourSpin.valueProperty().addListener((_, _, _) -> updateEndTime());
        startMinSpin.valueProperty().addListener((_, _, _) -> updateEndTime());
        endHourSpin.valueProperty().addListener((_, _, _) -> updateEndTime());
        endMinSpin.valueProperty().addListener((_, _, _) -> updateEndTime());
        updateEndTime();

        //Setup appointment table
        appointmentTableView.setItems(dbData.getAppointmentList());
        if(!dbData.getAppointmentList().isEmpty()) {
            appointmentTableView.getSelectionModel().selectFirst();
            updateFields();
        }
        customerIDColumn.setCellValueFactory(cellData -> cellData.getValue().customerIDProperty());
        customerNameColumn.setCellValueFactory(cellData -> dbData.getCustomerList().stream().filter(customer ->
                        customer.getCustomerID() == Integer.parseInt(cellData.getValue().customerIDProperty().get()))
                        .findFirst().get().customerNameProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        endDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
    }

    @FXML
    private void openMainMenu() {
        MainMenu.openMainMenu(mainPane);
    }

    private static DateCell getDatePicker(DatePicker picker) {
        return new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isBefore(today));
            }
        };
    }

    /**
     * Checks for time conflicts between the two appointments
     * @return The conflicting appointment if there is one.
     */
    private Optional<Appointment> checkTimeConflicts(){
        for(Appointment appointments : dbData.getAppointmentList()){
            if(isStartTimeDuring(appointments) || isEndTimeDuring(appointments)
                    || isAppointmentBetweenStartEnd(appointments))
                return Optional.of(appointments);
        }
        return Optional.empty();
    }


    @FXML
    private void updateFields() {
        if(!appointmentTableView.getItems().isEmpty() && !appointmentTableView.getSelectionModel().isEmpty()) {
            Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();
            datePicker.setValue(selectedAppointment.getStartDate().toLocalDateTime()
                    .toLocalDate());

            typeTextField.setText(selectedAppointment.getType());

            LocalDateTime startTime = selectedAppointment.getStartDate().toLocalDateTime();
            LocalDateTime endTime = selectedAppointment.getEndDate().toLocalDateTime();

            startHourSpin.getValueFactory().setValue(startTime.getHour());
            startMinSpin.getValueFactory().setValue(startTime.getMinute());

            int hours = (int) startTime.until( endTime, ChronoUnit.HOURS );
            int minutes = (int) startTime.until( endTime, ChronoUnit.MINUTES ) - hours*60;

            endHourSpin.getValueFactory().setValue(hours);
            endMinSpin.getValueFactory().setValue(minutes);

            appointmentTimeLbl.setText(getEndTime().toLocalDateTime().format(timeFormatter));

            updateEndTime();
            selectCustomer();
        }
    }

    private void selectCustomer(){
        if(appointmentTableView.getItems().isEmpty())
            return;
        for(Customer customers : dbData.getCustomerList()){
            if(appointmentTableView.getSelectionModel().getSelectedItem().getCustomerID() == customers.getCustomerID()){
                customerComboBox.getSelectionModel().select(customers);
                break;
            }
        }
    }

    private Timestamp getStartTime(){
        return Timestamp.valueOf(LocalDateTime.of(datePicker.getValue(),
                LocalTime.of(startHourSpin.getValue(), startMinSpin.getValue())));
    }

    private Timestamp getEndTime(){
        return Timestamp.valueOf(getStartTime().toLocalDateTime().plusHours(endHourSpin.getValue())
                .plusMinutes(endMinSpin.getValue()));
    }

    @FXML
    private void addAppointment() {
        Optional<Appointment> conflictAppointment = checkTimeConflicts();
        if(conflictAppointment.isPresent()){
            scheduleConflictAlert(conflictAppointment.get());
        }
        else if(isWithinBusinessHours()){
            businessHoursAlert();
        }
        else if(typeUnset()){
            typeUnsetAlert();
        }
        else if(isAppointmentBeforeCurrentTime()){
            appointmentBeforeCurrentTime();
        }
        else{
            Appointment appointment = new Appointment();
            appointment.setStartDate(getStartTime());
            appointment.setEndDate(getEndTime());
            appointment.setType(typeTextField.getText());
            appointment.setUsername(DBConnector.getLoggedInUser().getUsername());
            appointment.createNewID();
            appointment.setCustomerID(customerComboBox.getSelectionModel().getSelectedItem().getCustomerID());

            try {
                dbConnector.insertAppointment(appointment);
                appointmentTableView.getItems().add(appointment);
                appointmentTableView.getSelectionModel().selectLast();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "A new appointment has been " +
                        "scheduled for " + customerComboBox.getSelectionModel().getSelectedItem() + ".");
                alert.setTitle("Appointment Added");
                alert.setHeaderText("Your appointment has been scheduled.");
                Optional<ButtonType> okayPressed = alert.showAndWait();
                while (okayPressed.isEmpty()) ;
                updateHBox.setVisible(true);
                addHBox.setVisible(false);
                appointmentTableView.setDisable(false);

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error: "+e.getMessage());
                alert.setTitle("New Appointment Error");
                alert.setHeaderText("A New Appointment Could Not Be Created.");
                Optional<ButtonType> okayPressed = alert.showAndWait();
                while (okayPressed.isEmpty()) ;
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateEndTime() {
        if(appointmentTooShort())
            endMinSpin.getValueFactory().setValue(5);
        appointmentTimeLbl.setText(getEndTime().toLocalDateTime().format(timeFormatter));
    }

    @FXML
    private void updateAppointment() {
        if(!appointmentTableView.getSelectionModel().isEmpty()) {
            Optional<Appointment> conflictAppointment = checkTimeConflicts();
            if (conflictAppointment.isPresent()) {
                scheduleConflictAlert(conflictAppointment.get());
            } else if (isWithinBusinessHours()) {
                businessHoursAlert();
            } else if (typeUnset()) {
                typeUnsetAlert();
            }
            else if(isAppointmentBeforeCurrentTime()){
                appointmentBeforeCurrentTime();
            }
            else {
                Appointment appointment = appointmentTableView.getSelectionModel().getSelectedItem();
                appointment.setStartDate(getStartTime());
                appointment.setEndDate(getEndTime());
                appointment.setType(typeTextField.getText());
                appointment.setCustomerID(customerComboBox.getSelectionModel().getSelectedItem().getCustomerID());

                try {
                    dbConnector.updateAppointment(appointment);

                    int selectedItem = appointmentTableView.getSelectionModel().getSelectedIndex();
                    appointmentTableView.setItems(FXCollections.observableArrayList());
                    appointmentTableView.setItems(dbData.getAppointmentList());
                    appointmentTableView.getSelectionModel().select(selectedItem);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Your appointment has been updated.");
                    alert.setTitle("Appointment Updated");
                    alert.setHeaderText("Your appointment has been updated.");
                    Optional<ButtonType> okayPressed = alert.showAndWait();
                    while (okayPressed.isEmpty()) ;

                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
                    alert.setTitle("Update Appointment Error");
                    alert.setHeaderText("Appointment Could Not Be Updated.");
                    Optional<ButtonType> okayPressed = alert.showAndWait();
                    while (okayPressed.isEmpty()) ;
                    e.printStackTrace();
                }
            }
        }
    }

    private void businessHoursAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appoint time is outside of business" +
                " hours. Please select another time.");
        alert.setTitle("Schedule Conflict");
        alert.setHeaderText("Appointment Could Not Be Made.");
        Optional<ButtonType> okayPressed = alert.showAndWait();
        while (okayPressed.isEmpty()) ;
    }

    private void scheduleConflictAlert(Appointment appointment) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "There was a scheduling conflict at " +
                appointment.getStartDate() + ". Please select another time.");
        alert.setTitle("Schedule Conflict");
        alert.setHeaderText("Appointment Could Not Be Made.");
        Optional<ButtonType> okayPressed = alert.showAndWait();
        while (okayPressed.isEmpty()) ;
    }

    private void typeUnsetAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appointment type is not set.");
        alert.setTitle("Missing Information");
        alert.setHeaderText("Appointment Could Not Be Made.");
        Optional<ButtonType> okayPressed = alert.showAndWait();
        while (okayPressed.isEmpty()) ;
    }

    private void appointmentBeforeCurrentTime() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appointment time has been set before the" +
                " current time. Please choose another time.");
        alert.setTitle("Schedule Conflict");
        alert.setHeaderText("Appointment Could Not Be Made.");
        Optional<ButtonType> okayPressed = alert.showAndWait();
        while (okayPressed.isEmpty()) ;
    }

    @FXML
    private void deleteAppointment(){
        if(!appointmentTableView.getSelectionModel().isEmpty()) {
            Alert confirmDeletion = getAlert();

            if (confirmDeletion.getResult() == ButtonType.OK) {
                try {
                    dbConnector.deleteAppointment(appointmentTableView.getSelectionModel().getSelectedItem());
                    appointmentTableView.getItems().remove(appointmentTableView.getSelectionModel().getSelectedItem());

                    appointmentTableView.getSelectionModel().selectFirst();
                    updateFields();

                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
                    alert.setTitle("Update Deletion Error");
                    alert.setHeaderText("Appointment Could Not Be Deleted.");
                    Optional<ButtonType> okayPressed = alert.showAndWait();
                    while (okayPressed.isEmpty()) ;
                    e.printStackTrace();
                }
            }
        }
    }

    private Alert getAlert() {
        Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete" +
                "appointment starting at " + appointmentTableView.getSelectionModel().getSelectedItem().getStartDate() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        confirmDeletion.setTitle("Deletion Confirmation");
        confirmDeletion.setHeaderText(appointmentTableView.getSelectionModel().getSelectedItem() + " will be deleted");
        confirmDeletion.showAndWait();
        return confirmDeletion;
    }

    /**
     * Checks if the chosen start time is during the given appointment
     * @param appointment The appointment to check against.
     * @return True if the start time is during the appointment.
     */
    private boolean isStartTimeDuring(Appointment appointment){
        return !getStartTime().before(appointment.getStartDate()) && !getStartTime().after(appointment.getEndDate());
    }


    /**
     * Checks if the chosen end time is during the given appointment
     * @param appointment The appointment to check against.
     * @return True if the end time is during the appointment.
     */
    private boolean isEndTimeDuring(Appointment appointment){
        return !getEndTime().before(appointment.getStartDate()) && !getEndTime().after(appointment.getEndDate());
    }


    /**
     * Checks if the chosen start and end times encompass the given appointment
     * @param appointment The appointment to check against.
     * @return True if the start and end times encompass the appointment.
     */
    private boolean isAppointmentBetweenStartEnd(Appointment appointment){
        return getStartTime().before(appointment.getStartDate()) && getEndTime().after(appointment.getEndDate());
    }

    /**
     * Checks if the appointment is during business hours.
      * @return True if the appointment is during business hours.
     */
    boolean isWithinBusinessHours(){
        return getStartTime().toLocalDateTime().toLocalTime().isBefore(START_BUSINESS_HOURS)
                || getStartTime().toLocalDateTime().toLocalTime().isAfter(END_BUSINESS_HOURS)
                || getEndTime().toLocalDateTime().toLocalTime().isBefore(START_BUSINESS_HOURS)
                || getEndTime().toLocalDateTime().toLocalTime().isAfter(END_BUSINESS_HOURS);
    }

    /**
     * Checks to make sure the appointment time is at least 5 minutes.
     * @return True if the appointment is less than 5 minutes.
     */
    private boolean appointmentTooShort(){
        return endHourSpin.getValue() * 60 + endMinSpin.getValue() < 5;
    }

    /**
     * Checks if the appointment type is blank.
     * @return True if the appointment type is blank.
     */
    private boolean typeUnset(){
        return typeTextField.getText().trim().equalsIgnoreCase("");
    }

    /**
     * Checks if the appointment is schedules before the current time.
     * @return True if the appointment is before the current time.
     */
    private boolean isAppointmentBeforeCurrentTime(){
        return!getStartTime().after(Timestamp.valueOf(LocalDateTime.now()));
    }

    public void loadAppointment() {
        updateFields();
    }

    public void cancel() {
        updateHBox.setVisible(true);
        addHBox.setVisible(false);
        appointmentTableView.setDisable(false);
        updateFields();
    }

    /**
     * Sets up the page for adding a new appointment.
     */
    public void showAddMenu() {
        appointmentTableView.setDisable(true);
        updateHBox.setVisible(false);
        addHBox.setVisible(true);

        startHourSpin.getValueFactory().setValue(LocalDateTime.now().getHour());
        startMinSpin.getValueFactory().setValue(LocalDateTime.now().getMinute());
        endHourSpin.getValueFactory().setValue(0);
        endMinSpin.getValueFactory().setValue(0);

        datePicker.setValue(LocalDate.now());
        appointmentTimeLbl.setText("00:00");
        typeTextField.setText("");
    }
}
