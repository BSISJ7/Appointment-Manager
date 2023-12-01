package dbData;

public class Address implements Comparable<Address>{

    private int addressID;
    private String addressFieldOne;
    private String addressFieldTwo;
    private int cityID;
    private String postalCode;
    private String phoneNum;


    public Address(int addressID, String addressFieldOne, String addressFieldTwo, int cityID, String postalCode, String phoneNum) {
        this.addressID = addressID;
        this.addressFieldOne = addressFieldOne;
        this.addressFieldTwo = addressFieldTwo;
        this.cityID = cityID;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;
    }

    public Address() {}

    public int getAddressID(){
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    public String getAddressFieldOne() {
        return addressFieldOne;
    }

    public void setAddressFieldOne(String addressFieldOne) {
        this.addressFieldOne = addressFieldOne;
    }

    public String getAddressFieldTwo() {
        return addressFieldTwo;
    }

    public void setAddressFieldTwo(String addressFieldTwo) {
        this.addressFieldTwo = addressFieldTwo;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    /**
     * Checks to see if the provided address matches the data in the current address.
     * @param address The address to compare to.
     * @return Returns true if the data matches or false if it does not.
     */
    @Override
    public int compareTo(Address address) {
        if((address.addressFieldOne.equalsIgnoreCase(addressFieldOne))
                && (address.addressFieldTwo.equalsIgnoreCase(addressFieldTwo))
                && (address.postalCode.equalsIgnoreCase(postalCode))
                && (address.phoneNum.equalsIgnoreCase(phoneNum)) )
            return 0;
        else {
            String addressOne = address.getAddressFieldOne() + " " + address.getAddressFieldTwo();
            String addressTwo = addressFieldOne + " " + addressFieldTwo;
            return addressOne.compareToIgnoreCase(addressTwo);
        }
    }

    /**
     * Checks to see if the parameters match the data in the address.
     * @param addressFieldOne
     * @param addressFieldTwo
     * @param postalCode
     * @param phoneNum
     * @return Returns true if the data matches or false if it does not.
     */
    public boolean matches(String addressFieldOne, String addressFieldTwo, String postalCode, String phoneNum) {
        return (this.addressFieldOne.equalsIgnoreCase(addressFieldOne))
                && (this.addressFieldTwo.equalsIgnoreCase(addressFieldTwo))
                && (this.postalCode.equalsIgnoreCase(postalCode))
                && (this.phoneNum.equalsIgnoreCase(phoneNum));
    }
}
