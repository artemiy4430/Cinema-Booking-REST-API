package main.cinemabookingrestapi.dto.booking;

import main.cinemabookingrestapi.entity.Booking;
import main.cinemabookingrestapi.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateBookingResponse {
   private BigDecimal totalPrice;

   private LocalDateTime createAt;

   private BookingStatus status;

    public CreateBookingResponse(BigDecimal totalPrice, LocalDateTime createAt, BookingStatus status) {
        this.totalPrice = totalPrice;
        this.createAt = createAt;
        this.status = status;
    }

    public CreateBookingResponse() {
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public static CreateBookingResponse fromEntity(Booking booking) {
        CreateBookingResponse response = new CreateBookingResponse();

        response.setTotalPrice(booking.getTotalPrice());
        response.setCreateAt(booking.getCreateAt());
        response.setStatus(booking.getStatus());

        return response;
    }
}
