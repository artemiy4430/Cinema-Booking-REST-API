package main.cinemabookingrestapi.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Hall extends Entity {
    private String name;
    private int rows;
    private int seatsPerRow;
    private boolean active;

    public Hall(Long id, String name, int rows, int seatsPerRow, boolean active) {
        super(id);
        this.name = name;
        this.rows = rows;
        this.seatsPerRow = seatsPerRow;
        this.active = active;
    }

    public Hall(Long id) {
        super(id);
    }

    public Hall() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRows() {
        return rows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public boolean isActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setSeatsPerRow(int seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Hall hall = (Hall) o;
        return rows == hall.rows && seatsPerRow == hall.seatsPerRow && active == hall.active && Objects.equals(id, hall.id) && Objects.equals(name, hall.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, rows, seatsPerRow, active);
    }

    public List<String> generateAllSeats() {
        List<String> allSeats = new ArrayList<>();

        for (int row = 0; row < getRows(); row++) {
            char rowLetter = (char) ('A' + row);

            for (int seatNum = 1; seatNum <= getSeatsPerRow(); seatNum++) {
                allSeats.add(rowLetter + String.valueOf(seatNum));
            }
        }

        return allSeats;
    }


}
