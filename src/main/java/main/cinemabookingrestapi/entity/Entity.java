package main.cinemabookingrestapi.entity;

public abstract class Entity {
    protected Long id;

    public Entity(Long id) {
        this.id = id;
    }

    public Entity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
