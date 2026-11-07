package main.cinemabookingrestapi.dto.screeing;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import main.cinemabookingrestapi.entity.Screening;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class CreateScreeningRequest {

    @NotNull
    @Positive
    private Long movieId;

    @NotNull
    @Positive
    private Long hallId;

    @Future
    private LocalDateTime startAt;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal price;

    public CreateScreeningRequest(Long movieId, Long hallId, LocalDateTime startAt, BigDecimal price) {
        this.movieId = movieId;
        this.hallId = hallId;
        this.startAt = startAt;
        this.price = price;
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

    public BigDecimal getPrice() {
        return price;
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

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreateScreeningRequest that = (CreateScreeningRequest) o;
        return Objects.equals(movieId, that.movieId) && Objects.equals(hallId, that.hallId) && Objects.equals(startAt, that.startAt) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, hallId, startAt, price);
    }

    public Screening toEntity() {
        Screening screening = new Screening(null);

        screening.setMovieId(this.movieId);
        screening.setHallId(this.hallId);
        screening.setStartAt(this.startAt);
        screening.setPrice(this.price);

        return screening;
    }
}
