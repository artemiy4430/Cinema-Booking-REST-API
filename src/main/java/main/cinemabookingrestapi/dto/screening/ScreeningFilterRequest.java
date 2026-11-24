package main.cinemabookingrestapi.dto.screening;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class ScreeningFilterRequest {

    private Optional<Long> movieId;
    private Optional<Long> hallId;
    private Optional<LocalDateTime> startAt;
    private Optional<Boolean> isActive;

    public ScreeningFilterRequest(Long movieId, Long hallId, LocalDateTime startAt, Boolean isActive) {
        this.movieId = Optional.ofNullable(movieId);
        this.hallId = Optional.ofNullable(hallId);
        this.startAt = Optional.ofNullable(startAt);
        this.isActive = Optional.ofNullable(isActive);
    }

    public Optional<Long> getMovieId() {
        return movieId;
    }

    public Optional<Long> getHallId() {
        return hallId;
    }

    public Optional<LocalDateTime> getStartAt() {
        return startAt;
    }

    public Optional<Boolean> getIsActive() {
        return isActive;
    }

    public void setMovieId(Optional<Long> movieId) {
        this.movieId = movieId;
    }

    public void setHallId(Optional<Long> hallId) {
        this.hallId = hallId;
    }

    public void setStartAt(Optional<LocalDateTime> startAt) {
        this.startAt = startAt;
    }

    public void setIsActive(Optional<Boolean> isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ScreeningFilterRequest that = (ScreeningFilterRequest) o;
        return Objects.equals(movieId, that.movieId) && Objects.equals(hallId, that.hallId)
                && Objects.equals(startAt, that.startAt) && Objects.equals(isActive, that.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, hallId, startAt, isActive);
    }
}
