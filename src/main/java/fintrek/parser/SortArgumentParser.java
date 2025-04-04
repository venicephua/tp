package fintrek.parser;

import fintrek.command.add.AddParseResult;
import fintrek.command.registry.CommandResult;
import fintrek.command.sort.SortParseResult;
import fintrek.misc.MessageDisplayer;
import fintrek.util.InputValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortArgumentParser implements CommandParser<ParseResult<SortParseResult>> {
    private static final String COMMAND_NAME = "sort";

    @Override
    public ParseResult<SortParseResult> parse(String input) {
        if (InputValidator.isNullOrBlank(input)) {
            return ParseResult.failure(String.format(MessageDisplayer.ARG_EMPTY_MESSAGE_TEMPLATE, COMMAND_NAME)
            );
        }
        Pattern p = Pattern.compile("^\\s*(\\w+)\\s+(\\w+)\\s*$");
        Matcher m = p.matcher(input.trim().toUpperCase());

        if (!m.matches()) {
            return ParseResult.failure(String.format(MessageDisplayer.INVALID_FORMAT_MESSAGE_TEMPLATE, COMMAND_NAME));
        }
        String sortBy = m.group(1).trim();
        String sortDir = m.group(2).trim();

        return ParseResult.success(new SortParseResult(sortBy, sortDir));
    }
}
