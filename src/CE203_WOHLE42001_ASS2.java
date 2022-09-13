import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CE203_WOHLE42001_ASS2 extends Game{
    /*
     * Main, which extends game since it is an abstract class.
     */
    public static void main(String[] args) {
        MainMenu finalStart = new MainMenu();
        finalStart.mainFrame();
    }
}
class MainMenu extends JFrame {
    private static final CE203_WOHLE42001_ASS2 start = new CE203_WOHLE42001_ASS2();
    private static final FileSave mainFile = new FileSave();

    void mainFrame(){
        /*
         * The First JFrame which opens up when you first run the code.
         * Contains 2 buttons, 1 being the play button which will close the current window and open another JFrame holding just the game.
         * The second button will print the high scores which are saved to the file "Scores.txt".
         * The points will be 0.0 if the game hasn't been played before.
         */
        JFrame menuFrame = new JFrame();
        Font setFont = new Font("Verdana", Font.PLAIN, 20);
        JButton button1 = new JButton("Play");
        JButton button2 = new JButton("High-Scores");
        button1.setFont(setFont);
        button1.setPreferredSize(new Dimension(100,100));
        button1.setForeground(Color.BLACK);
        button1.setBackground(Color.RED);
        button2.setFont(new Font("Verdana", Font.PLAIN, 15));
        button2.setPreferredSize(new Dimension(200,50));
        button2.setForeground(Color.BLACK);
        button2.setBackground(Color.RED);
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.BLACK);
        menuPanel.add(button1);
        menuPanel.add(button2);
        menuFrame.add(menuPanel);
        menuFrame.setTitle("Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400,200);
        menuFrame.setVisible(true);

        button1.addActionListener(play -> {
            JOptionPane.showMessageDialog(null, """
                    Arrow keys to move...
                    Avoid COVID at all cost...(RED)
                    Vaccine must be obtained at the far right...(Green)
                    You have three lives, look out for extra lives...(Yellow)
                    Hit Space for hints if stuck..."""
            );
            menuFrame.setVisible(false);
            start.start();
        });
        button2.addActionListener(scores -> {
            menuFrame.dispose();
            mainFile.highScore();
            new DisplayScores();
        });
    }
}
abstract class Game extends JFrame{
    /*
     * Initialises all the values in Data and creates the grid panel and the grid array.
     * Connects the program to any keyboard keys pressed, by Keyboard Listener.
     * Updates the grid panel ever time the user enters a key.
     * Contains all the levels from zero to seven and a bonus level which includes an easter egg, which is symbolised as hand=sanitizer.
     * Also edits the number of lives when need be and how long the user has been playing for.
     * Most variables are named by their function so it is easier to understand.
     */
    JPanel gridPanel;                   // panel used to display grid panels
    JPanel[][] gridArray;               // array used to store panels in grid
    Data getData = new Data();
    long startTimer;
    FileSave writer = new FileSave();

