package render;

import game.entities.EntityType;
import saves.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class Textures {

    private static Map<Texture, Image> textureMap;
    private static Map<Tile, Image> tileTextures;
    private static Map<EntityType, Image> entityTextures;

    private Textures() {
        textureMap = new HashMap();
        tileTextures = new HashMap();
        entityTextures = new HashMap();

//	should probably make a separate class for all the image loading stuff so its easier to give a class access to an image
        textureMap.put(Texture.LAVA, new ImageIcon(getClass().getResource("/resources/lava.png")).getImage());
        textureMap.put(Texture.WALL, new ImageIcon(getClass().getResource("/resources/pog.png")).getImage());
        textureMap.put(Texture.DEAD, new ImageIcon(getClass().getResource("/resources/skull.png")).getImage());
        textureMap.put(Texture.GRASS, new ImageIcon(getClass().getResource("/resources/grass.png")).getImage());
        textureMap.put(Texture.ENEMY, new ImageIcon(getClass().getResource("/resources/zombie.png")).getImage());
        textureMap.put(Texture.PLAYER, new ImageIcon(getClass().getResource("/resources/face.png")).getImage());
        textureMap.put(Texture.LOGO, new ImageIcon(getClass().getResource("/resources/logo.png")).getImage());

        tileTextures.put(Tile.WALL, textureMap.get(Texture.WALL));
        tileTextures.put(Tile.LAVA, textureMap.get(Texture.LAVA));

        entityTextures.put(EntityType.PLAYER, textureMap.get(Texture.PLAYER));
        entityTextures.put(EntityType.ZOMBIE, textureMap.get(Texture.ENEMY));
    }

    public static Image get(Texture name) {
        if (textureMap == null) {
            new Textures();
        }

        return textureMap.get(name);
    }

    public static Image get(Tile tile) {
        if (tileTextures == null) {
            new Textures();
        }

        return tileTextures.get(tile);
    }

    public static Image get(EntityType entity) {
        if (entityTextures == null) {
            new Textures();
        }

        return entityTextures.get(entity);
    }

}
