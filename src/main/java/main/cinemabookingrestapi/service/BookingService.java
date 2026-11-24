package main.cinemabookingrestapi.service;

import main.cinemabookingrestapi.dto.booking.CancelBookingResponse;
import main.cinemabookingrestapi.dto.booking.CreateBookingResponse;
import main.cinemabookingrestapi.dto.booking.CreateBookingRequest;
import main.cinemabookingrestapi.entity.*;
import main.cinemabookingrestapi.exception.alreadyCancelled.BookingAlreadyCancelledException;
import main.cinemabookingrestapi.exception.inactive.InactiveCustomerException;
import main.cinemabookingrestapi.exception.inactive.InactiveScreeningException;
import main.cinemabookingrestapi.exception.notFound.BookingNotFoundException;
import main.cinemabookingrestapi.exception.notFound.CustomerNotFoundException;
import main.cinemabookingrestapi.exception.notFound.HallNotFoundException;
import main.cinemabookingrestapi.exception.notFound.ScreeningNotFoundException;
import main.cinemabookingrestapi.exception.screeningException.ScreeningAlreadyStartedException;
import main.cinemabookingrestapi.exception.seatException.DuplicateSeatException;
import main.cinemabookingrestapi.exception.seatException.InvalidSeatException;
import main.cinemabookingrestapi.exception.seatException.SeatAlreadyBookedException;
import main.cinemabookingrestapi.repository.BookingRepository;
import main.cinemabookingrestapi.repository.CustomerRepository;
import main.cinemabookingrestapi.repository.HallRepository;
import main.cinemabookingrestapi.repository.ScreeningRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final ScreeningRepository screeningRepository;
    private final HallRepository hallRepository;
    private final Clock clock;

    public BookingService(BookingRepository bookingRepository, CustomerRepository customerRepository,
                          ScreeningRepository screeningRepository, HallRepository hallRepository, Clock clock) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.screeningRepository = screeningRepository;
        this.hallRepository = hallRepository;
        this.clock = clock;
    }

    public List<Booking> getAll() {
        return bookingRepository.getAll();
    }

    public Optional<Booking> getById(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException();
        }
        Booking booking = bookingRepository.getById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        return Optional.of(booking);
    }

    public Optional<Booking> getByCustomerId(Long customerId) {
        if (customerId == null || customerId < 1) {
            throw new IllegalArgumentException();
        }

        Optional<Booking> booking = bookingRepository.getByCustomerID(customerId);

        if (booking.isEmpty()) throw new BookingNotFoundException(customerId);

        return booking;
    }

    public CreateBookingResponse addBooking(CreateBookingRequest request) {
        if (request == null) throw new IllegalArgumentException();

        Customer customer = customerRepository.getById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
        Screening screening = screeningRepository.getById(request.getScreeningId())
                .orElseThrow(() -> new ScreeningNotFoundException(request.getScreeningId()));

        if (!customer.isActive()) throw new InactiveCustomerException("Customer: "
                + request.getCustomerId() + " is currently inactive");

        if (!screening.isActive()) throw new InactiveScreeningException("Screening: "
                + request.getScreeningId() + " is currently inactive");

        if (!screening.getStartAt().isAfter(LocalDateTime.now(clock))) {
            throw new ScreeningAlreadyStartedException(screening.getStartAt());
        }

        Hall hall = hallRepository.getById(screening.getHallId())
                .orElseThrow(() -> new HallNotFoundException(screening.getHallId()));

        boolean isValid = request.getSeats().stream().allMatch(s -> isSeatValidInHall(s, hall));

        if (!isValid) {
            throw new InvalidSeatException();
        }

        Set<String> seats = new HashSet<>(request.getSeats());

        if (seats.size() != request.getSeats().size()) {
            throw new DuplicateSeatException("the given reservation contains duplicate seats");
        }

        boolean isReserved = bookingRepository.getAll()
                .stream()
                .filter(b -> Objects.equals(b.getScreeningId(), request.getScreeningId())
                        && b.getStatus() == BookingStatus.ACTIVE)
                .flatMap(b -> b.getSeats().stream())
                .anyMatch(occupiedSeat -> request.getSeats().contains(occupiedSeat));

        if (isReserved) throw new SeatAlreadyBookedException();

        BigDecimal totalPrice = screening.getPrice()
                .multiply(BigDecimal.valueOf(request.getSeats().size()));

        Booking booking = request.toEntity();

        booking.setTotalPrice(totalPrice);
        booking.setCreateAt(LocalDateTime.now(clock));
        booking.setStatus(BookingStatus.ACTIVE);

        Booking savedBooking = bookingRepository.addBooking(booking);

        return CreateBookingResponse.fromEntity(savedBooking);
    }

    public CancelBookingResponse cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.getById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED)
            throw new BookingAlreadyCancelledException(booking);

        Screening screening = screeningRepository.getById(booking.getScreeningId())
                .orElseThrow(() -> new ScreeningNotFoundException(booking.getScreeningId()));

        if (!screening.getStartAt().isAfter(LocalDateTime.now(clock)))
            throw new ScreeningAlreadyStartedException(screening.getStartAt());

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now(clock));

        Booking updatedBooking = bookingRepository.cancelBooking(bookingId, booking)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        return CancelBookingResponse.fromEntity(updatedBooking);
    }

    private boolean isSeatValidInHall(String seat, Hall hall) {
        if (seat == null || seat.length() < 2) {
            return false;
        }

        char rowChar = seat.charAt(0);
        int rowNumber = rowChar - 'A' + 1;

        if (rowNumber < 1 || rowNumber > hall.getRows()) {
            return false;
        }

        try {
            int seatNumber = Integer.parseInt(seat.substring(1));
            return seatNumber >= 1 && seatNumber <= hall.getSeatsPerRow();
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
