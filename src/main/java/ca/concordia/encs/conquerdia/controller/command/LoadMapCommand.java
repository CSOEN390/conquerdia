package ca.concordia.encs.conquerdia.controller.command;

import ca.concordia.encs.conquerdia.model.GameModel;

import java.util.Arrays;
import java.util.List;

/**
 * Implementation of loadmap command
 */
public class LoadMapCommand implements Command {

    private static final String EDIT_MAP_ERR1 = "Invalid loadmap command. a valid loadmap command is something like \"loadmap filename\".";

    /**
     * @param model             The model object of the game.
     * @param inputCommandParts the command line parameters.
     * @return List of Command Results
     */
    @Override
    public List<String> execute(GameModel model, List<String> inputCommandParts) {
        if (inputCommandParts.size() < 2)
            return Arrays.asList(EDIT_MAP_ERR1);
        return Arrays.asList(model.loadMap(inputCommandParts.get(1)));
    }
}