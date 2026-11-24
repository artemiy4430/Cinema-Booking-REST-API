package main.cinemabookingrestapi.services;

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
import main.cinemabookingrestapi.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private HallRepository hallRepository;

    @Spy // создаёт «шпион»-обёртку над реальным объектом
    private Clock clock = Clock.fixed(Instant.parse("2026-11-15T10:00:00Z"), ZoneOffset.UTC);

    @InjectMocks
    private BookingService bookingService;

    @Test
    void getAll_shouldReturnListOfBookings() {
        List<Booking> bookings = createListBookings();
        when(bookingRepository.getAll()).thenReturn(bookings);

        List<Booking> newBookings = bookingService.getAll();

        assertNotNull(newBookings);
        assertEquals(2, newBookings.size());
        assertEquals(bookings, newBookings);

        verify(bookingRepository).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList() {
        when(bookingRepository.getAll()).thenReturn(Collections.emptyList());

        List<Booking> bookings = bookingService.getAll();

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());

        verify(bookingRepository).getAll();
    }

    @Test
    void getById_shouldReturnOptionalOfBooking() {
        Booking booking = createBooking();

        when(bookingRepository.getById(booking.getId())).thenReturn(Optional.of(booking));

        Optional<Booking> bookingOptional = bookingService.getById(booking.getId());

        assertNotNull(bookingOptional);
        assertTrue(bookingOptional.isPresent());
        assertEquals(booking.getId(), bookingOptional.get().getId());

        verify(bookingRepository).getById(booking.getId());
    }

    @Test
    void getById_shouldThrowBookingNotFoundException_whenBookingIsNotFound() {
        Long incorrectId = 12L;

        when(bookingRepository.getById(incorrectId)).thenThrow(new BookingNotFoundException(incorrectId));

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.getById(incorrectId);
        });

        verify(bookingRepository).getById(anyLong());
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getById(null);
        });

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIsLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getById(0L);
        });

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getByCustomerId_shouldReturnOptionalOfBooking() {
        Booking booking = createBooking();

        when(bookingRepository.getByCustomerID(booking.getCustomerId())).thenReturn(Optional.of(booking));

        Optional<Booking> bookingOptional = bookingService.getByCustomerId(booking.getCustomerId());

        assertNotNull(bookingOptional);
        assertTrue(bookingOptional.isPresent());
        assertEquals(booking.getCustomerId(), bookingOptional.get().getCustomerId());

        verify(bookingRepository).getByCustomerID(booking.getCustomerId());
    }

    @Test
    void getCustomerById_shouldThrowBookingNotFoundException_whenBookingIsNotFound() {
        Long incorrectCustomerId = 12L;

        when(bookingRepository.getByCustomerID(incorrectCustomerId)).thenThrow(new BookingNotFoundException(incorrectCustomerId));

        assertThrows(BookingNotFoundException.class, () -> {
            bookingService.getByCustomerId(incorrectCustomerId);
        });

        verify(bookingRepository).getByCustomerID(anyLong());
    }

    @Test
    void getCustomerById_shouldThrowIllegalArgumentException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getByCustomerId(null);
        });

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void getCustomerById_shouldThrowIllegalArgumentException_whenIdIsLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getByCustomerId(0L);
        });

        verifyNoInteractions(bookingRepository);
    }

    @Test
    void addBooking_shouldReturnBookingResponse() {

        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        CreateBookingRequest request = new CreateBookingRequest(1L, 1L, List.of("A1", "A2", "A3"));
        Customer customer = createCustomer();
        Screening screening = createScreening();
        Hall hall = createHall();

        when(customerRepository.getById(request.getCustomerId())).thenReturn(Optional.of(customer));
        when(screeningRepository.getById(request.getScreeningId())).thenReturn(Optional.of(screening));
        when(hallRepository.getById(screening.getHallId())).thenReturn(Optional.of(hall));
        when(bookingRepository.getAll()).thenReturn(Collections.emptyList());
        when(bookingRepository.addBooking(any(Booking.class))).thenAnswer(invocation ->
                invocation.getArgument(0)); // мок вернёт ровно тот Booking, который сервис сам собрал внутри addBooking

        CreateBookingResponse response = bookingService.addBooking(request);

        assertNotNull(response);
        assertTrue(customer.isActive());

        verify(customerRepository).getById(anyLong());
        verify(screeningRepository).getById(anyLong());
        verify(hallRepository).getById(anyLong());
        verify(bookingRepository).getAll();

        // вытащить изолираванный обьект из bookingRepository.addBooking(booking);
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        verify(bookingRepository).addBooking(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertNotNull(savedBooking);
        assertEquals(savedBooking.getStatus(), response.getStatus());
        assertEquals(savedBooking.getCreateAt(), response.getCreateAt());
        assertEquals(savedBooking.getTotalPrice(), response.getTotalPrice());
    }


    @Test
    void addBooking_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.addBooking(null));
        verifyNoInteractions(customerRepository, screeningRepository, hallRepository, bookingRepository);
    }

    @Test
    void addBooking_shouldThrowCustomerNotFoundException_whenCustomerNotFound() {
        CreateBookingRequest request = createRequest(List.of("A1"));
        when(customerRepository.getById(request.getCustomerId())).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> bookingService.addBooking(request));
    }

    @Test
    void addBooking_shouldThrowInactiveCustomerException_whenCustomerIsInactive() {
        Customer customer = new Customer(
                1L,
                "Emre",
                "Can",
                "emrecan@gmail.com",
                "+59637993399",
                false
        );
        Screening screening = createScreening();
        CreateBookingRequest request = new CreateBookingRequest(customer.getId(), screening.getId(), List.of("A1"));
        when(customerRepository.getById(request.getCustomerId())).thenReturn(Optional.of(customer));
        when(screeningRepository.getById(request.getScreeningId())).thenReturn(Optional.of(screening));


        assertThrows(InactiveCustomerException.class, () -> bookingService.addBooking(request));
    }

    @Test
    void addBooking_shouldThrowScreeningNotFoundException_whenScreeningNotFound() {
        CreateBookingRequest request = createRequest(List.of("A1"));
        when(customerRepository.getById(request.getCustomerId())).thenReturn(Optional.of(createCustomer()));
        when(screeningRepository.getById(request.getScreeningId())).thenReturn(Optional.empty());

        assertThrows(ScreeningNotFoundException.class, () -> bookingService.addBooking(request));
    }

    @Test
    void addBooking_shouldThrowInactiveScreeningException_whenScreeningIsInactive() {
        Screening screening = new Screening(
                1L,
                1L,
                1L,
                LocalDateTime.now(clock).plusHours(2),
                LocalDateTime.now(clock).plusHours(4),
                BigDecimal.valueOf(100.00), false
        );
        Customer customer = createCustomer();
        CreateBookingRequest request = new CreateBookingRequest(customer.getId(), screening.getId(), List.of("A1"));
        when(customerRepository.getById(request.getCustomerId())).thenReturn(Optional.of(customer));
        when(screeningRepository.getById(request.getScreeningId())).thenReturn(Optional.of(screening));

        assertThrows(InactiveScreeningException.class, () -> bookingService.addBooking(request));
    }

    @Test
    void addBooking_shouldThrowScreeningAlreadyStartedException_whenScreeningInPast() {
        Screening screening = new Screening(
                1L,
                1L,
                1L,
                LocalDateTime.now(clock).minusHours(2),
                LocalDateTime.now(clock).plusHours(4),
                BigDecimal.valueOf(100.00), true
        );
        Customer customer = createCustomer();
        CreateBookingRequest request = new CreateBookingRequest(customer.getId(), screening.getId(), List.of("A1"));

        when(customerRepository.getById(request.getCustomerId())).thenReturn(Optional.of(customer));
        when(screeningRepository.getById(request.getScreeningId())).thenReturn(Optional.of(screening));

        assertThrows(ScreeningAlreadyStartedException.class, () -> bookingService.addBooking(request));
    }

    @Test
    void addBooking_shouldThrowHallNotFoundException_whenHallNotFound() {
        CreateBookingRequest request = createRequest(List.of("A1"));
        Screening screening = createScreening();

        when(customerRepository.getById(request.getCustomerId())).thenReturn(Optional.of(createCustomer()));
        when(screeningRepository.getById(request.getScreeningId())).thenReturn(Optional.of(screening));
        when(hallRepository.getById(screening.getHallId())).thenReturn(Optional.empty());

        assertThrows(HallNotFoundException.class, () -> bookingService.addBooking(request));
    }

    @Test
    void addBooking_shouldThrowInvalidSeatException_whenSeatDoesNotExist() {
        CreateBookingRequest request = createRequest(List.of("Z99"));
        mockValidPrerequisitesForSeatTests(request);

        assertThrows(InvalidSeatException.class, () -> bookingService.addBooking(request));
    }

    @Test
    void addBooking_shouldThrowDuplicateSeatException_whenSeatsAreDuplicated() {
        CreateBookingRequest request = createRequest(List.of("A1", "A1"));
        mockValidPrerequisitesForSeatTests(request);

        assertThrows(DuplicateSeatException.class, () -> bookingService.addBooking(request));
    }

    @Test
    void addBooking_shouldThrowSeatAlreadyBookedException_whenSeatIsOccupied() {
        CreateBookingRequest request = createRequest(List.of("A1"));
        mockValidPrerequisitesForSeatTests(request);

        Booking existingBooking = new Booking(99L, 2L, 1L, List.of("A1", "A2"),
                BigDecimal.valueOf(200.0), LocalDateTime.now(clock), null, BookingStatus.ACTIVE);
        when(bookingRepository.getAll()).thenReturn(List.of(existingBooking));

        assertThrows(SeatAlreadyBookedException.class, () -> bookingService.addBooking(request));
    }

    @Test
    void cancelBooking_shouldReturnBookingResponse() {
        Booking booking = createBooking();
        Screening screening = createScreening();

        when(bookingRepository.getById(booking.getId())).thenReturn(Optional.of(booking));
        when(screeningRepository.getById(screening.getId())).thenReturn(Optional.of(screening));
        when(bookingRepository.cancelBooking(eq(booking.getId()), any(Booking.class)))
                .thenAnswer(invocation -> Optional.of(invocation.getArgument(1)));

        CancelBookingResponse response = bookingService.cancelBooking(booking.getId());

        assertNotNull(response);
        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        assertNotNull(response.getCancelledAt());

        verify(bookingRepository).getById(booking.getId());
        verify(screeningRepository).getById(booking.getScreeningId());
        verify(bookingRepository).cancelBooking(eq(booking.getId()), any(Booking.class));
    }


    @Test
    void cancelBooking_shouldThrowBookingNotFoundException_whenBookingNotFound() {
        when(bookingRepository.getById(99L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.cancelBooking(99L));

        verify(bookingRepository).getById(99L);
        verifyNoInteractions(screeningRepository);
        verify(bookingRepository, never()).cancelBooking(anyLong(), any(Booking.class));
    }

    @Test
    void cancelBooking_shouldThrowBookingAlreadyCancelledException_whenBookingIsAlreadyCancelled() {
        Booking booking = createBooking();
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.getById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(BookingAlreadyCancelledException.class, () -> bookingService.cancelBooking(booking.getId()));

        verify(bookingRepository).getById(booking.getId());
        verifyNoInteractions(screeningRepository);
        verify(bookingRepository, never()).cancelBooking(anyLong(), any(Booking.class));
    }

    @Test
    void cancelBooking_shouldThrowScreeningNotFoundException_whenScreeningNotFound() {
        Booking booking = createBooking();

        when(bookingRepository.getById(booking.getId())).thenReturn(Optional.of(booking));
        when(screeningRepository.getById(booking.getScreeningId())).thenReturn(Optional.empty());

        assertThrows(ScreeningNotFoundException.class, () -> bookingService.cancelBooking(booking.getId()));

        verify(bookingRepository).getById(booking.getId());
        verify(screeningRepository).getById(booking.getScreeningId());
        verify(bookingRepository, never()).cancelBooking(anyLong(), any(Booking.class));
    }

    @Test
    void cancelBooking_shouldThrowScreeningAlreadyStartedException_whenScreeningAlreadyStarted() {
        Booking booking = createBooking();
        Screening screening = createScreening();
        screening.setStartAt(LocalDateTime.now(clock).minusMinutes(10));

        when(bookingRepository.getById(booking.getId())).thenReturn(Optional.of(booking));
        when(screeningRepository.getById(booking.getScreeningId())).thenReturn(Optional.of(screening));

        assertThrows(ScreeningAlreadyStartedException.class, () -> bookingService.cancelBooking(booking.getId()));

        verify(bookingRepository).getById(booking.getId());
        verify(screeningRepository).getById(booking.getScreeningId());
        verify(bookingRepository, never()).cancelBooking(anyLong(), any(Booking.class));
    }

    @Test
    void cancelBooking_shouldThrowBookingNotFoundException_whenRepositoryReturnsEmpty() {
        Booking booking = createBooking();
        Screening screening = createScreening();

        when(bookingRepository.getById(booking.getId())).thenReturn(Optional.of(booking));
        when(screeningRepository.getById(booking.getScreeningId())).thenReturn(Optional.of(screening));
        when(bookingRepository.cancelBooking(eq(booking.getId()), any(Booking.class))).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.cancelBooking(booking.getId()));

        verify(bookingRepository).getById(booking.getId());
        verify(screeningRepository).getById(booking.getScreeningId());
        verify(bookingRepository).cancelBooking(eq(booking.getId()), any(Booking.class));
    }

    private void mockValidPrerequisitesForSeatTests(CreateBookingRequest request) {
        Screening screening = createScreening();
        when(customerRepository.getById(request.getCustomerId())).thenReturn(Optional.of(createCustomer()));
        when(screeningRepository.getById(request.getScreeningId())).thenReturn(Optional.of(screening));
        when(hallRepository.getById(screening.getHallId())).thenReturn(Optional.of(createHall()));
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


    private Customer createCustomer() {
        return new Customer(
                1L,
                "Emre",
                "Can",
                "emrecan@gmail.com",
                "+59637993399",
                true
        );
    }

    private Screening createScreening() {
        return new Screening(
                1L,
                1L,
                1L,
                LocalDateTime.now(clock).plusHours(2),
                LocalDateTime.now(clock).plusHours(4),
                BigDecimal.valueOf(100.00), true);
    }

    private Hall createHall() {
        return new Hall(1L, "Hall №1", 10, 8, true);
    }

    private CreateBookingRequest createRequest(List<String> seats) {
        return new CreateBookingRequest(1L, 1L, seats);
    }

}
