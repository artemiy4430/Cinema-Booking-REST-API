package main.cinemabookingrestapi.repository.json;

import main.cinemabookingrestapi.entity.Screening;
import main.cinemabookingrestapi.repository.ScreeningRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JsonScreeningRepository extends JsonRepository<Screening> implements ScreeningRepository {
    private final JsonMaxIdRepository maxIdRepository;

    public JsonScreeningRepository(JsonMapper jsonMapper, @Value("./data/screenigs.json") String filePath,
                                   JsonMaxIdRepository maxIdRepository) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<Screening>>(){});
        this.maxIdRepository = maxIdRepository;
    }

    @Override
    public synchronized List<Screening> getAll() {
        return readEntities();
    }

    @Override
    public synchronized Optional<Screening> getById(Long id) {
        return readEntities().stream().filter(s -> Objects.equals(s.getId(), id)).findFirst();
    }

    @Override
    public synchronized Screening addScreening(Screening screening) {
        List<Screening> screenings = readEntities();

        if (screenings.contains(screening)) {
            return null;
        }

        screening.setId(maxIdRepository.generateNewId(screening));
        screenings.add(screening);
        writeEntities(screenings);
        return screening;
    }

    @Override
    public synchronized boolean removeScreening(Long id) {
        List<Screening> screenings = readEntities();
        Optional<Screening> screening = screenings
                .stream()
                .filter(s -> Objects.equals(s.getId(), id))
                .findFirst();

        if (screening.isEmpty()) return false;

        screenings.remove(screening.get());
        writeEntities(screenings);
        return true;
    }
}
