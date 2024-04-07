package com.example.withDatabase.service;



import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.withDatabase.dto.GeocodingApiResponse;
import com.example.withDatabase.dto.WeatherApiResponse;
import com.example.withDatabase.entity.PincodeLocation;
import com.example.withDatabase.entity.WeatherInfo;
import com.example.withDatabase.repository.PincodeLocationRepository;
import com.example.withDatabase.repository.WeatherInfoRepository;

@Service
public class WeatherService {

    @Autowired
    private WeatherInfoRepository weatherInfoRepository;

    @Autowired
    private PincodeLocationRepository pincodeLocationRepository;

    private String OPEN_WEATHER_API_KEY = "33ffa636b988918f9870bf2ff1af507f";

    public PincodeLocation getPincodeLocation(Integer pincode) throws Exception {
        System.out.println("-------------------Inside Pincode location function------------------------");
        String url = "https://api.openweathermap.org/geo/1.0/zip?zip=" + pincode + ",in&appid=" + OPEN_WEATHER_API_KEY;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<GeocodingApiResponse> response = restTemplate.getForEntity(url, GeocodingApiResponse.class);
        double latitude = 0, longitude = 0;
        if (response.getStatusCode().is2xxSuccessful()) {
            latitude = response.getBody().getLat();
            longitude = response.getBody().getLon();
        } else
            throw new Exception(response.getStatusCode().toString());

        return new PincodeLocation(pincode, latitude, longitude);
    }

    public WeatherApiResponse getWeatherApiResponse(double latitude, double longitude, LocalDate date)
            throws Exception {

        long unixTimestamp = date.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid="
                + OPEN_WEATHER_API_KEY + "&dt=" + unixTimestamp;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<WeatherApiResponse> response = restTemplate.getForEntity(url, WeatherApiResponse.class);
        if (response.getStatusCode().isError()) {
            throw new Exception(response.getStatusCode().toString());
        }
        return response.getBody();
    }

    // optimised method to get weather information;
    public WeatherInfo getWeatherInfo(Integer pincode, LocalDate date) throws Exception {


        Optional<WeatherInfo> optionalWeatherInfo=this.weatherInfoRepository.findByPincodeAndDate(pincode, date);
        //check if data is already present in database
        if(optionalWeatherInfo.isPresent()) return optionalWeatherInfo.get();

        double latitude;
        double longitude;
        // Check if the latitude and longitude are already saved in the database

        Optional<PincodeLocation> optionalPincodeLocation = null;
        try {
            optionalPincodeLocation = this.pincodeLocationRepository.findById(pincode);
        } catch (DataAccessException ex) {
            ex.printStackTrace();
        }

        if (optionalPincodeLocation.isPresent()) {
            latitude = optionalPincodeLocation.get().getLatitude();
            longitude = optionalPincodeLocation.get().getLongitude();
        }

        else {
            // If the latitude and longitude are not saved in the database, call the
            // function to get lattitude and longitude;
            // and Save the latitude and longitude in the pincode_location table for further
            // use
            PincodeLocation pincodeLocation = getPincodeLocation(pincode);
            latitude = pincodeLocation.getLatitude();
            longitude = pincodeLocation.getLongitude();
            this.pincodeLocationRepository.save(pincodeLocation);
        }

        WeatherApiResponse weatherApiResponse = getWeatherApiResponse(latitude, longitude, date);

        // convert apiresponse to requred info;
        WeatherInfo weatherInfo = new WeatherInfo();
        if (weatherApiResponse != null) {
            weatherInfo.setPincode(pincode);
            weatherInfo.setDate(date);
            weatherInfo.setTemperature(weatherApiResponse.getMain().getTemp());
            weatherInfo.setHumidity(weatherApiResponse.getMain().getHumidity());
            weatherInfo.setPressure(weatherApiResponse.getMain().getPressure());
            weatherInfo.setWindSpeed(weatherApiResponse.getWind().getSpeed());
            weatherInfo.setDescription(weatherApiResponse.getWeather().get(0).getDescription());
            weatherInfo.setPlace(weatherApiResponse.getName());
            this.weatherInfoRepository.save(weatherInfo);
        }
        System.out.println(weatherInfo.toString());
        return weatherInfo;
    }

}