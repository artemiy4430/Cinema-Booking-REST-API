package main.cinemabookingrestapi.controller;

import main.cinemabookingrestapi.dto.hall.CreateHallRequest;
import main.cinemabookingrestapi.dto.hall.HallResponse;
import main.cinemabookingrestapi.dto.hall.UpdateHallRequest;
import main.cinemabookingrestapi.entity.Hall;
import main.cinemabookingrestapi.service.HallService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/halls")
public class HallController {
    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping
    public List<Hall> getAll() {
        return hallService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hall> getById(@PathVariable Long id) {
        return hallService.getById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HallResponse> addHall(@RequestBody CreateHallRequest request) {
        HallResponse response = hallService.addHall(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<HallResponse> updateHall(@PathVariable Long id, @RequestBody UpdateHallRequest request) {
        HallResponse response = hallService.updateHall(id, request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @DeleteMapping("{/id}")
    public ResponseEntity<Void> removeHall(@PathVariable Long id) {
        hallService.removeHall(id);

        return ResponseEntity.noContent().build();
    }
}
