package main.cinemabookingrestapi.controller;

import main.cinemabookingrestapi.dto.screening.CreateScreeningRequest;
import main.cinemabookingrestapi.dto.screening.ScreeningFilterRequest;
import main.cinemabookingrestapi.dto.screening.ScreeningResponse;
import main.cinemabookingrestapi.entity.Screening;
import main.cinemabookingrestapi.service.ScreeningService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/screenings")
public class ScreeningController {

    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @GetMapping
    public ResponseEntity<List<ScreeningResponse>> getMovies(@RequestBody(required = false) Long movieId,
                                                             @RequestBody(required = false) Long hallId,
                                                             @RequestBody(required = false) LocalDateTime startAt,
                                                             @RequestBody(required = false) Boolean isActive
    ) {
        ScreeningFilterRequest request = new ScreeningFilterRequest(movieId, hallId, startAt, isActive);

        List<ScreeningResponse> filterList = screeningService.getFilteredScreenings(request);

        return ResponseEntity.status(HttpStatus.OK).body(filterList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Screening> getById(@PathVariable Long id) {
        return screeningService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ScreeningResponse> addScreening(@RequestParam CreateScreeningRequest request) {
        ScreeningResponse response = screeningService.addScreening(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeScreening(@PathVariable Long id) {
        screeningService.removeScreening(id);

        return ResponseEntity.noContent().build();
    }
}
