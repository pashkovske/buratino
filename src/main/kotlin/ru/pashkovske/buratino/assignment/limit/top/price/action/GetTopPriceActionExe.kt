package ru.pashkovske.buratino.assignment.limit.top.price.action

import org.springframework.stereotype.Service
import ru.pashkovske.buratino.assignment.limit.top.price.model.TopPriceCtx
import ru.pashkovske.buratino.flow.exe.action.ActionExe
import ru.pashkovske.buratino.price.service.MarketPriceService

@Service
class GetTopPriceActionExe(
    override val name: String = "get_top_price",
    val marketDataService: MarketPriceService,
) : ActionExe<TopPriceCtx> {

    override fun execute(ctx: TopPriceCtx) {
        if (ctx.oneStepOver) {
            ctx.topPrice = marketDataService.getOneStepOverTopOfBook(
                iid = ctx.iid,
                direction = ctx.direction
            )!!
            ctx.rawTopPrice = ctx.topPrice
        } else {
            ctx.topPrice = marketDataService.getTopOfBook(
                iid = ctx.iid,
                direction = ctx.direction
            )!!
            ctx.oneStepOverTopPrice = ctx.topPrice
        }
    }
}