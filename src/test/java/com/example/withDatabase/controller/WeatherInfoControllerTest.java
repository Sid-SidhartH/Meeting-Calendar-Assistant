package com.example.withDatabase.controller;



import static org.mockito.Mockito.verify;
import java.time.LocalDate;

import com.example.withDatabase.Controller.WeatherInfoController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.withDatabase.entity.WeatherInfo;
import com.example.withDatabase.service.WeatherService;

@SpringBootTest
public class WeatherInfoControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherInfoController weatherInfoController;

    private WeatherInfo expectedWeatherInfo ;

    @BeforeEach
    public void setUp(){
        this.expectedWeatherInfo = new WeatherInfo();
        expectedWeatherInfo.setPincode(12345);
        expectedWeatherInfo.setPlace("XYZ");
        LocalDate for_date = LocalDate.parse("2024-04-06");
        expectedWeatherInfo.setDate(for_date);
        expectedWeatherInfo.setTemperature(20.0);
        expectedWeatherInfo.setHumidity(50);
        expectedWeatherInfo.setPressure(1000);
        expectedWeatherInfo.setWindSpeed(3.46);
        expectedWeatherInfo.setDescription("clear");
    }

    @Test
    void testGetWeatherInfo() throws Exception {
        Mockito.when(weatherService.getWeatherInfo(12345,LocalDate.parse("2024-03-02"))).thenReturn(expectedWeatherInfo);
        ResponseEntity<WeatherInfo> actualWeatherInfo = weatherInfoController.getWeatherInfo(12345,LocalDate.parse("2024-03-02"));
        // verify results
        Assertions.assertEquals(HttpStatus.OK, actualWeatherInfo.getStatusCode());
        Assertions.assertEquals(expectedWeatherInfo.getTemperature(), actualWeatherInfo.getBody().getTemperature());
        Assertions.assertEquals(expectedWeatherInfo.getHumidity(), actualWeatherInfo.getBody().getHumidity());
        verify(weatherService, Mockito.times(1)).getWeatherInfo(12345,LocalDate.parse("2024-03-02") );

    }
}