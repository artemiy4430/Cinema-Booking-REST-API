package main.cinemabookingrestapi.repository.json;

import main.cinemabookingrestapi.entity.Entity;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class JsonRepository<T> {
    private final JsonMapper jsonMapper;
    private final Path filePath;
    private final TypeReference<List<T>> typeReference;

    public JsonRepository(JsonMapper jsonMapper, Path filePath, TypeReference<List<T>> typeReference) {
        this.jsonMapper = jsonMapper;
        this.filePath = filePath;
        this.typeReference = typeReference;
        initializeFile();
    }

    public List<T> readEntities() {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }

            String jsonText = Files.readString(filePath);

            if (jsonText.isBlank()) {
                return new ArrayList<>();
            }

            return jsonMapper.readValue(jsonText, typeReference);

        } catch (IOException e) {
            throw new IllegalStateException("impossible to read file", e);
        }
    }

    public void writeEntities(List<T> entities) {
        try {
            jsonMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), entities);
        } catch (JacksonException e) {
            throw new IllegalStateException("Impossible to write down entities into file", e);
        }
    }

    private void initializeFile() {
        try {
            Path parent = filePath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            if (!Files.exists(filePath)) {
                Files.writeString(filePath, "[]");
            }
        } catch (IOException e) {
            throw new IllegalStateException("impossible to create file", e);
        }
    }

    public Long generateNewId(List<? extends Entity> entities) {
        return entities.stream().mapToLong(Entity::getId).max().orElse(0) + 1;
    }

}
