package ru.pashkovske.buratino.unit.assignment

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import ru.pashkovske.buratino.assignment.limit.spread.fraction.service.FractionalSpreadAssignmentExe
import ru.pashkovske.buratino.assignment.limit.top.price.service.TopPriceAssignmentExe
import ru.pashkovske.buratino.assignment.nested.continuous.spread.fraction.service.ContinuousFractionalSpreadAssignmentExe

class FinalAssignmentExeChainsTest {

    @Test
    fun checkContinuousFractionalSpreadAssignmentExeChains() {
        val assignmentExe = ContinuousFractionalSpreadAssignmentExe(
            assignmentRepo = mock(),
            nestedAssignmentExe = mock()
        )

        val expectedStartChain: List<String> = listOf(
            "log_start",
            "check_and_start_nested",
            "set_status_in_progress",
            "create_in_repo"
        )
        assertEquals(expectedStartChain, assignmentExe.getStartChainNames())

        val expectedRefreshChain: List<String> = listOf(
            "log_refresh",
            "check_completed",
            "refresh_nested",
            "set_status_in_progress",
            "update_in_repo"
        )
        assertEquals(expectedRefreshChain, assignmentExe.getRefreshChainNames())

        val expectedCancelChain: List<String> = listOf(
            "log_cancel",
            "check_completed",
            "cancel_nested",
            "set_status_completed",
            "update_in_repo"
        )
        assertEquals(expectedCancelChain, assignmentExe.getCancelChainNames())

        val expectedContinueChain: List<String> = listOf(
            "log_continue",
            "check_status",
            "check_nested_status",
            "start_new_spread_fraction_assignment",
            "update_in_repo"
        )
        assertEquals(expectedContinueChain, assignmentExe.getContinueChainNames())
    }

    @Test
    fun checkFractionalSpreadAssignmentExeChains() {
        val assignmentExe = FractionalSpreadAssignmentExe(
            orderService = mock(),
            assignmentRepo = mock(),
            marketDataService = mock(),
            instrumentService = mock()
        )

        val expectedStartChain: List<String> = listOf(
            "log_start",
            "start_limit_order",
            "set_status_in_progress",
            "create_in_repo"
        )
        assertEquals(expectedStartChain, assignmentExe.getStartChainNames())

        val expectedRefreshChain: List<String> = listOf(
            "log_refresh",
            "check_completed",
            "refresh_limit_order",
            "set_status_in_progress",
            "update_in_repo"
        )
        assertEquals(expectedRefreshChain, assignmentExe.getRefreshChainNames())

        val expectedCancelChain: List<String> = listOf(
            "log_cancel",
            "check_completed",
            "cancel_limit_order",
            "set_status_completed",
            "update_in_repo"
        )
        assertEquals(expectedCancelChain, assignmentExe.getCancelChainNames())
    }

    @Test
    fun checkTopPriceAssignmentExeChains() {
        val assignmentExe = TopPriceAssignmentExe(
            orderService = mock(),
            assignmentRepo = mock(),
            marketDataService = mock()
        )

        val expectedStartChain: List<String> = listOf(
            "log_start",
            "start_limit_order",
            "set_status_in_progress",
            "create_in_repo"
        )
        assertEquals(expectedStartChain, assignmentExe.getStartChainNames())

        val expectedRefreshChain: List<String> = listOf(
            "log_refresh",
            "check_completed",
            "refresh_limit_order",
            "set_status_in_progress",
            "update_in_repo"
        )
        assertEquals(expectedRefreshChain, assignmentExe.getRefreshChainNames())

        val expectedCancelChain: List<String> = listOf(
            "log_cancel",
            "check_completed",
            "cancel_limit_order",
            "set_status_completed",
            "update_in_repo"
        )
        assertEquals(expectedCancelChain, assignmentExe.getCancelChainNames())
    }
}
