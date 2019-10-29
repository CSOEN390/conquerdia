package ca.concordia.encs.conquerdia.controller.command;

import ca.concordia.encs.conquerdia.model.map.WorldMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the <i>editneighbor</i> command
 */
public class EditNeighborCommand extends AbstractCommand {
    public final static String COMMAND_HELP_MSG = "Invalid input! The \"editneighbor\" command must has at least one option like \"-add\" or \"-remove\".";

    @Override
    protected CommandType getCommandType() {
        return CommandType.EDIT_NEIGHBOR;
    }

    @Override
    protected String getCommandHelpMessage() {
        return COMMAND_HELP_MSG;
    }

    /**
     * @param inputCommandParts the command line parameters.
     * @return List of Command Results
     */
    @Override
    public List<String> runCommand(List<String> inputCommandParts) {
        List<String> commands = new ArrayList<>();
        Iterator<String> iterator = inputCommandParts.iterator();
        iterator.next();
        while (iterator.hasNext()) {
            String option = iterator.next();
            String firstCountryName = iterator.next();
            String secondCountryName = iterator.next();
            switch (option) {
                case ("-add"): {
                    commands.add(WorldMap.getInstance().addNeighbour(firstCountryName, secondCountryName));
                    break;
                }
                case "-remove": {
                    commands.add(WorldMap.getInstance().removeNeighbour(firstCountryName, secondCountryName));
                    break;
                }
                default: {
                    return Arrays.asList("Invalid input! " + getCommandHelpMessage());
                }
            }
        }
        return commands;

    }
}
