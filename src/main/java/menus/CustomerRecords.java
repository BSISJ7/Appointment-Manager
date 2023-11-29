package menus;

import dbData.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import static dataIO.DBConnector.dbConnector;
import static dbData.DBData.dbData;

public class CustomerRecords implements Initializable {

    public ComboBox customerIDComboBox;
    @FXML
    private GridPane addGridPane; //GridPane for adding new a customer.
    @FXML
    private GridPane updateGridPane; //GridPane for updating and deleting a customer.
    @FXML
    private TableColumn<Customer, String> nameCol;
    @FXML
    private TableColumn<Customer, String> addressCol;
    @FXML
    private TableColumn<Customer, String> addressFieldTwoCol;
    @FXML
    private TableColumn<Customer, String> phoneCol;
    @FXML
    private TableColumn<Customer, String> cityCol;
    @FXML
    private TableColumn<Customer, String> countryCol;
    @FXML
    private TableColumn<Customer, String> postalCol;
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TextField countryTextField;
    @FXML
    private TextField postalTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField addressFieldOne;
    @FXML
    private TextField addressFieldTwo;
    @FXML
    private TextField cityTextField;
    @FXML
    private AnchorPane mainPane;

    //Errors to be printed when fields are left blank.
    public enum BlankErrors {
        NAME("The customer name is missing."),
        ADDRESS("The customer address is missing."),
        PHONE("The customer phone number is missing."),
        CITY("The customer city is missing."),
        COUNTRY("The customer country is missing."),
        POSTAL("The customer postal code is missing."),
        NONE("None");

        private final String alert;

        BlankErrors(String alert){
            this.alert = alert;
        }
    }

    public enum FieldLength{
        NAME("name", 45),
        addressFieldOne("address field 1", 50),
        addressFieldTwo("address field 2", 50),
        PHONE("phone number", 20),
        CITY("city name", 50),
        COUNTRY("country name", 50),
        POSTAL("postal code", 10),
        NONE("none", 0);

        private final String name;
        private final int length;

        FieldLength(String name, int length){
            this.name = name;
            this.length = length;
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        customerTableView.setItems(dbData.getCustomerList());
        if(!customerTableView.getItems().isEmpty()) {
            customerTableView.getSelectionModel().select(0);
            updateFieldData();
        }

        if(!dbData.getAppointmentList().isEmpty()) {
            customerTableView.setItems(dbData.getCustomerList());
            customerTableView.getSelectionModel().selectFirst();
            updateFieldData();
        }

        nameCol.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        addressCol.setCellValueFactory(cellData -> cellData.getValue().addressFieldOneProperty());
        addressFieldTwoCol.setCellValueFactory(cellData -> cellData.getValue().addressFieldTwoProperty());
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        cityCol.setCellValueFactory(cellData -> cellData.getValue().cityNameProperty());
        countryCol.setCellValueFactory(cellData -> cellData.getValue().countryNameProperty());
        postalCol.setCellValueFactory(cellData -> cellData.getValue().postalProperty());

        Platform.runLater(() -> {
            System.out.println("Is addressfield length null: "+FieldLength.addressFieldTwo.length);
            System.out.println("Is addressfield length null: "+FieldLength.addressFieldTwo == null);
        });
    }


    /**
     * Updates the current selected customer information in the list and database.
     */
    @FXML
    private void updateCustomer() {
        if(isInformationFilledOut() && isCustomerSelected() && isFieldLengthCorrect()){
            editCustomerData(customerTableView.getSelectionModel().getSelectedItem());
            int selectedItem = customerTableView.getSelectionModel().getSelectedIndex();
            customerTableView.setItems(FXCollections.observableArrayList());
            customerTableView.setItems(dbData.getCustomerList());
            customerTableView.getSelectionModel().select(selectedItem);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, customerTableView.getSelectionModel().getSelectedItem().getCustomerName()
                    + "'s information has been updated.");
            alert.setTitle("Customer Updated");
            alert.setHeaderText("Customer Information Has Been Updated");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (okayPressed.isEmpty()) ;
        }
    }

