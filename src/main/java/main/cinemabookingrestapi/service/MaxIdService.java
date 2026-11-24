package main.cinemabookingrestapi.service;

import main.cinemabookingrestapi.entity.MaxId;
import main.cinemabookingrestapi.repository.json.JsonMaxIdRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaxIdService {
    private final JsonMaxIdRepository jsonMaxIdRepository;

    public MaxIdService(JsonMaxIdRepository jsonMaxIdRepository) {
        this.jsonMaxIdRepository = jsonMaxIdRepository;
    }

    public List<MaxId> getAll() {
        return jsonMaxIdRepository.readEntities();
    }

    public Optional<Long> getBookingMaxId() {
        return jsonMaxIdRepository.getBookingMaxId();
    }

    public Optional<Long> getCustomerMaxId() {
        return jsonMaxIdRepository.getCustomerMaxId();
    }

    public Optional<Long> getHallMaxId() {
        return jsonMaxIdRepository.getHallMaxId();
    }

    public Optional<Long> getMovieMaxId() {
        return jsonMaxIdRepository.getMovieMaxId();
    }

    public Optional<Long> getScreeningMaxId() {
        return jsonMaxIdRepository.getScreeningMaxId();
    }
}
