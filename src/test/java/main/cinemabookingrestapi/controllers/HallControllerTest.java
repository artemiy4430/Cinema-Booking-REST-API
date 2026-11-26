package main.cinemabookingrestapi.controllers;

import main.cinemabookingrestapi.controller.HallController;
import main.cinemabookingrestapi.dto.hall.CreateHallRequest;
import main.cinemabookingrestapi.dto.hall.HallResponse;
import main.cinemabookingrestapi.dto.hall.UpdateHallRequest;
import main.cinemabookingrestapi.entity.Hall;
import main.cinemabookingrestapi.exception.alreadyExist.HallNameAlreadyExistsException;
import main.cinemabookingrestapi.exception.notFound.HallNotFoundException;
import main.cinemabookingrestapi.service.HallService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HallControllerTest {

    @Mock
    private HallService hallService;

    @InjectMocks
    private HallController hallController;

    @Test
    void getAll_shouldReturnAllHalls() {
        when(hallService.getAll()).thenReturn(createListHalls());

        List<Hall> result = hallController.getAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());

        verify(hallService).getAll();
    }

    @Test
    void getAll_shouldReturnEmptyList_whenServiceReturnsEmptyList() {
        when(hallService.getAll()).thenReturn(Collections.emptyList());

        List<Hall> result = hallController.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hallService).getAll();
    }

    @Test
    void getById_shouldReturnOk_whenHallExists() {
        Hall hall = createHall();

        when(hallService.getById(hall.getId())).thenReturn(Optional.of(hall));

        ResponseEntity<Hall> response = hallController.getById(hall.getId());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(hall);

        verify(hallService).getById(hall.getId());
    }

    @Test
    void getById_shouldReturnStatusNotFound_whenHallDoesNotExist() {
        Long incorrectId = 99L;

        when(hallService.getById(incorrectId)).thenReturn(Optional.empty());

        ResponseEntity<Hall> response = hallController.getById(incorrectId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(hallService).getById(incorrectId);
    }

    @Test
    void getById_shouldThrowHallNotFoundException_whenHallDoesNotExist() {
        Long incorrectId = 99L;

        when(hallService.getById(incorrectId)).thenThrow(new HallNotFoundException(incorrectId));

        assertThrows(HallNotFoundException.class, () -> hallController.getById(incorrectId));

        verify(hallService).getById(incorrectId);
    }

    @Test
    void addHall_shouldReturnStatusCreatedAndHallResponse_whenRequestIsValid() {
        CreateHallRequest request = new CreateHallRequest("Green Hall", 10, 15);
        HallResponse expectedResponse = new HallResponse("Green Hall", 10, 15);

        when(hallService.addHall(request)).thenReturn(expectedResponse);

        ResponseEntity<HallResponse> response = hallController.addHall(request);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(expectedResponse);

        verify(hallService).addHall(request);
    }

    @Test
    void addHall_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        when(hallService.addHall(null)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> hallController.addHall(null));

        verify(hallService).addHall(null);
    }

    @Test
    void addHall_shouldThrowHallNameAlreadyExistsException_whenNameExists() {
        CreateHallRequest request = new CreateHallRequest("Red Hall", 8, 12);

        when(hallService.addHall(request)).thenThrow(new HallNameAlreadyExistsException(request.getName()));

        assertThrows(HallNameAlreadyExistsException.class, () -> hallController.addHall(request));

        verify(hallService).addHall(request);
    }

    @Test
    void updateHall_shouldReturnStatusAcceptedAndHallResponse_whenRequestIsValid() {
        Long hallId = 1L;
        UpdateHallRequest request = new UpdateHallRequest("Gold Hall", 10, 14);
        HallResponse expectedResponse = new HallResponse("Gold Hall", 10, 14);

        when(hallService.updateHall(hallId, request)).thenReturn(expectedResponse);

        ResponseEntity<HallResponse> response = hallController.updateHall(hallId, request);

        assertNotNull(response);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isSameAs(expectedResponse);

        verify(hallService).updateHall(hallId, request);
    }

    @Test
    void updateHall_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        Long hallId = 1L;

        when(hallService.updateHall(hallId, null)).thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () -> hallController.updateHall(hallId, null));

        verify(hallService).updateHall(hallId, null);
    }

    @Test
    void updateHall_shouldThrowHallNotFoundException_whenHallDoesNotExist() {
        Long incorrectId = 99L;
        UpdateHallRequest request = new UpdateHallRequest("Gold Hall", 10, 14);

        when(hallService.updateHall(incorrectId, request)).thenThrow(new HallNotFoundException(incorrectId));

        assertThrows(HallNotFoundException.class, () -> hallController.updateHall(incorrectId, request));

        verify(hallService).updateHall(incorrectId, request);
    }

    @Test
    void updateHall_shouldThrowHallNameAlreadyExistsException_whenNameIsTaken() {
        Long hallId = 1L;
        UpdateHallRequest request = new UpdateHallRequest("Blue Hall", 10, 14);

        when(hallService.updateHall(hallId, request)).thenThrow(new HallNameAlreadyExistsException(request.getName()));

        assertThrows(HallNameAlreadyExistsException.class, () -> hallController.updateHall(hallId, request));

        verify(hallService).updateHall(hallId, request);
    }

    @Test
    void removeHall_shouldReturnNoContent_whenHallExists() {
        Long hallId = 1L;

        ResponseEntity<Void> response = hallController.removeHall(hallId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        verify(hallService).removeHall(hallId);
    }

    @Test
    void removeHall_shouldThrowHallNotFoundException_whenHallDoesNotExist() {
        Long incorrectId = 99L;

        doThrow(new HallNotFoundException(incorrectId)).when(hallService).removeHall(incorrectId);

        assertThrows(HallNotFoundException.class, () -> hallController.removeHall(incorrectId));

        verify(hallService).removeHall(incorrectId);
    }

    private Hall createHall() {
        return new Hall(1L, "Red Hall", 8, 12, true);
    }

    private List<Hall> createListHalls() {
        return List.of(
                new Hall(1L, "Red Hall", 8, 12, true),
                new Hall(2L, "Blue Hall", 11, 10, true),
                new Hall(3L, "Purple Hall", 9, 12, false)
        );
    }
}