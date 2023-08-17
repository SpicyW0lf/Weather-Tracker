package ru.petrov.weathertracker.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.petrov.weathertracker.DTO.LocationDTO;

import java.util.Set;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "country")
    private String country;
    @ManyToMany(mappedBy = "locations")
    private Set<User> users;

    public Location(String name, Double latitude, Double longitude, String country) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
    }

    public LocationDTO toDto() {
        return new LocationDTO(
                this.name,
                this.latitude,
                this.longitude,
                this.country
        );
    }
}
