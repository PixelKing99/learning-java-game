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
		
//		it doesnt really matter if the set is in order but i want to have it that way so that if there is some bug with the hud itll be more predictable
		SequencedSet<Entry<Integer, DynamicString>> set = hud.sequencedEntrySet();
		
		for (Entry<Integer, DynamicString> e : set) {
			String s = e.getValue().get();
			
			Rectangle2D r = g.getFontMetrics().getStringBounds(s, g);
			
//			the way it renders currently theres a gap between each line of text even if their supposed to be back to back
//			if i want to get rid of it i have to switch out r.getHeight for fontHeight and maybe some other stuff, but i think it looks kinda clean so ima keep it for now
			g.setColor(new Color(150, 150, 150, 100));
			g.fillRect(0, (spacing + fontHeight) * e.getKey(), (int) r.getWidth() + 5, (int) r.getHeight());
			
			g.setColor(new Color(255, 255, 255));
			g.drawString(s, 3, (spacing + fontHeight) * e.getKey() - (int) r.getY());
		}
	}
}
