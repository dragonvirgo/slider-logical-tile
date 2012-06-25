package shima.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

class Tile {
	int serial;	// start with zero
	Point lp;	// logical position
	private int distance = 0;
	Bitmap bitmap;
	Tile (int s, Point p) {
		serial = s;
		lp = p;
	}
}

public class Tiles {
	private static final String TAG = Tiles.class.getSimpleName();
	private static final float DISTANCE_FACTOR	= 2.0F;
	private static final float SHUFFLE_FACTOR	= 2.0F;

	float width;
	float height;
	int rows;
	int cols;
	Tile[][] tiles;
	Tile hole;
	int distance = 0;
	List<Point> footprints = new ArrayList<Point>();
	Bitmap bitmap;
	Random random = new Random();
	Tiles(int r, int c) {
		rows = r; cols = c;
		tiles =  new Tile[rows][cols];
		initializeTiles(tiles);
	}
	private void initializeTiles(Tile[][] ts) {
		footprints.clear();
		int holeSerial = random.nextInt(rows*cols);
		int serial = 0;
		for (int i=0; i<rows; i++) {
			for (int j=0; j<cols; j++) {
				Tile tile = new Tile(serial, new Point(j, i));
				if (serial == holeSerial) hole = tile;
				ts[i][j] = tile;
				serial++;
			}
		}
	}
	private Point initialPosition(int serial) { return new Point(serial%cols, serial/cols); }
	private int distance(Tile tile) {
		Point ip = initialPosition(tile.serial);
		return Math.abs(tile.lp.x - ip.x) + Math.abs(tile.lp.y - ip.y);
	}
	private Tile slideTileAtRandom(Tile previous) {
		Tile[] nominees = new Tile[4];
		int counter = 0;
		Point h = hole.lp;
		// 移動させるタイルの候補を選定
		if (h.y > 0) {		// upper tile
			nominees[counter] = tiles[h.y-1][h.x];
			if (nominees[counter] != previous) counter++;
		}
		if (h.x < cols-1) {	// right tile
			nominees[counter] = tiles[h.y][h.x+1];
			if (nominees[counter] != previous) counter++;
		}
		if (h.y < rows-1) {	// lower tile
			nominees[counter] = tiles[h.y+1][h.x];
			if (nominees[counter] != previous) counter++;
		}
		if (h.x > 0) {		// left tile
			nominees[counter] = tiles[h.y][h.x-1];
			if (nominees[counter] != previous) counter++;
		}
		// 移動させるタイルを決定
		Tile target = nominees[random.nextInt(counter)];
		distance -= distance(target);	// 現状の離散度を減算
		// タイルを移動する
		Point t = target.lp;
		Tile tmp = tiles[t.y][t.x];
		tiles[t.y][t.x] = tiles[h.y][h.x];
		tiles[h.y][h.x] = tmp;
		// 論理位置を付け替える
		target.lp = hole.lp;
		hole.lp = t;
		footprints.add(t);				// 棋譜に追加
		distance += distance(target);	// 新しい離散度を加算
		return target;
	}
	Tile shuffle() {
		int total = (int)(rows * cols * DISTANCE_FACTOR);
		int maxSlide = (int)(total * SHUFFLE_FACTOR);
		return shuffle(total, maxSlide);
	}
	private Tile shuffle(int totalDistance, int maxSlide) {
		Log.d(TAG, "totalDistance=" + totalDistance + ", maxSlide=" + maxSlide);
		if (distance != 0) initializeTiles(tiles);
		Tile previous = null;
		for (int i=0; i<maxSlide; i++) {
			previous = slideTileAtRandom(previous);
			if (distance >= totalDistance) break;
			print();
		}
		return null;
	}
	void print() {
		Log.d(TAG, "- footprints=" + footprints.size() + ", distance=" + distance);
		for (int i=0; i<rows; i++) {
			StringBuffer buffer = new StringBuffer();
			for (int j=0; j<cols; j++) {
				Tile tile = tiles[i][j];
				if (tile == hole)
					buffer.append("XX");
				else
					buffer.append(String.format("%02d", tile.serial));
				if (j < cols - 1) buffer.append("-");
			}
			Log.d(TAG, buffer.toString());
		}
	}
}