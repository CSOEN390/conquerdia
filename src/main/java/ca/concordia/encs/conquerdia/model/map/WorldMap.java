package ca.concordia.encs.conquerdia.model.map;

import ca.concordia.encs.conquerdia.model.PhaseModel;
import ca.concordia.encs.conquerdia.model.map.io.IMapReader;
import ca.concordia.encs.conquerdia.model.map.io.IMapWriter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the world map of the game.
 */
public class WorldMap {
    private final static String NO_MAP_TO_EDIT_ERROR = "There is no map to %s. Use \"editmap filename\" command to load or create a map.";
    private static WorldMap instance;
    private final Map<String, Continent> continents = new HashMap<>();
    private final Map<String, Country> countries = new HashMap<>();
    private String fileName;
    private boolean newMapFromScratch;
    private boolean readyForEdit;
    private boolean mapLoaded;


    private WorldMap() {
    }

    /**
     * This method is used for getting a single instance of the {@link WorldMap}
     *
     * @return single instance of the {@link WorldMap map}
     */
    public static WorldMap getInstance() {
        if (instance == null) {
            synchronized (WorldMap.class) {
                if (instance == null) {
                    instance = new WorldMap();
                }
            }
        }
        return instance;
    }

    /**
     * Check there is some path between two countries or not
     *
     * @param fromCountry source country
     * @param toCountry   dest country
     * @return the result
     */
    public static boolean isTherePath(Country fromCountry, Country toCountry) {
        if (fromCountry.getAdjacentCountries().isEmpty())
            return false;
        HashSet<String> reachableCountries = new HashSet<>();
        traversCountry(fromCountry, reachableCountries);
        return reachableCountries.contains(toCountry.getName());
    }

    /**
     * @param country   country
     * @param countries list of visited countries
     */
    private static void traversCountry(Country country, HashSet<String> countries) {
        countries.add(country.getName());
        for (Country adjacent : country.getAdjacentCountries()) {
            if (!countries.contains(adjacent.getName()) && adjacent.getOwner().equals(PhaseModel.getInstance().getCurrentPlayer())) {
                traversCountry(adjacent, countries);
            }
        }
    }

    /**
     * Loading a map from an existing “domination” map file to edit or create a new map from scratch if the file does not
     * exist.
     *
     * @param fileName name of the map file file to edit
     * @return the result message
     */
    public String editMap(String fileName) {
        this.fileName = fileName;
        readyForEdit = true;
        newMapFromScratch = !openMapFile();
        return String.format("Map with file name \"%s\" is ready to edit", fileName);
    }

    /**
     * @param fileName name of the map file file to load
     * @return the result message
     */
    public String loadMap(String fileName) {
        this.fileName = fileName;
        this.readyForEdit = true;
        boolean mapIsLoaded = openMapFile();
        this.readyForEdit = false;
        if (!mapIsLoaded)
            return String.format("Map with file name \"%s\" is not found!", fileName);
        if (!checkAllMapValidationRules())
            return validateMap();
        mapLoaded = true;
        return String.format("Map with file name \"%s\" is loaded successfully.", fileName);
    }

    /**
     * @return return true when a fileName is a valid name.
     */
    private boolean validateMapFileName() {
        return true;
    }

    /**
     * @param fileName file name
     * @return return true if a map file was successfully saved.
     * return false if a map file was successfully saved
     */
    public String saveMap(String fileName) {
        if (!readyForEdit)
            return String.format(NO_MAP_TO_EDIT_ERROR, "save");
        IMapWriter.createMapWriter(this).writeMap(fileName);
        return String.format("Map with file name \"%s\" has been saved successfully", fileName);
    }

    /**
     * @return return true if load a map from an existing “domination” map file successfully. return false if the file does not exist.
     */
    private boolean openMapFile() {
        return IMapReader.createMapReader(this).readMap(fileName);
    }

    /**
     * This method add a continent to map
     *
     * @param continentName  name of the continent
     * @param continentValue value of the continent
     * @return return the result
     */
    public String addContinent(String continentName, Integer continentValue) {
        if (StringUtils.isBlank(continentName))
            return "Continent name is not valid!";
        if (continentValue == null || continentValue <= 0)
            return "Continent value is not valid!";
        if (continents.containsKey(continentName)) {
            return String.format("Continent with name \"%s\" is already exist.", continentName);
        }
        Continent continent = new Continent.Builder(continentName)
                .setValue(Integer.valueOf(continentValue))
                .build();
        continents.put(continentName, continent);
        return String.format("Continent with name \"%s\" is successfully added to map", continentName);
    }

