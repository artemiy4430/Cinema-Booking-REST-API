package main.cinemabookingrestapi.service;

import main.cinemabookingrestapi.dto.hall.CreateHallRequest;
import main.cinemabookingrestapi.dto.hall.HallResponse;
import main.cinemabookingrestapi.dto.hall.UpdateHallRequest;
import main.cinemabookingrestapi.entity.Hall;
import main.cinemabookingrestapi.exception.alreadyExist.HallNameAlreadyExistsException;
import main.cinemabookingrestapi.exception.notFound.HallNotFoundException;
import main.cinemabookingrestapi.repository.HallRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class HallService {
    private final HallRepository hallRepository;

    public HallService(HallRepository hallRepository) {
        this.hallRepository = hallRepository;
    }

    public List<Hall> getAll() {
        return hallRepository.getAll();
    }

    public Optional<Hall> getById(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException();
        }

        Optional<Hall> hall = hallRepository.getById(id);

        if (hall.isEmpty()) {
            throw new HallNotFoundException(id);
        }

        return hall;
    }

    public HallResponse addHall(CreateHallRequest createHallRequest) {
        if (createHallRequest == null) throw new IllegalArgumentException();

        if (hallRepository.getByName(createHallRequest.getName()).isPresent()) {
            throw new HallNameAlreadyExistsException(createHallRequest.getName());
        }

        Hall hall = hallRepository.addHall(createHallRequest.toEntity());

        return HallResponse.fromEntity(hall);
    }

    public HallResponse updateHall(Long id, UpdateHallRequest updateHallRequest) {
        if (updateHallRequest == null) throw new IllegalArgumentException();

        Optional<Hall> hallOptional = hallRepository.getById(id);

        if (hallOptional.isEmpty()) throw new HallNotFoundException(id);

        boolean isNameTaken = hallRepository
                .getAll()
                .stream().anyMatch(h -> Objects.equals(h.getName(), updateHallRequest.getName()));

        if (isNameTaken) throw new HallNameAlreadyExistsException(updateHallRequest.getName());

        Optional<Hall> newHall = hallRepository.updateHall(id, updateHallRequest.updateHall(hallOptional.get()));

        if (newHall.isEmpty()) throw new IllegalArgumentException();

        return HallResponse.fromEntity(newHall.get());
    }

    public void removeHall(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException();

        Hall hall = hallRepository.getById(id).orElseThrow(() -> new HallNotFoundException(id));

        hall.setActive(false);

        hallRepository.updateHall(id, hall);
    }

}
