package main.cinemabookingrestapi.controller;

import main.cinemabookingrestapi.dto.booking.CancelBookingResponse;
import main.cinemabookingrestapi.dto.booking.CreateBookingRequest;
import main.cinemabookingrestapi.dto.booking.CreateBookingResponse;
import main.cinemabookingrestapi.entity.Booking;
import main.cinemabookingrestapi.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/bookings")
//http://localhost:8080/api/bookings
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Booking> getAll() {
        return bookingService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getById(@PathVariable Long id) {
        return bookingService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-customer/{id}")
    public ResponseEntity<Booking> getByCustomerId(@PathVariable Long id) {
        return bookingService.getByCustomerId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-screening/{id}")
    public ResponseEntity<Booking> getByScreeningId(@PathVariable Long id) {
        return bookingService.getByScreeningId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponse> addBooking(@RequestBody CreateBookingRequest request) {
        CreateBookingResponse response = bookingService.addBooking(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CancelBookingResponse> cancelBooking(@PathVariable Long id) {
        CancelBookingResponse response = bookingService.cancelBooking(id);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
