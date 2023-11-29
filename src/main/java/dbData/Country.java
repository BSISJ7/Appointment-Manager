package dbData;

public class Country{

    public String countryName;
    public int countryID;


    public Country(String countryName, int countryID) {
        this.countryName = countryName;
        this.countryID = countryID;
    }

    public Country() {

    }

    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
}
