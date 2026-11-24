package main.cinemabookingrestapi.dto.movie;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class MovieFilterRequest {
    private Optional<String> genre;
    private Optional<Boolean> active;
    private Optional<BigDecimal> minPrice;
    private Optional<BigDecimal> maxPrice;
    private Optional<String> title;



    public MovieFilterRequest(String genre, Boolean active, BigDecimal minPrice,
                              BigDecimal maxPrice, String title) {
        this.genre = Optional.ofNullable(genre);
        this.active = Optional.ofNullable(active);
        this.minPrice = Optional.ofNullable(minPrice);
        this.maxPrice = Optional.ofNullable(maxPrice);
        this.title = Optional.ofNullable(title);
    }

    public Optional<String> getGenre() {
        return genre;
    }

    public Optional<Boolean> getActive() {
        return active;
    }

    public Optional<BigDecimal> getMinPrice() {
        return minPrice;
    }

    public Optional<BigDecimal> getMaxPrice() {
        return maxPrice;
    }

    public Optional<String> getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MovieFilterRequest that = (MovieFilterRequest) o;
        return Objects.equals(genre, that.genre) && Objects.equals(active, that.active) && Objects.equals(minPrice, that.minPrice) && Objects.equals(maxPrice, that.maxPrice) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genre, active, minPrice, maxPrice, title);
    }
}
