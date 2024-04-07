package com.example.withDatabase.service;



import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.example.withDatabase.dto.Coord;
import com.example.withDatabase.dto.GeocodingApiResponse;
import com.example.withDatabase.dto.Weather;
import com.example.withDatabase.dto.WeatherApiResponse;
import com.example.withDatabase.entity.PincodeLocation;
import com.example.withDatabase.entity.WeatherInfo;
import com.example.withDatabase.repository.PincodeLocationRepository;
import com.example.withDatabase.repository.WeatherInfoRepository;

@SpringBootTest
public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PincodeLocationRepository pincodeLocationRepository;

    @Mock
    private WeatherInfoRepository weatherInfoRepository;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void testGetPincodeLocation() throws Exception {
        // create mock response
        GeocodingApiResponse mockResponse = new GeocodingApiResponse("560066","Hoskote",12.9716,77.7473,"IN");
        ResponseEntity<GeocodingApiResponse> mockResponseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        Mockito.when(restTemplate.getForEntity(anyString(), eq(GeocodingApiResponse.class))).thenReturn(mockResponseEntity);
        PincodeLocation actualPincodeLocation=weatherService.getPincodeLocation(560066);
        // verify results
        Assertions.assertEquals(mockResponse.getZip(),Integer.toString(actualPincodeLocation.getPincode()) );
        Assertions.assertEquals(mockResponse.getLat(), actualPincodeLocation.getLatitude());
        Assertions.assertEquals(mockResponse.getLon(), actualPincodeLocation.getLongitude());
    }


    @Test
    void testGetWeatherApiResponse() throws Exception {
        WeatherApiResponse mockResponse=new WeatherApiResponse();
        mockResponse.setCoord(new Coord(73.9158,18.5685));
        List<Weather> temp=new ArrayList<>();
        temp.add(new Weather( 800, "Clear",  "clear sky",  "01d"));
        mockResponse.setWeather(temp);
        Mockito.when(restTemplate.getForEntity(anyString(), eq(WeatherApiResponse.class))).thenReturn(new ResponseEntity<>(mockResponse,HttpStatus.OK));
        WeatherApiResponse actualResponse=weatherService.getWeatherApiResponse(18.5685, 73.9158,LocalDate.parse("2024-04-06"));
        Assertions.assertEquals(mockResponse.getCoord().getLat(), actualResponse.getCoord().getLat());
        Assertions.assertEquals(mockResponse.getCoord().getLon(), actualResponse.getCoord().getLon());
        Assertions.assertEquals(mockResponse.getWeather().get(0).getMain(), actualResponse.getWeather().get(0).getMain());
    }

    @Test
    void testGetWeatherInfo() throws Exception {

        PincodeLocation pincodeLocationmock = new PincodeLocation(560066, 12.9716, 77.7473);


        WeatherInfo weatherInfomock=new WeatherInfo();
        weatherInfomock.setPincode(560066);
        weatherInfomock.setDate(LocalDate.parse("2024-04-06"));
        weatherInfomock.setPlace("hoskote");
        weatherInfomock.setDescription("clear sky");


        WeatherApiResponse mockResponse=new WeatherApiResponse();
        mockResponse.setCoord(new Coord(73.9158,18.5685));
        List<Weather> temp=new ArrayList<>();
        temp.add(new Weather( 800, "Clear",  "clear sky",  "01d"));
        mockResponse.setWeather(temp);

        when(pincodeLocationRepository.findById(560066)).thenReturn(Optional.of(pincodeLocationmock));
        when(weatherInfoRepository.findByPincodeAndDate(560066,LocalDate.parse("2024-04-06"))).thenReturn(Optional.of(weatherInfomock));
        when(weatherInfoRepository.save(weatherInfomock)).thenReturn(weatherInfomock);
        when(restTemplate.getForEntity(anyString(),eq(WeatherApiResponse.class))).thenReturn(ResponseEntity.ok(mockResponse));

        WeatherInfo weatherInfo = weatherService.getWeatherInfo(560066, LocalDate.parse("2024-04-06"));
        assertNotNull(weatherInfo);
        Assertions.assertEquals(weatherInfomock.getPincode(), weatherInfo.getPincode());
        Assertions.assertEquals(weatherInfomock.getDate(), weatherInfo.getDate());
        Assertions.assertEquals(weatherInfomock.getPlace(), weatherInfo.getPlace());
        Mockito.verify(weatherInfoRepository).findByPincodeAndDate(560066, LocalDate.parse("2024-04-06"));
        Mockito.verifyNoMoreInteractions(weatherInfoRepository);
    }
}