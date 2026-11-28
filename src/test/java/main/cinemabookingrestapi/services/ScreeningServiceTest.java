package main.cinemabookingrestapi.services;

import main.cinemabookingrestapi.dto.screening.CreateScreeningRequest;
import main.cinemabookingrestapi.dto.screening.ScreeningFilterRequest;
import main.cinemabookingrestapi.dto.screening.ScreeningResponse;
import main.cinemabookingrestapi.entity.*;
import main.cinemabookingrestapi.exception.inactive.InactiveHallException;
import main.cinemabookingrestapi.exception.inactive.InactiveMovieException;
import main.cinemabookingrestapi.exception.isPresentException.ScreeningIsPresentInBookingException;
import main.cinemabookingrestapi.exception.notFound.HallNotFoundException;
import main.cinemabookingrestapi.exception.notFound.MovieNotFoundException;
import main.cinemabookingrestapi.exception.notFound.ScreeningNotFoundException;
import main.cinemabookingrestapi.exception.screeningException.ScreeningAlreadyStartedException;
import main.cinemabookingrestapi.exception.screeningException.ScreeningTimeException;
import main.cinemabookingrestapi.repository.BookingRepository;
import main.cinemabookingrestapi.repository.HallRepository;
import main.cinemabookingrestapi.repository.MovieRepository;
import main.cinemabookingrestapi.repository.ScreeningRepository;
import main.cinemabookingrestapi.service.ScreeningService;
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
public class ScreeningServiceTest {

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private HallRepository hallRepository;

    @Spy
    private Clock clock = Clock.fixed(Instant.parse("2026-11-25T10:00:00Z"), ZoneOffset.UTC);

    @InjectMocks
    private ScreeningService screeningService;

    @Test
    void getAll_shouldReturnEmptyList() {
        when(screeningRepository.getAll()).thenReturn(Collections.emptyList());

        List<Screening> screenings = screeningService.getAll();

        assertNotNull(screenings);
        assertTrue(screenings.isEmpty());

        verify(screeningRepository).getAll();
    }

    @Test
    void getAll_shouldReturnListOfScreenings() {
        List<Screening> screeningList = createList();

        when(screeningRepository.getAll()).thenReturn(screeningList);

        List<Screening> screenings = screeningService.getAll();

        assertNotNull(screenings);
        assertEquals(3, screenings.size());
        assertEquals(1L, screenings.get(0).getId());
        assertFalse(screenings.get(2).isActive());

        verify(screeningRepository).getAll();
    }

    @Test
    void getById_shouldReturnScreening() {
        Screening screening = createScreening();

        when(screeningRepository.getById(1L)).thenReturn(Optional.of(screening));

        Optional<Screening> result = screeningService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(screening.getMovieId(), result.get().getMovieId());

        verify(screeningRepository).getById(1L);
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> screeningService.getById(null));

