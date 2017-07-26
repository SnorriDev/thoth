import sys, os
from PIL import Image

for dirpath, _, filenames in os.walk(sys.argv[1]):
	print filenames
	for filename in filenames:
		if not filename.endswith(".png"):
			continue
		filepath = os.path.join(dirpath, filename)
		Image.open(filepath).transpose(Image.FLIP_LEFT_RIGHT).save(filepath)