package main.cinemabookingrestapi.services;

import main.cinemabookingrestapi.dto.hall.CreateHallRequest;
import main.cinemabookingrestapi.dto.hall.HallResponse;
import main.cinemabookingrestapi.dto.hall.UpdateHallRequest;
import main.cinemabookingrestapi.entity.Hall;
import main.cinemabookingrestapi.entity.Screening;
import main.cinemabookingrestapi.exception.alreadyExist.HallNameAlreadyExistsException;
import main.cinemabookingrestapi.exception.notFound.HallNotFoundException;
import main.cinemabookingrestapi.exception.isPresentException.HallIsPresentInScreeningException;
import main.cinemabookingrestapi.repository.HallRepository;
import main.cinemabookingrestapi.repository.ScreeningRepository;
import main.cinemabookingrestapi.service.HallService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HallServiceTest {

    @Mock
    private HallRepository hallRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @InjectMocks
    private HallService hallService;

    @Test
    void getAll_shouldReturnEmptyList() {
        when(hallRepository.getAll()).thenReturn(Collections.emptyList());

        List<Hall> halls = hallService.getAll();

        assertNotNull(halls);
        assertTrue(halls.isEmpty());

        verify(hallRepository).getAll();
    }

    @Test
    void getAll_shouldReturnListOfHalls() {
        List<Hall> hallList = createList();

        when(hallRepository.getAll()).thenReturn(hallList);

        List<Hall> halls = hallService.getAll();

        assertNotNull(halls);
        assertFalse(halls.isEmpty());
        assertEquals(1L, halls.get(0).getId());
        assertFalse(halls.get(2).isActive());

        verify(hallRepository).getAll();
    }

    @Test
    void getById_shouldReturnHall() {
        Hall hall = createHall();

        when(hallRepository.getById(1L)).thenReturn(Optional.of(hall));

        Optional<Hall> result = hallService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Red Hall", result.get().getName());

        verify(hallRepository).getById(1L);
    }

    @Test
    void getById_shouldThrowIllegalArgumentException_whenIdIsLessThanOne() {
        assertThrows(IllegalArgumentException.class, () -> hallService.getById(0L));

        verifyNoInteractions(hallRepository);
    }

    @Test
    void getById_shouldThrowHallNotFoundException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> hallService.getById(null));

        verifyNoInteractions(hallRepository);
    }

    @Test
    void getById_shouldThrowHallNotFoundException_whenNotFound() {
        when(hallRepository.getById(12L)).thenReturn(Optional.empty());

        assertThrows(HallNotFoundException.class, () -> hallService.getById(12L));

        verify(hallRepository).getById(anyLong());
    }

    @Test
    void addHall_shouldReturnHallResponse() {
        CreateHallRequest request = new CreateHallRequest("Green Hall", 10, 15);
        Hall savedHall = new Hall(4L, "Green Hall", 10, 15, true);

        when(hallRepository.getByName("Green Hall")).thenReturn(Optional.empty());
        when(hallRepository.addHall(any(Hall.class))).thenReturn(savedHall);

        HallResponse response = hallService.addHall(request);

        assertNotNull(response);
        assertEquals("Green Hall", response.getName());
        assertEquals(10, response.getRows());
        assertEquals(15, response.getSeatsPerRows());

        verify(hallRepository).getByName("Green Hall");
        verify(hallRepository).addHall(any(Hall.class));
    }

    @Test
    void addHall_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> hallService.addHall(null));

        verifyNoInteractions(hallRepository);
    }

    @Test
    void addHall_shouldThrowHallNameAlreadyExistsException_whenNameExists() {
        CreateHallRequest request = new CreateHallRequest("Red Hall", 8, 12);

        when(hallRepository.getByName("Red Hall")).thenReturn(Optional.of(createHall()));

        assertThrows(HallNameAlreadyExistsException.class, () -> hallService.addHall(request));

        verify(hallRepository).getByName("Red Hall");
        verify(hallRepository, never()).addHall(any(Hall.class));
    }

    @Test
    void updateHall_shouldReturnUpdatedHallResponse() {
        Hall existingHall = createHall();
        existingHall.setId(12L);
        UpdateHallRequest request = new UpdateHallRequest("Gold Hall", 10, 14);
        Hall updatedHall = new Hall(12L, "Gold Hall", 10, 14, true);
        List<Screening> screenings = createScreeningList();

        when(hallRepository.getById(12L)).thenReturn(Optional.of(existingHall));
        when(screeningRepository.getAll()).thenReturn(screenings);
        when(hallRepository.getAll()).thenReturn(createList());
        when(hallRepository.updateHall(eq(12L), any(Hall.class))).thenReturn(Optional.of(updatedHall));

        HallResponse response = hallService.updateHall(12L, request);

        assertNotNull(response);
        assertEquals("Gold Hall", response.getName());
        assertEquals(10, response.getRows());
        assertEquals(14, response.getSeatsPerRows());

        verify(hallRepository).getById(12L);
        verify(hallRepository).getAll();
        verify(hallRepository).updateHall(eq(12L), any(Hall.class));
    }

    @Test
    void updateHall_shouldThrowIllegalArgumentException_whenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> hallService.updateHall(1L, null));

        verifyNoInteractions(hallRepository);
    }

    @Test
    void updateHall_shouldThrowHallNotFoundException_whenHallNotFound() {
        UpdateHallRequest request = new UpdateHallRequest("Gold Hall", 10, 14);

        when(hallRepository.getById(99L)).thenReturn(Optional.empty());

        assertThrows(HallNotFoundException.class, () -> hallService.updateHall(99L, request));

        verify(hallRepository).getById(99L);
        verify(hallRepository, never()).updateHall(anyLong(), any(Hall.class));
    }

    @Test
    void updateHall_shouldThrowHallIsPresentInScreeningException_whenScreeningContainsGivenHall() {
        Hall hall = createHall();
        List<Screening> screenings = createScreeningList();
        UpdateHallRequest request = new UpdateHallRequest("Gold Hall", 10, 14);

        when(hallRepository.getById(1L)).thenReturn(Optional.of(hall));
        when(screeningRepository.getAll()).thenReturn(screenings);

        assertThrows(HallIsPresentInScreeningException.class, () -> hallService.updateHall(hall.getId(), request));

        verify(hallRepository).getById(1L);
        verify(screeningRepository).getAll();
        verify(hallRepository, never()).updateHall(anyLong(), any(Hall.class));
    }

    @Test
    void updateHall_shouldThrowHallNameAlreadyExistsException_whenNameIsTaken() {
        Hall existingHall = createHall();
        UpdateHallRequest request = new UpdateHallRequest("Blue Hall", 10, 14);

        when(hallRepository.getById(1L)).thenReturn(Optional.of(existingHall));
        when(hallRepository.getAll()).thenReturn(createList());

        assertThrows(HallNameAlreadyExistsException.class, () -> hallService.updateHall(1L, request));

        verify(hallRepository).getById(1L);
        verify(hallRepository).getAll();
        verify(hallRepository, never()).updateHall(anyLong(), any(Hall.class));
    }

    @Test
    void updateHall_shouldThrowIllegalArgumentException_whenRepositoryReturnsEmpty() {
        Hall existingHall = createHall();
        UpdateHallRequest request = new UpdateHallRequest("Gold Hall", 10, 14);

        when(hallRepository.getById(1L)).thenReturn(Optional.of(existingHall));
        when(hallRepository.getAll()).thenReturn(createList());
        when(hallRepository.updateHall(eq(1L), any(Hall.class))).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> hallService.updateHall(1L, request));

        verify(hallRepository).updateHall(eq(1L), any(Hall.class));
    }

    @Test
    void removeHall_shouldDeactivateHall() {
        Hall hall = createHall();

        when(hallRepository.getById(1L)).thenReturn(Optional.of(hall));

        hallService.removeHall(1L);

        ArgumentCaptor<Hall> hallCaptor = ArgumentCaptor.forClass(Hall.class);
        verify(hallRepository).getById(1L);
        verify(hallRepository).updateHall(eq(1L), hallCaptor.capture());

        assertFalse(hallCaptor.getValue().isActive());
    }

    @Test
    void removeHall_shouldThrowIllegalArgumentException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> hallService.removeHall(null));

        verifyNoInteractions(hallRepository);
    }

    @Test
    void removeHall_shouldThrowIllegalArgumentException_whenIdIsZero() {
        assertThrows(IllegalArgumentException.class, () -> hallService.removeHall(0L));

        verifyNoInteractions(hallRepository);
    }

    @Test
    void removeHall_shouldThrowHallNotFoundException_whenHallNotFound() {
        when(hallRepository.getById(99L)).thenReturn(Optional.empty());

        assertThrows(HallNotFoundException.class, () -> hallService.removeHall(99L));

        verify(hallRepository).getById(99L);
        verify(hallRepository, never()).updateHall(anyLong(), any(Hall.class));
    }

    private Hall createHall() {
        return new Hall(1L, "Red Hall", 8, 12, true);
    }

    private List<Hall> createList() {
        return List.of(
                new Hall(
                        1L,
                        "Red Hall",
                        8,
                        12,
                        true
                ),
                new Hall(
                        2L,
                        "Blue Hall",
                        11,
                        10,
                        true
                ),
                new Hall(
                        3L,
                        "Purple Hall",
                        9,
                        12,
                        false
                )
        );
    }

    private List<Screening> createScreeningList() {
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
}
