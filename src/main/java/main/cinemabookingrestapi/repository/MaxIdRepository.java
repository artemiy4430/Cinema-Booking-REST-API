package main.cinemabookingrestapi.repository;

import main.cinemabookingrestapi.entity.MaxId;

import java.util.List;
import java.util.Optional;

public interface MaxIdRepository {
    List<MaxId> getAll();

    Optional<Long> getBookingMaxId();

    Optional<Long> getCustomerMaxId();

    Optional<Long> getHallMaxId();

    Optional<Long> getMovieMaxId();

    Optional<Long> getScreeningMaxId();
}
