package ru.petrov.weathertracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.petrov.weathertracker.models.Location;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByName(String name);

    @Query("SELECT loc FROM Location loc WHERE loc.latitude=?1 AND loc.longitude=?2")
    Optional<Location> findLocationByLatitudeAndLongitude(Double lat, Double lon);
}
