//@@author szeyingg
package fintrek.command.add;

import fintrek.expense.core.Expense;
import fintrek.budget.BudgetManager;
import fintrek.command.Command;
import fintrek.command.registry.CommandInfo;
import fintrek.command.registry.CommandResult;
import fintrek.misc.MessageDisplayer;
import fintrek.util.InputValidator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandInfo(
        recurringFormat = "Format: /add-recurring <DESCRIPTION> $<AMOUNT> [/c <CATEGORY>] [/d <DATE>]",
        regularFormat = "Format: /add <DESCRIPTION> $<AMOUNT> [/c <CATEGORY>] [/d <DATE>]",
        description = """
                AMOUNT must be a positive number greater than 0
                CATEGORY is an optional argument
                DATE is an optional argument which must be in the form dd-MM-yyyy
                Example: /add concert tickets $35.80 /c LEISURE /d [03-05-2025] -
                        adds an expense with description 'concert tickets' with the amount $35.80,
                        with the category 'LEISURE' and date '03-05-2025'.
                """
)

public class AddCommand extends Command {
    private static final String COMMAND_NAME = "add";

    public AddCommand(boolean isRecurring) {
        super(isRecurring);
    }
    /**
     * Adds an expense into the expense list, and also checks for any invalid inputs
     * @param arguments the string containing important parameters pertaining to the expense
     * @return a {@code CommandResult} object telling whether the
     *      execution is successful or not, and an error/success message
     */
    @Override
    public CommandResult execute(String arguments) {
        if (InputValidator.isNullOrBlank(arguments)) {
            return new CommandResult(false, MessageDisplayer.EMPTY_DESC_AND_AMT_MESSAGE);
        }

        Pattern p = Pattern.compile(InputValidator.validAddFormat());
        Matcher m = p.matcher(arguments.trim());
        if (!m.matches()) {
            return new CommandResult(false,
                    String.format(MessageDisplayer.INVALID_FORMAT_MESSAGE_TEMPLATE, COMMAND_NAME));
        }

        String description = m.group(1).trim();
        String amountStr = m.group(2);
        String category = (m.group(3) != null) ? m.group(3).trim() : "Uncategorized";
        String dateStr = (m.group(4) != null) ? m.group(4).trim() : null;
        if (!InputValidator.isValidAmountInput(amountStr)) {
            return new CommandResult(false, MessageDisplayer.INVALID_AMT_MESSAGE);
        }
        double amount = Double.parseDouble(amountStr);
        assert amount > 0 : MessageDisplayer.INVALID_AMT_MESSAGE;

        if(dateStr != null && !InputValidator.isValidDate(dateStr)) {
            return new CommandResult(false, MessageDisplayer.INVALID_DATE_MESSAGE);
        }

        return getCommandResult(dateStr, description, amount, category);
    }

    /**
     * This function collates all the variables required to create a new expense
     *      It will then return a CommandResult after the process is done, signifying it is succeeded
     * @param dateStr is the date of the new general or recurring expense to be added
     * @param description is the description of the new general or recurring expense to be added
     * @param amount is the amount spent for the new general or recurring expense to be added
     * @param category is the category of the new general or recurring expense to be added
     *                 which will be used to group with same-category expenses
     * @return a {@code CommandResult} once the process is done
     */
    private CommandResult getCommandResult(String dateStr, String description, double amount, String category) {
        LocalDate date = extractDate(dateStr);
        Expense newExpense = new Expense(description, amount, category, date);
        service.addExpense(newExpense);
        System.out.println(checkBudgetWarnings(LocalDate.now()));
        String message = (isRecurringExpense) ?
                String.format(MessageDisplayer.ADD_RECURRING_SUCCESS_MESSAGE_TEMPLATE, newExpense):
                String.format(MessageDisplayer.ADD_SUCCESS_MESSAGE_TEMPLATE, newExpense);
        return new CommandResult(true, message);
    }

    /**
     * This function will generate warnings if the total expenses of the current month
     * almost exceeds or exceeds the current monthly budget. Note that expenses in
     * the previous month will not be accounted for anymore.
     * @param date the date today, which will be used to generate the current year and month
     * @return warnings depending on whether total expenses exceeds or almost exceeds the monthly budget.
     */
    private String checkBudgetWarnings(LocalDate date) {
        BudgetManager budgetManager = BudgetManager.getInstance();
        if (!budgetManager.isBudgetSet()) {
            return ""; // No budget set, no warning needed
        }

        double budget = budgetManager.getBudget();
        double totalExpenses = reporter.getTotalByMonth(date.getYear(), date.getMonthValue());

        if (totalExpenses >= budget) {
            return String.format(MessageDisplayer.EXCEEDED_BUDGET_MESSAGE, budget, totalExpenses-budget);
        } else if (totalExpenses >= budget * 0.9) {
            return String.format(MessageDisplayer.ALMOST_EXCEEDED_BUDGET_MESSAGE, budget, budget-totalExpenses);
        }
        return "";
    }

    /**
     * This functions verifies a date is in the format that we want and then converts it into LocalDate type.
     * @param dateStr the date in the form of String, which will be checked for the pattern
     *                and converted into LocalDate
     * @return a converted dateStr into a LocalDate variable following the format we want
     */
    public LocalDate extractDate(String dateStr) {
        if (dateStr == null) {
            return LocalDate.now(); // Default to today's date if not provided
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(dateStr, formatter);
    }
}
