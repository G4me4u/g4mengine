package com.g4mesoft.graphic;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GColor {

	/* REDS */
	public static final GColor INDIAN_RED             = new GColor(0xCD5C5C);
	public static final GColor LIGHT_CORAL            = new GColor(0xF08080);
	public static final GColor SALMON                 = new GColor(0xFA8072);
	public static final GColor DARK_SALMON            = new GColor(0xE9967A);
	public static final GColor LIGHT_SALMON           = new GColor(0xFFA07A);
	public static final GColor CRIMSON_SALMON         = new GColor(0xDC143C);
	public static final GColor RED                    = new GColor(0xFF0000);
	public static final GColor FIRE_BRICK             = new GColor(0xB22222);
	public static final GColor DARK_RED               = new GColor(0x8B0000);

	/* PINKS */
	public static final GColor PINK                   = new GColor(0xFFC0CB);
	public static final GColor LIGHT_PINK             = new GColor(0xFFB6C1);
	public static final GColor HOT_PINK               = new GColor(0xFF69B4);
	public static final GColor DEEP_PINK              = new GColor(0xFF1493);
	public static final GColor MEDIUM_VIOLET_RED      = new GColor(0xC71585);
	public static final GColor PALE_VIOLET_RED        = new GColor(0xDB7093);
	
	/* ORANGES */
	public static final GColor CORAL                  = new GColor(0xFF7F50);
	public static final GColor TOMATO                 = new GColor(0xFF6347);
	public static final GColor ORANGE_RED             = new GColor(0xFF4500);
	public static final GColor DARK_ORANGE            = new GColor(0xFF8C00);
	public static final GColor ORANGE                 = new GColor(0xFFA500);
	
	/* YELLOWS */
	public static final GColor GOLD                   = new GColor(0xFFD700);
	public static final GColor YELLOW                 = new GColor(0xFFFF00);
	public static final GColor LIGHT_YELLOW           = new GColor(0xFFFFE0);
	public static final GColor LEMON_CHIFFON          = new GColor(0xFFFACD);
	public static final GColor LIGHT_GOLDENROD_YELLOW = new GColor(0xFAFAD2);
	public static final GColor PAPAYA_WHIP            = new GColor(0xFFEFD5);
	public static final GColor MOCCASIN               = new GColor(0xFFE4B5);
	public static final GColor PEACH_PUFF             = new GColor(0xFFDAB9);
	public static final GColor PALE_GOLDENROD         = new GColor(0xEEE8AA);
	public static final GColor KHAKI                  = new GColor(0xF0E68C);
	public static final GColor DARK_KHAKI             = new GColor(0xBDB76B);
	
	/* PURPLES */
	public static final GColor LAVENDER               = new GColor(0xE6E6FA);
	public static final GColor THISTLE                = new GColor(0xD8BFD8);
	public static final GColor PLUM                   = new GColor(0xDDA0DD);
	public static final GColor VIOLET                 = new GColor(0xEE82EE);
	public static final GColor ORCHID                 = new GColor(0xDA70D6);
	public static final GColor FUCHSIA                = new GColor(0xFF00FF);
	public static final GColor MAGENTA                = FUCHSIA;
	public static final GColor MEDIUM_ORCHID          = new GColor(0xBA55D3);
	public static final GColor MEDIUM_PURPLE          = new GColor(0x9370DB);
	public static final GColor BLUE_VIOLET            = new GColor(0x8A2BE2);
	public static final GColor DARK_VIOLET            = new GColor(0x9400D3);
	public static final GColor DARK_ORCHID            = new GColor(0x9932CC);
	public static final GColor DARK_MAGENTA           = new GColor(0x8B008B);
	public static final GColor PURPLE                 = new GColor(0x800080);
	public static final GColor REB_PURPLE             = new GColor(0x663399);
	public static final GColor INDIGO                 = new GColor(0x4B0082);
	public static final GColor MEDIUM_SLATE_BLUE      = new GColor(0x7B68EE);
	public static final GColor SLATE_BLUE             = new GColor(0x6A5ACD);
	public static final GColor DARK_SLATE_BLUE        = new GColor(0x483D8B);
	
	/* GREENS */
	public static final GColor GREEN_YELLOW           = new GColor(0xADFF2F);
	public static final GColor CHARTREUSE             = new GColor(0x7FFF00);
	public static final GColor LAWN_GREEN             = new GColor(0x7CFC00);
	public static final GColor LIME                   = new GColor(0x00FF00);
	public static final GColor LIME_GREEN             = new GColor(0x32CD32);
	public static final GColor PALE_GREEN             = new GColor(0x98FB98);
	public static final GColor LIGHT_GREEN            = new GColor(0x90EE90);
	public static final GColor MEDIUM_SPRING_GREEN    = new GColor(0x00FA9A);
	public static final GColor SPRING_GREEN           = new GColor(0x00FF7F);
	public static final GColor MEDIUM_SEA_GREEN       = new GColor(0x3CB371);
	public static final GColor SEA_GREEN              = new GColor(0x2E8B57);
	public static final GColor FOREST_GREEN           = new GColor(0x228B22);
	public static final GColor GREEN                  = new GColor(0x008000);
	public static final GColor DARK_GREEN             = new GColor(0x006400);
	public static final GColor YELLOW_GREEN           = new GColor(0x9ACD32);
	public static final GColor OLIVE_DRAB             = new GColor(0x6B8E23);
	public static final GColor OLIVE                  = new GColor(0x808000);
	public static final GColor DARK_OLIVE_GREEN       = new GColor(0x556B2F);
	public static final GColor MEDIUM_AQUAMARINE      = new GColor(0x66CDAA);
	public static final GColor DARK_SEA_GREEN         = new GColor(0x8FBC8F);
	public static final GColor LIGHT_SEA_GREEN        = new GColor(0x20B2AA);
	public static final GColor DARK_CYAN              = new GColor(0x008B8B);
	public static final GColor TEAL                   = new GColor(0x008080);
	
	/* BLUES / CYANS */
	public static final GColor AQUA                   = new GColor(0x00FFFF);
	public static final GColor CYAN                   = AQUA;
	public static final GColor LIGHT_CYAN             = new GColor(0xE0FFFF);
	public static final GColor PALE_TURQUOISE         = new GColor(0xAFEEEE);
	public static final GColor AQUAMARINE             = new GColor(0x7FFFD4);
	public static final GColor TURQUOISE              = new GColor(0x40E0D0);
	public static final GColor MEDIUM_TURQUOISE       = new GColor(0x48D1CC);
	public static final GColor DARK_TURQUOISE         = new GColor(0x00CED1);
	public static final GColor CADET_BLUE             = new GColor(0x5F9EA0);
	public static final GColor STEEL_BLUE             = new GColor(0x4682B4);
	public static final GColor LIGHT_STEEL_BLUE       = new GColor(0xB0C4DE);
	public static final GColor POWDER_BLUE            = new GColor(0xB0E0E6);
	public static final GColor LIGHT_BLUE             = new GColor(0xADD8E6);
	public static final GColor SKY_BLUE               = new GColor(0x87CEEB);
	public static final GColor LIGHT_SKY_BLUE         = new GColor(0x87CEFA);
	public static final GColor DEEP_SKY_BLUE          = new GColor(0x00BFFF);
	public static final GColor DODGER_BLUE            = new GColor(0x1E90FF);
	public static final GColor CORNFLOWER_BLUE        = new GColor(0x6495ED);
	public static final GColor ROYAL_BLUE             = new GColor(0x4169E1);
	public static final GColor BLUE                   = new GColor(0x0000FF);
	public static final GColor MEDIUM_BLUE            = new GColor(0x0000CD);
	public static final GColor JENIFER_BLUE           = new GColor(0x06066D);
	public static final GColor DARK_BLUE              = new GColor(0x00008B);
	public static final GColor NAVY                   = new GColor(0x000080);
	public static final GColor MIDNIGHT_BLUE          = new GColor(0x191970);
	
	/* BROWNS */
	public static final GColor CORNSILK               = new GColor(0xFFF8DC);
	public static final GColor BLANCHED_ALMOND        = new GColor(0xFFEBCD);
	public static final GColor BISQUE                 = new GColor(0xFFE4C4);
	public static final GColor NAVAJO_WHITE           = new GColor(0xFFDEAD);
	public static final GColor WHEAT                  = new GColor(0xF5DEB3);
	public static final GColor BURLY_WOOD             = new GColor(0xDEB887);
	public static final GColor TAN                    = new GColor(0xD2B48C);
	public static final GColor ROSY_BROWN             = new GColor(0xBC8F8F);
	public static final GColor SANDY_BROWN            = new GColor(0xF4A460);
	public static final GColor GOLDENROD              = new GColor(0xDAA520);
	public static final GColor DARK_GOLDENROD         = new GColor(0xB8860B);
	public static final GColor PERU                   = new GColor(0xCD853F);
	public static final GColor CHOCOLATE              = new GColor(0xD2691E);
	public static final GColor SADDLE_BROWN           = new GColor(0x8B4513);
	public static final GColor SIENNA                 = new GColor(0xA0522D);
	public static final GColor BROWN                  = new GColor(0xA52A2A);
	public static final GColor MAROON                 = new GColor(0x800000);
	
	/* WHITES */
	public static final GColor WHITE                  = new GColor(0xFFFFFF);
	public static final GColor SNOW                   = new GColor(0xFFFAFA);
	public static final GColor HONEYDEW               = new GColor(0xF0FFF0);
	public static final GColor MINT_CREAM             = new GColor(0xF5FFFA);
	public static final GColor AZURE                  = new GColor(0xF0FFFF);
	public static final GColor ALICE_BLUE             = new GColor(0xF0F8FF);
	public static final GColor GHOST_WHITE            = new GColor(0xF8F8FF);
	public static final GColor WHITE_SMOKE            = new GColor(0xF5F5F5);
	public static final GColor SEASHELL               = new GColor(0xFFF5EE);
	public static final GColor BEIGE                  = new GColor(0xF5F5DC);
	public static final GColor OLD_LACE               = new GColor(0xFDF5E6);
	public static final GColor FLORAL_WHITE           = new GColor(0xFFFAF0);
	public static final GColor IVORY                  = new GColor(0xFFFFF0);
	public static final GColor ANTIQUE_WHITE          = new GColor(0xFAEBD7);
	public static final GColor LINEN                  = new GColor(0xFAF0E6);
	public static final GColor LAVENDER_BLUSH         = new GColor(0xFFF0F5);
	public static final GColor MISTY_ROSE             = new GColor(0xFFE4E1);
	
	/* GREYS */
	public static final GColor GAINSBORO              = new GColor(0xDCDCDC);
	public static final GColor LIGHT_GRAY             = new GColor(0xD3D3D3);
	public static final GColor LIGHT_GREY             = LIGHT_GRAY;
	public static final GColor SILVER                 = new GColor(0xC0C0C0);
	public static final GColor DARK_GRAY              = new GColor(0xA9A9A9);
	public static final GColor DARK_GREY              = DARK_GRAY;
	public static final GColor GRAY                   = new GColor(0x808080);
	public static final GColor GREY                   = GRAY;
	public static final GColor DIM_GRAY               = new GColor(0x696969);
	public static final GColor DIM_GREY               = DIM_GRAY;
	public static final GColor LIGHT_SLATE_GRAY       = new GColor(0x778899);
	public static final GColor LIGHT_SLATE_GREY       = LIGHT_SLATE_GRAY;
	public static final GColor SLATE_GRAY             = new GColor(0x708090);
	public static final GColor SLATE_GREY             = SLATE_GRAY;
	public static final GColor DARK_SLATE_GRAY        = new GColor(0x2F4F4F);
	public static final GColor DARK_SLATE_GREY        = DARK_SLATE_GRAY;
	public static final GColor BLACK                  = new GColor(0x000000);
	
	/**
	 * All the above-written colors in their respective order.
	 * Duplicate colors are commented out, as they do not add
	 * an additional value to the list of colors. The list can
	 * not be modified, and should be considered constant.
	 */
	public static final List<GColor> COLORS = Collections.unmodifiableList(Arrays.asList(new GColor[] {
		/* REDS */
		INDIAN_RED, LIGHT_CORAL, SALMON, DARK_SALMON, LIGHT_SALMON,
		CRIMSON_SALMON, RED, FIRE_BRICK, DARK_RED,

		/* PINKS */
		PINK, LIGHT_PINK, HOT_PINK, DEEP_PINK, MEDIUM_VIOLET_RED,
		PALE_VIOLET_RED,
		
		/* ORANGES */
		CORAL, TOMATO, ORANGE_RED, DARK_ORANGE, ORANGE,
		
		/* YELLOWS */
		GOLD, YELLOW, LIGHT_YELLOW, LEMON_CHIFFON, LIGHT_GOLDENROD_YELLOW,
		PAPAYA_WHIP, MOCCASIN, PEACH_PUFF, PALE_GOLDENROD, KHAKI,
		DARK_KHAKI,
		
		/* PURPLES */
		LAVENDER, THISTLE, PLUM, VIOLET, ORCHID, FUCHSIA, /* MAGENTA, */
		MEDIUM_ORCHID, MEDIUM_PURPLE, BLUE_VIOLET, DARK_VIOLET,
		DARK_ORCHID, DARK_MAGENTA, PURPLE, REB_PURPLE, INDIGO,
		MEDIUM_SLATE_BLUE, SLATE_BLUE, DARK_SLATE_BLUE,
		
		/* GREENS */
		GREEN_YELLOW, CHARTREUSE, LAWN_GREEN, LIME, LIME_GREEN,
		PALE_GREEN, LIGHT_GREEN, MEDIUM_SPRING_GREEN, SPRING_GREEN,
		MEDIUM_SEA_GREEN, SEA_GREEN, FOREST_GREEN, GREEN, DARK_GREEN,
		YELLOW_GREEN, OLIVE_DRAB, OLIVE, DARK_OLIVE_GREEN,
		MEDIUM_AQUAMARINE, DARK_SEA_GREEN, LIGHT_SEA_GREEN, DARK_CYAN,
		TEAL,
		
		/* BLUES / CYANS */
		AQUA, /* CYAN, */ LIGHT_CYAN, PALE_TURQUOISE, AQUAMARINE,
		TURQUOISE, MEDIUM_TURQUOISE, DARK_TURQUOISE, CADET_BLUE,
		STEEL_BLUE, LIGHT_STEEL_BLUE, POWDER_BLUE, LIGHT_BLUE,
		SKY_BLUE, LIGHT_SKY_BLUE, DEEP_SKY_BLUE, DODGER_BLUE,
		CORNFLOWER_BLUE, ROYAL_BLUE, BLUE, MEDIUM_BLUE, JENIFER_BLUE /* <3 */, 
		DARK_BLUE, NAVY, MIDNIGHT_BLUE,
		
		/* BROWNS */
		CORNSILK, BLANCHED_ALMOND, BISQUE, NAVAJO_WHITE, WHEAT,
		BURLY_WOOD, TAN, ROSY_BROWN, SANDY_BROWN, GOLDENROD,
		DARK_GOLDENROD, PERU, CHOCOLATE, SADDLE_BROWN, SIENNA,
		BROWN, MAROON,
		
		/* WHITES */
		WHITE, SNOW, HONEYDEW, MINT_CREAM, AZURE, ALICE_BLUE, GHOST_WHITE,
		WHITE_SMOKE, SEASHELL, BEIGE, OLD_LACE, FLORAL_WHITE, IVORY,
		ANTIQUE_WHITE, LINEN, LAVENDER_BLUSH, MISTY_ROSE,
		
		/* GREYS */
		GAINSBORO, LIGHT_GRAY, /* LIGHT_GREY, */ SILVER, DARK_GRAY,
		/* DARK_GREY, */ GRAY, /* GREY, */ DIM_GRAY, /* DIM_GREY, */
		LIGHT_SLATE_GRAY, /* LIGHT_SLATE_GREY, */ SLATE_GRAY,
		/* SLATE_GREY, */ DARK_SLATE_GRAY, /* DARK_SLATE_GREY, */ BLACK
	}));
	
	protected int value;
	private Color awtColor;
	
	public GColor(int r, int g, int b) {
		this(r, g, b, 0xFF);
	}
	
	public GColor(int r, int g, int b, int a) {
		value = toARGB(r, g, b, a);
	}
	
	public GColor(int rgb) {
		this(rgb, false);
	}

	public GColor(int argb, boolean hasAlpha) {
		value = hasAlpha ? argb : (argb | 0xFF000000);
	}

	public GColor invert() {
		int ri = 0xFF - getRed();
		int gi = 0xFF - getGreen();
		int bi = 0xFF - getBlue();
		
		return new GColor(ri, gi, bi, getAlpha());
	}
	
	public int getAlpha() {
		return (value >>> 24) & 0xFF;
	}
	
	public int getRed() {
		return (value >>> 16) & 0xFF;
	}
	
	public int getGreen() {
		return (value >>> 8) & 0xFF;
	}

	public int getBlue() {
		return value & 0xFF;
	}

	public float getTransparency() {
		return getAlpha() / 255.0f;
	}
	
	public boolean hasAlpha() {
		return (value & 0xFF000000) != 0xFF000000;
	}

	public int getRGB() {
		return value & 0x00FFFFFF;
	}

	public int getARGB() {
		return value;
	}
	
	public Color toAWTColor() {
		if (awtColor == null)
			awtColor = new Color(value, true);
		return awtColor;
	}
	
	private static void validateColor(int r, int g, int b, int a) {
		if (r < 0 || r > 0xFF) {
			throw new IllegalArgumentException("Red channel out of bounds! Expected " + 
					"0 <= r <= 255 but got " + r);
		}
		
		if (g < 0 || g > 0xFF) {
			throw new IllegalArgumentException("Green channel out of bounds! Expected " + 
					"0 <= g <= 255 but got " + g);
		}

		if (b < 0 || b > 0xFF) {
			throw new IllegalArgumentException("Blue channel out of bounds! Expected " + 
					"0 <= b <= 255 but got " + b);
		}

		if (a < 0 || a > 0xFF) {
			throw new IllegalArgumentException("Alpha channel out of bounds! Expected " + 
					"0 <= a <= 255 but got " + a);
		}
	}
	
	public static int toARGB(int r, int g, int b, int a) {
		validateColor(r, g, b, a);

		return (a << 24) | (r << 16) | (g <<  8) | b;
	}
	
	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}
	
	public boolean equals(GColor other) {
		return (other == null) ? false : (value == other.value);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof GColor))
			return false;
		return equals((GColor)other);
	}
}
