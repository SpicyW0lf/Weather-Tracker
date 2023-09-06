package ru.petrov.weathertracker.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ru.petrov.weathertracker.DTO.LocationDTO;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "locations")
@Getter
@Setter
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
    @ManyToMany(
            cascade = CascadeType.ALL,
            mappedBy = "locations",
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    public Location(String name, Double latitude, Double longitude, String country) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.country = country;
    }

    public LocationDTO toDto() {
        return new LocationDTO(
                this.id,
                this.name,
                this.latitude,
                this.longitude,
                this.country
        );
    }
}
