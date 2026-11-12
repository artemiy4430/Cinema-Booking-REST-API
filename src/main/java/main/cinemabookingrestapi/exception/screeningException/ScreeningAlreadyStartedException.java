package main.cinemabookingrestapi.exception.screeningException;

import java.time.LocalDateTime;

public class ScreeningAlreadyStartedException extends ScreeningTimeException {
    public ScreeningAlreadyStartedException(LocalDateTime startAt) {
        super("Screening has already started at:" + startAt);
    }
}
