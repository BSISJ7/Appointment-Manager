package dbData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Timestamp;

import static dbData.DBData.dbData;


public class Appointment implements Comparable{

    private int appointmentID;
    private int customerID;
    private int userID;
    private Timestamp startDate;
    private Timestamp endDate;
    private String type;

    private StringProperty customerIDProp = new SimpleStringProperty();
    private final StringProperty dateProp = new SimpleStringProperty();
    private StringProperty startDateProp = new SimpleStringProperty();
    private StringProperty endDateProp = new SimpleStringProperty();
    private StringProperty typeProp = new SimpleStringProperty();


    public String toString(){
        return "[ID]: " + appointmentID + " [User]: " + dbData.findUserByID(userID).get() + " [Customer]: "
                + dbData.findCustomerByID(customerID).get() + " [Date]: " + startDate + "  [Type]: " + type;
    }


    public StringProperty dateProperty() {
        return dateProp;
    }

    public StringProperty customerIDProperty() {
        if (customerIDProp == null)
            customerIDProp = new SimpleStringProperty(this, "customerIDProp");
        return customerIDProp;
    }

    public StringProperty startDateProperty() {
        if (startDateProp == null) startDateProp = new SimpleStringProperty(this, "startDateProp");
        return startDateProp;
    }

    public StringProperty endDateProperty() {
        if (endDateProp == null) endDateProp = new SimpleStringProperty(this, "endDateProp");
        return endDateProp;
    }

    public StringProperty typeProperty() {
        if (typeProp == null) typeProp = new SimpleStringProperty(this, "typeProp");
        return typeProp;
    }

    public Appointment(){
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        typeProperty().set(type);
        this.type = type;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        customerIDProperty().set(String.valueOf(customerID));
        this.customerID = customerID;
    }

    public void setStartDate(Timestamp timestamp) {
        startDateProperty().set(timestamp.toLocalDateTime().toLocalTime().toString());
        dateProperty().set(timestamp.toLocalDateTime().toLocalDate().toString());
        this.startDate = timestamp;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp timestamp) {
        endDateProperty().set(timestamp.toLocalDateTime().toLocalTime().toString());
        endDate = timestamp;
    }

    @Override
    public int compareTo(Object o) {
        return Integer.compare(this.appointmentID, ((Appointment) o).appointmentID);
    }

    public void createNewID() {
        int maxID = -1;
        for(Appointment appointments : dbData.getAppointmentList()){
            maxID = Math.max(maxID, appointments.getAppointmentID());
        }
        appointmentID = maxID+1;
    }
}