    /**
     * @param continentName continent name
     * @return result
     */
    public String removeContinent(String continentName) {
        if (StringUtils.isBlank(continentName))
            return "Continent name is not valid!";
        if (!continents.containsKey(continentName)) {
            return String.format("Continent with name \"%s\" is not found.", continentName);
        }
        Continent toRemove = continents.get(continentName);
        for (String countryName : toRemove.getCountriesName()) {
            removeCountry(countryName);
        }
        continents.remove(continentName);
        return String.format("Continent with name \"%s\" is successfully removed from World Map", continentName);
    }

    /**
     * @param countryName   name of the country
     * @param continentName continent name
     * @return result
     */
    public String addCountry(String countryName, String continentName) {
        if (StringUtils.isBlank(countryName))
            return "Country name is not valid!";
        if (StringUtils.isBlank(continentName))
            return "Continent name is not valid!";
        if (countries.containsKey(countryName)) {
            return String.format("Country with name \"%s\" is already exist.", countryName);
        }
        if (!continents.containsKey(continentName)) {
            return String.format("Continent with name \"%s\" does not exist in World Map!", continentName);
        }
        Country country = new Country.Builder(countryName, continents.get(continentName)).build();
        countries.put(countryName, country);
        continents.get(continentName).addCountry(country);
        return String.format("Country with name \"%s\" is successfully added to \"%s\"", countryName, continentName);
    }

    /**
     * This Method add this country to the map
     *
     * @param countryName country name
     * @return return message result
     */
    public String removeCountry(String countryName) {
        if (!countries.containsKey(countryName)) {
            return String.format("Country with name \"%s\" is not found.", countryName);
        }
        Country removed = countries.remove(countryName);
        removed.getContinent().removeCountry(countryName);
        for (Country adjacentCountry : removed.getAdjacentCountries()) {
            removeNeighbour(removed, adjacentCountry);
        }
        return String.format("Country with name \"%s\" is successfully removed from World Map", countryName);
    }

    /**
     * @param firstCountryName  first country
     * @param secondCountryName second country
     * @return result
     */
    public String addNeighbour(String firstCountryName, String secondCountryName) {
        if (!countries.containsKey(firstCountryName))
            return String.format("Country with name \"%s\" is not found.", firstCountryName);
        if (!countries.containsKey(secondCountryName))
            return String.format("Country with name \"%s\" is not found.", secondCountryName);
        Country firstCountry = countries.get(firstCountryName);
        Country secondCountry = countries.get(secondCountryName);

        if (firstCountry.isAdjacentTo(secondCountryName) && secondCountry.isAdjacentTo(firstCountryName))
            return String.format("\"%s\" and \"%s\" are already adjacent countries.", firstCountryName, secondCountryName);

        firstCountry.addNeighbour(secondCountry);
        secondCountry.addNeighbour(firstCountry);

        return String.format("\"%s\" and \"%s\" are adjacent countries now.", firstCountryName, secondCountryName);
    }

    /**
     * @param firstCountryName  first country
     * @param secondCountryName second country
     * @return result
     */
    public String removeNeighbour(String firstCountryName, String secondCountryName) {
        if (!countries.containsKey(firstCountryName))
            return String.format("Country with name \"%s\" is not found.", firstCountryName);
        if (!countries.containsKey(secondCountryName))
            return String.format("Country with name \"%s\" is not found.", secondCountryName);
        Country firstCountry = countries.get(firstCountryName);
        Country secondCountry = countries.get(secondCountryName);
        if (!firstCountry.isAdjacentTo(secondCountryName) && !secondCountry.isAdjacentTo(firstCountryName))
            return String.format("\"%s\" and \"%s\" are not adjacent countries.", firstCountryName, secondCountryName);
        removeNeighbour(firstCountry, secondCountry);
        if (!countries.containsKey(firstCountryName))
            return String.format("Country with name \"%s\" is not found.", firstCountryName);

        return String.format("\"%s\" and \"%s\" are not adjacent countries now.", firstCountryName, secondCountryName);
    }

