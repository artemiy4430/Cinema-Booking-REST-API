package main.cinemabookingrestapi.service;

import main.cinemabookingrestapi.dto.screening.CreateScreeningRequest;
import main.cinemabookingrestapi.dto.screening.ScreeningFilterRequest;
import main.cinemabookingrestapi.dto.screening.ScreeningResponse;
import main.cinemabookingrestapi.entity.Hall;
import main.cinemabookingrestapi.entity.Movie;
import main.cinemabookingrestapi.entity.Screening;
import main.cinemabookingrestapi.exception.inactive.InactiveHallException;
import main.cinemabookingrestapi.exception.inactive.InactiveMovieException;
import main.cinemabookingrestapi.exception.isPresentException.ScreeningIsPresentInBookingException;
import main.cinemabookingrestapi.exception.notFound.HallNotFoundException;
import main.cinemabookingrestapi.exception.notFound.MovieNotFoundException;
import main.cinemabookingrestapi.exception.notFound.ScreeningNotFoundException;
import main.cinemabookingrestapi.exception.screeningException.ScreeningAlreadyStartedException;
import main.cinemabookingrestapi.exception.screeningException.ScreeningOverlapException;
import main.cinemabookingrestapi.repository.BookingRepository;
import main.cinemabookingrestapi.repository.HallRepository;
import main.cinemabookingrestapi.repository.MovieRepository;
import main.cinemabookingrestapi.repository.ScreeningRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ScreeningService {

    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;
    private final Clock clock;

    public ScreeningService(ScreeningRepository screeningRepository, BookingRepository bookingRepository, MovieRepository movieRepository,
                            HallRepository hallRepository, Clock clock) {
        this.screeningRepository = screeningRepository;
        this.bookingRepository = bookingRepository;
        this.movieRepository = movieRepository;
        this.hallRepository = hallRepository;
        this.clock = clock;
    }

    public List<Screening> getAll() {
        return screeningRepository.getAll();
    }

    public Optional<Screening> getById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException();

        Screening screening = screeningRepository
                .getById(id).orElseThrow(() -> new ScreeningNotFoundException(id));

        return Optional.of(screening);
    }

    public synchronized ScreeningResponse addScreening(CreateScreeningRequest request) {
        if (request == null || request.getHallId() == null
                || request.getMovieId() == null || request.getStartAt() == null) throw new IllegalArgumentException();

        Movie movie = movieRepository
                .getById(request.getMovieId()).orElseThrow(() -> new MovieNotFoundException(request.getMovieId()));

        if (!movie.isActive()) throw new InactiveMovieException("given movie is inactive");

        Hall hall = hallRepository.getById(request.getHallId())
                .orElseThrow(() -> new HallNotFoundException(request.getHallId()));

        if (!hall.isActive()) throw new InactiveHallException("given hall is inactive");

        if (request.getStartAt().isBefore(LocalDateTime.now(clock)))
            throw new ScreeningAlreadyStartedException(request.getStartAt());

        LocalDateTime endAt = request.getStartAt().plusMinutes(movie.getDurationMinutes());
        List<Screening> screenings = screeningRepository.getAll();

        boolean isScreeningTimeOverlapping = screenings.stream()
                .filter(Screening::isActive)
                .filter(s -> Objects.equals(s.getHallId(), request.getHallId()))
                .anyMatch(s -> s.getEndAt().isAfter(request.getStartAt()) && s.getStartAt().isBefore(endAt));

        if (isScreeningTimeOverlapping) throw new ScreeningOverlapException(request.getStartAt());

        Screening screening = request.toEntity();

        screening.setEndAt(endAt);
        screening.setActive(true);

        Screening savedScreening = screeningRepository.addScreening(screening);

        return ScreeningResponse.fromEntity(savedScreening);
    }

    public List<ScreeningResponse> getFilteredScreenings(ScreeningFilterRequest request) {
        if (request == null) throw new IllegalArgumentException();
        List<Screening> screenings = getAll();

        if (request.getHallId().isPresent() || request.getMovieId().isPresent()
                || request.getIsActive().isPresent() || request.getStartAt().isPresent()) {
            screenings = screenings.stream().filter(s -> request.getMovieId().isEmpty()
                    || Objects.equals(s.getMovieId(), request.getMovieId().get()))
                    .filter(s -> request.getHallId().isEmpty()
                            || Objects.equals(s.getHallId(), request.getHallId().get()))
                    .filter(s -> request.getStartAt().isEmpty()
                            || s.getStartAt().isEqual(request.getStartAt().get()))
                    .filter(s -> request.getIsActive().isEmpty()
                            || s.isActive() == request.getIsActive().get()).toList();
        }

        return screenings.stream().map(ScreeningResponse::fromEntity).toList();
    }

    public void removeScreening(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException();

        Screening screening = screeningRepository.getById(id)
                .orElseThrow(() -> new ScreeningNotFoundException(id));

        boolean isScreeningPresentInBooking = bookingRepository.getAll()
                .stream().anyMatch(b -> Objects.equals(b.getScreeningId(), id));

        if (isScreeningPresentInBooking) throw new ScreeningIsPresentInBookingException(id);

        screeningRepository.removeScreening(id);
    }
}
