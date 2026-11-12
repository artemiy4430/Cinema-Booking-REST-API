package main.cinemabookingrestapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Screening extends Entity {
    private Long movieId;
    private Long hallId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private BigDecimal price;
    private boolean active;

    public Screening(Long id, Long movieId, Long hallId, LocalDateTime startAt, LocalDateTime endAt,
                     BigDecimal price, boolean active) {
        super(id);
        this.movieId = movieId;
        this.hallId = hallId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.price = price;
        this.active = active;
    }

    public Screening(Long id) {
        super(id);
    }

    public Screening() {
    }

    public Long getId() {
        return id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public Long getHallId() {
        return hallId;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public void setHallId(Long hallId) {
        this.hallId = hallId;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Screening screening = (Screening) o;
        return active == screening.active && Objects.equals(id, screening.id) && Objects.equals(movieId, screening.movieId) && Objects.equals(hallId, screening.hallId) && Objects.equals(startAt, screening.startAt) && Objects.equals(endAt, screening.endAt) && Objects.equals(price, screening.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, movieId, hallId, startAt, endAt, price, active);
    }
}
