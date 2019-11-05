package ca.concordia.encs.conquerdia.model;

import ca.concordia.encs.conquerdia.exception.ValidationException;
import ca.concordia.encs.conquerdia.util.Observable;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class PlayersModel extends Observable {
    private static PlayersModel instance;
    private final Queue<Player> players = new LinkedList<>();
    private final Set<String> playerNames = new HashSet<>();
    private Player firstPlayer;

    /**
     * private Constructor to implementation of the Singleton Pattern
     */
    private PlayersModel() {
    }

    /**
     * This method is used for getting a single instance of the {@link PlayersModel}
     *
     * @return single instance of the {@link PlayersModel phase}
     */
    public static PlayersModel getInstance() {
        if (instance == null) {
            synchronized (PlayersModel.class) {
                if (instance == null) {
                    instance = new PlayersModel();
                }
            }
        }
        return instance;
    }

    public Queue<Player> getPlayers() {
        return players;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public void setFirstPlayer(Player firstPlayer) {
        this.firstPlayer = firstPlayer;
    }

    /**
     * @return number of players
     */
    public int getNumberOfPlayers() {
        return playerNames.size();
    }

    /**
     * Add a new player to the game if player name will not found in current player
     * name is
     *
     * @param playerName name of the plater to add
     */
    public void addPlayer(String playerName) throws ValidationException {
        if (StringUtils.isBlank(playerName))
            throw new ValidationException("Player name is not valid!");
        if (playerNames.contains(playerName))
            throw new ValidationException(String.format("Player with name \"%s\" is already exist.", playerName));
        playerNames.add(playerName);
        Player player = new Player.Builder(playerName).build();
        players.add(player);
    }

    /**
     * This Method remove a player
     *
     * @param playerName name of the player to remove
     */
    public void removePlayer(String playerName) throws ValidationException {
        if (!playerNames.contains(playerName))
            throw new ValidationException(String.format("Player with name \"%s\" is not found.", playerName));
        playerNames.remove(playerName);
        players.removeIf(player -> player.getName().equals(playerName));
    }

    /**
     * @return current player
     */
    public Player getCurrentPlayer() {
        return players.peek();
    }

    /**
     * give turn to another player based on player positions
     */
    public void giveTurnToAnotherPlayer() {
        Player player = players.poll();
        player.cleanPlayerStatus();
        players.add(player);
    }

    /**
     * Give turn to the first player
     */
    public void giveTurnToFirstPlayer() {
        while (!firstPlayer.equals(getCurrentPlayer())) {
            giveTurnToAnotherPlayer();
        }
    }

    /**
     * @return true if there is any player with unplaced army
     */
    public boolean isThereAnyUnplacedArmy() {
        return PlayersModel.getInstance().getPlayers().stream().filter(player -> player.getUnplacedArmies() > 0).count() > 0;
    }

}
