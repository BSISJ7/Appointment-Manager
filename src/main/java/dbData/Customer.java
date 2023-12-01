package dbData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static dbData.DBData.dbData;


public class Customer implements Comparable{

    private int customerID;
    private String customerName;

    private int addressId;
    private String addressFieldOne;
    private String addressFieldTwo;
    private String postalCode;
    private String phoneNum;

    private int cityID;
    private String cityName;

    private int countryID;
    public String countryName;

    private final StringProperty customerNameProp = new SimpleStringProperty();
    private final StringProperty addressFieldOneProp = new SimpleStringProperty();
    private final StringProperty addressFieldTwoProp = new SimpleStringProperty();
    private final StringProperty postalProp = new SimpleStringProperty();
    private final StringProperty phoneProp = new SimpleStringProperty();
    private final StringProperty cityNameProp = new SimpleStringProperty();
    private final StringProperty countryNameProp = new SimpleStringProperty();


    public String getAddressFieldOne() {
        return addressFieldOne;
    }

    /**Sets the address and generates a new addressID and adds the new address to addressList if it is a new address
     *
     * @param addressFieldOne first address line
     * @param addressFieldTwo second address line
     * @param postalCode postal code
     * @param phoneNum phone number
     */
    public void setAddress(String addressFieldOne, String addressFieldTwo, String postalCode, String phoneNum) {
        this.addressFieldOne = addressFieldOne;
        this.addressFieldTwo = addressFieldTwo;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;

        addressFieldOneProperty().set(addressFieldOne);
        addressFieldTwoProperty().set(addressFieldTwo);
        postalProperty().set(postalCode);
        phoneProperty().set(phoneNum);

        int maxID = 1;
        for(Address addresses : DBData.getAddressList()){
            maxID = Math.max(maxID, addresses.getAddressID());
            if(addresses.matches(addressFieldOne, addressFieldTwo, postalCode, phoneNum)) {
                addressId = addresses.getAddressID();
                return;
            }
        }
        addressId = maxID+1;
        DBData.getAddressList().add(new Address(addressId, addressFieldOne, addressFieldTwo, cityID, postalCode, phoneNum));
    }



    public String getAddressFieldTwo() {
        return addressFieldTwo;
    }

    public void setAddressFieldTwo(String addressFieldTwo) {
        addressFieldTwoProperty().set(addressFieldTwo);
        this.addressFieldTwo = addressFieldTwo;
    }

    public void setAddressFieldOne(String addressFieldOne) {
        addressFieldOneProperty().set(addressFieldOne);
        this.addressFieldOne = addressFieldOne;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        postalProperty().set(postalCode);
        this.postalCode = postalCode;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNumber(String phoneNum) {
        phoneProperty().set(phoneNum);
        this.phoneNum = phoneNum;
    }

    public String getCityName() {
        return cityName;
    }

    /**Sets the city name and generates a new cityID and adds the name to cityList if it is a new city
     *
     * @param cityName name of city
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
        cityNameProperty().set(cityName);
        int maxID = -1;
        for(City cities : dbData.getCityList()){
            maxID = Math.max(maxID, cities.getCityID());
            if(cities.getCityName().equalsIgnoreCase(cityName)) {
                cityID = cities.getCityID();
                return;
            }
        }
        cityID = maxID+1;
        dbData.getCityList().add(new City(cityID, cityName, countryID));
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    /**Sets the country name and generates a new countryID and adds the name to countryList if it is a new country
     *
     * @param countryName name of country
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
        countryNameProperty().set(countryName);
        int maxID = -1;
        for(Country country : dbData.getCountryList()){
            maxID = Math.max(maxID, country.getCountryID());
            if(country.getCountryName().equalsIgnoreCase(countryName)) {
                countryID = country.getCountryID();
                return;
            }
        }
        countryID = maxID+1;
        dbData.getCountryList().add(new Country(countryName, maxID+1));
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String name) {
        customerNameProperty().set(name);
        this.customerName = name;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    @Override
    public int compareTo(Object o) {
        return Integer.compare(this.customerID, ((Customer) o).customerID);
    }

    public String toString(){
        return customerName;
    }


    public String getCustomerNameProp() {
        return customerNameProp.get();
    }

    public StringProperty customerNameProperty() {
        return customerNameProp;
    }

    public void setCustomerNameProp(String customerNameProp) {
        this.customerNameProp.set(customerNameProp);
    }

    public String getAddressFieldOneProp() {
        return addressFieldOneProp.get();
    }

    public StringProperty addressFieldOneProperty() {
        return addressFieldOneProp;
    }

    public void setAddressFieldOneProp(String addressFieldOneProp) {
        this.addressFieldOneProp.set(addressFieldOneProp);
    }

    public String getAddressFieldTwoProp() {
        return addressFieldTwoProp.get();
    }

    public StringProperty addressFieldTwoProperty() {
        return addressFieldTwoProp;
    }

    public void setAddressFieldTwoProp(String addressFieldTwoProp) {
        this.addressFieldTwoProp.set(addressFieldTwoProp);
    }

    public String getPostalProp() {
        return postalProp.get();
    }

    public StringProperty postalProperty() {
        return postalProp;
    }

    public void setPostalProp(String postalProp) {
        this.postalProp.set(postalProp);
    }

    public String getPhoneProp() {
        return phoneProp.get();
    }

    public StringProperty phoneProperty() {
        return phoneProp;
    }

    public void setPhoneProp(String phoneProp) {
        this.phoneProp.set(phoneProp);
    }

    public String getCityNameProp() {
        return cityNameProp.get();
    }

    public StringProperty cityNameProperty() {
        return cityNameProp;
    }

    public void setCityNameProp(String cityNameProp) {
        this.cityNameProp.set(cityNameProp);
    }

    public String getCountryNameProp() {
        return countryNameProp.get();
    }

    public StringProperty countryNameProperty() {
        return countryNameProp;
    }

    public void setCountryNameProp(String countryNameProp) {
        this.countryNameProp.set(countryNameProp);
    }
}