    /**
     * @param firstCountry  first country
     * @param secondCountry second country
     */
    private void removeNeighbour(Country firstCountry, Country secondCountry) {
        firstCountry.removeNeighbour(secondCountry.getName());
        secondCountry.removeNeighbour(firstCountry.getName());
    }

    /**
     * @return return all continents in map
     */
    public Set<Continent> getContinents() {
        return continents.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toSet());
    }


    /**
     * @return All counties in map
     */
    public Set<Country> getCountries() {
        return countries.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toSet());
    }

    /**
     * Find a continent by name and return it
     *
     * @param continentName name of the continent
     * @return the continent if the continent exist, return null when a continent does not exist.
     */
    public Continent getContinent(String continentName) {
        if (continents.containsKey(continentName))
            return continents.get(continentName);
        return null;
    }

    /**
     * Find a Country by name and return it
     *
     * @param countryName name of the country
     * @return the country if the country exist, return null when a country does not exist.
     */
    public Country getCountry(String countryName) {
        if (countries.containsKey(countryName))
            return countries.get(countryName);
        return null;
    }

    /**
     * @param countryName country name
     * @return return true if the map contains country
     */
    public boolean isMapContainsCountry(String countryName) {
        return countries.containsKey(countryName);
    }


    /**
     * @return return true if the map was loaded
     */
    public boolean isMapLoaded() {
        return mapLoaded;
    }

    /**
     * @return the map
     */
    public String showMap() {
        return new MapFormattor(countries).format(mapLoaded ? MapFormattor.FormatType.Detail : MapFormattor.FormatType.Default);
    }

    public boolean isReadyForEdit() {
        return readyForEdit;
    }

    private boolean connectedGraph;
    private boolean connectedSubGraph;

    /**
     * Validate the map
     *
     * @return validation result
     */
    public String validateMap() {
        boolean result = checkAllMapValidationRules();
        StringBuilder validationResult = new StringBuilder();
        if (result) {
            validationResult.append("Map is Valid:").append(System.getProperty("line.separator"));
            validationResult.append("1.Map is a connected Graph").append(System.getProperty("line.separator"));
            validationResult.append("2.Every continent is a connected subgraph in the Map")
                    .append(System.getProperty("line.separator"));
        } else {
            validationResult.append("Map is not Valid:").append(System.getProperty("line.separator"));
            if (!connectedGraph) {
                validationResult.append("worldMap is not a connected graph.")
                        .append(System.getProperty("line.separator"));
            }
            if (!connectedSubGraph) {
                validationResult.append("All the continents in the worldMap are not connected subgraphs of worldMap.")
                        .append(System.getProperty("line.separator"));
            }
        }
        return validationResult.toString();
    }

    /**
     * @return true if All validation rules are
     */
    public boolean checkAllMapValidationRules() {
        {
            Set<String> countryNames = countries.keySet();
            connectedGraph = isMapConnected(getCountry(countryNames.iterator().next()), countryNames);
        }
        {
            connectedSubGraph = true;
            for (Continent continent : getContinents()) {
                Set<String> countryNames = continent.getCountriesName();
                if (countryNames.isEmpty())
                    continue;
                connectedSubGraph &= isMapConnected(continent.getCountries().iterator().next(), countryNames);
            }

        }
        return connectedGraph && connectedSubGraph;
    }

    /**
     * @param country   coumtry
     * @param countries countries
     */
    private void traversCountry2(Country country, HashSet<String> countries) {
        countries.add(country.getName());
        for (Country adjacent : country.getAdjacentCountries()) {
            if (!countries.contains(adjacent.getName())) {
                traversCountry2(adjacent, countries);
            }
        }
    }

    /**
     * This Method verifies if the worldMap is a connected graph.
     *
     * @param firstCountry firstCountry
     * @param countryNames countryNames
     * @return result
     */
    public boolean isMapConnected(Country firstCountry, Set<String> countryNames) {
        HashSet<String> countries = new HashSet<>();
        if (countryNames.isEmpty())
            return true;
        traversCountry2(firstCountry, countries);
        boolean result = countries.containsAll(countryNames);
        return result;
    }
}
