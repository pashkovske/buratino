package ru.pashkovske.buratino.tinkoff.service.assignment.mapper;

import ru.pashkovske.buratino.tinkoff.service.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.assignment.FollowPriceAssignment;
import ru.pashkovske.buratino.tinkoff.service.assignment.model.AssignmentStateDto;
import ru.pashkovske.buratino.tinkoff.service.order.mapper.OrderDataMapper;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AssignmentMapper {
    public static AssignmentStateDto mapBestPrice(Assignment assignment) {
        FollowPriceAssignment followPriceAssignment = (FollowPriceAssignment) assignment;
        ScheduledFuture<?> scheduledFuture = assignment.getScheduledFuture();
        Instant nextTicTime = null;
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            nextTicTime = Instant.now().plusMillis(assignment.getScheduledFuture().getDelay(TimeUnit.MILLISECONDS));
        }
        return new AssignmentStateDto(
                assignment.getId(),
                OrderDataMapper.mapResponse(followPriceAssignment.getOrder()),
                assignment.getCommand().getInstrument().getTicker(),
                assignment.getCommand().getInstrument().getName(),
                nextTicTime
        );
    }
}
