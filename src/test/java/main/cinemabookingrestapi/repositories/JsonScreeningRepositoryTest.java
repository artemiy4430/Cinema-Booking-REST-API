package main.cinemabookingrestapi.repositories;


import main.cinemabookingrestapi.entity.Screening;
import main.cinemabookingrestapi.repository.json.JsonMaxIdRepository;
import main.cinemabookingrestapi.repository.json.JsonScreeningRepository;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


public class JsonScreeningRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonScreeningRepository repository;
    private JsonMaxIdRepository maxIdRepository;

    @BeforeEach
    void setUp() {
        JsonMapper jsonMapper = new JsonMapper();

        Path maxIdsFile = tempDir.resolve("temp_max_ids.json");
        maxIdRepository = new JsonMaxIdRepository(jsonMapper, maxIdsFile.toString());

        Path file = tempDir.resolve("temp_screenings.json");
        repository = new JsonScreeningRepository(jsonMapper, file.toString(), maxIdRepository);
    }

    @Test
    void getAll_shouldReturnEmptyList_whenFileDoesNotExist() {
        assertThat(repository.getAll()).isEmpty();
    }

    @Test
    void getAll_shouldReturnListOfScreenings() {
        Screening screening1 = createScreening();
        Screening screening2 = new Screening(
                2L,
                2L,
                2L,
                LocalDateTime.of(2026, 11, 15, 18, 0),
                LocalDateTime.of(2026, 11, 15, 20, 32),
                new BigDecimal("250.00"),
                true
        );

        repository.addScreening(screening1);
        repository.addScreening(screening2);

        List<Screening> screenings = repository.getAll();

        assertNotNull(screenings);
        assertEquals(2, screenings.size());
        assertEquals(1L, screenings.get(0).getId());
        assertEquals(2L, screenings.get(1).getId());
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenScreeningIsNotFound() {
        Long incorrectId = 12L;

        Optional<Screening> screening = repository.getById(incorrectId);

        assertNotNull(screening);
        assertTrue(screening.isEmpty());
    }

    @Test
    void getById_shouldReturnOptionalOfScreening() {
        Screening screening = createScreening();
        repository.addScreening(screening);

        Optional<Screening> screeningOptional = repository.getById(screening.getId());

        assertNotNull(screeningOptional);
        assertTrue(screeningOptional.isPresent());
        assertEquals(screening.getId(), screeningOptional.get().getId());
    }


    @Test
    void addScreening_shouldGenerateIdAndAddEntityInFile() {
        Screening screening = createScreening();
        screening.setId(null);

        Screening addedScreening = repository.addScreening(screening);

        assertNotNull(addedScreening.getId());
        assertEquals(screening.getMovieId(), addedScreening.getMovieId());
        assertEquals(1, repository.getAll().size());

        Screening loadedScreening = repository.getById(addedScreening.getId()).orElseThrow();
        assertThat(loadedScreening).usingRecursiveComparison().isEqualTo(addedScreening);
    }

    @Test
    void removeScreening_shouldReturnFalse_whenScreeningIsNotPresent() {
        Long incorrectId = 12L;

        boolean result = repository.removeScreening(incorrectId);

        assertFalse(result);
    }

    @Test
    void removeScreening_shouldReturnTrue() {
        Screening screening = createScreening();
        repository.addScreening(screening);

        boolean result = repository.removeScreening(screening.getId());

        assertTrue(result);
    }

    private Screening createScreening() {
        return new Screening(
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 11, 15, 14, 0),
                LocalDateTime.of(2026, 11, 15, 16, 42),
                new BigDecimal("300.00"),
                true
        );
    }
}