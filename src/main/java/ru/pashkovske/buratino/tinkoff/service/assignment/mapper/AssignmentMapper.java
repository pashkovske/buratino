package ru.pashkovske.buratino.tinkoff.service.assignment.mapper;

import ru.pashkovske.buratino.tinkoff.service.assignment.Assignment;
import ru.pashkovske.buratino.tinkoff.service.assignment.FollowPriceAssignment;
import ru.pashkovske.buratino.tinkoff.service.assignment.model.AssignmentStateDto;
import ru.pashkovske.buratino.tinkoff.service.order.mapper.OrderDataMapper;

public class AssignmentMapper {
    public static AssignmentStateDto mapBestPrice(Assignment assignment) {
        FollowPriceAssignment followPriceAssignment = (FollowPriceAssignment) assignment;
        return new AssignmentStateDto(
                assignment.getId(),
                OrderDataMapper.mapResponse(followPriceAssignment.getOrder()),
                assignment.getCommand().getInstrument().getTicker(),
                assignment.getCommand().getInstrument().getName()
        );
    }
}
