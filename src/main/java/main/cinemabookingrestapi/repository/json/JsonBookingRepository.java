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
    private final JsonMaxIdRepository maxIdRepository;

    public JsonBookingRepository(JsonMapper jsonMapper, @Value("./data/bookings.json") String filePath,
                                 JsonMaxIdRepository maxIdRepository) {
        super(jsonMapper, Path.of(filePath), new TypeReference<List<Booking>>(){});
        this.maxIdRepository = maxIdRepository;
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

        booking.setId(maxIdRepository.generateNewId(booking));
        bookings.add(booking);
        writeEntities(bookings);

        return booking;
    }

    @Override
    public synchronized Optional<Booking> cancelBooking(Long bookingId, Booking updatedBooking) {
        if (updatedBooking == null || bookingId == null || bookingId < 1) {
            return Optional.empty();
        }

        List<Booking> bookings = readEntities();
        Optional<Booking> booking = bookings.stream().filter(b -> Objects.equals(b.getId(), bookingId)).findFirst();

        if (booking.isEmpty()) return Optional.empty();

        updatedBooking.setId(bookingId);

        bookings.remove(booking.get());
        bookings.add(updatedBooking);
        writeEntities(bookings);

        return Optional.of(updatedBooking);
    }
}
