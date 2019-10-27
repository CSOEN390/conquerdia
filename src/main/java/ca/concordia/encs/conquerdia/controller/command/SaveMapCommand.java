package ca.concordia.encs.conquerdia.controller.command;

import ca.concordia.encs.conquerdia.model.GameModel;

import java.util.Arrays;
import java.util.List;

public class SaveMapCommand implements Command {

    private static final String ERR1 = "Invalid \"savemap\" command. a valid \"savemap\" command is something like \"savemap filename\".";

    /**
     * @param model             The model object of the game.
     * @param inputCommandParts the command line parameters.
     * @return List of Command Results
     */
    @Override
    public List<String> execute(GameModel model, List<String> inputCommandParts) {
        if (inputCommandParts.size() < 2)
            return Arrays.asList(ERR1);
        return Arrays.asList(model.saveMap(inputCommandParts.get(1)));
    }
}