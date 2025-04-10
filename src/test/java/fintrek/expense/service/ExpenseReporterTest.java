package fintrek.expense.service;

import fintrek.util.TestUtils;
import fintrek.misc.MessageDisplayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExpenseReporterTest {
    private ExpenseReporter reporter;

    /**
     * Clears the list of regular expenses before each test.
     */
    @BeforeEach
    void setUp() {
        AppServices.REGULAR_SERVICE.clearExpenses();
        TestUtils.addConstantExpenses();
        reporter = AppServices.REGULAR_REPORTER;
    }

    @Test
    void testGetTotal() {
        assertEquals(TestUtils.TOTAL_TEST_EXPENSE_SUM, reporter.getTotal(), TestUtils.DELTA);
    }

    @Test
    void testGetTotalByCategory() {
        Map<String, Double> map = reporter.getTotalByCategory();
        assertEquals(TestUtils.FOOD_TOTAL, map.get(TestUtils.CATEGORY_FOOD), TestUtils.DELTA);
        assertEquals(TestUtils.TRANSPORT_TOTAL, map.get(TestUtils.CATEGORY_TRANSPORT), TestUtils.DELTA);
        assertEquals(TestUtils.ENTERTAINMENT_TOTAL, map.get(TestUtils.CATEGORY_ENTERTAINMENT), TestUtils.DELTA);
    }

    @Test
    void getHighestCategory_returnsCorrectCategoryAndAmount() {
        Map<String, Double> categoryTotals = reporter.getTotalByCategory();
        String result = reporter.getHighestCategory(categoryTotals);
        boolean correct = result.contains(TestUtils.HIGHEST_SPEND_CATEGORY)
                && result.contains(String.valueOf(TestUtils.HIGHEST_SPEND_AMOUNT));
        assertTrue(correct);
    }

    @Test
    void testGetAverage() {
        assertEquals(TestUtils.EXPECTED_AVERAGE, reporter.getAverage(), TestUtils.DELTA);
    }

    @Test
    void testGetTotalByMonth() {
        LocalDate today = LocalDate.now();
        double expected = TestUtils.TOTAL_TEST_EXPENSE_SUM;
        double actual = reporter.getTotalByMonthOfYear(today.getYear(), today.getMonthValue());
        assertEquals(expected, actual, TestUtils.DELTA);
    }

    @Test
    void testListExpensesFormatNotEmpty() {
        String list = reporter.listExpenses();
        assertFalse(list.isEmpty());
        assertTrue(list.contains("$"));
    }

    @Test
    void testListAllCategoryTotals() {
        Map<String, Double> categoryTotals = reporter.getTotalByCategory();
        String result = reporter.listAllCategoryTotals(categoryTotals);
        assertTrue(result.contains(TestUtils.CATEGORY_FOOD));
        assertTrue(result.contains(TestUtils.CATEGORY_TRANSPORT));
        assertTrue(result.contains(TestUtils.CATEGORY_ENTERTAINMENT));
        assertTrue(result.contains(MessageDisplayer.SUMMARY_GRAND_TOTAL));
    }

    @Test
    void testListSingleCategoryTotal_success() {
        Map<String, Double> categoryTotals = reporter.getTotalByCategory();
        String result = reporter.listSingleCategoryTotal(categoryTotals, TestUtils.CATEGORY_FOOD);
        assertTrue(result.contains(TestUtils.CATEGORY_FOOD));
        assertTrue(result.contains("$"));
    }

    @Test
    void testListSingleCategoryTotal_invalidCategory() {
        Map<String, Double> categoryTotals = reporter.getTotalByCategory();
        String result = reporter.listSingleCategoryTotal(categoryTotals, TestUtils.CATEGORY_INVALID);
        assertEquals(MessageDisplayer.CATEGORY_NOT_FOUND, result);
    }

    @Test
    void testEmptyReporterReturnsSafeDefaults() {
        AppServices.REGULAR_SERVICE.clearExpenses();
        reporter = AppServices.REGULAR_REPORTER;

        StringBuilder list = new StringBuilder();
        Map<String, Double> categoryTotals = reporter.getTotalByCategory();
        double noTotal = 0.00;
        list.append(String.format(MessageDisplayer.HIGHEST_CAT_FORMAT,
                MessageDisplayer.SUMMARY_HIGHEST_SPEND, MessageDisplayer.EMPTY_LIST_MESSAGE));
        list.append(String.format(MessageDisplayer.GRAND_TOTAL_FORMAT,
                MessageDisplayer.SUMMARY_GRAND_TOTAL, noTotal));
        assertEquals(0, reporter.getTotal(), TestUtils.DELTA);
        assertEquals(0, reporter.getAverage(), TestUtils.DELTA);
        assertEquals(MessageDisplayer.EMPTY_LIST_MESSAGE, reporter.getHighestCategory(categoryTotals));
        assertEquals(list.toString(), reporter.listAllCategoryTotals(categoryTotals));
    }

    @Test
    void testGetExpensesByCategory_nullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> reporter.getExpensesByCategory(null));
    }

    @Test
    void testConstructor_nullManagerThrows() {
        assertThrows(IllegalArgumentException.class, () -> new ExpenseReporter(null));
    }
}
