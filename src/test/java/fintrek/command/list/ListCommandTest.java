package fintrek.command.list;

import fintrek.command.registry.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fintrek.misc.MessageDisplayer;
import fintrek.util.TestUtils;

/**
 * Unit tests for the {@code ListCommand} class.
 * Ensures that the list of expense is correctly displayed.
 */
public class ListCommandTest {
    /**
     * Clear all existing expenses in RegularExpenseManager and RecurringExpenseManager
     * and adds set list of expenses before each test.
     */
    @BeforeEach
    public void setUp() {
        TestUtils.regularService.clearExpenses();
        TestUtils.recurringService.clearExpenses();
    }

    /**
     * Tests list command with empty ArrayList.
     * Verifies the command returns a successful CommandResult with the correct empty list message.
     */
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testListCommand_emptyList_success(boolean isRecurring) {
        ListCommand command = new ListCommand(isRecurring);
        CommandResult result = command.execute("");

        String expectedMessage;
        if (isRecurring) {
            expectedMessage = String.format(MessageDisplayer.LIST_RECURRING_SUCCESS_MESSAGE_TEMPLATE,
                    TestUtils.recurringReporter.listExpenses());
        } else {
            expectedMessage = String.format(MessageDisplayer.LIST_SUCCESS_MESSAGE_TEMPLATE,
                    TestUtils.regularReporter.listExpenses());
        }

        TestUtils.assertCommandSuccess(result, MessageDisplayer.ASSERT_EMPTY_LIST);
        TestUtils.assertCommandMessage(result, MessageDisplayer.ASSERT_EMPTY_LIST, expectedMessage);
    }

    /**
     * Tests list command with list of predefined expenses.
     * Verifies the command returns a successful CommandResult with the correct list of expenses.
     */
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testListCommand_filledList_success(boolean isRecurring) {
        if (isRecurring) {
            TestUtils.addConstantRecurringExpenses();
        } else {
            TestUtils.addConstantExpenses();
        }

        ListCommand command = new ListCommand(isRecurring);
        CommandResult result = command.execute("");
      
        String expectedMessage;
        if (isRecurring) {
            expectedMessage = String.format(MessageDisplayer.LIST_RECURRING_SUCCESS_MESSAGE_TEMPLATE,
                    TestUtils.recurringReporter.listExpenses());
        } else {
            expectedMessage = String.format(MessageDisplayer.LIST_SUCCESS_MESSAGE_TEMPLATE,
                    TestUtils.regularReporter.listExpenses());
        }

        TestUtils.assertCommandSuccess(result, MessageDisplayer.ASSERT_FILLED_LIST);
        TestUtils.assertCommandMessage(result, MessageDisplayer.ASSERT_FILLED_LIST, expectedMessage);
    }

    /**
     * Tests the description of list command.
     * Verifies the command returns the correct description.
     */
    @Test
    public void testListCommand_getDescription_success() {
        ListCommand command = new ListCommand(false);
        String expectedDescription = """
                Format: /list
                Lists all recorded expenses.
                """;

        assertEquals(expectedDescription, command.getDescription(),
                MessageDisplayer.ASSERT_COMMAND_EXPECTED_OUTPUT + MessageDisplayer.ASSERT_GET_DESC);
    }
}
