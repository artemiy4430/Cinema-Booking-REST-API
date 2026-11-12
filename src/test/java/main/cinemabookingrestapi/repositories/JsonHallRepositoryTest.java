package main.cinemabookingrestapi.repositories;

import main.cinemabookingrestapi.entity.Hall;
import main.cinemabookingrestapi.repository.json.JsonHallRepository;
import main.cinemabookingrestapi.repository.json.JsonMaxIdRepository;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class JsonHallRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonHallRepository repository;
    private JsonMaxIdRepository maxIdRepository;

    @BeforeEach
    void setUp() {
        JsonMapper jsonMapper = new JsonMapper();

        Path maxIdsFile = tempDir.resolve("temp_max_ids.json");
        maxIdRepository = new JsonMaxIdRepository(jsonMapper, maxIdsFile.toString());

        Path file = tempDir.resolve("temp_halls.json");
        repository = new JsonHallRepository(jsonMapper, file.toString(), maxIdRepository);
    }

    @Test
    void getAll_shouldReturnEmptyList_whenFileDoesNotExist() {
       assertThat(repository.getAll().isEmpty());
    }

    @Test
    void getAll_shouldReturnListOfHalls() {
        Hall hall1 = createHall();
        Hall hall2 = new Hall(2L, "Blue Hall", 10, 15, true);

        repository.addHall(hall1);
        repository.addHall(hall2);

        List<Hall> halls = repository.getAll();

        assertNotNull(halls);
        assertEquals(2, halls.size());
        assertEquals(1L, halls.get(0).getId());
        assertEquals(2L, halls.get(1).getId());
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenHallIsNotFound() {
        Long incorrectId = 12L;

        Optional<Hall> hall = repository.getById(incorrectId);

        assertNotNull(hall);
        assertTrue(hall.isEmpty());
    }

    @Test
    void getById_shouldReturnOptionalOfHall() {
        Hall hall = createHall();
        repository.addHall(hall);

        Optional<Hall> hallOptional = repository.getById(hall.getId());

        assertNotNull(hallOptional);
        assertTrue(hallOptional.isPresent());
        assertEquals(hall.getId(), hallOptional.get().getId());
    }

    @Test
    void addHall_shouldGenerateIdAndAddEntityInFile() {
        Hall hall = createHall();
        hall.setId(null);

        Hall addedHall = repository.addHall(hall);

        assertNotNull(addedHall.getId());
        assertEquals(hall.getName(), addedHall.getName());
        assertEquals(1, repository.getAll().size());

        Hall loadedHall = repository.getById(addedHall.getId()).orElseThrow();
        assertThat(loadedHall).usingRecursiveComparison().isEqualTo(addedHall);
    }

    @Test
    void updateHall_shouldReturnEmptyOptional_whenHallIsNotFound() {
        Long incorrectId = 12L;
        Hall hall = createHall();

        Optional<Hall> hallOptional = repository.updateHall(incorrectId, hall);

        assertNotNull(hallOptional);
        assertTrue(hallOptional.isEmpty());
    }

    @Test
    void updateHall_shouldReturnEmptyOptional_whenIdIsZero() {
        Long incorrectId = 0L;
        Hall hall = createHall();

        Optional<Hall> hallOptional = repository.updateHall(incorrectId, hall);

        assertNotNull(hallOptional);
        assertTrue(hallOptional.isEmpty());
    }

    @Test
    void updateHall_shouldReturnOptionalOfUpdatedHall() {
        Hall hall = createHall();
        repository.addHall(hall);
        Hall updatedHall = new Hall(
                hall.getId(),
                "VIP Red Hall",
                6,
                10,
                true
        );

        Optional<Hall> hallOptional = repository.updateHall(hall.getId(), updatedHall);

        assertNotNull(hallOptional);
        assertTrue(hallOptional.isPresent());
        assertEquals(updatedHall.getId(), hallOptional.get().getId());
        assertEquals(updatedHall.getName(), hallOptional.get().getName());

        Hall loadedHall = repository.getById(hall.getId()).orElseThrow();
        assertEquals("VIP Red Hall", loadedHall.getName());
    }

    @Test
    void removeHall_shouldReturnFalse_whenHallIsNotPresent() {
        Long incorrectId = 12L;

        boolean result = repository.removeHall(incorrectId);

        assertFalse(result);
    }

    @Test
    void removeHall_shouldReturnTrue() {
        Hall hall = createHall();
        repository.addHall(hall);

        boolean result = repository.removeHall(hall.getId());

        assertTrue(result);
    }

    private Hall createHall() {
        return new Hall(1L, "Red Hall", 8, 12, true);
    }
}
