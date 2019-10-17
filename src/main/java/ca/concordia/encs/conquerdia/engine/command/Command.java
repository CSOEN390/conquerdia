package ca.concordia.encs.conquerdia.engine.command;

import ca.concordia.encs.conquerdia.engine.ConquerdiaModel;

import java.util.List;

/**
 * Command Interface
 */
public interface Command {
    /**
     * Factory method for the commands based on user input
     *
     * @param inputCommandParts user input
     * @return the command type
     */
    static CommandType createCommand(String[] inputCommandParts) {
        CommandType commandType = CommandType.findCommandTypeByName(inputCommandParts[0]);
        return commandType;
    }


    /**
     * @param model             The model object of the game.
     * @param inputCommandParts the command line parameters.
     * @return List of Command Results
     */
    List<String> execute(ConquerdiaModel model, List<String> inputCommandParts);

}
