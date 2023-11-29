package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CityManager {

    private static final CityManager CITY_MANAGER = new CityManager();
    private static List<String> cityList;

    private CityManager() {
        cityList = new ArrayList<>();
        try {
            InputStream is = CityManager.class.getResourceAsStream("/textData/City List.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(is), StandardCharsets.UTF_8));
            String cityName;
            while ((cityName = reader.readLine()) != null) {
                cityList.add(cityName.substring(0, cityName.indexOf(',')));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CityManager getCityManager() {
        return CITY_MANAGER;
    }

    public String getRandomCity() {
        return cityList.get(new Random(System.nanoTime()).nextInt(cityList.size()));
    }

    public void addCity(String newWord) {
        cityList.add(newWord);
        Collections.sort(cityList);
    }

    public int size() {
        return cityList.size();
    }
}
