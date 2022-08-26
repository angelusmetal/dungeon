package com.dungeon.game.level.generator;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.dungeon.engine.render.Material;
import com.dungeon.engine.resource.Resources;
import com.dungeon.game.level.Tile;
import com.dungeon.game.level.TileType;
import com.dungeon.game.tileset.Tileset;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class TilesetSolver {
	private final Function<Tileset, Animation<Material>>[] animations;
	public TilesetSolver() {
		animations = new Function[256];
		animations[0b00000000] = Tileset::none;
		animations[0b00000001] = Tileset::cornerB;
		animations[0b00000010] = Tileset::up;
		animations[0b00000011] = Tileset::up;
		animations[0b00000100] = Tileset::cornerC;
		animations[0b00000101] = Tileset::cornerBC;
		animations[0b00000110] = Tileset::up;
		animations[0b00000111] = Tileset::up;
		animations[0b00001000] = Tileset::left;
		animations[0b00001001] = Tileset::left;
		animations[0b00001010] = Tileset::upLeft;
		animations[0b00001011] = Tileset::upLeft;
		animations[0b00001100] = Tileset::cornerCLeft;
		animations[0b00001101] = Tileset::cornerCLeft;
		animations[0b00001110] = Tileset::upLeft;
		animations[0b00001111] = Tileset::upLeft;
		animations[0b00010000] = Tileset::right;
		animations[0b00010001] = Tileset::cornerBRight;
		animations[0b00010010] = Tileset::upRight;
		animations[0b00010011] = Tileset::upRight;
		animations[0b00010100] = Tileset::right;
		animations[0b00010101] = Tileset::cornerBRight;
		animations[0b00010110] = Tileset::upRight;
		animations[0b00010111] = Tileset::upRight;
		animations[0b00011000] = Tileset::leftRight;
		animations[0b00011001] = Tileset::leftRight;
		animations[0b00011010] = Tileset::upLeftRight;
		animations[0b00011011] = Tileset::upLeftRight;
		animations[0b00011100] = Tileset::leftRight;
		animations[0b00011101] = Tileset::leftRight;
		animations[0b00011110] = Tileset::upLeftRight;
		animations[0b00011111] = Tileset::upLeftRight;
		animations[0b00100000] = Tileset::cornerA;
		animations[0b00100001] = Tileset::cornerAB;
		animations[0b00100010] = Tileset::cornerAUp;
		animations[0b00100011] = Tileset::cornerAUp;
		animations[0b00100100] = Tileset::cornerAC;
		animations[0b00100101] = Tileset::cornerABC;
		animations[0b00100110] = Tileset::cornerAUp;
		animations[0b00100111] = Tileset::cornerAUp;
		animations[0b00101000] = Tileset::left;
		animations[0b00101001] = Tileset::left;
		animations[0b00101010] = Tileset::upLeft;
		animations[0b00101011] = Tileset::upLeft;
		animations[0b00101100] = Tileset::cornerCLeft;
		animations[0b00101101] = Tileset::cornerCLeft;
		animations[0b00101110] = Tileset::upLeft;
		animations[0b00101111] = Tileset::upLeft;
		animations[0b00110000] = Tileset::cornerARight;
		animations[0b00110001] = Tileset::cornerABRight;
		animations[0b00110010] = Tileset::cornerAUpRight;
		animations[0b00110011] = Tileset::cornerAUpRight;
		animations[0b00110100] = Tileset::cornerARight;
		animations[0b00110101] = Tileset::cornerABRight;
		animations[0b00110110] = Tileset::cornerAUpRight;
		animations[0b00110111] = Tileset::cornerAUpRight;
		animations[0b00111000] = Tileset::leftRight;
		animations[0b00111001] = Tileset::leftRight;
		animations[0b00111010] = Tileset::upLeftRight;
		animations[0b00111011] = Tileset::upLeftRight;
		animations[0b00111100] = Tileset::leftRight;
		animations[0b00111101] = Tileset::leftRight;
		animations[0b00111110] = Tileset::upLeftRight;
		animations[0b00111111] = Tileset::upLeftRight;
		animations[0b01000000] = Tileset::down;
		animations[0b01000001] = Tileset::cornerBDown;
		animations[0b01000010] = Tileset::upDown;
		animations[0b01000011] = Tileset::upDown;
		animations[0b01000100] = Tileset::cornerCDown;
		animations[0b01000101] = Tileset::cornerBCDown;
		animations[0b01000110] = Tileset::upDown;
		animations[0b01000111] = Tileset::upDown;
		animations[0b01001000] = Tileset::downLeft;
		animations[0b01001001] = Tileset::downLeft;
		animations[0b01001010] = Tileset::upDownLeft;
		animations[0b01001011] = Tileset::upDownLeft;
		animations[0b01001100] = Tileset::cornerCDownLeft;
		animations[0b01001101] = Tileset::cornerCDownLeft;
		animations[0b01001110] = Tileset::upDownLeft;
		animations[0b01001111] = Tileset::upDownLeft;
		animations[0b01010000] = Tileset::downRight;
		animations[0b01010001] = Tileset::cornerBDownRight;
		animations[0b01010010] = Tileset::upDownRight;
		animations[0b01010011] = Tileset::upDownRight;
		animations[0b01010100] = Tileset::downRight;
		animations[0b01010101] = Tileset::cornerBDownRight;
		animations[0b01010110] = Tileset::upDownRight;
		animations[0b01010111] = Tileset::upDownRight;
		animations[0b01011000] = Tileset::downLeftRight;
		animations[0b01011001] = Tileset::downLeftRight;
		animations[0b01011010] = Tileset::all;
		animations[0b01011011] = Tileset::all;
		animations[0b01011100] = Tileset::downLeftRight;
		animations[0b01011101] = Tileset::downLeftRight;
		animations[0b01011110] = Tileset::all;
		animations[0b01011111] = Tileset::all;
		animations[0b01100000] = Tileset::down;
		animations[0b01100001] = Tileset::cornerBDown;
		animations[0b01100010] = Tileset::upDown;
		animations[0b01100011] = Tileset::upDown;
		animations[0b01100100] = Tileset::cornerCDown;
		animations[0b01100101] = Tileset::cornerBCDown;
		animations[0b01100110] = Tileset::upDown;
		animations[0b01100111] = Tileset::upDown;
		animations[0b01101000] = Tileset::downLeft;
		animations[0b01101001] = Tileset::downLeft;
		animations[0b01101010] = Tileset::upDownLeft;
		animations[0b01101011] = Tileset::upDownLeft;
		animations[0b01101100] = Tileset::cornerCDownLeft;
		animations[0b01101101] = Tileset::cornerCDownLeft;
		animations[0b01101110] = Tileset::upDownLeft;
		animations[0b01101111] = Tileset::upDownLeft;
		animations[0b01110000] = Tileset::downRight;
		animations[0b01110001] = Tileset::cornerBDownRight;
		animations[0b01110010] = Tileset::upDownRight;
		animations[0b01110011] = Tileset::upDownRight;
		animations[0b01110100] = Tileset::downRight;
		animations[0b01110101] = Tileset::cornerBDownRight;
		animations[0b01110110] = Tileset::upDownRight;
		animations[0b01110111] = Tileset::upDownRight;
		animations[0b01111000] = Tileset::downLeftRight;
		animations[0b01111001] = Tileset::downLeftRight;
		animations[0b01111010] = Tileset::all;
		animations[0b01111011] = Tileset::all;
		animations[0b01111100] = Tileset::downLeftRight;
		animations[0b01111101] = Tileset::downLeftRight;
		animations[0b01111110] = Tileset::all;
		animations[0b01111111] = Tileset::all;
		animations[0b10000000] = Tileset::cornerD;
		animations[0b10000001] = Tileset::cornerBD;
		animations[0b10000010] = Tileset::cornerDUp;
		animations[0b10000011] = Tileset::cornerDUp;
		animations[0b10000100] = Tileset::cornerCD;
		animations[0b10000101] = Tileset::cornerBCD;
		animations[0b10000110] = Tileset::cornerDUp;
		animations[0b10000111] = Tileset::cornerDUp;
		animations[0b10001000] = Tileset::cornerDLeft;
		animations[0b10001001] = Tileset::cornerDLeft;
		animations[0b10001010] = Tileset::cornerDUpLeft;
		animations[0b10001011] = Tileset::cornerDUpLeft;
		animations[0b10001100] = Tileset::cornerCDLeft;
		animations[0b10001101] = Tileset::cornerCDLeft;
		animations[0b10001110] = Tileset::cornerDUpLeft;
		animations[0b10001111] = Tileset::cornerDUpLeft;
		animations[0b10010000] = Tileset::right;
		animations[0b10010001] = Tileset::cornerBRight;
		animations[0b10010010] = Tileset::upRight;
		animations[0b10010011] = Tileset::upRight;
		animations[0b10010100] = Tileset::right;
		animations[0b10010101] = Tileset::cornerBRight;
		animations[0b10010110] = Tileset::upRight;
		animations[0b10010111] = Tileset::upRight;
		animations[0b10011000] = Tileset::leftRight;
		animations[0b10011001] = Tileset::leftRight;
		animations[0b10011010] = Tileset::upLeftRight;
		animations[0b10011011] = Tileset::upLeftRight;
		animations[0b10011100] = Tileset::leftRight;
		animations[0b10011101] = Tileset::leftRight;
		animations[0b10011110] = Tileset::upLeftRight;
		animations[0b10011111] = Tileset::upLeftRight;
		animations[0b10100000] = Tileset::cornerAD;
		animations[0b10100001] = Tileset::cornerABD;
		animations[0b10100010] = Tileset::cornerADUp;
		animations[0b10100011] = Tileset::cornerADUp;
		animations[0b10100100] = Tileset::cornerACD;
		animations[0b10100101] = Tileset::cornerABCD;
		animations[0b10100110] = Tileset::cornerADUp;
		animations[0b10100111] = Tileset::cornerADUp;
		animations[0b10101000] = Tileset::cornerDLeft;
		animations[0b10101001] = Tileset::cornerDLeft;
		animations[0b10101010] = Tileset::cornerDUpLeft;
		animations[0b10101011] = Tileset::cornerDUpLeft;
		animations[0b10101100] = Tileset::cornerCDLeft;
		animations[0b10101101] = Tileset::cornerCDLeft;
		animations[0b10101110] = Tileset::cornerDUpLeft;
		animations[0b10101111] = Tileset::cornerDUpLeft;
		animations[0b10110000] = Tileset::cornerARight;
		animations[0b10110001] = Tileset::cornerABRight;
		animations[0b10110010] = Tileset::cornerAUpRight;
		animations[0b10110011] = Tileset::cornerAUpRight;
		animations[0b10110100] = Tileset::cornerARight;
		animations[0b10110101] = Tileset::cornerABRight;
		animations[0b10110110] = Tileset::cornerAUpRight;
		animations[0b10110111] = Tileset::cornerAUpRight;
		animations[0b10111000] = Tileset::leftRight;
		animations[0b10111001] = Tileset::leftRight;
		animations[0b10111010] = Tileset::upLeftRight;
		animations[0b10111011] = Tileset::upLeftRight;
		animations[0b10111100] = Tileset::leftRight;
		animations[0b10111101] = Tileset::leftRight;
		animations[0b10111110] = Tileset::upLeftRight;
		animations[0b10111111] = Tileset::upLeftRight;
		animations[0b11000000] = Tileset::down;
		animations[0b11000001] = Tileset::cornerBDown;
		animations[0b11000010] = Tileset::upDown;
		animations[0b11000011] = Tileset::upDown;
		animations[0b11000100] = Tileset::cornerCDown;
		animations[0b11000101] = Tileset::cornerBCDown;
		animations[0b11000110] = Tileset::upDown;
		animations[0b11000111] = Tileset::upDown;
		animations[0b11001000] = Tileset::downLeft;
		animations[0b11001001] = Tileset::downLeft;
		animations[0b11001010] = Tileset::upDownLeft;
		animations[0b11001011] = Tileset::upDownLeft;
		animations[0b11001100] = Tileset::cornerCDownLeft;
		animations[0b11001101] = Tileset::cornerCDownLeft;
		animations[0b11001110] = Tileset::upDownLeft;
		animations[0b11001111] = Tileset::upDownLeft;
		animations[0b11010000] = Tileset::downRight;
		animations[0b11010001] = Tileset::cornerBDownRight;
		animations[0b11010010] = Tileset::upDownRight;
		animations[0b11010011] = Tileset::upDownRight;
		animations[0b11010100] = Tileset::downRight;
		animations[0b11010101] = Tileset::cornerBDownRight;
		animations[0b11010110] = Tileset::upDownRight;
		animations[0b11010111] = Tileset::upDownRight;
		animations[0b11011000] = Tileset::downLeftRight;
		animations[0b11011001] = Tileset::downLeftRight;
		animations[0b11011010] = Tileset::all;
		animations[0b11011011] = Tileset::all;
		animations[0b11011100] = Tileset::downLeftRight;
		animations[0b11011101] = Tileset::downLeftRight;
		animations[0b11011110] = Tileset::all;
		animations[0b11011111] = Tileset::all;
		animations[0b11100000] = Tileset::down;
		animations[0b11100001] = Tileset::cornerBDown;
		animations[0b11100010] = Tileset::upDown;
		animations[0b11100011] = Tileset::upDown;
		animations[0b11100100] = Tileset::cornerCDown;
		animations[0b11100101] = Tileset::cornerBCDown;
		animations[0b11100110] = Tileset::upDown;
		animations[0b11100111] = Tileset::upDown;
		animations[0b11101000] = Tileset::downLeft;
		animations[0b11101001] = Tileset::downLeft;
		animations[0b11101010] = Tileset::upDownLeft;
		animations[0b11101011] = Tileset::upDownLeft;
		animations[0b11101100] = Tileset::cornerCDownLeft;
		animations[0b11101101] = Tileset::cornerCDownLeft;
		animations[0b11101110] = Tileset::upDownLeft;
		animations[0b11101111] = Tileset::upDownLeft;
		animations[0b11110000] = Tileset::downRight;
		animations[0b11110001] = Tileset::cornerBDownRight;
		animations[0b11110010] = Tileset::upDownRight;
		animations[0b11110011] = Tileset::upDownRight;
		animations[0b11110100] = Tileset::downRight;
		animations[0b11110101] = Tileset::cornerBDownRight;
		animations[0b11110110] = Tileset::upDownRight;
		animations[0b11110111] = Tileset::upDownRight;
		animations[0b11111000] = Tileset::downLeftRight;
		animations[0b11111001] = Tileset::downLeftRight;
		animations[0b11111010] = Tileset::all;
		animations[0b11111011] = Tileset::all;
		animations[0b11111100] = Tileset::downLeftRight;
		animations[0b11111101] = Tileset::downLeftRight;
		animations[0b11111110] = Tileset::all;
		animations[0b11111111] = Tileset::all;
	}
	public Animation<Material> getTile(TileType[][] tiles, Predicate<TileType> predicate, int x, int y, int width, int height, Tileset tileset) {
		if (tiles[x][y] == TileType.FLOOR) {
			return Resources.animations.get("invisible");
		}
		int freeUpLeft = y < height - 1 && x > 0 && predicate.test(tiles[x-1][y+1]) ? 0b00000001 : 0;
		int freeUp = y < height - 1 && predicate.test(tiles[x][y+1]) ? 0b00000010 : 0;
		int freeUpRight = y < height - 1 && x < width - 1 && predicate.test(tiles[x+1][y+1]) ? 0b00000100 : 0;
		int freeLeft = x > 0 && predicate.test(tiles[x-1][y]) ? 0b00001000 : 0;
		int freeRight = x < width - 1 && predicate.test(tiles[x+1][y]) ? 0b00010000 : 0;
		int freeDownLeft = y > 0 && x > 0 && predicate.test(tiles[x-1][y-1]) ? 0b00100000 : 0;
		int freeDown = y > 0 && predicate.test(tiles[x][y-1]) ? 0b01000000 : 0;
		int freeDownRight = y > 0 && x < width - 1 && predicate.test(tiles[x+1][y-1]) ? 0b10000000 : 0;
		int index = freeUpLeft | freeUp | freeUpRight | freeLeft | freeRight | freeDownLeft | freeDown | freeDownRight;
		return animations[index].apply(tileset);
	}
	public Animation<Material> getTile(Tile[][] tiles, BiFunction<Tile, Tile, Boolean> biFunction, int x, int y, int width, int height, Function<Tile,Tileset> tilesetFunction) {
		Tile tile = tiles[x][y];
		int freeUpLeft = y < height - 1 && x > 0 && biFunction.apply(tile, tiles[x-1][y+1]) ? 0b00000001 : 0;
		int freeUp = y < height - 1 && biFunction.apply(tile, tiles[x][y+1]) ? 0b00000010 : 0;
		int freeUpRight = y < height - 1 && x < width - 1 && biFunction.apply(tile, tiles[x+1][y+1]) ? 0b00000100 : 0;
		int freeLeft = x > 0 && biFunction.apply(tile, tiles[x-1][y]) ? 0b00001000 : 0;
		int freeRight = x < width - 1 && biFunction.apply(tile, tiles[x+1][y]) ? 0b00010000 : 0;
		int freeDownLeft = y > 0 && x > 0 && biFunction.apply(tile, tiles[x-1][y-1]) ? 0b00100000 : 0;
		int freeDown = y > 0 && biFunction.apply(tile, tiles[x][y-1]) ? 0b01000000 : 0;
		int freeDownRight = y > 0 && x < width - 1 && biFunction.apply(tile, tiles[x+1][y-1]) ? 0b10000000 : 0;
		int index = freeUpLeft | freeUp | freeUpRight | freeLeft | freeRight | freeDownLeft | freeDown | freeDownRight;
		return animations[index].apply(tilesetFunction.apply(tile));
	}
}
