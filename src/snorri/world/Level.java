package snorri.world;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import snorri.main.Main;



public class Level {
	
	private Tile[][]	map;
	private Vector		dim;
						
	//TODO: load from file, not empty grid with parameters
	//not that indexing conventions are Cartesian, not matrix-based
	
	public Level(int width, int height) {
		map = new Tile[width][height];
		dim = new Vector(width, height);
	}
	
	public void setTile(int x, int y, Tile t) {
		map[x / Tile.WIDTH][y / Tile.WIDTH] = t;
	}
	
	public void setTileRaw(int x, int y, Tile t) {
		map[x][y] = t;
	}
	
	public Tile getTile(int x, int y) {
		return map[x / Tile.WIDTH][y / Tile.WIDTH];
	}
	
	public Tile getTile(Vector v) {
		return getTile(v.getX(), v.getY());
	}
	
	public Vector getDimensions() {
		return dim;
	}
	
	public void load(String fileName) {
		try {
			byte[] b = new byte[4];
			
			FileInputStream is = new FileInputStream(fileName);
			
			is.read(b);
			int width = ByteBuffer.wrap(b).getInt();
			is.read(b);
			int height = ByteBuffer.wrap(b).getInt();
			
			for (int i = 0; i < height; i++ ) {
				byte[] b2 = new byte[width];
				is.read(b2);
			}
			
			is.close();
		}
		catch (FileNotFoundException ex) {
			Main.error("Unable to open file '" + fileName + "'");
		}
		catch (IOException ex) {
			Main.error("Error reading file '" + fileName + "'");
		}
	}
	
	public void save(String fileName) {
		try {
			FileOutputStream os = new FileOutputStream(fileName);
			ByteBuffer b1 = ByteBuffer.allocate(4);
			
			byte[] buffer = b1.putInt(dim.getX()).array();
			os.write(buffer);
			buffer = b1.putInt(dim.getY()).array();
			os.write(buffer);
			
			for (int i = 0; i < dim.getX(); i++ ) {
				for (int j = 0; j < dim.getY(); j++ ) {
					os.write(((byte) map[i][j].getId()) & 0xFF);
				}
			}
			
			os.close();
		}
		catch (IOException ex) {
			Main.error("Error writing file '" + fileName + "'");
		}
	}
}
