package main.cinemabookingrestapi.repository;

import main.cinemabookingrestapi.entity.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    List<Booking> getAll();

    Optional<Booking> getById(Long id);

    Optional<Booking> getByCustomerID(Long customerId);

    Booking addBooking(Booking booking);

    Optional<Booking> cancelBooking(Long bookingId, Booking updatedBooking);
}
