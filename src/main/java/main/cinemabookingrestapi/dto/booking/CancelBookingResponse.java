package main.cinemabookingrestapi.dto.booking;

import main.cinemabookingrestapi.entity.Booking;
import main.cinemabookingrestapi.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CancelBookingResponse {

    private LocalDateTime cancelledAt;

    private BookingStatus status;

    public CancelBookingResponse(BigDecimal totalPrice, LocalDateTime cancelledAt, BookingStatus status) {
        this.cancelledAt = cancelledAt;
        this.status = status;
    }

    public CancelBookingResponse() {
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public static CancelBookingResponse fromEntity(Booking booking) {
        CancelBookingResponse response = new CancelBookingResponse();

        response.setCancelledAt(booking.getCancelledAt());
        response.setStatus(booking.getStatus());

        return response;
    }
}
