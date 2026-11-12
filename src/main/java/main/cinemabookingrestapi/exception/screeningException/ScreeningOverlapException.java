package main.cinemabookingrestapi.exception.screeningException;

import java.time.LocalDateTime;

public class ScreeningOverlapException extends ScreeningTimeException {
    public ScreeningOverlapException(LocalDateTime startAt) {
        super("screening time overlap at: " + startAt);
    }
}
