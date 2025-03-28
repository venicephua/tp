//@@author venicephua
package fintrek.command;

import fintrek.ExpenseManager;
import fintrek.misc.MessageDisplayer;

@CommandInfo(
        description = """
            Format: /total
            Returns sum of all expenses in the list, but will return 0 if the list is empty.
            Example: For a list of expenses: TransportExpense1, TransportExpense2, FoodExpense1
            /total returns (TransportExpense1 + TransportExpense2 + FoodExpense1).
            """
)
public class TotalCommand extends Command {

    @Override
    public CommandResult execute(String arguments) {
        try {
            double total = ExpenseManager.getTotalExpenses();
            String message = String.format(MessageDisplayer.TOTAL_SUCCESS_MESSAGE_TEMPLATE, total);
            return new CommandResult(true, message);
        } catch (Exception e) {
            return new CommandResult(false,
                    MessageDisplayer.ERROR_CALCULATING_TOTAL_EXPENSES + e.getMessage());
        }
    }

    @Override
    public String getDescription() {
        return """
            Format: /total
            Returns sum of all expenses in the list, but will return 0 if the list is empty.
            Example: For a list of expenses: TransportExpense1, TransportExpense2, FoodExpense1
            /total returns (TransportExpense1 + TransportExpense2 + FoodExpense1).
            """;
    }
}
