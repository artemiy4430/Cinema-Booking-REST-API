package main.cinemabookingrestapi.repository.json;

import main.cinemabookingrestapi.entity.*;
import main.cinemabookingrestapi.repository.MaxIdRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Repository
public class JsonMaxIdRepository extends JsonRepository<MaxId> implements MaxIdRepository {

    public JsonMaxIdRepository(JsonMapper jsonMapper, @Value("./data/maxIds.json") String filePath) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<MaxId>>() {});
    }

    public Optional<Long> getMaxIdByClass(Class<? extends Entity> entityClass) {
        String entityName = entityClass.getSimpleName();

        return readEntities().stream()
                .filter(m -> entityName.equals(m.getEntityName()))
                .map(MaxId::getMaxId)
                .findFirst();
    }

    public Long generateNewId(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        String entityName = entity.getClass().getSimpleName();
        List<MaxId> maxIds = readEntities();
        MaxId targetRecord = maxIds.stream()
                .filter(m -> entityName.equals(m.getEntityName()))
                .findFirst()
                .orElseGet(() -> {
                    MaxId newRecord = new MaxId(entityName, 0L);
                    maxIds.add(newRecord);
                    return newRecord;
                });

        long nextId = targetRecord.getMaxId() + 1;
        targetRecord.setMaxId(nextId);

        writeEntities(maxIds);
        return nextId;
    }

    @Override
    public List<MaxId> getAll() {
        return readEntities();
    }

    @Override
    public Optional<Long> getBookingMaxId() {
        return getMaxIdByClass(Booking.class);
    }

    @Override
    public Optional<Long> getCustomerMaxId() {
        return getMaxIdByClass(Customer.class);
    }

    @Override
    public Optional<Long> getHallMaxId() {
        return getMaxIdByClass(Hall.class);
    }

    @Override
    public Optional<Long> getMovieMaxId() {
        return getMaxIdByClass(Movie.class);
    }

    @Override
    public Optional<Long> getScreeningMaxId() {
        return getMaxIdByClass(Screening.class);
    }
}
