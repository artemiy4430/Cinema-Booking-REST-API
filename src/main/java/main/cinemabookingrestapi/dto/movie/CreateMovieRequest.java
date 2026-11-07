package main.cinemabookingrestapi.dto.movie;

import jakarta.validation.constraints.*;
import main.cinemabookingrestapi.entity.Movie;

import java.math.BigDecimal;

public class CreateMovieRequest {

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

    public CreateMovieRequest(String title, String genre, int durationMinutes, int ageRating, BigDecimal basePrice) {
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.ageRating = ageRating;
        this.basePrice = basePrice;
    }

    public CreateMovieRequest() {
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

    public Movie toEntity() {
        Movie movie = new Movie();

        movie.setTitle(this.title);
        movie.setGenre(this.genre);
        movie.setDurationMinutes(this.durationMinutes);
        movie.setAgeRating(this.ageRating);
        movie.setBasePrice(this.basePrice);

        return movie;
    }
}
