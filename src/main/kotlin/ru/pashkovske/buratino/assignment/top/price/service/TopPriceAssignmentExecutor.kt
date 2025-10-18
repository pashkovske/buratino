package ru.pashkovske.buratino.assignment.top.price.service

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.repo.AssignmentRepo
import ru.pashkovske.buratino.assignment.service.LimitOrderAssignmentExecutor
import ru.pashkovske.buratino.assignment.top.price.model.TopPriceAssignment
import ru.pashkovske.buratino.instrument.model.Instrument
import ru.pashkovske.buratino.instrument.service.InstrumentService
import ru.pashkovske.buratino.order.model.OrderDirection
import ru.pashkovske.buratino.order.service.OrderService
import ru.pashkovske.buratino.price.money.model.MoneyPrice
import ru.pashkovske.buratino.price.money.service.MarketMoneyPriceService

@Service
class TopPriceAssignmentExecutor(
    orderService: OrderService,
    assignmentRepo: AssignmentRepo<TopPriceAssignment>,
    val marketDataService: MarketMoneyPriceService,
    val instrumentService: InstrumentService
) : LimitOrderAssignmentExecutor<TopPriceAssignment>(
    orderService = orderService,
    assignmentRepo = assignmentRepo
) {
    override fun getPrice(assignment: TopPriceAssignment): MoneyPrice {
        val instrument: Instrument = instrumentService.get(assignment.iid)
        var moneyTopPrice: MoneyPrice = marketDataService.getTopOfBook(
            iid = assignment.iid,
            direction = assignment.direction
        )!!
        if (assignment.oneStepOver) {
            if (assignment.direction == OrderDirection.BUY) {
                moneyTopPrice += instrument.minPriceIncrement
            } else {
                moneyTopPrice -= instrument.minPriceIncrement
            }
        }
        return moneyTopPrice
    }
}
