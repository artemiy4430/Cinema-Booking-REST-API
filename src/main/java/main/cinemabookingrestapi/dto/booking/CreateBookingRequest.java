package main.cinemabookingrestapi.dto.booking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import main.cinemabookingrestapi.entity.Booking;

import java.util.List;

public class CreateBookingRequest {
    @Positive
    private Long customerId;

    @Positive
    private Long screeningId;

    @NotEmpty(message = "Список мест не должен быть пустым")
    @Size(max = 6, message = "В одном бронировании можно указать максимум 6 мест")
    private List<String> seats;

    public CreateBookingRequest(Long customerId, Long screeningId, List<String> seats) {
        this.customerId = customerId;
        this.screeningId = screeningId;
        this.seats = seats;
    }

    public Booking toEntity() {
        Booking booking = new Booking();

        booking.setCustomerId(this.customerId);
        booking.setScreeningId(this.screeningId);
        booking.setSeats(this.seats);

        return booking;
    }
}
