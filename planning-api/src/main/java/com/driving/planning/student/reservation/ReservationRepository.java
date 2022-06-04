package com.driving.planning.student.reservation;

import com.driving.planning.student.Student;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Traced
public class ReservationRepository implements PanacheMongoRepository<Student> {

    public List<Reservation> findByDate(@NotNull LocalDate date){
        var result = new ArrayList<Reservation>();
        list("reservations.date", date)
                .forEach(s -> result.addAll(s.getReservations()
                        .stream()
                        .filter(r -> date.isEqual(r.getDate()))
                        .collect(Collectors.toList())));
        return result;
    }
}
