package main.cinemabookingrestapi.dto.movie;

import jakarta.validation.constraints.*;
import main.cinemabookingrestapi.entity.Movie;

import java.math.BigDecimal;
import java.util.Objects;

public class MovieResponse {
    private String title;
    private String genre;
    private int durationMinutes;
    private int ageRating;
    private BigDecimal basePrice;

    public MovieResponse(String title, String genre, int durationMinutes, int ageRating, BigDecimal basePrice) {
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.ageRating = ageRating;
        this.basePrice = basePrice;
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
        MovieResponse that = (MovieResponse) o;
        return durationMinutes == that.durationMinutes && ageRating == that.ageRating && Objects.equals(title, that.title) && Objects.equals(genre, that.genre) && Objects.equals(basePrice, that.basePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, genre, durationMinutes, ageRating, basePrice);
    }

    public static MovieResponse fromEntity(Movie movie) {
        return new MovieResponse(
                movie.getTitle(),
                movie.getGenre(),
                movie.getDurationMinutes(),
                movie.getAgeRating(),
                movie.getBasePrice()
        );
    }
}
