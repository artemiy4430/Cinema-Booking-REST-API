package main.cinemabookingrestapi.exception;

import main.cinemabookingrestapi.entity.Screening;
import main.cinemabookingrestapi.exception.alreadyCancelled.BookingAlreadyCancelledException;
import main.cinemabookingrestapi.exception.alreadyExist.AlreadyExist;
import main.cinemabookingrestapi.exception.inactive.InactiveException;
import main.cinemabookingrestapi.exception.notFound.NotFoundException;
import main.cinemabookingrestapi.exception.screeningException.ScreeningTimeException;
import main.cinemabookingrestapi.exception.seatException.SeatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingAlreadyCancelledException.class)
    public ResponseEntity<ApiErrorResponse> handleAlreadyCancelled(BookingAlreadyCancelledException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(AlreadyExist.class)
    public ResponseEntity<ApiErrorResponse> handleAlreadyExist(AlreadyExist e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InactiveException.class)
    public ResponseEntity<ApiErrorResponse> handleInactive(InactiveException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ScreeningTimeException.class)
    public ResponseEntity<ApiErrorResponse> handleScreeningTimeException(ScreeningTimeException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(SeatException.class)
    public ResponseEntity<ApiErrorResponse> handleSeatException(SeatException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );

        return ResponseEntity.status(status).body(response);
    }
}
