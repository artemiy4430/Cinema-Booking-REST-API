package main.cinemabookingrestapi.controllers;

import main.cinemabookingrestapi.controller.ScreeningController;
import main.cinemabookingrestapi.dto.screening.CreateScreeningRequest;
import main.cinemabookingrestapi.dto.screening.ScreeningFilterRequest;
import main.cinemabookingrestapi.dto.screening.ScreeningResponse;
import main.cinemabookingrestapi.entity.Screening;
import main.cinemabookingrestapi.exception.inactive.InactiveHallException;
import main.cinemabookingrestapi.exception.inactive.InactiveMovieException;
import main.cinemabookingrestapi.exception.notFound.HallNotFoundException;
import main.cinemabookingrestapi.exception.notFound.MovieNotFoundException;
import main.cinemabookingrestapi.exception.notFound.ScreeningNotFoundException;
import main.cinemabookingrestapi.exception.screeningException.ScreeningAlreadyStartedException;
import main.cinemabookingrestapi.exception.screeningException.ScreeningOverlapException;
import main.cinemabookingrestapi.service.ScreeningService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScreeningControllerTest {

    @Mock
    private ScreeningService screeningService;

    @InjectMocks
    private ScreeningController screeningController;

    @Test
    void getMovies_shouldReturnOkAndAllScreenings_whenFiltersAreNull() {
        ScreeningFilterRequest expectedFilter = new ScreeningFilterRequest(null, null, null, null);
        List<ScreeningResponse> expectedResponses = createScreeningResponseList();

        when(screeningService.getFilteredScreenings(expectedFilter)).thenReturn(expectedResponses);

        ResponseEntity<List<ScreeningResponse>> response =
                screeningController.getMovies(null, null, null, null);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expectedResponses);
        assertEquals(2, response.getBody().size());

        verify(screeningService).getFilteredScreenings(expectedFilter);
    }

    @Test
    void getMovies_shouldReturnOkAndFilteredScreenings_whenFiltersAreProvided() {
        LocalDateTime startAt = LocalDateTime.of(2026, 11, 26, 14, 0);
        ScreeningFilterRequest expectedFilter = new ScreeningFilterRequest(1L, 1L, startAt, true);
        List<ScreeningResponse> expectedResponses = List.of(
                new ScreeningResponse(1L, 1L, startAt, startAt.plusMinutes(162), new BigDecimal("300.00"))
        );

        when(screeningService.getFilteredScreenings(expectedFilter)).thenReturn(expectedResponses);

        ResponseEntity<List<ScreeningResponse>> response =
                screeningController.getMovies(1L, 1L, startAt, true);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expectedResponses);
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getMovieId());

        verify(screeningService).getFilteredScreenings(expectedFilter);
    }

    @Test
    void getMovies_shouldReturnOkAndEmptyList_whenNoScreeningsMatch() {
        ScreeningFilterRequest expectedFilter = new ScreeningFilterRequest(99L, null, null, null);

        when(screeningService.getFilteredScreenings(expectedFilter)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ScreeningResponse>> response =
                screeningController.getMovies(99L, null, null, null);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(screeningService).getFilteredScreenings(expectedFilter);
    }

    @Test
    void getById_shouldReturnOk_whenScreeningExists() {
        Screening screening = createScreening();

        when(screeningService.getById(screening.getId())).thenReturn(Optional.of(screening));

        ResponseEntity<Screening> response = screeningController.getById(screening.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(screening);

        verify(screeningService).getById(screening.getId());
    }

    @Test
    void getById_shouldReturnStatusNotFound_whenScreeningDoesNotExist() {
        Long incorrectId = 99L;

        when(screeningService.getById(incorrectId)).thenReturn(Optional.empty());

        ResponseEntity<Screening> response = screeningController.getById(incorrectId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(screeningService).getById(incorrectId);
    }

    @Test
    void getById_shouldThrowScreeningNotFoundException_whenScreeningDoesNotExist() {
        Long incorrectId = 99L;

        when(screeningService.getById(incorrectId)).thenThrow(new ScreeningNotFoundException(incorrectId));

        assertThrows(ScreeningNotFoundException.class, () -> screeningController.getById(incorrectId));

        verify(screeningService).getById(incorrectId);
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIdIsIncorrect() {
        Long incorrectId = -1L;

        when(screeningService.getById(incorrectId)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> screeningController.getById(incorrectId));

        verify(screeningService).getById(incorrectId);
    }

    @Test
    void addScreening_shouldReturnStatusCreatedAndScreeningResponse_whenRequestIsValid() {
        LocalDateTime startAt = LocalDateTime.of(2026, 11, 26, 14, 0);
        CreateScreeningRequest request = new CreateScreeningRequest(1L, 1L, startAt, new BigDecimal("300.00"));
        ScreeningResponse expectedResponse = new ScreeningResponse(
                1L, 1L, startAt, startAt.plusMinutes(162), new BigDecimal("300.00")
        );

        when(screeningService.addScreening(request)).thenReturn(expectedResponse);

        ResponseEntity<ScreeningResponse> response = screeningController.addScreening(request);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(expectedResponse);

        verify(screeningService).addScreening(request);
    }

    @Test
    void addScreening_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        when(screeningService.addScreening(null)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> screeningController.addScreening(null));

        verify(screeningService).addScreening(null);
    }

    @Test
    void addScreening_shouldThrowMovieNotFoundException_whenMovieDoesNotExist() {
        CreateScreeningRequest request = createScreeningRequest();

        when(screeningService.addScreening(request)).thenThrow(new MovieNotFoundException(request.getMovieId()));

        assertThrows(MovieNotFoundException.class, () -> screeningController.addScreening(request));

        verify(screeningService).addScreening(request);
    }

    @Test
    void addScreening_shouldThrowInactiveMovieException_whenMovieIsInactive() {
        CreateScreeningRequest request = createScreeningRequest();

        when(screeningService.addScreening(request)).thenThrow(new InactiveMovieException("given movie is inactive"));

        assertThrows(InactiveMovieException.class, () -> screeningController.addScreening(request));

        verify(screeningService).addScreening(request);
    }

    @Test
    void addScreening_shouldThrowHallNotFoundException_whenHallDoesNotExist() {
        CreateScreeningRequest request = createScreeningRequest();

        when(screeningService.addScreening(request)).thenThrow(new HallNotFoundException(request.getHallId()));

        assertThrows(HallNotFoundException.class, () -> screeningController.addScreening(request));

        verify(screeningService).addScreening(request);
    }

    @Test
    void addScreening_shouldThrowInactiveHallException_whenHallIsInactive() {
        CreateScreeningRequest request = createScreeningRequest();

        when(screeningService.addScreening(request)).thenThrow(new InactiveHallException("given hall is inactive"));

        assertThrows(InactiveHallException.class, () -> screeningController.addScreening(request));

        verify(screeningService).addScreening(request);
    }

    @Test
    void addScreening_shouldThrowScreeningAlreadyStartedException_whenStartTimeIsInPast() {
        CreateScreeningRequest request = createScreeningRequest();

        when(screeningService.addScreening(request))
                .thenThrow(new ScreeningAlreadyStartedException(request.getStartAt()));

        assertThrows(ScreeningAlreadyStartedException.class, () -> screeningController.addScreening(request));

        verify(screeningService).addScreening(request);
    }

    @Test
    void addScreening_shouldThrowScreeningOverlapException_whenScreeningOverlaps() {
        CreateScreeningRequest request = createScreeningRequest();

        when(screeningService.addScreening(request))
                .thenThrow(new ScreeningOverlapException(request.getStartAt()));

        assertThrows(ScreeningOverlapException.class, () -> screeningController.addScreening(request));

        verify(screeningService).addScreening(request);
    }

    @Test
    void removeScreening_shouldReturnNoContent_whenScreeningExists() {
        Long screeningId = 1L;

        ResponseEntity<Void> response = screeningController.removeScreening(screeningId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(screeningService).removeScreening(screeningId);
    }

    @Test
    void removeScreening_shouldThrowIllegalArgumentException_whenIdIsIncorrect() {
        Long incorrectId = -1L;

        doThrow(new IllegalArgumentException()).when(screeningService).removeScreening(incorrectId);

        assertThrows(IllegalArgumentException.class, () -> screeningController.removeScreening(incorrectId));

        verify(screeningService).removeScreening(incorrectId);
    }

    @Test
    void removeScreening_shouldThrowScreeningNotFoundException_whenScreeningDoesNotExist() {
        Long incorrectId = 99L;

        doThrow(new ScreeningNotFoundException(incorrectId)).when(screeningService).removeScreening(incorrectId);

        assertThrows(ScreeningNotFoundException.class, () -> screeningController.removeScreening(incorrectId));

        verify(screeningService).removeScreening(incorrectId);
    }

    private CreateScreeningRequest createScreeningRequest() {
        return new CreateScreeningRequest(
                1L,
                1L,
                LocalDateTime.of(2026, 11, 26, 14, 0),
                new BigDecimal("300.00")
        );
    }

    private Screening createScreening() {
        return new Screening(
                1L,
                1L,
                1L,
                LocalDateTime.of(2026, 11, 26, 14, 0),
                LocalDateTime.of(2026, 11, 26, 16, 42),
                new BigDecimal("300.00"),
                true
        );
    }

    private List<ScreeningResponse> createScreeningResponseList() {
        return List.of(
                new ScreeningResponse(
                        1L, 1L,
                        LocalDateTime.of(2026, 11, 26, 14, 0),
                        LocalDateTime.of(2026, 11, 26, 16, 42),
                        new BigDecimal("300.00")
                ),
                new ScreeningResponse(
                        2L, 2L,
                        LocalDateTime.of(2026, 11, 26, 18, 0),
                        LocalDateTime.of(2026, 11, 26, 20, 32),
                        new BigDecimal("250.00")
                )
        );
    }
}