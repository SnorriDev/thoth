import Image, sys

MARGIN = 230

name = sys.argv[1]

img = Image.open(name)
img = img.convert("RGBA")
datas = img.getdata()

newData = []
for item in datas:
    if item[0] > MARGIN and item[1] > MARGIN and item[2] > MARGIN:
        newData.append((255, 255, 255, 0))
    else:
        newData.append(item)

img.putdata(newData)
img.save(name[:-4] + "-t.png", "PNG")