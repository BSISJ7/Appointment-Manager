package dbData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static dataIO.DBConnector.dbConnector;

public class DBData {

    private static final ArrayList<User> userList = new ArrayList<>();
    private static final ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private static final ObservableList<Appointment> currentUserAppointmentsList = FXCollections.observableArrayList();
    private static final ArrayList<Appointment> reportsAppointmentList = new ArrayList<>();
    private static final ArrayList<City> cityList = new ArrayList<>();
    private static final ArrayList<Country> countryList = new ArrayList<>();
    private static final ArrayList<Address> addressList = new ArrayList<>();

    public static final DBData dbData = new DBData();

    private DBData(){}

    /**
     * Calls the database methods for setting all but the appointment lists with database data.
     */
    public void populateDbInfo() throws SQLException, NullPointerException{
        if (dbConnector == null)
            throw new NullPointerException("Could not connect to database. dbConnector is null.");
        dbConnector.setUsers();
        dbConnector.setCountries();
        dbConnector.setCities();
        dbConnector.setAddresses();
        dbConnector.setCustomers();
    }

    public void addCustomerToList(Customer customer){
        customerList.add(customer);
    }


    public Optional<Customer> findCustomerByID(int customerID) throws NoSuchElementException {
        System.out.println("Searching for: "+customerID);
        for(Customer customer : customerList){
            System.out.println("Found: "+customer.getCustomerID() + " " + customer.getCustomerName());
            if(customer.getCustomerID() == customerID)
                return Optional.of(customer);
        }
        return Optional.empty();
    }


    public Optional<User> findUserByID(int userID){
        for(User users : userList){
            if(users.getUserID() == userID) {
                return Optional.of(users);
            }
        }
        return Optional.empty();
    }

    public void addUserToList(User user){
        userList.add(user);
    }

    public ArrayList<Address> getAddressList() {
        return addressList;
    }

    public ArrayList<City> getCityList() {
        return cityList;
    }

    public ArrayList<Country> getCountryList() {
        return countryList;
    }

    public ObservableList<Customer> getCustomerList() {
        return customerList;
    }

    public ObservableList<Appointment> getAppointmentList() {
        return currentUserAppointmentsList;
    }

    public static ArrayList<Appointment> getReportsAppointmentList() {
        return reportsAppointmentList;
    }

    public int getNumUsers(){
        return userList.size();
    }
}
