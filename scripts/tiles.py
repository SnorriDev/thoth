import Image

PATH = "../textures/tiles/background/"
NEW_SIZE = 32

tile1 = raw_input("tile1: ")
tile2 = raw_input("tile2: ")
t1 = Image.open(PATH + tile1 + ".png")
t2 = Image.open(PATH + tile2 + ".png")
t1.thumbnail((NEW_SIZE, NEW_SIZE), Image.ANTIALIAS)
t2.thumbnail((NEW_SIZE, NEW_SIZE), Image.ANTIALIAS)

new = Image.new("RGBA", (64, 64), (255, 255, 255, 255))
for x in range(0, 64, NEW_SIZE):
	for y in range(0, 64, NEW_SIZE):
		new.paste(t1 if (x + y) / NEW_SIZE % 2 == 0 else t2, (x, y))

new.save(tile1 + "-" + tile2 + ".png")