    public void start() {
        getData.setTotalLives(3);           //Sets total lives to three.
        getData.setRangeX(30);              //Sets the size of the grid array for the x values.
        getData.setRangeY(30);              //Sets the size of the grid array for the y values.
        getData.setCurrentPosX(10);         //Current x position of user.
        getData.setCurrentPosY(3);          //Current y position of user.
        initialise();                       // set up game environment
        addKeyListener(new KeyboardListener(this));      //key listener to respond to key events
        setSize(new Dimension(500, 500));          //standard configuration
        setTitle("1905035");                                   //Registration ID.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        startTimer = System.currentTimeMillis();                //Starts the timer.
    }
    public void initialise() {
        if(gridPanel != null) {
            this.remove(gridPanel);
            gridPanel = null;
        }
        gridPanel = new JPanel(new GridLayout(Data.getRangeX(), Data.getRangeY()));     //The grid Panel
        gridArray = new JPanel[Data.getRangeX()][Data.getRangeY()];                     //The grid Array

        for(int x = 0; x < gridArray.length; x++) {// for loop to create grid
            for(int y = 0; y < gridArray[x].length; y++) {
                gridArray[x][y] = new JPanel();
                gridArray[x][y].setBackground(Color.BLACK);                             //Sets the entire background for the grid array to black.
                gridArray[x][y].addMouseListener(new MouseClickListener());             //unique mouse listener per panel to determine which panel was clicked
                gridPanel.add(gridArray[x][y]);
            }
        }
        if (Data.getLevelNumber() == 0){                        //Calls each level, changes every time the user enters the right side border.
            levelZero();
        }else if(Data.getLevelNumber() == 1){
            levelOne();
        }else if(Data.getLevelNumber() == 2){
            levelTwo();
        }else if(Data.getLevelNumber() == 3){
            levelThree();
        }else if(Data.getLevelNumber() == 4){
            levelFour();
        }else if(Data.getLevelNumber() == 5){
            levelFive();
        }else if(Data.getLevelNumber() == 6){
            if(Data.getBonusLevel()){
                bonusLevel();
            }else {
                levelSix();
            }
        }else if(Data.getLevelNumber() == 7){
            finalLevel();
        }
        gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);     //This is the user.
        this.add(gridPanel);                            //add panel to frame
    }
    public void blockChanger(int x,int y){
        /*
         * This method is used for when the user moves off the screen.
         * Teleports the user to the other end of the grid panel.
         * By checking if the next block is not red, once it find out that its black it will change it to white and
         * convert the what was white into black.
         */
        int nextPosX = Data.getCurrentPosX() + x;
        int nextPosY = Data.getCurrentPosY() + y;

        if (nextPosX >= 0 && nextPosX < Data.getRangeX() && nextPosY >= 0 && nextPosY < Data.getRangeY()) {
            gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.BLACK);
            getData.setCurrentPosX(nextPosX);
            getData.setCurrentPosY(nextPosY);
            blocker(Data.getCurrentPosX(), Data.getCurrentPosY());
            gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);
        } else if (nextPosX < 0) {
            if (Data.getLevelNumber() == 6) {
                int vitaminC = 0;
                for (int i = 0; i < Data.getRangeX(); i++) {
                    for (int j = 0; j < Data.getRangeX(); j++) {
                        if (gridArray[i][j].getBackground() == Color.ORANGE) {      //The orange in level 6, it opens the bonus level once an algorithm check that there is no mor orange on the grid.
                            vitaminC += 1;
                        }
                    }
                }
                if (vitaminC == 0) {                //When theres no orange left on screen, it sets bonus level to true.
                    getData.setBonusLevel(true);
                }
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.BLACK);
                getData.setCurrentPosX(Data.getRangeX() - 1);
                blocker(Data.getCurrentPosX(), Data.getCurrentPosY());
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);
                initialise();
                revalidate();
            } else {        //This is when the user exits the grid from the top side.
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.BLACK);
                getData.setCurrentPosX(Data.getRangeX() - 1);
                blocker(Data.getCurrentPosX(), Data.getCurrentPosY());
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);
            }
        } else if (nextPosX > Data.getRangeX() - 1) {
            if (Data.getLevelNumber() == 6) {
                getData.setBonusLevel(false);
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.BLACK);
                getData.setCurrentPosX(0);
                blocker(Data.getCurrentPosX(), Data.getCurrentPosY());
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);
                initialise();
                revalidate();

            } else {    //This is when the user exits the grid from the bottom side.
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.BLACK);
                getData.setCurrentPosX(0);
                blocker(Data.getCurrentPosX(), Data.getCurrentPosY());
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);
            }
        } else if (nextPosY < 0) {
            if (Data.getBonusLevel()) {
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.BLACK);
                getData.setCurrentPosY(Data.getRangeY() - 1);
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);
            } else {        //This is when the user exits the grid on the left side, it will subtract 1 from the current level just so it can return to the previous level.
                getData.setLevelNumber(Data.getLevelNumber() - 1);
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.BLACK);
                getData.setCurrentPosY(Data.getRangeY() - 1);
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);
                initialise();
                revalidate();
            }
        } else {
            if (Data.getBonusLevel()) {
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.BLACK);
                getData.setCurrentPosY(0);
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);
            } else {    //This is when the user exits the grid on the left side, it will add 1 to the current level so it will set the grid to the next level pattern.
                getData.setLevelNumber(Data.getLevelNumber() + 1);
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.BLACK);
                getData.setCurrentPosY(0);
                gridArray[Data.getCurrentPosX()][Data.getCurrentPosY()].setBackground(Color.WHITE);
                initialise();
                revalidate();
            }
        }
    }
    public void levelZero() {       //Level Zero
        System.out.println("Level Zero");
        for (int i = 0; i < Data.getRangeY(); i++) {
            gridArray[Data.getRangeX() - 1][i].setBackground(Color.RED);
            gridArray[i][0].setBackground(Color.RED);
            gridArray[0][i].setBackground(Color.RED);
        }
    }
    public void levelOne(){     //Level One
        System.out.println("Level One");
        for (int i = 0; i < Data.getRangeY(); i++) {
            gridArray[Data.getRangeX() - 1][i].setBackground(Color.RED);
            gridArray[0][i].setBackground(Color.RED);
        }
    }
    public void levelTwo(){     //Level Two
        System.out.println("Level Two");
        for (int i = 0; i < Data.getRangeY(); i++){
            gridArray[5][i].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY()-5; i++) {
            for (int j = 0; j < Data.getRangeY()-5; j++) {
                gridArray[j+5][i+5].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeY()-5; i++){
            gridArray[Data.getRangeX()-1][i+5].setBackground(Color.RED);
            gridArray[0][i+5].setBackground(Color.RED);
        }
    }
    public void levelThree(){   //Level Three
        System.out.println("Level Three");
        for (int i = 0; i < Data.getRangeY(); i++){
            gridArray[Data.getRangeX()-1][i].setBackground(Color.RED);
            gridArray[0][i].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-4; i++){
            gridArray[i][Data.getRangeX()-1].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY()-5; i++){
            for (int j = 0; j < Data.getRangeY()-5; j++){
                gridArray[j + 5][i].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                gridArray[i+9][j+2].setBackground(Color.BLACK);
            }
        }
        for (int i = 0; i < 5; i++){
            gridArray[i+4][3].setBackground(Color.BLACK);
        }
    }
    public void levelFour(){    //Level Four
        System.out.println("Level Four");
        for (int i = 0; i < Data.getRangeY(); i++){
            gridArray[Data.getRangeX()-1][i].setBackground(Color.RED);
            gridArray[0][i].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY()-3; i++){
            gridArray[i+3][(int) (Data.getRangeY() * 0.2)].setBackground(Color.RED);
            gridArray[i][(int) (Data.getRangeY() * 0.4)].setBackground(Color.RED);
            gridArray[i+3][(int) (Data.getRangeY() * 0.6)].setBackground(Color.RED);
            gridArray[i][(int) (Data.getRangeY() * 0.8)].setBackground(Color.RED);
            gridArray[i+3][Data.getRangeY()-1].setBackground(Color.RED);
        }
    }
    public void levelFive(){    //Level Five
        System.out.println("Level Five");
        for (int i = 0; i < Data.getRangeY()-19; i++) {
            gridArray[Data.getRangeX() - 1][i].setBackground(Color.RED);
            gridArray[0][i].setBackground(Color.RED);
        }
        for (int i = 0; i< Data.getRangeY()-16; i++){
            gridArray[Data.getRangeX()-1][i+16].setBackground(Color.RED);
            gridArray[0][i+16].setBackground(Color.RED);

        }
        for(int i = 0; i < Data.getRangeY()-11; i++){
            gridArray[5][i+11].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY()-10; i++){
            gridArray[i][10].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY()-24; i++){
            gridArray[i+24][10].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY()-15; i++){
            gridArray[24][i+10].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-5; i++){
            gridArray[i+5][Data.getRangeX()-1].setBackground(Color.RED);
        }
    }
    public void levelSix(){     //Level Six
        System.out.println("Level Six");
        gridArray[23][Data.getRangeY()-4].setBackground(Color.YELLOW);
        gridArray[20][9].setBackground(Color.RED);
        gridArray[14][3].setBackground(Color.RED);
        for(int i = 0; i < Data.getRangeY()-10; i++){
            for(int j = 0; j < Data.getRangeY()-26; j++) {
                gridArray[i][j+11].setBackground(Color.ORANGE);
            }
        }
        for(int i = 0; i < Data.getRangeY()-19; i++) {
            gridArray[0][i].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-15; i++){
            gridArray[0][i+15].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY()-24; i++){
            for(int j = 0; j < Data.getRangeY()-19; j++){
                gridArray[Data.getRangeY()-1-i][j].setBackground(Color.RED);
                gridArray[Data.getRangeY()-1-i][Data.getRangeY()-1-j].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeY()-24; i++){
            for(int j = 0; j < 4; j++){
                gridArray[i+24][j+15].setBackground(Color.RED);
            }
        }
        for(int i = 0; i < Data.getRangeY()-21; i++){
            gridArray[20][i+10].setBackground(Color.RED);
            gridArray[4][i+19].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-10; i++){
            gridArray[i][10].setBackground(Color.RED);
            gridArray[i][15].setBackground(Color.RED);

        }
        for(int i = 0; i < Data.getRangeY()-14; i++){
            gridArray[i+5][19].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-22; i++){
            gridArray[i+4][Data.getRangeY()-3].setBackground(Color.RED);
            gridArray[i+16][Data.getRangeY()-3].setBackground(Color.RED);
        }
        for(int i = 0; i < 2; i ++){
            gridArray[i+10][Data.getRangeY()-1].setBackground(Color.RED);
            gridArray[11][Data.getRangeY()-1-i].setBackground(Color.RED);
            gridArray[i+16][Data.getRangeY()-1].setBackground(Color.RED);
            gridArray[16][Data.getRangeY()-1-i].setBackground(Color.RED);
            gridArray[5][i+4].setBackground(Color.RED);
            gridArray[i+1][4].setBackground(Color.RED);
            gridArray[14][i+1].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-12; i++){
            gridArray[i+6][Data.getRangeY()-5].setBackground(Color.RED);
            gridArray[i+3][8].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-27; i++){
            gridArray[24][i+25].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-25; i++){
            gridArray[6][i+21].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-13; i++){
            gridArray[i+6][21].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-15; i++){
            gridArray[22][i+6].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-13; i++){
            gridArray[i+5][6].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-26; i++){
            gridArray[3][i+4].setBackground(Color.RED);
        }
        for(int i = 0; i < Data.getRangeY()-20; i++){
            gridArray[i+5][4].setBackground(Color.RED);
        }
    }
    public void bonusLevel(){       //The bonus level.
        System.out.println("bOnUs LeVeL!!!");
        for (int i = 0; i < Data.getRangeX()-10; i++){
            for (int j = 0; j < Data.getRangeX()-10; j++){
                gridArray[i+5][j+5].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeX()-12; i++){
            for (int j = 0; j < Data.getRangeX()-12; j++){
                gridArray[i+6][j+6].setBackground(Color.BLACK);
            }
        }
        for (int i = 0; i < Data.getRangeX()-14; i++){
            for (int j = 0; j < Data.getRangeX()-14; j++){
                gridArray[i+7][j+7].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeX()-16; i++){
            for (int j = 0; j < Data.getRangeX()-16; j++){
                gridArray[i+8][j+8].setBackground(Color.BLACK);
            }
        }
        for (int i = 0; i < Data.getRangeX()-18; i++){
            for (int j = 0; j < Data.getRangeX()-18; j++){
                gridArray[i+9][j+9].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeX()-20; i++){
            for (int j = 0; j < Data.getRangeX()-20; j++){
                gridArray[i+10][j+10].setBackground(Color.BLACK);
            }
        }
        for (int i = 0; i < Data.getRangeX()-22; i++){
            for (int j = 0; j < Data.getRangeX()-22; j++){
                gridArray[i+11][j+11].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeX()-24; i++){
            for (int j = 0; j < Data.getRangeX()-24; j++){
                gridArray[i+12][j+12].setBackground(Color.BLACK);
            }
        }
        for (int i = 0; i < Data.getRangeX()-26; i++){
            for (int j = 0; j < Data.getRangeX()-26; j++){
                gridArray[i+13][j+13].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeX()-28; i++){
            for (int j = 0; j < Data.getRangeX()-28; j++){
                gridArray[i+14][j+14].setBackground(Color.CYAN);
            }
        }
        for (int i = 0; i < Data.getRangeY()-19; i++) {
            gridArray[Data.getRangeX() - 1][i].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY()-15; i++) {
            gridArray[Data.getRangeX() - 1][i+15].setBackground(Color.RED);
        }
    }
    public void finalLevel() {      //Final Level/ Level Seven.
        System.out.println("Final Level");
        for (int i = 0; i < Data.getRangeY()-6; i++) {
            gridArray[Data.getRangeX() - 1][i].setBackground(Color.RED);
            gridArray[0][i].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY() - 2; i++) {
            gridArray[i][5].setBackground(Color.RED);
            gridArray[i + 2][8].setBackground(Color.RED);
            gridArray[i][11].setBackground(Color.RED);
        }
        for (int i = 0; i < Data.getRangeY()-20; i++){
            for (int j = 0; j < Data.getRangeY()-16; j++) {
                gridArray[i][j+14].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeY()-15; i++){
            for (int j = 0; j < Data.getRangeY()-14; j++) {
                gridArray[i + 15][j + 14].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeY()-15; i++){
            for (int j = 0; j < Data.getRangeY()-28; j++){
                gridArray[i+15][j+28].setBackground(Color.BLACK);
            }
        }
        for (int i = 0; i < Data.getRangeY()-20; i++){
            for (int j = 0; j < Data.getRangeY()-17; j++){
                gridArray[i+18][j+17].setBackground(Color.BLACK);
            }
        }
        for (int i = 0; i < Data.getRangeY()-22; i++){
            for (int j = 0; j < Data.getRangeY()-18; j++){
                gridArray[i+19][j+18].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeY()-28; i++){
            for (int j = 0; j < Data.getRangeY()-24; j++){
                gridArray[i+8][j+24].setBackground(Color.RED);
            }
        }
        for (int i = 0; i < Data.getRangeY(); i++){
            gridArray[i][Data.getRangeY()-1].setBackground(Color.RED);
        }
        gridArray[7][28].setBackground(Color.GREEN);
    }
    public void blocker(int nextPosX,int nextPosY){
        /*
         * Iterates every time the user enters a key just to check if the block will cause an action, i.e red = death.
         */
        try {
            if (gridArray[nextPosX][nextPosY].getBackground() == Color.YELLOW) {    //Yellow box gives the user another life.
                System.out.println("Extra Life!");
                lives(+1);
            } else if (gridArray[nextPosX][nextPosY].getBackground() == Color.RED) {    //minus 1 from life because of red.
                System.out.println("Minus 1 life");
                lives(-1);
                System.out.println(Data.getTotalLives() + " live(s) left.");
                if (Data.getTotalLives() > 0) {
                    TimeUnit.MILLISECONDS.sleep(100);
                    getData.setCurrentPosX(10);
                    getData.setCurrentPosY(3);
                } else {
                    System.out.println("You have COVID-19...GameOver" + "\n" + "Now make sure to quarantine for at least 2 weeks, and drink Vitamin C with lemon :)!");
                    timer();
                    writer.fileWriter();
                    dispose();
                    new DisplayScores();
                }
            } else if (gridArray[nextPosX][nextPosY].getBackground() == Color.CYAN) {   //Easter egg.. which is Hand-Sanitizer.
                System.out.println("Hand Sanitizer!...Completed easter egg.");
                getData.setEasterEgg(true);     //Sets this to true so it informs FileSave which will subtract a certain amount to the users' time for a better score.
            } else if (gridArray[nextPosX][nextPosY].getBackground() == Color.GREEN) {      //The finish line/ square.
                System.out.println("Winner!!!!");
                Data.setComplete(true);
                timer();
                writer.fileWriter();
                dispose();
                new DisplayScores();
            }
        }catch (InterruptedException e){
            System.out.println("Interruption!");
        }
    }
    public void lives(int effect){
        getData.setTotalLives(Data.getTotalLives()+effect);     //Subtracts or Adds 1 to the total number of lives.
    }

    public void timer() {       //How long the user is playing for.
        long endTime = System.currentTimeMillis();
        double timeTaken = (endTime - startTimer) / 1000.0;
        getData.setLengthTimer(timeTaken);
    }
}
class Data {
    /*
     * Holds most of the variables which are all linked by getter and setter methods.
     */
    private static int currentPosX;         //changes according to the current x position of the user within the grid.
    private static int currentPosY;         //Changes according to the current y position of the user within the grid.
    private static int rangeX;              //Holds the size of x values for the grid panel.
    private static int rangeY;              //Holds the size of y values for the grid panel.
    private static int levelNumber = 0;     //Changes according to each level the user goes into or returns from.
    private static int totalLives;          //Total number of lives, at the start is 3.
    private static boolean bonusLevel;      //If the user enters a specific level, they will be able to enter the bonus level, which is set to true when entering the level number.
    private static boolean easterEgg;       //Changes depending if the user has completed the easter egg.
    private static int numberOfLines = 0;   //Assigned to the number of lines in "Scores.txt".
    private static double[] topFive;        //Holds the top five high scores in "Scores.txt".
    private static double lengthTimer;      //How long the user lasted in the game.
    private static boolean complete;        //If the user manages to finish the game this changes to true.

    /*
     * All the getter and setter methods:
     */
    public static int getCurrentPosX() {
        return currentPosX;
    }

    public void setCurrentPosX(int newCurrentPosX) {
        currentPosX = newCurrentPosX;
    }

    public static int getCurrentPosY() {
        return currentPosY;
    }

    public void setCurrentPosY(int newCurrentPosY) {
        currentPosY = newCurrentPosY;
    }

    public static int getRangeX() {
        return rangeX;
    }

    public void setRangeX(int newRangeX) {
        rangeX = newRangeX;
    }

    public static int getRangeY() {
        return rangeY;
    }

    public void setRangeY(int newRangeY) {
        rangeY = newRangeY;
    }

    public static int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int newLevelNumber) {
        levelNumber = newLevelNumber;
    }

    public static int getTotalLives() {
        return totalLives;
    }

    public void setTotalLives(int totalLives) {
        Data.totalLives = totalLives;
    }

    public static boolean getBonusLevel(){
        return bonusLevel;
    }

    public void setBonusLevel(boolean bonusLevel){
        Data.bonusLevel = bonusLevel;
    }

    public static boolean getEasterEgg() {
        return easterEgg;
    }

    public void setEasterEgg(boolean easterEgg) {
        Data.easterEgg = easterEgg;
    }

    public static int getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines) {
        Data.numberOfLines = numberOfLines;
    }

    public static double[] getTopFive() {
        return topFive;
    }

    public void setTopFive(double[] topFive) {
        Data.topFive = topFive;
    }

    public static double getLengthTimer() {
        return lengthTimer;
    }

    public void setLengthTimer(double lengthTimer) {
        Data.lengthTimer = lengthTimer;
    }

    public static boolean isComplete() {
        return complete;
    }

    public static void setComplete(boolean complete) {
        Data.complete = complete;
    }
}
class DisplayScores extends JFrame {
    /*
     *Creates a JFrame which will display the top five scores.
     */
    FileSave saveData = new FileSave();
    public double[] tempTopFive = saveData.topFiveScores();

    DisplayScores() {
        JFrame displayScores = new JFrame("Top 5 Scores");
        displayScores.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayScores.setBounds(0,0,500,500);
        displayScores.setPreferredSize(new Dimension(500,500));

        JPanel displayPanel = new JPanel();
        if (this.tempTopFive == null) {
            this.tempTopFive = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
        }
        JLabel[] labelArray = new JLabel[tempTopFive.length];                       //Sets the array max as the length of the tempTopFive.

        JLabel highScore = new JLabel("HighScore: ");
        highScore.setForeground(Color.RED);
        highScore.setFont(new Font("Verdana", Font.BOLD, 20));
        highScore.setBorder(BorderFactory.createEmptyBorder(25, 100, 0, 0));    //Used to position the data in the center of the JFrame, since BorderLayout.Center will not work.
        displayPanel.add(highScore);

        for (int i = 0; i < tempTopFive.length; i++){
            JLabel eachLabel = new JLabel( i+1 + "-                       " + tempTopFive[i] + " Seconds");
            eachLabel.setForeground(Color.RED);
            labelArray[i] = eachLabel;
        }
        for (JLabel labelSpecific : labelArray) {
            labelSpecific.setFont(new Font("Verdana", Font.PLAIN, 16));
            labelSpecific.setBorder(BorderFactory.createEmptyBorder(45, 100, 0, 0));    //Used to position in the middle of the JFrame since .Center would not work.
            displayPanel.add(labelSpecific);
        }
        displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.PAGE_AXIS));       //Used to position the data in a downward format instead of across.
        displayPanel.setBackground(Color.BLACK);
        displayScores.add(displayPanel, BorderLayout.CENTER);
        displayScores.pack();
        displayScores.setVisible(true);
    }
}
class FileSave extends Data {
    /*
     *Creates the file and takes in data only when the game was running, since there is a high-score button.
     * Rounds the values before adding it to the final double[] array, just so the jframe wont show long numbers off screen.
     */
    public void fileWriter() {
        try {
            FileWriter fW = new FileWriter("src/Scores.txt", true);  //Creates the file if not already created.
            BufferedWriter bW = new BufferedWriter(fW);
            if (getEasterEgg()) {
                setLengthTimer(getLengthTimer() - 45.0);          //Edits the users score only if they manage to complete the easter Egg. Subtracts their time by 30 seconds.
            }
            if (isComplete()) {
                setLengthTimer(getLengthTimer() - 30.0);          //Edits the users score only if they manage to complete the game, and get to the end.
            } else {
                setLengthTimer(getLengthTimer() + 100);           //Adds 100 seconds to their score because they died before getting to the end. Wouldn't be fair if they just died the first second they spawn.

            }
            bW.write(getLengthTimer() + "\n");              //Adds the score to the file then goes to the next line.
            System.out.println("Saved.");                       //Prints Save just so i know the data has been saved properly.
            JOptionPane.showMessageDialog(null, "Your Score: " + getLengthTimer());     //Prints the score of the user in a option pane.
            bW.close();                                         //Closes file.
            highScore();                                        //Calls highScore,which will assign/sort the topFive array.
        } catch (IOException e) {
            System.out.println("I/O Exception");
        }
    }
    public void highScore(){
        try {
            FileReader fR = new FileReader("src/Scores.txt");
            BufferedReader bR = new BufferedReader(fR);
            List<Double> linesList = new ArrayList<>();             //Holds the entire data on the file in a list.
            for (int i = 0; i < 5; i++) {
                linesList.add(0.0);                                 //Adds 5 zeros just so there is enough data to run through each algorithm.
            }
            String lines = bR.readLine();                           //Assigns the first line to lines.
            while (lines != null) {
                linesList.add(Double.parseDouble(lines));           //Converts it to double then adds it to the main array list called linesList.
                lines = bR.readLine();                              //Assigns the next line to lines.
                setNumberOfLines(getNumberOfLines() + 1);           //Changes the numberOfLines by increasing it by 1, this will hold an accurate number of how many lines are in the file.
            }
            DecimalFormat formats = new DecimalFormat("#.##");      //Changes the format of the saved data to 2 decimal places.
            formats.setRoundingMode(RoundingMode.CEILING);
            for (int i = 0; i < linesList.size(); i++){
                linesList.set(i,Double.parseDouble(formats.format(linesList.get(i))));  //since when sorting the first 5 digits will be zeros, it replaces them with the next 5 or so values.
            }
            Collections.sort(linesList);                        //Sorts the linesList in ascending order.
            int diff = linesList.size() - 5;                    //This can be used to distinguish how many zeros must be switched out.
            for (int i = 0; i < diff; i++) {
                if (i + 5 < linesList.size()) {
                    linesList.set(i, linesList.get(i + 5));
                }
            }
            double[] topFive = new double[5];
            for (int i = 0; i < linesList.size();) {
                if (i < 5) {
                    if (linesList.get(i) != null) {
                        topFive[i] = linesList.get(i);         //Finally sets the top five values into its own double array.
                    }
                }
                i++;
            }
            setTopFive(topFive);
        } catch (FileNotFoundException e) {
            System.out.println("File not Found");
        } catch (IOException e) {
            System.out.println("I/O Exception");
        }
    }
    public double[] topFiveScores(){
        return getTopFive();
    }       //Getter method to return the topfive array.
}
class KeyboardListener implements KeyListener {
    private final Game game; // game passed through to allow for game manipulation

    public KeyboardListener(Game game) {
        this.game = game;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        /*
         * Actions keys, every time the user enters a key.
         */
        int x = 0;
        int y = 0;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> {
                x =  -1;
                this.game.blockChanger(x, y);
            }
            case KeyEvent.VK_DOWN -> {
                x = 1;
                y = 0;
                this.game.blockChanger(x, y);
            }
            case KeyEvent.VK_LEFT -> {
                y = -1;
                this.game.blockChanger(x, y);
            }
            case KeyEvent.VK_RIGHT -> {
                y = 1;
                x = 0;
                this.game.blockChanger(x, y);
            }
            case KeyEvent.VK_SPACE -> {
                System.out.println("Hints");
                int currentLevel = Data.getLevelNumber();
                if (currentLevel < 5 && currentLevel != 2){
                    JOptionPane.showMessageDialog(null,"Move to the right, gets you closer to the Vaccine!");
                } else if (currentLevel == 2){
                    JOptionPane.showMessageDialog(null, "You can go back to the previous level or do you dare to go down, Go Get That VACCINE.");
                }else if (currentLevel == 5){
                    JOptionPane.showMessageDialog(null, "You can teleport through the top to the bottom and vice versa, Go Get That VACCINE.");
                } else if (currentLevel == 6){
                    if (Data.getBonusLevel()){
                        JOptionPane.showMessageDialog(null, """
                                Dont forget you have a mouse for a reason. You have the ability to release and remove COVID.
                                YES Hand-Sanitizer, decreases your time greatly, giving you a better score.
                                Quick grab it and return back to your mission.""");
                    }else {
                        JOptionPane.showMessageDialog(null, "I wonder what happens when drink all that Vitamin C, oO that lemon looks juicy!");
                    }
                }else if (currentLevel == 7){
                    JOptionPane.showMessageDialog(null, "There is the Vaccine, grab it to end the game AND WIN!");
                }
            }
            default -> System.out.println("Random Key which has no use to the game!");
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
class MouseClickListener implements MouseListener {

    public MouseClickListener() {
        // game passed through to allow for game manipulation
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /*
         * This is only active when the suer is in the bonus level, the user is able to press on any square to change it from black to red and vice versa.
         */
        if(Data.getLevelNumber() == 6 && Data.getBonusLevel()) {
            if (e.getComponent().getBackground() == Color.BLACK) {
                e.getComponent().setBackground(Color.RED);
            } else if (e.getComponent().getBackground() == Color.RED) {
                e.getComponent().setBackground(Color.BLACK);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
