package com.driving.planning.student.reservation;

import com.driving.planning.common.DatePattern;
import com.driving.planning.student.Student;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.eclipse.microprofile.opentracing.Traced;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequestScoped
@Traced
public class ReservationRepository {

    private final MongoDatabase mongoDatabase;

    private final DateTimeFormatter dateFormatter;

    @Inject
    public ReservationRepository(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
        dateFormatter = DateTimeFormatter.ofPattern(DatePattern.DATE);
    }

    public List<Reservation> findByDate(@NotNull LocalDate date){
        var cursor = mongoDatabase.getCollection(Student.COLLECTION_NAME, Student.class)
                .find(Filters.eq("reservations.date", dateFormatter.format(date)))
                .iterator();
        var result = new ArrayList<Reservation>();
        while (cursor.hasNext()){
            var student = cursor.next();
            result.addAll(student.getReservations()
                    .stream()
                    .filter(r -> date.isEqual(r.getDate()))
                    .collect(Collectors.toList()));
        }
        return result;
    }
}
