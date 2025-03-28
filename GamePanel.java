import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int bodyParts = 6;
    int applesEaten = 0;
    int totalApples = 3;
    int[] appleX = new int[totalApples];
    int[] appleY = new int[totalApples];

    int slowAppleX;
    int slowAppleY;

    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;


    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame(){
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){

        if(running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            //apples draw
            g.setColor(Color.red);
            for (int i = 0; i < totalApples; i++) {
                g.fillOval(appleX[i], appleY[i], UNIT_SIZE, UNIT_SIZE);
            }

            //slow apples draw
            g.setColor(Color.white);
            g.fillOval(slowAppleX, slowAppleY, UNIT_SIZE, UNIT_SIZE);

            //snake draw
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            //score draw
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

        }else {
            gameOver(g);
        }
    }
    //normal apples
    public void newApple() {
        for (int i = 0; i < totalApples; i++) {
            appleX[i] = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY[i] = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        }
    }

    //slow down apples
    public void newSlowApple() {
        slowAppleX = random.nextInt((SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        slowAppleY = random.nextInt((SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }


    public void move(){
        for(int i = bodyParts; i > 0; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction){
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }
    public void checkApple(){
        for (int i = 0; i < totalApples; i++) {

            //if apple is eaten
            if ((x[0] == appleX[i]) && (y[0] == appleY[i])) {
                bodyParts += 4;
                applesEaten++;

                appleX[i] = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
                appleY[i] = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

                timer.setDelay(timer.getDelay() - 5); //speeding up the game
            }

            //if SLOW apple is eaten
            if ((x[0] == slowAppleX) && (y[0] == slowAppleY)) {
                timer.setDelay(Math.min(200, timer.getDelay() + 4));
                newSlowApple();
            }
        }
    }
    public void checkCollisions(){
        //check if head collide with body
        for(int i = bodyParts; i > 0; i--){
            if((x[0] == x[i]) && (y[0] == y[i])){
                running = false;
            }
        }
        //check if head collide with frame
        if(x[0] < 0 ||
                x[0] > SCREEN_WIDTH - UNIT_SIZE||
                y[0] < 0 ||
                y[0] > SCREEN_HEIGHT - UNIT_SIZE){

                    running = false;
        }

        if(!running){
            timer.stop();
        }
    }
    public void gameOver(Graphics g){
        //score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());


        //Game Over text
        g.setColor(Color.magenta);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        if(applesEaten <= 4){
            g.drawString("You are baaad", (SCREEN_WIDTH - metrics2.stringWidth("You are baaad"))/2, (SCREEN_HEIGHT/2) );
        }else if (applesEaten > 4 && applesEaten <= 10){
            g.drawString("You are so good!", (SCREEN_WIDTH - metrics2.stringWidth("You are so good!"))/2, (SCREEN_HEIGHT/2) );
        }else {
            g.drawString("So easy, right?", (SCREEN_WIDTH - metrics2.stringWidth("So easy, right?"))/2, (SCREEN_HEIGHT/2) );
        }
    }

    public void restartGame(){
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;

        for (int i = 0; i < bodyParts; i++) {
            x[i] = 50 - i * UNIT_SIZE;
            y[i] = 50;
        }

        if (timer != null) {
            timer.stop();
        }

        timer = new Timer(DELAY, this);
        timer.start();

        repaint();
    }

    public void actionPerformed(ActionEvent e){

        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e){
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if (!running){
                        restartGame();
                    }
                    break;
            }

        }
    }
}
