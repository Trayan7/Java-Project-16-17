package common;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class World {
	private int rows;
	private int columns;
	private Byte[][] Map;
	private Random rgen = new Random();
	private int DIFFERENT_BIOMES = 6;
	private int RANDOM_SIZE = 5;
	
	/**
	 * Loads a map from a file. Leave empty for a random square map with RANDOM_SIZE as length and width. 
	 * 
	 * @worldName The path of the map you want to load. Leave empty for a random map.
	 */
	public World(String worldName, int columns, int rows) {
		if (worldName.equals("random")){
			Map = randomMap(columns, rows);
		} else if (worldName.equals("empty")) {
			Map = emptyMap(columns, rows);
		} else {
			try {
				Map = loadFromFile(worldName);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Generates an empty map of a given size
	 * @param columns	Width of columns of the map
	 * @param rows	Height or rows of the map
	 * @return
	 */
	private Byte[][] emptyMap(int columns, int rows){
		this.rows = rows;
		this.columns = columns;
		Byte[][] Map = new Byte[columns][rows];
		for(int i = 0; i < columns; i++){
			for(int j = 0; j < rows; j++){
				Map[i][j] = 0;
			}
		}
		return Map;
	}

	/**
	 * Takes a the path of a txt file and converts the file into a map object.
	 * Formatting: Each tile has a one byte hexadecimal biome number.
	 * <rows>
	 * <columns>
	 * <HexDec><HexDec><HexDec>...
	 * 
	 * @path The path of the map to load.
	 * @return the generated map
	 */
	private Byte[][] loadFromFile(String path) throws NumberFormatException, IOException {
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		columns = Integer.parseInt(br.readLine());
		rows = Integer.parseInt(br.readLine());
		String level = br.readLine();
		Byte[][] Map = new Byte[columns][rows];
		for (int i = 0; i < rows * columns; i++) {
			int n = i / rows;
			int m = i % rows;
			Byte current = (byte) ((Character.digit(level.charAt(2 * i), 16) << 4)
					+ Character.digit(level.charAt(2 * i + 1), 16));
			System.out.println(current + " " + n + " " + m);
			Map[n][m] = current;
		}
		br.close();
		return Map;
	}
	
	/**
	 * Creates a map with randomly set terrain
	 * 
	 * @rows The number of rows the map should have.
	 * @columns The number of columns the map should have.
	 * @return The generated map
	 */
	private Byte[][] randomMap(int columns, int rows){
		this.columns = columns;
		this.rows = rows;
		Byte[][] Map = new Byte[columns][rows];
		for(int i = 0; i < columns; i++){
			for(int j = 0; j < rows; j++){
				Map[i][j] = (byte) (rgen.nextInt(DIFFERENT_BIOMES - 1) + 1);
			}
		}
		return Map;
	}

	/**
	 * Gives you the name of the biome for any tile on the map.
	 * @param column the row of the tile
	 * @param row the column of the tile
	 * @return the biome name as a string
	 */
	public String getBiomeName(int column, int row) {
		return biomeNames(Map[column][row]);
	}
	
	/**
	 * Gives you the code of the biome for any tile on the map.
	 * @param column the column of the tile
	 * @param row the row of the tile
	 * @return the biome as byte
	 */
	public Byte getBiome(int column, int row) {
		return Map[column][row];
	}
	
	/**
	 * A collection of all biomes.
	 * @param n the number of the tile
	 * @return string of the biome name
	 */
	public String biomeNames(Byte n){
		switch (n) {
		case 0: return "empty";
		case 1: return "forest";
		case 2: return "plains";
		case 3: return "mountain";
		case 4: return "swamp";
		case 5: return "island";
		}
		return null;
	}
	
	/**
	 * Gets the height or the rows of the map
	 * @return The number of rows of the map
	 */
	public int getHeight() {
		return this.rows;
	}
	
	/**
	 * Gets the width or the columns of the map
	 * @return The number of columns of the map
	 */
	public int getWidth() {
		return this.columns;
	}
	
	/**
	 * Gets a two-dimensional representation of the terrain of the map
	 * @return A two-dimensional array of bytes representing tiles
	 */
	public Byte[][] getMap() {
		return this.Map;
	}
	
	/**
	 * Sets the biome of one spot of the map.
	 * @param column	The column of the requested tile
	 * @param row		The row of the requested tile
	 * @param n			The biome code of the tile
	 */
	public void setBiome(int column, int row, Byte n) {
		Map[column][row] = n;
	}
	
	/**
	 * Returns the area around a single spot on the map
	 * @param row		The row of the spot
	 * @param column	The column of the spot
	 * @return	A Hash Map of biome of the chosen spot and its adjacent tiles
	 */
	public HashMap<String, Byte> getArea(int column, int row) {
		HashMap<String, Byte> area = new HashMap<String, Byte>();
		if (row > 0) area.put("North", getBiome(column, row - 1));
		if (row < getHeight() - 1) area.put("South", getBiome(column, row + 1));
		if (column > 0) area.put("West", getBiome(column - 1, row));
		if (column < getWidth() - 1) area.put("East", getBiome(column + 1, row));
		area.put("Stay", getBiome(column, row));
		
		return area;
	}
}