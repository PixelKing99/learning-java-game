package render;

import util.DynamicString;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Map.Entry;
import java.util.SequencedSet;
import java.util.TreeMap;

public class Hud {
	private static TreeMap<Integer, DynamicString> hud = new TreeMap<>();
	private static int spacing = 0;
	
	
	public static void add(int index, DynamicString dynamicString) {
		if (hud.containsKey(index)) {
			throw new IllegalArgumentException("cannot assign to index that already has been assigned");
		}
		hud.put(index, dynamicString);
	}
	
	public static void render(Graphics g) {
		
		Font font = new Font(null);
		g.setFont(font);
		int fontHeight = g.getFontMetrics().getHeight();
		
		
		SequencedSet<Entry<Integer, DynamicString>> set = hud.sequencedEntrySet();
		
		for (Entry<Integer, DynamicString> e : set) {
			String s = e.getValue().get();
			
			g.setColor(new Color(255, 255, 255));
			g.drawString(s, 3, (spacing + fontHeight) * (e.getKey() + 1));
		}
	}
}
