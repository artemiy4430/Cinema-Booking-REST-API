package main.cinemabookingrestapi.dto.movie;

import java.math.BigDecimal;

public class MovieFilterRequest {
    private String genre;
    private Boolean active;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String search;


    public MovieFilterRequest(String genre, Boolean active, BigDecimal minPrice, BigDecimal maxPrice, String search) {
        this.genre = genre;
        this.active = active;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.search = search;
    }

    public String getGenre() {
        return genre;
    }

    public Boolean getActive() {
        return active;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public String getSearch() {
        return search;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
