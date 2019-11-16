package ca.concordia.encs.conquerdia.controller.command;

import ca.concordia.encs.conquerdia.model.io.GameIO;
import ca.concordia.encs.conquerdia.model.map.WorldMap;

import java.util.List;

/**
 * Show map Command
 */
public class ShowMapCommand extends AbstractCommand {

    private static final String COMMAND_HELP_MSG = "";

    /**
     * {@inheritDoc}
     */
    @Override
    protected CommandType getCommandType() {
        return CommandType.SHOW_MAP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCommandHelpMessage() {
        return COMMAND_HELP_MSG;
    }

    /**
     * @param inputCommandParts the command line parameters.
     */
    @Override
    public void runCommand(List<String> inputCommandParts) {
        resultList.add(WorldMap.getInstance().showMap());
       
        GameIO stateIO = new GameIO();
        stateIO.SaveGame();
        try {
        	if(stateIO != null) {
        		stateIO = null;
        		stateIO = new GameIO();
        	}
			stateIO.LoadGame();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
