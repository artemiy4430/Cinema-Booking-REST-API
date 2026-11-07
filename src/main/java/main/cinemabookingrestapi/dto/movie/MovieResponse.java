package main.cinemabookingrestapi.dto.movie;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Objects;

public class MovieResponse {

    private Long id;
    private String title;
    private String genre;
    private int durationMinutes;
    private int ageRating;
    private BigDecimal basePrice;
    private boolean active;

    public MovieResponse(Long id, String title, String genre, int durationMinutes, int ageRating, BigDecimal basePrice, boolean active) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.ageRating = ageRating;
        this.basePrice = basePrice;
        this.active = active;
    }

    public Long getId() {
        return id;
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

    public boolean isActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MovieResponse that = (MovieResponse) o;
        return durationMinutes == that.durationMinutes && ageRating == that.ageRating && active == that.active && Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(genre, that.genre) && Objects.equals(basePrice, that.basePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, genre, durationMinutes, ageRating, basePrice, active);
    }
}
