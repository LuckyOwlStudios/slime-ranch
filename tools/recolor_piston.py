"""Makes the Bouncy Piston textures from the vanilla piston textures.

The stone parts (low saturation pixels) are tinted blue; the wooden platform stays as it is, so the
vanilla piston head, which can't be re-textured per piston, still matches.

Usage: python3 recolor_piston.py <vanilla block texture dir> <output dir>
"""
import colorsys
import sys

from PIL import Image

HUE = 215 / 360
SATURATION = 0.5


def recolor(name, out_name):
    src = Image.open(f"{sys.argv[1]}/{name}.png").convert("RGBA")
    out = Image.new("RGBA", src.size)
    for x in range(src.width):
        for y in range(src.height):
            r, g, b, a = src.getpixel((x, y))
            h, s, v = colorsys.rgb_to_hsv(r / 255, g / 255, b / 255)
            if s < 0.25:  # stone, not wood
                nr, ng, nb = colorsys.hsv_to_rgb(HUE, SATURATION, v)
                r, g, b = round(nr * 255), round(ng * 255), round(nb * 255)
            out.putpixel((x, y), (r, g, b, a))
    out.save(f"{sys.argv[2]}/{out_name}.png")


for n in ("piston_side", "piston_bottom", "piston_inner"):
    recolor(n, "bouncy_" + n)
