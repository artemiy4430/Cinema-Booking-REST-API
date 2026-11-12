package main.cinemabookingrestapi.repository;

import main.cinemabookingrestapi.entity.Hall;

import java.util.List;
import java.util.Optional;

public interface HallRepository {

    List<Hall> getAll();

    Optional<Hall> getById(Long id);

    Hall addHall(Hall hall);

    Optional<Hall> updateHall(Long id, Hall updatedHall);

    boolean removeHall(Long id);

    Optional<Hall> getByName(String name);
}
