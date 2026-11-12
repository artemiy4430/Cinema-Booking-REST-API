package main.cinemabookingrestapi.entity;

public class MaxId {
    private String entityName;
    private Long maxId;

    public MaxId() {}

    public MaxId(String entityName, Long maxId) {
        this.entityName = entityName;
        this.maxId = maxId;
    }

    public String getEntityName() {
        return entityName;
    }

    public Long getMaxId() {
        return maxId;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public void setMaxId(Long maxId) {
        this.maxId = maxId;
    }
}
