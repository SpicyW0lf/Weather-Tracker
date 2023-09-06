package ru.petrov.weathertracker.DTO.weatherView;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationView {
    @JsonIgnore
    Integer id;
    List<Weather> weather;
    Main main;
    Wind wind;
    String name;
}