    /**
     * Sets up the fields for inputting a new customer.
     */
    @FXML
    private void createNewCustomer() {
        addGridPane.setVisible(true);
        updateGridPane.setVisible(false);

        nameTextField.setText("");
        addressFieldOne.setText("");
        addressFieldTwo.setText("");
        phoneTextField.setText("");
        postalTextField.setText("");
        cityTextField.setText("");
        countryTextField.setText("");
    }

    /**
     * Adds a new customer to the customer list and database.
     */
    @FXML
    private void addCustomer(){
        if(isInformationFilledOut() && isFieldLengthCorrect()) {
            Customer NewCustomer = new Customer();
            dbData.addCustomerToList(NewCustomer);
            editCustomerData(NewCustomer);
            customerTableView.getSelectionModel().selectLast();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, customerTableView.getSelectionModel().getSelectedItem().getCustomerName()
                    + " has been added.");
            alert.setTitle("Customer Added");
            alert.setHeaderText("A New Customer Has Been Added");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (!okayPressed.isPresent()) ;

            addGridPane.setVisible(false);
            updateGridPane.setVisible(true);
        }
    }

    /**
     * Sets customer data from input fields.
     */
    private void editCustomerData(Customer customer){
        customer.setCountryName(countryTextField.getText());
        customer.setCityName(cityTextField.getText());
        customer.setAddress(addressFieldOne.getText(), addressFieldTwo.getText(), postalTextField.getText(),
                phoneTextField.getText());
        customer.setCustomerName(nameTextField.getText());

        try {
            dbConnector.insertUpdateCountry(new Country(customer.getCountryID(), customer.getCountryName()));
            dbConnector.insertUpdateCity(new City(customer.getCityID(), customer.getCityName(),
                    customer.getCountryID()));
            dbConnector.insertUpdateAddress(new Address(customer.getAddressId(), customer.getAddressFieldOne(),
                    customer.getAddressFieldTwo(), customer.getCityID(), customer.getPostalCode(),
                    customer.getPhoneNum()));
            dbConnector.insertUpdateCustomer(customer);

        }catch(SQLException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "SQL Error: " + e.getMessage());
            alert.setTitle("Customer Update Error");
            alert.setHeaderText("Customer Update Could Not Be Performed");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (okayPressed.isEmpty()) ;
        }
    }

    /**
     * Removes a customer from the customer list and database.
     */
    public void deleteCustomer() {
        if(!customerTableView.getSelectionModel().isEmpty()) {
            Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " +
                    customerTableView.getSelectionModel().getSelectedItem().getCustomerName() + "?", ButtonType.OK, ButtonType.CANCEL);
            confirmDeletion.setTitle("Deletion Confirmation");
            confirmDeletion.setHeaderText(customerTableView.getSelectionModel().getSelectedItem().getCustomerName() +
                    " will be deleted");
            confirmDeletion.showAndWait();

            if (confirmDeletion.getResult() == ButtonType.OK && isCustomerSelected()) {
                try {
                    dbConnector.deleteCustomer(customerTableView.getSelectionModel().getSelectedItem());
                    customerTableView.getItems().remove(customerTableView.getSelectionModel().getSelectedItem());
                    customerTableView.getSelectionModel().selectFirst();
                    updateFieldData();
                } catch (SQLException | NullPointerException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "SQL Error: " + e.getMessage());
                    alert.setTitle("Customer Deletion Error");
                    alert.setHeaderText("Customer deletion could not be performed");
                    Optional<ButtonType> okayPressed = alert.showAndWait();
                    while (okayPressed.isEmpty()) ;
                }
            }
        }
    }

    @FXML
    private void openMainMenu() {
        MainMenu.openMainMenu(mainPane);
    }

    /**
     * Update customer information fields.
     */
    public void updateFieldData(){
        if(!customerTableView.getSelectionModel().isEmpty()) {
            nameTextField.setText(customerTableView.getSelectionModel().getSelectedItem().getCustomerName());
            addressFieldOne.setText(customerTableView.getSelectionModel().getSelectedItem().getAddressFieldOne());
            addressFieldTwo.setText(customerTableView.getSelectionModel().getSelectedItem().getAddressFieldTwo());
            phoneTextField.setText(customerTableView.getSelectionModel().getSelectedItem().getPhoneNum());
            postalTextField.setText(customerTableView.getSelectionModel().getSelectedItem().getPostalCode());
            cityTextField.setText(customerTableView.getSelectionModel().getSelectedItem().getCityName());
            countryTextField.setText(customerTableView.getSelectionModel().getSelectedItem().getCountryName());
        }else{
            nameTextField.setText("");
            addressFieldOne.setText("");
            addressFieldTwo.setText("");
            phoneTextField.setText("");
            postalTextField.setText("");
            cityTextField.setText("");
            countryTextField.setText("");
        }
    }

    /**
     * Checks to see if all fields are filled out and gives an alert based on which one is not filled out.
     * @return True if all fields are filled out.
     */
    private boolean isInformationFilledOut(){
        BlankErrors blankError = getBlankErrors();


        if(!blankError.equals(BlankErrors.NONE)){
            Alert alert = new Alert(Alert.AlertType.ERROR, blankError.alert+" Please fill out the rest of the" +
                    " customer information before submitting.");
            alert.setTitle("Customer Records Error");
            alert.setHeaderText("Customer information missing.");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (okayPressed.isEmpty());
            return false;
        }

        return true;
    }

    private BlankErrors getBlankErrors() {
        BlankErrors blankError = BlankErrors.NONE;
        if(nameTextField.getText().isEmpty())
            blankError = BlankErrors.NAME;
        else if(addressFieldOne.getText().isEmpty())
            blankError = BlankErrors.ADDRESS;
        else if(phoneTextField.getText().isEmpty())
            blankError = BlankErrors.PHONE;
        else if(postalTextField.getText().isEmpty())
            blankError = BlankErrors.POSTAL;
        else if(cityTextField.getText().isEmpty())
            blankError = BlankErrors.CITY;
        else if(countryTextField.getText().isEmpty())
            blankError = BlankErrors.COUNTRY;
        return blankError;
    }

    /**
     * Checks to make sure the field lengths are not too large.
     * @return True if the field lengths are not too large.
     */
    private boolean isFieldLengthCorrect(){
        FieldLength fieldLengthError = getFieldLength();
        if(!fieldLengthError.equals(FieldLength.NONE)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "The customer " + fieldLengthError.name + " given is too long.");
            alert.setTitle("Customer Records Error");
            alert.setHeaderText("Postal code error.");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (okayPressed.isEmpty());
            return false;
        }
        return true;
    }

    private FieldLength getFieldLength() {
        FieldLength fieldLengthError = FieldLength.NONE;
        if (nameTextField.getText() != null && nameTextField.getText().length() > FieldLength.NAME.length)
            fieldLengthError = FieldLength.NAME;
        else if (addressFieldOne.getText() != null && addressFieldOne.getText().length() > FieldLength.addressFieldOne.length)
            fieldLengthError = FieldLength.addressFieldOne;
        else if (addressFieldTwo.getText() != null && addressFieldTwo.getText().length() > FieldLength.addressFieldTwo.length)
            fieldLengthError = FieldLength.addressFieldTwo;
        else if (phoneTextField.getText() != null && phoneTextField.getText().length() > FieldLength.PHONE.length)
            fieldLengthError = FieldLength.PHONE;
        else if (postalTextField.getText() != null && postalTextField.getText().length() > FieldLength.POSTAL.length)
            fieldLengthError = FieldLength.POSTAL;
        else if (cityTextField.getText() != null && cityTextField.getText().length() > FieldLength.CITY.length)
            fieldLengthError = FieldLength.CITY;
        else if (countryTextField.getText() != null && countryTextField.getText().length() > FieldLength.COUNTRY.length)
            fieldLengthError = FieldLength.COUNTRY;
        return fieldLengthError;
    }

    /**
     * Outputs an alert if no customer is selected.
     * @return True if a customer is selected.
     */
    private boolean isCustomerSelected(){
        if(customerTableView.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer before attempting to" +
                    " make changes.");
            alert.setTitle("Customer Records Error");
            alert.setHeaderText("No customer selected.");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (okayPressed.isEmpty());
            return false;
        }
        return true;
    }

    @FXML
    private void loadCustomer() {
        updateFieldData();
    }

    @FXML
    private void cancel() {
        addGridPane.setVisible(false);
        updateGridPane.setVisible(true);
        updateFieldData();
    }
}
