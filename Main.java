import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame{
    private static final int WIDTH =1600;
    private static final int HEIGHT=1200;

public Main () {

super("Visual Art");


setSize(WIDTH, HEIGHT);

Game play = new Game();

((Component) play).setFocusable(true);

//Color RoyalBlue = new Color(22,13,193);


getContentPane().add(play);

setVisible(true);

setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


}

public static void main(String[] args) {

Main run = new Main();

 


}

}