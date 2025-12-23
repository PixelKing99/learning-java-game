package render.gui;

import game.Server;
import render.Render;
import render.Screen;
import render.Texture;
import saves.Save;

import java.awt.*;
import java.io.IOException;
import java.util.zip.DataFormatException;

import static render.Render.getScreenSetter;

public class WorldSelection extends MenuRenderer {


    public WorldSelection() {
        Partition mainElement = new Partition(2);
        mainElement.splitHorizontally();

        Partition<Partition> worldsHolder = new Partition<>(3);
        worldsHolder.setWeight(1, 2);
        worldsHolder.set(1,
                new Partition<>(10, () -> {
                    Partition p = new Partition(3);
                    p.set(1,
                            new Element()
                                    .setColor(new Color(0,0,0,100))
                                    .setOutlineColor(
                                            new UiColor(
                                                    new Color(0,0,0,0),
                                                    new Color(255,255,255))
                                    )
                    );
                    p.splitHorizontally();
                    return p.setWeight(1, 18);
                })
        );
        worldsHolder.<Partition>get(1).splitHorizontally();

        mainElement.setWeight(0,3);
        mainElement.set(0, worldsHolder);



        Partition<Partition> buttons = new Partition<>(3, ()->{
            Partition x = new Partition(2, buttonDefault);
            x.splitHorizontally();
            x.get(0).setMessage("test1");
            x.get(1).setMessage("test2");
            return x;
        });
        mainElement.set(1, buttons);



        String[] worlds = Save.getWorlds();
        for (int i = 0; i < 10 && i < worlds.length; i++) {
            Element curButton = worldsHolder.<Partition<Partition>>get(1).<Partition>get(i).get(1);
            curButton.setMessage(worlds[i]);
            String worldName = worlds[i];
            curButton.addMousePressListener(() -> {
                try {
                    Server.start(worldName);
                    Render.initializeWorld();
                    setScreenToGame.run();
                } catch (DataFormatException e) {
                    throw new RuntimeException("the worlds file is incorrectly formatted\n" + e);
                } catch (IOException e) {
                    throw new RuntimeException("something went wrong while loading the world\n" + e);
                }
            });
        }

        this.menu = mainElement;
    }
}
