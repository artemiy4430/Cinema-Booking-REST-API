package main.cinemabookingrestapi.dto.movie;

import jakarta.validation.constraints.*;
import main.cinemabookingrestapi.entity.Movie;

import java.math.BigDecimal;
import java.util.Objects;

public class UpdateMovieRequest {

    @NotNull
    @Size(max = 150)
    private String title;

    @NotBlank
    private String genre;

    @Min(30)
    @Max(30)
    private int durationMinutes;

    @Positive
    @Max(21)
    private int ageRating;

    @NotNull
    @Positive
    private BigDecimal basePrice;

    public UpdateMovieRequest(String title, String genre, int durationMinutes, int ageRating, BigDecimal basePrice) {
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.ageRating = ageRating;
        this.basePrice = basePrice;
    }

    public UpdateMovieRequest() {
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getAgeRating() {
        return ageRating;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public void setAgeRating(int ageRating) {
        this.ageRating = ageRating;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UpdateMovieRequest that = (UpdateMovieRequest) o;
        return durationMinutes == that.durationMinutes && ageRating == that.ageRating && Objects.equals(title, that.title)
                && Objects.equals(genre, that.genre) && Objects.equals(basePrice, that.basePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, genre, durationMinutes, ageRating, basePrice);
    }

    public static UpdateMovieRequest fromEntity(Movie movie) {
        UpdateMovieRequest request = new UpdateMovieRequest();

        request.setTitle(movie.getTitle());
        request.setGenre(movie.getGenre());
        request.setDurationMinutes(movie.getDurationMinutes());
        request.setAgeRating(movie.getAgeRating());
        request.setBasePrice(movie.getBasePrice());

        return request;
    }
}
