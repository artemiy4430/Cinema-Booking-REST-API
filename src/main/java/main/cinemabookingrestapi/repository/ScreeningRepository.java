package main.cinemabookingrestapi.repository;

import main.cinemabookingrestapi.entity.Screening;

import java.util.List;
import java.util.Optional;

public interface ScreeningRepository {

    List<Screening> getAll();

    Optional<Screening> getById(Long id);

    Screening addScreening(Screening screening);

    boolean removeScreening(Long id);
}
