package ca.concordia.encs.conquerdia.engine.map.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import ca.concordia.encs.conquerdia.engine.map.Continent;
import ca.concordia.encs.conquerdia.engine.map.Country;
import ca.concordia.encs.conquerdia.engine.util.FileHelper;

class MapWriter extends MapIO implements IMapWriter {

	static final String CONTINENT_ROW_FORMAT = "%s" + TOKENS_DELIMETER + "%s" + TOKENS_DELIMETER + "%s";

	protected BufferedWriter writer;

	public boolean writeMap(String filename, ArrayList<Continent> continents) {
		String[] borderRows;
		ArrayList<CountryRow> countryRows;

		try {
			final String mapName = FileHelper.getFileNameWithoutExtension(filename);
			writer = new BufferedWriter(new FileWriter(MapIO.getMapFilePath(filename)));

			writeComments(new String[] { "RISK MAP", "Conquerdia Map Editor" });
			writer.newLine();
			
			writeResourceFiles(new String[] { "pic " + mapName + "_pic.png",
					"map " + mapName + "_map.gif",
					"crd card.cards", "prv " + mapName + ".jpg" });
			writer.newLine();
			
			writeMapName(mapName.toUpperCase());
			writer.newLine();
			
			writeContinents(continents);
			writer.newLine();

			countryRows = getAllCountryRows(continents);
			writeCountries(countryRows);
			writer.newLine();
			
			borderRows = getBorders(countryRows);
			writeBorders(borderRows);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void writeBorders(String[] borderRows) throws IOException {
		writeSection(BORDERS_SECTION_IDENTIFIER, borderRows);
	}

	private void writeCountries(ArrayList<CountryRow> countryRows) throws IOException {
		String[] rows = countryRows.stream()
				.map(CountryRow::toString)
				.toArray(String[]::new);
		writeSection(COUNTRIES_SECTION_IDENTIFIER, rows);
	}

	private void writeContinents(ArrayList<Continent> continents) throws IOException {
		String[] rows = new String[continents.size()];
		Continent continent;

		for (int i = 0; i < continents.size(); i++) {
			continent = continents.get(i);
			rows[i] = String.format(CONTINENT_ROW_FORMAT, continent.getName(), continent.getValue());
		}

		writeSection(CONTINENTS_SECTION_IDENTIFIER, rows);
	}

	private void writeResourceFiles(String[] files) throws IOException {
		writeSection(FILES_SECTION_IDENTIFIER, files);
	}

	/**
	 * Writes the name of the map to the file in name {@code mapName} Map
	 * 
	 * @param mapName The name of the map
	 * @throws IOException
	 */
	private void writeMapName(String mapName) throws IOException {
		writeLine("name " + mapName + " map");
	}

	private void writeComments(String[] comments) throws IOException {
		for (String comment : comments) {
			writeLine(COMMENT_SYMBOL + " " + comment);
		}
	}

	private void writeSection(String sectionIdentifier, String[] rows) throws IOException {
		writeLine(sectionIdentifier);
		for (String row : rows) {
			writeLine(row);
		}
	}

	private void writeLine(String line) throws IOException {
		writer.write(line);
		writer.newLine();
	}

	private ArrayList<CountryRow> getAllCountryRows(ArrayList<Continent> continents) {
		ArrayList<CountryRow> rows = new ArrayList<CountryRow>();
		Set<Country> countries;
		CountryRow countryRow;
		int countryNumber = 0;

		for (int i = 0; i < continents.size(); i++) {
			countries = continents.get(i).getCountries();

			for (Country country : countries) {
				countryNumber++;
				countryRow = countryRowFromCountry(country, countryNumber, i + 1);
				rows.add(countryRow);
			}
		}
		return rows;
	}

	private String[] getBorders(ArrayList<CountryRow> countryRows) {
		String[] borders = new String[countryRows.size()];
		CountryRow countryRow;
		HashMap<String, Integer> countryNumbers = new HashMap<>();
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < countryRows.size(); i++) {
			countryRow = countryRows.get(i);
			countryNumbers.put(countryRow.getName(), countryRow.getNumber());
		}

		for (int i = 0; i < countryRows.size(); i++) {
			countryRow = countryRows.get(i);
			builder.append(countryRow.getNumber());

			for (String countryName : countryRow.getAdjacentCountryNames()) {
				builder.append(TOKENS_DELIMETER + countryNumbers.get(countryName));
			}
			borders[i] = builder.toString();
		}
		return borders;
	}

	private CountryRow countryRowFromCountry(Country country, int number, int continentNumber) {
		CountryRow countryRow = new CountryRow(number, country.getName(), continentNumber);
		countryRow.setAdjacentCountryNames(country.getAdjacentCountries()
				.stream()
				.map(Country::getName)
				.toArray(String[]::new));

		return countryRow;
	}
}