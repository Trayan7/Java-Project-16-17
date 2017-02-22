import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class World {
	int zeilen;
	int spalten;
	Byte[][] Map;
	static Random rgen = new Random();
	static int DIFFERENT_BIOMES = 4;
	int RANDOM_SIZE = 10;
	
	/**
	 * Loads a map from a file. Leave empty for a random square map with RANDOM_SIZE as length and width. 
	 * 
	 * @worldName The path of the map you want to load. Leave empty for a random map.
	 */
	public World(String worldName) {
		if(worldName.length() == 0){
			Map = randomMap(RANDOM_SIZE,RANDOM_SIZE);
			return this;
		}else{
			try {
				Map = loadFromFile(worldName);
				return this;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
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
	static Byte[][] loadFromFile(String path) throws NumberFormatException, IOException {
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		int zeilen = Integer.parseInt(br.readLine());
		int spalten = Integer.parseInt(br.readLine());
		String level = br.readLine();
		Byte[][] Map = new Byte[zeilen][spalten];
		for (int i = 0; i < spalten * zeilen; i++) {
			int n = i / spalten;
			int m = i % spalten;
			Byte current = (byte) ((Character.digit(level.charAt(2 * i), 16) << 4)
					+ Character.digit(level.charAt(2 * i + 1), 16));
			System.out.println(current + " " + n + " " + m);
			Map[n][m] = current;
		}
		br.close();
		return Map;
	}
	
	/**
	 * Creates a new random map.
	 * 
	 * @zeilen The number of rows the map should have.
	 * @spalten The number of columns the map should have.
	 * @return the generated map
	 */
	static Byte[][] randomMap(int zeilen, int spalten){
		Byte[][] Map = new Byte[zeilen][spalten];
		for(int i = 0; i < zeilen; i++){
			for(int j = 0; j < spalten; j++){
				Map[i][j] = (byte) rgen.nextInt(DIFFERENT_BIOMES);
			}
		}
		return Map;
	}

	/**
	 * Gives you the name of the biome for any tile on the map.
	 * @param m the row of the tile
	 * @param n the column of the tile
	 * @return the biome name as a string
	 */
	public String getBiome(int m, int n) {
		return biomeNames(Map[m][n]);
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
		case 2: return "desert";
		case 3: return "mountains";
		}
		return null;
	}
}
