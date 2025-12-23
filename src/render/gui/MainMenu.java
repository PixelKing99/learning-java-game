package render.gui;

import render.Screen;
import render.Texture;

import java.awt.*;

import static render.Render.getScreenSetter;

public class MainMenu extends MenuRenderer {

    public MainMenu() {

        Partition mainElement = new Partition(2, menuDefault).splitHorizontally();
        Partition buttonArea = new Partition(3, menuDefault);
        Partition buttonPartition = new Partition(7, menuDefault).splitHorizontally();

        Partition logoHolder = new Partition(3);
        logoHolder.setWeight(1, 5).get(1).setTexture(Texture.LOGO);
        mainElement.set(0, logoHolder);

        buttonPartition.set(0, buttonDefault.get())
                .setMessage("start")
                .addMousePressListener(getScreenSetter(Screen.WORLD_SELECTION));

        buttonPartition.set(2, buttonDefault.get())
                .setMessage("options")
                .addMousePressListener(getScreenSetter(Screen.WORLD_SELECTION));

        buttonPartition.set(4, buttonDefault.get())
                .setMessage("something else")
                .addMousePressListener(getScreenSetter(Screen.WORLD_SELECTION));


        buttonArea.set(1, buttonPartition);
        mainElement.set(1, buttonArea);


        this.menu = mainElement;
    }
}
