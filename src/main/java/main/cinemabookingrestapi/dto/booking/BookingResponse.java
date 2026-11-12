package main.cinemabookingrestapi.dto.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import main.cinemabookingrestapi.entity.Booking;

import java.util.List;

public class BookingResponse {
    @Positive
    private Long customerId;

    @Positive
    private Long screeningId;

    @NotEmpty(message = "Список мест не должен быть пустым")
    @Size(max = 6, message = "В одном бронировании можно указать максимум 6 мест")
    private List<String> seats;

    public BookingResponse(Long customerId, Long screeningId, List<String> seats) {
        this.customerId = customerId;
        this.screeningId = screeningId;
        this.seats = seats;
    }

    public BookingResponse() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getScreeningId() {
        return screeningId;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setScreeningId(Long screeningId) {
        this.screeningId = screeningId;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public static BookingResponse fromEntity(Booking booking) {
        BookingResponse response = new BookingResponse();

        response.setCustomerId(booking.getCustomerId());
        response.setScreeningId(booking.getScreeningId());
        response.setSeats(booking.getSeats());

        return response;
    }
}
