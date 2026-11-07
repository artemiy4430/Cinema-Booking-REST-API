package main.cinemabookingrestapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Booking extends Entity {
    private Long customerId;
    private Long screeningId;
    private List<String> seats;
    private BigDecimal totalPrice;
    private LocalDateTime createAt;
    private LocalDateTime cancelledAt;
    private BookingStatus status;

    public Booking(Long id, Long customerId, Long screeningId, List<String> seats, BigDecimal totalPrice,
                   LocalDateTime createAt, LocalDateTime cancelledAt, BookingStatus status) {
        super(id);
        this.customerId = customerId;
        this.screeningId = screeningId;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.createAt = createAt;
        this.cancelledAt = cancelledAt;
        this.status = status;
    }

    public Booking(Long id) {
        super(id);
    }

    public Booking() {
    }

    public Long getId() {
        return id;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

}
