package ca.concordia.encs.conquerdia.engine.command.factory;

import ca.concordia.encs.conquerdia.engine.ConquerdiaModel;
import ca.concordia.encs.conquerdia.engine.command.Command;
import ca.concordia.encs.conquerdia.engine.command.CommandFactory;

import java.util.Arrays;
import java.util.List;

public class ValidateMapCommandFactory implements CommandFactory {
    @Override
    public List<Command> getCommands(ConquerdiaModel model, List<String> inputCommandParts) {
        return Arrays.asList(model.getWorldMap()::validateMap);
    }

}