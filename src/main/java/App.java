import dataIO.DBConnector;
import dbData.Appointment;
import dbData.Customer;
import dbData.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import utils.CityManager;
import utils.DictionaryManager;
import utils.NamesManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static dataIO.DBConnector.setLoggedInUser;
import static dbData.DBData.dbData;
import static java.lang.StringTemplate.STR;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        quickstart();
        try {
            DBConnector.dbConnector.connectToDatabase();
            dbData.populateDbInfo();
        } catch (SQLException | NullPointerException throwable) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Database Connection Failed");
            dialog.setContentText(STR."The database connection failed and appointments will not be saved.  " +
                    "Would you like to continue?");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.showAndWait().ifPresent(response -> {
               if(response == ButtonType.CANCEL){
                   Platform.exit();
                   System.exit(0);
               }
           });
        }

        ResourceBundle resourceBundle = ResourceBundle.getBundle("Lang", Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/menusFXML/LoginScreen.fxml"), resourceBundle);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle("Database Login");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void quickstart(){
        User newUser = new User(NamesManager.getNamesList().getRandomName(), "password");
        dbData.addUserToList(newUser);
        setLoggedInUser(newUser);
        for(int x = 0; x < 5; x++) {
            Customer newCustomer = new Customer();
            newCustomer.setCustomerName(NamesManager.getNamesList().getRandomName());
            newCustomer.setCustomerID(x);
            newCustomer.setCityName(CityManager.getCityManager().getRandomCity());
            int randNum = new Random(System.nanoTime()).nextInt(1000);
            newCustomer.setAddressFieldOne(randNum + " " + DictionaryManager.getDictionary().getRandomWord() + " "
                    + DictionaryManager.getDictionary().getRandomWord() + " Street");
            newCustomer.setCountryName("United States");
            newCustomer.setPostalCode("12345");
            newCustomer.setPhoneNumber("123-456-7890");
            dbData.addCustomerToList(newCustomer);


            Appointment newAppointment = new Appointment();
            newAppointment.setCustomerID(x);
            newAppointment.setType(DictionaryManager.getDictionary().getRandomWord());
            newAppointment.setUsername(newUser.getUsername());

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, x <= 1 ? 0 : x);
            calendar.add(Calendar.DAY_OF_MONTH, x == 1 ? 8 : 1);
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            calendar.add(Calendar.HOUR_OF_DAY, new Random(System.nanoTime()).nextInt(4));
            calendar.add(Calendar.MINUTE, new Random(System.nanoTime()).nextInt(60));
            newAppointment.setStartDate(new Timestamp(calendar.getTimeInMillis()));

            calendar.add(Calendar.HOUR_OF_DAY, new Random(System.nanoTime()).nextInt(4));
            calendar.add(Calendar.MINUTE, new Random(System.nanoTime()).nextInt(60));
            newAppointment.setEndDate(new Timestamp(calendar.getTimeInMillis()));

            dbData.getAppointmentList().add(newAppointment);
        }
    }
}
