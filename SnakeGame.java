import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public final class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int bWidth;
    int bHeight;
    int tileSize = 25;

    // snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // target
    Tile target;
    Random random;


    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;

    SnakeGame(int bWidth, int bHeight) {
        this.bWidth = bWidth;
        this.bHeight = bHeight;
        setPreferredSize(new Dimension(this.bWidth, this.bHeight));
        setBackground(Color.decode("#000000"));
        addKeyListener(this);
        setFocusable(true);
        setDoubleBuffered(true);

        random = new Random();
        initializeGame();
    }

    public void initializeGame() {
        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<>();

        placetarget();

        velocityX = 1;
        velocityY = 0;

        gameOver = false;

        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public void startGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        gameLoop = new Timer(85, this);
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Grid Lines
        for (int i = 0; i < bWidth / tileSize; i++) {
            // (x1, y1, x2, y2)
            g.drawLine(i * tileSize, 0, i * tileSize, bHeight);
            g.drawLine(0, i * tileSize, bWidth, i * tileSize);
        }

        // target
        g.setColor(Color.decode("#003DFA"));
        g.fill3DRect(target.x * tileSize, target.y * tileSize, tileSize, tileSize, true);

        // Snake Head
        g.setColor(Color.decode("#FF00C9"));
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        // Snake Body
        for (Tile snakePart : snakeBody) {
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize, true);
        }

        // Keeping the score or showing the score one the game ended.
        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        if (gameOver) {
            g.setColor(Color.decode("#FF00C9"));
            String text = "GAME OVER! SCORE: " + snakeBody.size();
            int stringWidth = g.getFontMetrics().stringWidth(text);
            int x = (getWidth() - stringWidth) / 2;
            g.drawString(text, x, getHeight() / 2); // Adjust y position as needed
        } else {
            g.setColor(Color.decode("#FF00C9"));
            g.drawString("SCORE: " + snakeBody.size(), tileSize - 16, tileSize);
        }
    }

    public void placetarget() {
        target = new Tile(random.nextInt(bWidth / tileSize), random.nextInt(bHeight / tileSize));
    }

    public void move() {
        if (gameOver) return;

        
        if (collision(snakeHead, target)) {
            snakeBody.add(new Tile(target.x, target.y));
            placetarget();
        }

        
        if (!snakeBody.isEmpty()) {
            snakeBody.add(0, new Tile(snakeHead.x, snakeHead.y));
            snakeBody.remove(snakeBody.size() - 1);
        }

       
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        
        checkCollisions();
    }

    private void checkCollisions() {
       
        for (Tile snakePart : snakeBody) {
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
                break;
            }
        }

       
        if (snakeHead.x < 0 || snakeHead.x >= bWidth / tileSize ||
            snakeHead.y < 0 || snakeHead.y >= bHeight / tileSize) {
            gameOver = true;
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        }
    }
// Keys used to play the game
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> {
                if (velocityY != 1) {
                    velocityX = 0;
                    velocityY = -1;
                }
            }
            case KeyEvent.VK_DOWN -> {
                if (velocityY != -1) {
                    velocityX = 0;
                    velocityY = 1;
                }
            }
            case KeyEvent.VK_LEFT -> {
                if (velocityX != 1) {
                    velocityX = -1;
                    velocityY = 0;
                }
            }
            case KeyEvent.VK_RIGHT -> {
                if (velocityX != -1) {
                    velocityX = 1;
                    velocityY = 0;
                }
            }
            case KeyEvent.VK_ENTER -> {
                if (gameOver) {
                    initializeGame();
                    startGame();
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}

class SnakeGameFrame extends JFrame {
    private final SnakeGame snakeGame;

    SnakeGameFrame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        snakeGame = new SnakeGame(500, 500);
        add(snakeGame, BorderLayout.CENTER);

        JButton startButton = new JButton("PLAY");
        startButton.addActionListener(e -> {
            snakeGame.initializeGame();
            snakeGame.startGame();
            snakeGame.requestFocusInWindow();
        });
        add(startButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new SnakeGameFrame();
    }
}
