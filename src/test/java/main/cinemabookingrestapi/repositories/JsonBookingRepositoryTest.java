package main.cinemabookingrestapi.repositories;

import main.cinemabookingrestapi.entity.Booking;
import main.cinemabookingrestapi.entity.BookingStatus;
import main.cinemabookingrestapi.repository.json.JsonBookingRepository;
import main.cinemabookingrestapi.repository.json.JsonMaxIdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class JsonBookingRepositoryTest {

    @TempDir
    Path tempDir;

    private JsonBookingRepository repository;
    private JsonMaxIdRepository maxIdRepository;

    @BeforeEach
    void setUp() {
        JsonMapper jsonMapper = new JsonMapper();

        Path maxIdsFile = tempDir.resolve("temp_max_ids.json");
        maxIdRepository = new JsonMaxIdRepository(jsonMapper, maxIdsFile.toString());

        Path file = tempDir.resolve("temp_bookings.json");
        repository = new JsonBookingRepository(jsonMapper, file.toString(), maxIdRepository);
    }

    @Test
    void getAll_shouldReturnEmptyList_whenFileDoesNotExist() {
        assertThat(repository.getAll()).isEmpty();
    }

    @Test
    void getAll_shouldReturnListOfBookings() {
        Booking booking1 = createBooking();
        Booking booking2 = new Booking(
                2L,
                2L,
                1L,
                List.of("B1", "B2"),
                new BigDecimal("600.00"),
                LocalDateTime.of(2026, 11, 10, 13, 0),
                null,
                BookingStatus.ACTIVE
        );

        repository.addBooking(booking1);
        repository.addBooking(booking2);

        List<Booking> bookings = repository.getAll();

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(2L, bookings.get(1).getId());
    }

    @Test
    void getById_shouldReturnEmptyOptional_whenBookingIsNotFound() {
        Long incorrectId = 12L;

        Optional<Booking> booking = repository.getById(incorrectId);

        assertNotNull(booking);
        assertTrue(booking.isEmpty());
    }

    @Test
    void getById_shouldReturnOptionalOfBooking() {
        Booking booking = createBooking();
        repository.addBooking(booking);

        Optional<Booking> bookingOptional = repository.getById(booking.getId());

        assertNotNull(bookingOptional);
        assertTrue(bookingOptional.isPresent());
        assertEquals(booking.getId(), bookingOptional.get().getId());
    }

    @Test
    void getByCustomerId_shouldReturnOptionalOfBooking() {
        Booking booking = createBooking();
        repository.addBooking(booking);

        Optional<Booking> bookingOptional = repository.getByCustomerID(booking.getCustomerId());

        assertNotNull(bookingOptional);
        assertTrue(bookingOptional.isPresent());
        assertEquals(booking.getCustomerId(), bookingOptional.get().getCustomerId());
    }

    @Test
    void getByCustomerId_shouldReturnEmptyOptional_whenBookingIsNotFound() {
        Long incorrectCustomerId = 12L;

        Optional<Booking> bookingOptional = repository.getByCustomerID(incorrectCustomerId);

        assertNotNull(bookingOptional);
        assertTrue(bookingOptional.isEmpty());
    }

    @Test
    void addBooking_shouldGenerateIdAndAddEntityInFile() {
        Booking booking = createBooking();
        booking.setId(null);

        Booking addedBooking = repository.addBooking(booking);

        assertNotNull(addedBooking.getId());
        assertEquals(booking.getCustomerId(), addedBooking.getCustomerId());
        assertEquals(1, repository.getAll().size());

        Booking loadedBooking = repository.getById(addedBooking.getId()).orElseThrow();
        assertThat(loadedBooking).usingRecursiveComparison().isEqualTo(addedBooking);
    }


    @Test
    void cancelBooking_shouldReturnEmptyOptional_whenBookingIsNotFound() {
        Long incorrectId = 12L;
        Booking booking = createBooking();

        Optional<Booking> bookingOptional = repository.cancelBooking(incorrectId, booking);

        assertNotNull(bookingOptional);
        assertTrue(bookingOptional.isEmpty());
    }

    @Test
    void cancelBooking_shouldReturnEmptyOptional_whenIdIsZero() {
        Long incorrectId = 0L;
        Booking booking = createBooking();

        Optional<Booking> bookingOptional = repository.cancelBooking(incorrectId, booking);

        assertNotNull(bookingOptional);
        assertTrue(bookingOptional.isEmpty());
    }

    @Test
    void cancelBooking_shouldReturnOptionalOfBooking() {
        Booking booking = createBooking();
        repository.addBooking(booking);
        Booking updatedBooking = new Booking(
                1L,
                1L,
                1L,
                List.of("A1", "A2", "A3", "A4", "A5"),
                new BigDecimal("900.00"),
                LocalDateTime.of(2026, 11, 10, 12, 30),
                null,
                BookingStatus.ACTIVE
        );

        Optional<Booking> bookingOptional = repository.cancelBooking(booking.getId(), updatedBooking);

        assertNotNull(bookingOptional);
        assertTrue(bookingOptional.isPresent());
        assertEquals(booking.getId(), bookingOptional.get().getId());
        assertNotEquals(booking.getSeats(), bookingOptional.get().getSeats());
    }

    private Booking createBooking() {
        return new Booking(
                1L,
                1L,
                1L,
                List.of("A1", "A2", "A3"),
                new BigDecimal("900.00"),
                LocalDateTime.of(2026, 11, 10, 12, 30),
                null,
                BookingStatus.ACTIVE
        );
    }
}