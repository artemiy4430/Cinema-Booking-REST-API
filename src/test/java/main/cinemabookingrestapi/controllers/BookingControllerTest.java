package main.cinemabookingrestapi.controllers;

import main.cinemabookingrestapi.controller.BookingController;
import main.cinemabookingrestapi.dto.booking.CancelBookingResponse;
import main.cinemabookingrestapi.dto.booking.CreateBookingRequest;
import main.cinemabookingrestapi.dto.booking.CreateBookingResponse;
import main.cinemabookingrestapi.entity.*;
import main.cinemabookingrestapi.exception.alreadyCancelled.BookingAlreadyCancelledException;
import main.cinemabookingrestapi.exception.inactive.InactiveCustomerException;
import main.cinemabookingrestapi.exception.inactive.InactiveHallException;
import main.cinemabookingrestapi.exception.inactive.InactiveScreeningException;
import main.cinemabookingrestapi.exception.notFound.BookingNotFoundException;
import main.cinemabookingrestapi.exception.notFound.ScreeningNotFoundException;
import main.cinemabookingrestapi.exception.screeningException.ScreeningAlreadyStartedException;
import main.cinemabookingrestapi.exception.seatException.DuplicateSeatException;
import main.cinemabookingrestapi.exception.seatException.InvalidSeatException;
import main.cinemabookingrestapi.exception.seatException.SeatAlreadyBookedException;
import main.cinemabookingrestapi.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Spy // создаёт «шпион»-обёртку над реальным объектом
    private Clock clock = Clock.fixed(Instant.parse("2026-11-15T10:00:00Z"), ZoneOffset.UTC);

    @InjectMocks
    private BookingController bookingController;

    @Test
    void getAll_ShouldReturnAllBookings() {
        when(bookingService.getAll()).thenReturn(createListBookings());

        List<Booking> result = bookingController.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void getAll_shouldReturnEmptyList_whenServiceReturnsEmptyList() {
        when(bookingService.getAll()).thenReturn(Collections.emptyList());

        List<Booking> result = bookingController.getAll();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void getById_shouldReturnOk_whenBookingExists() {
        Booking booking = createBooking();

        when(bookingService.getById(booking.getId())).thenReturn(Optional.of(booking));

        ResponseEntity<Booking> response = bookingController.getById(booking.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(booking);

        verify(bookingService).getById(booking.getId());
    }

    @Test
    void getById_shouldReturnStatusNotFound_whenBookingDoesNotExist() {
        Long incorrectId = 12L;

        when(bookingService.getById(incorrectId)).thenReturn(Optional.empty());

        ResponseEntity<Booking> response = bookingController.getById(incorrectId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(bookingService).getById(incorrectId);
    }

    @Test
    void getById_shouldThrowBookingNotFoundException_whenBookingDoesNotExist() {
        Long incorrectId = 12L;

        when(bookingService.getById(incorrectId)).thenThrow(new BookingNotFoundException(incorrectId));

        assertThrows(BookingNotFoundException.class, () -> bookingController.getById(incorrectId));

        verify(bookingService).getById(incorrectId);
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIdIsIncorrect() {
        Long incorrectId = -1L;

        when(bookingService.getById(incorrectId)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> bookingController.getById(incorrectId));

        verify(bookingService).getById(incorrectId);
    }

    @Test
    void getByCustomerId_shouldReturnOk_whenBookingExists() {
        Booking booking = createBooking();

        when(bookingService.getByCustomerId(booking.getId())).thenReturn(Optional.of(booking));

        ResponseEntity<Booking> response = bookingController.getByCustomerId(booking.getCustomerId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(booking);

        verify(bookingService).getByCustomerId(booking.getId());
    }


    @Test
    void getByCustomerId_shouldReturnStatusNotFound_whenBookingDoesNotExist() {
        Long incorrectId = 12L;

        when(bookingService.getByCustomerId(incorrectId)).thenReturn(Optional.empty());

        ResponseEntity<Booking> response = bookingController.getByCustomerId(incorrectId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(bookingService).getByCustomerId(incorrectId);
    }

    @Test
    void getByCustomerId_shouldThrowBookingNotFoundException_whenBookingDoesNotExist() {
        Long incorrectId = 12L;

        when(bookingService.getByCustomerId(incorrectId)).thenThrow(new BookingNotFoundException(incorrectId));

        assertThrows(BookingNotFoundException.class, () -> bookingController.getByCustomerId(incorrectId));

        verify(bookingService).getByCustomerId(incorrectId);
    }

    @Test
    void getByCustomerId_shouldThrowIllegalArgumentException_whenIdIsIncorrect() {
        Long incorrectId = -1L;

        when(bookingService.getByCustomerId(incorrectId)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> bookingController.getByCustomerId(incorrectId));

        verify(bookingService).getByCustomerId(incorrectId);
    }

    @Test
    void getByScreeningId_shouldReturnOk_whenBookingExists() {
        Booking booking = createBooking();

        when(bookingService.getByScreeningId(booking.getScreeningId())).thenReturn(Optional.of(booking));

        ResponseEntity<Booking> response = bookingController.getByScreeningId(booking.getScreeningId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(booking);

        verify(bookingService).getByScreeningId(booking.getId());
    }


    @Test
    void getByScreeningId_shouldReturnStatusNotFound_whenBookingDoesNotExist() {
        Long incorrectId = 12L;

        when(bookingService.getByScreeningId(incorrectId)).thenReturn(Optional.empty());

        ResponseEntity<Booking> response = bookingController.getByScreeningId(incorrectId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(bookingService).getByScreeningId(incorrectId);
    }

    @Test
    void getByScreeningId_shouldThrowBookingNotFoundException_whenBookingDoesNotExist() {
        Long incorrectId = 12L;

        when(bookingService.getByScreeningId(incorrectId)).thenThrow(new BookingNotFoundException(incorrectId));

        assertThrows(BookingNotFoundException.class, () -> bookingController.getByScreeningId(incorrectId));

        verify(bookingService).getByScreeningId(incorrectId);
    }

    @Test
    void getByScreeningId_shouldThrowIllegalArgumentException_whenIdIsIncorrect() {
        Long incorrectId = -1L;

        when(bookingService.getByScreeningId(incorrectId)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> bookingController.getByScreeningId(incorrectId));

        verify(bookingService).getByScreeningId(incorrectId);
    }

    @Test
    void addBooking_shouldReturnStatusCreatedAndCreateBookingResponse_whenRequestIsValid() {
        CreateBookingRequest request =
                new CreateBookingRequest(2L, 2L, List.of("A1", "A2", "A3"));

        when(bookingService.addBooking(request)).thenReturn(new CreateBookingResponse(
                BigDecimal.valueOf(120.00),
                LocalDateTime.of(2026, 11, 15, 10, 0),
                BookingStatus.ACTIVE
        ));

        ResponseEntity<CreateBookingResponse> response = bookingController.addBooking(request);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        verify(bookingService).addBooking(request);
    }


    @Test
    void addBooking_shouldThrowInactiveCustomerException_whenCustomerIsInactive() {
        Customer customer = createCustomer();
        customer.setActive(false);

        CreateBookingRequest request =
                new CreateBookingRequest(1L, 1L, List.of("A1", "A2", "A3"));

        when(bookingService.addBooking(request)).thenThrow(new InactiveCustomerException("inactive customer"));

        assertThrows(InactiveCustomerException.class, () -> bookingController.addBooking(request));

        verify(bookingService).addBooking(request);
    }

    @Test
    void addBooking_shouldThrowInactiveScreeningException_whenScreeningIsInactive() {
        Screening screening = createScreening();
        screening.setActive(false);
        CreateBookingRequest request =
                new CreateBookingRequest(1L, 1L, List.of("A1", "A2", "A3"));

        when(bookingService.addBooking(request)).thenThrow(new InactiveScreeningException("inactive screening"));

        assertThrows(InactiveScreeningException.class, () -> bookingController.addBooking(request));

        verify(bookingService).addBooking(request);
    }

    @Test
    void addBooking_shouldThrowScreeningAlreadyStartedException_whenScreeningStartTimeHasAlreadyPassed() {
        Screening screening = createScreening();
        screening.setStartAt(LocalDateTime.now(clock).minusHours(1));
        CreateBookingRequest request =
                new CreateBookingRequest(1L, 1L, List.of("A1", "A2", "A3"));

        when(bookingService.addBooking(request)).thenThrow(new ScreeningAlreadyStartedException(screening.getStartAt()));

        assertThrows(ScreeningAlreadyStartedException.class, () -> bookingController.addBooking(request));

        verify(bookingService).addBooking(request);
    }

    @Test
    void addBooking_shouldThrowInactiveHallException_whenHallIsInactive() {
        Screening screening = createScreening();
        Hall hall = createHall();

        hall.setId(screening.getHallId());
        hall.setActive(false);

        CreateBookingRequest request =
                new CreateBookingRequest(1L, 1L, List.of("A1", "A2", "A3"));

        when(bookingService.addBooking(request)).thenThrow(new InactiveHallException("inactive hall"));

        assertThrows(InactiveHallException.class, () -> bookingController.addBooking(request));

        verify(bookingService).addBooking(request);
    }

    @Test
    void addBooking_shouldThrowInvalidSeatException_whenSelectedSeatIsInvalid() {
        CreateBookingRequest request =
                new CreateBookingRequest(1L, 1L, List.of("A21", "A22", "A23"));

        when(bookingService.addBooking(request)).thenThrow(new InvalidSeatException());

        assertThrows(InvalidSeatException.class, () -> bookingController.addBooking(request));

        verify(bookingService).addBooking(request);
    }

    @Test
    void addBooking_shouldThrowDuplicateSeatException_whenSelectedSeatIsDuplicated() {
        CreateBookingRequest request =
                new CreateBookingRequest(1L, 1L, List.of("A1", "A2", "A2"));

        when(bookingService.addBooking(request)).thenThrow(new DuplicateSeatException("selected seat is duplicate"));

        assertThrows(DuplicateSeatException.class, () -> bookingController.addBooking(request));

        verify(bookingService).addBooking(request);
    }

    @Test
    void addBooking_shouldThrowSeatAlreadyBookedException_whenSelectedSeatIsPresentInAnotherBooking() {
        CreateBookingRequest request =
                new CreateBookingRequest(1L, 1L, List.of("A1", "A2", "A2"));

        when(bookingService.addBooking(request)).thenThrow(new SeatAlreadyBookedException());

        assertThrows(SeatAlreadyBookedException.class, () -> bookingController.addBooking(request));

        verify(bookingService).addBooking(request);
    }

    @Test
    void cancelBooking_shouldReturnStatusAcceptedAndCancelBookingResponse_whenBookingIsValid() {
        Long bookingId = 1L;
        CancelBookingResponse expectedResponse = new CancelBookingResponse(
                BigDecimal.valueOf(120.00),
                LocalDateTime.of(2026, 11, 15, 10, 0),
                BookingStatus.CANCELLED
        );

        when(bookingService.cancelBooking(bookingId)).thenReturn(expectedResponse);

        ResponseEntity<CancelBookingResponse> response = bookingController.cancelBooking(bookingId);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isSameAs(expectedResponse);

        verify(bookingService).cancelBooking(bookingId);
    }

    @Test
    void cancelBooking_shouldThrowBookingNotFoundException_whenBookingDoesNotExist() {
        Long incorrectId = 99L;

        when(bookingService.cancelBooking(incorrectId)).thenThrow(new BookingNotFoundException(incorrectId));

        assertThrows(BookingNotFoundException.class, () -> bookingController.cancelBooking(incorrectId));

        verify(bookingService).cancelBooking(incorrectId);
    }

    @Test
    void cancelBooking_shouldThrowBookingAlreadyCancelledException_whenBookingIsAlreadyCancelled() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingService.cancelBooking(booking.getId()))
                .thenThrow(new BookingAlreadyCancelledException(booking));

        assertThrows(BookingAlreadyCancelledException.class, () -> bookingController.cancelBooking(booking.getId()));

        verify(bookingService).cancelBooking(booking.getId());
    }

    @Test
    void cancelBooking_shouldThrowScreeningNotFoundException_whenScreeningDoesNotExist() {
        Long bookingId = 1L;

        when(bookingService.cancelBooking(bookingId)).thenThrow(new ScreeningNotFoundException(1L));

        assertThrows(ScreeningNotFoundException.class, () -> bookingController.cancelBooking(bookingId));

        verify(bookingService).cancelBooking(bookingId);
    }

    @Test
    void cancelBooking_shouldThrowScreeningAlreadyStartedException_whenScreeningAlreadyStarted() {
        Booking booking = createBooking();
        Screening screening = createScreening();

        screening.setStartAt(LocalDateTime.now(clock).minusHours(1));
        booking.setScreeningId(1L);

        when(bookingService.cancelBooking(booking.getId()))
                .thenThrow(new ScreeningAlreadyStartedException(screening.getStartAt()));

        assertThrows(ScreeningAlreadyStartedException.class, () -> bookingController.cancelBooking(booking.getId()));

        verify(bookingService).cancelBooking(booking.getId());
    }

    private List<Booking> createListBookings() {
        return List.of(
                new Booking(
                        1L,
                        1L,
                        1L,
                        List.of("A1", "A2", "A3"),
                        BigDecimal.valueOf(120.00),
                        LocalDateTime.of(2026, 11, 15, 10, 0),
                        null,
                        BookingStatus.ACTIVE
                ),
                new Booking(
                        2L,
                        2L,
                        1L,
                        List.of("B1", "B2"),
                        BigDecimal.valueOf(80.00),
                        LocalDateTime.of(2026, 11, 15, 11, 30),
                        null,
                        BookingStatus.ACTIVE
                )
        );
    }

    private Booking createBooking() {
        return new Booking(
                1L,
                1L,
                1L,
                List.of("A1", "A2", "A3"),
                BigDecimal.valueOf(120.00),
                LocalDateTime.of(2026, 11, 15, 10, 0),
                null,
                BookingStatus.ACTIVE
        );
    }

    private Screening createScreening() {
        return new Screening(
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 11, 25, 14, 0),
                LocalDateTime.of(2026, 11, 25, 16, 42),
                new BigDecimal("300.00"),
                true
        );
    }


    private Customer createCustomer() {
        return new Customer(1L, "Ivan", "Ivanov",
                "ivanov@gmail.com", "+48225476856", true);
    }

    private Hall createHall() {
        return new Hall(1L, "Hall №1", 10, 8, true);
    }

}
