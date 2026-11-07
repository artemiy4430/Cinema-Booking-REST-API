package main.cinemabookingrestapi.dto.hall;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import main.cinemabookingrestapi.entity.Hall;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Objects;

public class CreateHallRequest {

    @NotBlank
    private String name;

    @Size(min = 1, max = 50, message = "рядов должно быть от 1 до 30")
    private int rows;

    @Size(min = 1, max = 50, message = "сидений должно быть от 1 до 30")
    private int seatsPerRows;

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
        CreateHallRequest that = (CreateHallRequest) o;
        return rows == that.rows && seatsPerRows == that.seatsPerRows && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, rows, seatsPerRows);
    }

    public Hall toEntity() {
        Hall hall = new Hall();

        hall.setName(this.name);
        hall.setRows(this.rows);
        hall.setSeatsPerRow(this.seatsPerRows);

        return hall;
    }
}