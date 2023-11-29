package dbData;

public class City {

    private String cityName;
    private int cityID;
    private int countryID;

    public City() {

    }

    public City(int cityID, String cityName, int countryID) {
        this.cityID = cityID;
        this.cityName = cityName;
        this.countryID = countryID;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

}
