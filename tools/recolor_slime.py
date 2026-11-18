"""Makes the Bouncy Slime texture: the vanilla slime texture with its green hue shifted to blue.

Usage: python3 recolor_slime.py <vanilla slime.png> <output png>
"""
import colorsys
import sys

from PIL import Image

TARGET_HUE = 215 / 360  # blue; vanilla slime green is about 120 degrees

src = Image.open(sys.argv[1]).convert("RGBA")
out = Image.new("RGBA", src.size)
for x in range(src.width):
    for y in range(src.height):
        r, g, b, a = src.getpixel((x, y))
        h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
        # Grays (the eyes and mouth) have no hue to shift, so only recolor pixels that have some
        if s > 0.08:
            h = TARGET_HUE
        nr, ng, nb = colorsys.hsv_to_rgb(h, s, v)
        out.putpixel((x, y), (round(nr * 255), round(ng * 255), round(nb * 255), a))
out.save(sys.argv[2])
