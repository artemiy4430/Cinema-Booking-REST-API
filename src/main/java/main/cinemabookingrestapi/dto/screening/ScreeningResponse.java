package main.cinemabookingrestapi.dto.screening;

import main.cinemabookingrestapi.entity.Screening;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ScreeningResponse {
    private Long movieId;
    private Long hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;

    public ScreeningResponse(Long movieId, Long hallId, LocalDateTime startTime, LocalDateTime endTime ,BigDecimal price) {
        this.movieId = movieId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public Long getMovieId() {
        return movieId;
    }

    public Long getHallId() {
        return hallId;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
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

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ScreeningResponse that = (ScreeningResponse) o;
        return Objects.equals(movieId, that.movieId) && Objects.equals(hallId, that.hallId) && Objects.equals(startTime, that.startTime) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, hallId, startTime, price);
    }

    public static ScreeningResponse fromEntity(Screening screening) {
        return new ScreeningResponse(
                screening.getMovieId(),
                screening.getHallId(),
                screening.getStartAt(),
                screening.getEndAt(),
                screening.getPrice()
        );
    }
}
