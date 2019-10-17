package ca.concordia.encs.conquerdia.engine.command.factory;

import ca.concordia.encs.conquerdia.engine.ConquerdiaModel;
import ca.concordia.encs.conquerdia.engine.command.Command;

import java.util.Arrays;
import java.util.List;

/**
 * Show map Command
 */
public class ShowMapCommand implements Command {

    /**
     * @param model             The model object of the game.
     * @param inputCommandParts the command line parameters.
     * @return List of Command Results
     */
    @Override
    public List<String> execute(ConquerdiaModel model, List<String> inputCommandParts) {
        return Arrays.asList(model.getWorldMap().showMap());
    }

}
