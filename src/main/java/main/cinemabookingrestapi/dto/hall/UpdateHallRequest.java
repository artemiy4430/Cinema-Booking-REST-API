package main.cinemabookingrestapi.dto.hall;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UpdateHallRequest {

    @NotBlank
    private String name;

    @Size(min = 1, max = 50, message = "рядов должно быть от 1 до 30")
    private int rows;

    @Size(min = 1, max = 50, message = "сидений должно быть от 1 до 30")
    private int seatsPerRows;

    public UpdateHallRequest(String name, int rows, int seatsPerRows) {
        this.name = name;
        this.rows = rows;
        this.seatsPerRows = seatsPerRows;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public int getSeatsPerRows() {
        return seatsPerRows;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setSeatsPerRows(int seatsPerRows) {
        this.seatsPerRows = seatsPerRows;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UpdateHallRequest that = (UpdateHallRequest) o;
        return rows == that.rows && seatsPerRows == that.seatsPerRows && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rows, seatsPerRows);
    }
}
