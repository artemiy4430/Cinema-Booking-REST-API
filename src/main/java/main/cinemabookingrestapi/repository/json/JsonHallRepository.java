package main.cinemabookingrestapi.repository.json;

import main.cinemabookingrestapi.entity.Hall;
import main.cinemabookingrestapi.repository.HallRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JsonHallRepository extends JsonRepository<Hall> implements HallRepository {

    public JsonHallRepository(JsonMapper jsonMapper, @Value("./data/halls.json") String filePath) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<Hall>>() {
        });
    }

    @Override
    public synchronized List<Hall> getAll() {
        return readEntities();
    }

    @Override
    public synchronized Optional<Hall> getById(Long id) {
        return readEntities().stream().filter(h -> Objects.equals(h.getId(), id)).findFirst();
    }

    @Override
    public synchronized Hall addHall(Hall hall) {
        List<Hall> halls = readEntities();

        if (halls.contains(hall)) return null;

        hall.setId(generateNewId(halls));
        halls.add(hall);
        writeEntities(halls);

        return hall;
    }

    @Override
    public synchronized Optional<Hall> updateHall(Long id, Hall updatedHall) {
        List<Hall> halls = readEntities();
        Optional<Hall> hall = halls.stream().filter(h -> Objects.equals(h.getId(), id)).findFirst();

        if (hall.isEmpty()) return Optional.empty();

        halls.remove(hall.get());
        halls.add(updatedHall);
        writeEntities(halls);

        return Optional.of(updatedHall);
    }

    @Override
    public synchronized boolean removeHall(Long id) {
        List<Hall> halls = readEntities();
        Optional<Hall> hall = halls.stream().filter(h -> Objects.equals(h.getId(), id)).findFirst();

        if (hall.isEmpty()) return false;

        halls.remove(hall.get());
        writeEntities(halls);

        return true;
    }
}
