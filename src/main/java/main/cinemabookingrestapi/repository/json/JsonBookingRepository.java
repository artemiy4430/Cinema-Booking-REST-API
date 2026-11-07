package main.cinemabookingrestapi.repository.json;

import main.cinemabookingrestapi.entity.Booking;
import main.cinemabookingrestapi.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class JsonBookingRepository extends JsonRepository<Booking> implements BookingRepository {
    public JsonBookingRepository(JsonMapper jsonMapper, @Value("./data/bookings.json") String filePath) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<Booking>>(){});
    }

    @Override
    public synchronized List<Booking> getAll() {
        return readEntities();
    }

    @Override
    public synchronized Optional<Booking> getById(Long id) {
        return readEntities().stream().filter(b -> Objects.equals(b.getId(), id)).findFirst();
    }

    @Override
    public synchronized Optional<Booking> getByCustomerID(Long customerId) {
        return readEntities().stream().filter(b -> Objects.equals(b.getCustomerId(), customerId)).findFirst();
    }

    @Override
    public synchronized Booking addBooking(Booking booking) {
        List<Booking> bookings = readEntities();

        if (bookings.contains(booking)) return null;

        booking.setId(generateNewId(bookings));
        bookings.add(booking);
        writeEntities(bookings);

        return booking;
    }

    @Override
    public synchronized Optional<Booking> cancelBooking(Long bookingId, Booking updatedBooking) {
        List<Booking> bookings = readEntities();
        Optional<Booking> booking = bookings.stream().filter(b -> Objects.equals(b.getId(), bookingId)).findFirst();

        if (booking.isEmpty()) return Optional.empty();

        bookings.remove(booking.get());
        bookings.add(updatedBooking);
        writeEntities(bookings);

        return Optional.of(updatedBooking);
    }
}
