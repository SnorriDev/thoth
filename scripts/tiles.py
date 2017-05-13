import Image

IN_PATH = "baseTiles/"
OUT_PATH = "mixedTiles/"
NEW_SIZE = 32
NUM_TILES = 13

int2texture = lambda z: "floor%02d" % (z,)

tiles = [Image.open(IN_PATH + int2texture(i) + ".png") for i in range(NUM_TILES)]
for tile in tiles:
	tile.thumbnail((NEW_SIZE, NEW_SIZE), Image.ANTIALIAS)

for i in range(NUM_TILES):
	for j in range(NUM_TILES):

		new = Image.new("RGBA", (64, 64), (255, 255, 255, 255))
		for x in range(0, 64, NEW_SIZE):
			for y in range(0, 64, NEW_SIZE):
				new.paste(tiles[i if (x + y) / NEW_SIZE % 2 == 0 else j], (x, y))

		new.save(OUT_PATH + int2texture(i) + "-" + int2texture(j) + ".png")