        verifyNoInteractions(screeningRepository);
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIdIsLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> screeningService.getById(0L));

        verifyNoInteractions(screeningRepository);
    }

    @Test
    void getById_shouldThrowScreeningNotFoundException_whenNotFound() {
        when(screeningRepository.getById(99L)).thenReturn(Optional.empty());

        assertThrows(ScreeningNotFoundException.class, () -> screeningService.getById(99L));

        verify(screeningRepository).getById(99L);
    }

    @Test
    void addScreening_shouldReturnScreeningResponse() {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        CreateScreeningRequest request = new CreateScreeningRequest(1L, 1L,
                LocalDateTime.of(2026, 11, 26, 14, 0),
                new BigDecimal("300.00"));
        Movie movie = createMovie();
        Hall hall = createHall();

        when(movieRepository.getById(request.getMovieId())).thenReturn(Optional.of(movie));
        when(hallRepository.getById(request.getHallId())).thenReturn(Optional.of(hall));
        when(screeningRepository.getAll()).thenReturn(Collections.emptyList());
        when(screeningRepository.addScreening(any(Screening.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ScreeningResponse response = screeningService.addScreening(request);

        assertNotNull(response);
        assertTrue(movie.isActive());
        assertTrue(hall.isActive());

        verify(movieRepository).getById(anyLong());
        verify(hallRepository).getById(anyLong());
        verify(screeningRepository).getAll();

        ArgumentCaptor<Screening> screeningCaptor = ArgumentCaptor.forClass(Screening.class);

        verify(screeningRepository).addScreening(screeningCaptor.capture());
        Screening savedScreening = screeningCaptor.getValue();

        assertNotNull(savedScreening);
        assertEquals(savedScreening.getMovieId(), response.getMovieId());
        assertEquals(savedScreening.getHallId(), response.getHallId());
        assertEquals(savedScreening.getStartAt(), response.getStartTime());
    }

    @Test
    void addScreening_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> screeningService.addScreening(null));
        verifyNoInteractions(screeningRepository, movieRepository, hallRepository);
    }

    @Test
    void addScreening_shouldThrowMovieNotFoundException_whenMovieIdIsIncorrect() {
        CreateScreeningRequest request = new CreateScreeningRequest(12L, 1L,
                LocalDateTime.of(2026, 11, 26, 14, 0),
                new BigDecimal("300.00"));

        when(movieRepository.getById(request.getMovieId())).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> screeningService.addScreening(request));
    }

    @Test
    void addScreening_shouldThrowHallNotFoundException_whenHallIdIsIncorrect() {
        CreateScreeningRequest request = new CreateScreeningRequest(1L, 12L,
                LocalDateTime.of(2026, 11, 26, 14, 0),
                new BigDecimal("300.00"));
        Movie movie = createMovie();

        when(movieRepository.getById(request.getMovieId())).thenReturn(Optional.of(movie));
        when(hallRepository.getById(request.getHallId())).thenReturn(Optional.empty());

        assertThrows(HallNotFoundException.class, () -> screeningService.addScreening(request));
    }

    @Test
    void addScreening_shouldThrowInactiveMovieException_whenMovieIsInactive() {
        Movie movie = new Movie(
                1L,
                "Avatar",
                "Sci-Fi",
                162,
                12,
                BigDecimal.valueOf(300),
                false
        );
        CreateScreeningRequest request = new CreateScreeningRequest(1L, 12L,
                LocalDateTime.of(2026, 11, 26, 14, 0),
                new BigDecimal("300.00"));
        when(movieRepository.getById(request.getMovieId())).thenReturn(Optional.of(movie));

        assertThrows(InactiveMovieException.class, () -> screeningService.addScreening(request));

    }

    @Test
    void addScreening_shouldThrowInactiveHallException_whenHallIsInactive() {
        Movie movie = createMovie();
        Hall hall = new Hall(1L, "Red Hall", 8, 12, false);

        CreateScreeningRequest request = new CreateScreeningRequest(1L, 12L,
                LocalDateTime.of(2026, 11, 15, 14, 0),
                new BigDecimal("300.00"));
        when(movieRepository.getById(request.getMovieId())).thenReturn(Optional.of(movie));
        when(hallRepository.getById(request.getHallId())).thenReturn(Optional.of(hall));

        assertThrows(InactiveHallException.class, () -> screeningService.addScreening(request));
    }

    @Test
    void addScreening_shouldThrowScreeningAlreadyStartedException_whenScreeningHasAlreadyStarted() {
        CreateScreeningRequest request = new CreateScreeningRequest(1L, 1L,
                LocalDateTime.of(2026, 11, 15, 14, 0),
                new BigDecimal("300.00"));
        Movie movie = createMovie();
        Hall hall = createHall();

        when(movieRepository.getById(request.getMovieId())).thenReturn(Optional.of(movie));
        when(hallRepository.getById(request.getHallId())).thenReturn(Optional.of(hall));

        assertThrows(ScreeningAlreadyStartedException.class, () -> screeningService.addScreening(request));
    }

    @Test
    void addScreening_shouldThrowScreeningOverlappingException_whenScreeningOverlap() {
        CreateScreeningRequest request = new CreateScreeningRequest(1L, 1L,
                LocalDateTime.of(2026, 11, 27, 14, 0),
                new BigDecimal("300.00"));
        Movie movie = createMovie();
        Hall hall = createHall();
        List<Screening> screenings = new java.util.ArrayList<>(createList());

        screenings.add(new Screening(
                11L,
                11L,
                1L,
                LocalDateTime.of(2026, 11, 27, 14, 0),
                LocalDateTime.of(2026, 11, 27, 16, 42),
                new BigDecimal("300.00"),
                true));

        when(movieRepository.getById(request.getMovieId())).thenReturn(Optional.of(movie));
        when(hallRepository.getById(request.getHallId())).thenReturn(Optional.of(hall));
        when(screeningRepository.getAll()).thenReturn(screenings);

        assertThrows(ScreeningTimeException.class, () -> screeningService.addScreening(request));
    }

    @Test
    void getFilteredScreenings_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> screeningService.getFilteredScreenings(null));

        verifyNoInteractions(screeningRepository);
    }

    @Test
    void getFilteredScreenings_shouldReturnAllScreenings_whenFilterIsEmpty() {
        List<Screening> screenings = createList();
        ScreeningFilterRequest request = new ScreeningFilterRequest(null, null, null, null);

        when(screeningRepository.getAll()).thenReturn(screenings);

        List<ScreeningResponse> response = screeningService.getFilteredScreenings(request);

        assertNotNull(response);
        assertEquals(3, response.size());

        verify(screeningRepository).getAll();
    }

    @Test
    void getFilteredScreenings_shouldFilterByMovieId_whenMovieIdIsProvided() {
        List<Screening> screenings = createList();
        ScreeningFilterRequest request = new ScreeningFilterRequest(1L, null, null, null);

        when(screeningRepository.getAll()).thenReturn(screenings);

        List<ScreeningResponse> response = screeningService.getFilteredScreenings(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getMovieId());

        verify(screeningRepository).getAll();
    }

    @Test
    void getFilteredScreenings_shouldFilterByHallId_whenHallIdIsProvided() {
        List<Screening> screenings = createList();
        ScreeningFilterRequest request = new ScreeningFilterRequest(null, 2L, null, null);

        when(screeningRepository.getAll()).thenReturn(screenings);

        List<ScreeningResponse> response = screeningService.getFilteredScreenings(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(2L, response.get(0).getHallId());

        verify(screeningRepository).getAll();
    }

    @Test
    void getFilteredScreenings_shouldFilterByStartAt_whenStartAtIsProvided() {
        List<Screening> screenings = createList();
        LocalDateTime targetTime = LocalDateTime.of(2026, 11, 26, 14, 0);
        ScreeningFilterRequest request = new ScreeningFilterRequest(null, null, targetTime, null);

        when(screeningRepository.getAll()).thenReturn(screenings);

        List<ScreeningResponse> response = screeningService.getFilteredScreenings(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(targetTime, response.get(0).getStartTime());

        verify(screeningRepository).getAll();
    }

    @Test
    void getFilteredScreenings_shouldFilterByActive_whenActiveIsProvided() {
        List<Screening> screenings = createList();
        ScreeningFilterRequest request = new ScreeningFilterRequest(null, null, null, false);

        when(screeningRepository.getAll()).thenReturn(screenings);

        List<ScreeningResponse> response = screeningService.getFilteredScreenings(request);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(3L, response.get(0).getMovieId());

        verify(screeningRepository).getAll();
    }

    @Test
    void removeScreening_shouldRemoveScreening() {
        Screening screening = createScreening();

        when(screeningRepository.getById(1L)).thenReturn(Optional.of(screening));

        screeningService.removeScreening(1L);

        verify(screeningRepository).getById(1L);
        verify(screeningRepository).removeScreening(1L);
    }

    @Test
    void removeScreening_shouldThrowIllegalArgumentException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> screeningService.removeScreening(null));

        verifyNoInteractions(screeningRepository);
    }

    @Test
    void removeScreening_shouldThrowIllegalArgumentException_whenIdIsLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> screeningService.removeScreening(0L));

        verifyNoInteractions(screeningRepository);
    }

    @Test
    void removeScreening_shouldThrowScreeningNotFoundException_whenNotFound() {
        when(screeningRepository.getById(99L)).thenReturn(Optional.empty());

        assertThrows(ScreeningNotFoundException.class, () -> screeningService.removeScreening(99L));

        verify(screeningRepository).getById(99L);
        verify(screeningRepository, never()).removeScreening(anyLong());
    }

    @Test
    void removeScreening_shouldThrowScreeningIsPresentInBooking_whenGivenScreeningIsPresentInBooking() {
        Screening screening = createScreening();
        List<Booking> bookings = createListBookings();

        when(screeningRepository.getById(screening.getId())).thenReturn(Optional.of(screening));
        when(bookingRepository.getAll()).thenReturn(bookings);

        assertThrows(ScreeningIsPresentInBookingException.class,
                () -> screeningService.removeScreening(screening.getId()));

        verify(screeningRepository).getById(screening.getId());
        verify(screeningRepository, never()).removeScreening(anyLong());
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

    private Movie createMovie() {
        return new Movie(
                1L,
                "Avatar",
                "Sci-Fi",
                162,
                12,
                BigDecimal.valueOf(300),
                true
        );
    }

    private Hall createHall() {
        return new Hall(1L, "Red Hall", 8, 12, true);
    }

    private List<Screening> createList() {
        return List.of(
                new Screening(
                        1L,
                        1L,
                        1L,
                        LocalDateTime.of(2026, 11, 26, 14, 0),
                        LocalDateTime.of(2026, 11, 26, 16, 42),
                        new BigDecimal("300.00"),
                        true
                ),
                new Screening(
                        2L,
                        2L,
                        2L,
                        LocalDateTime.of(2026, 11, 16, 18, 0),
                        LocalDateTime.of(2026, 11, 16, 20, 32),
                        new BigDecimal("250.00"),
                        true
                ),
                new Screening(
                        3L,
                        3L,
                        3L,
                        LocalDateTime.of(2026, 11, 8, 12, 0),
                        LocalDateTime.of(2026, 11, 8, 14, 49),
                        new BigDecimal("280.00"),
                        false
                )
        );
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
}