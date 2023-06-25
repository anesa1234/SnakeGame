import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;


public class SnakeGame {
	public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setResizable(false);

        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);

        frame.setVisible(true);
        gamePanel.startGame();
    }
}
class GamePanel extends JPanel implements KeyListener {

    private static final long serialVersionUID = 1L;
	private static final int PANEL_WIDTH = 1000;
    private static final int PANEL_HEIGHT = 800;
    private static final int OUTER_PANEL_SIZE = 700;
    private static final int SQUARE_SIZE = 20;
    private static final int MOVE_SPEED = 5;

    private int squareX;
    private int squareY;
    private int directionX;
    private int directionY;
    private boolean paused;
    private boolean gameOver;
    private int score;

    private Point food;
    
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.LIGHT_GRAY);
        addKeyListener(this);
        setFocusable(true);
        food = new Point();
        gameOver = false;
        score = 0; 

        JPanel innerPanel = new JPanel() {
        	
        	private static final long serialVersionUID = 1L;

			@Override
        	protected void paintComponent(Graphics g) {
        	    super.paintComponent(g);
        	    
        	    // Draw the food
        	    g.setColor(Color.RED);
        	    int foodSize = SQUARE_SIZE;
        	    g.fillOval(food.x, food.y, foodSize, foodSize);
        	    
        	    // Set the color and draw the square
        	    g.setColor(Color.GREEN);
        	    g.fillRect(squareX, squareY, SQUARE_SIZE, SQUARE_SIZE);
        	    
        	    // Display Game over message
        	    if (gameOver) {
        	        // Display "Game Over" message
        	        g.setColor(Color.RED);
        	        g.setFont(new Font("Arial", Font.BOLD, 50));
        	        FontMetrics metrics = g.getFontMetrics();
        	        String gameOverText = "Game Over";
        	        int textWidth = metrics.stringWidth(gameOverText);
        	        int x = (getWidth() - textWidth) / 2;
        	        int y = getHeight() / 2;
        	        g.drawString(gameOverText, x, y);
        	        
        	    }
        	    
        	    // Display score
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                String scoreText = "Score: " + score;
                g.drawString(scoreText, 10, 30);
        	}

        };
        innerPanel.setPreferredSize(new Dimension(OUTER_PANEL_SIZE, OUTER_PANEL_SIZE));
        innerPanel.setBackground(Color.DARK_GRAY);   
        
        setLayout(new FlowLayout(FlowLayout.CENTER));

        add(innerPanel);
    }

    public void startGame() {
        squareX = OUTER_PANEL_SIZE / 4;
        squareY = OUTER_PANEL_SIZE / 2;
        directionX = MOVE_SPEED;
        directionY = 0;
        paused = false;
        requestFocusInWindow();

        generateFood();

        // Start the game loop
        Thread gameLoopThread = new Thread(() -> {
            while (true) {
                if (!paused) {
                    moveSquare();
                    checkCollision();
                    checkFood();
                }
                repaint();

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        gameLoopThread.start();
    }

    private void moveSquare() {
        squareX += directionX;
        squareY += directionY;
    }

    private void checkCollision() {
        if (squareX <= 0 || squareX >= OUTER_PANEL_SIZE - SQUARE_SIZE ||
                squareY <= 0 || squareY >= OUTER_PANEL_SIZE - SQUARE_SIZE) {
            // Square hit the edge, stop movement
            paused = true;
            gameOver = true;
        }
    }
    

    private void generateFood() {
    	
    	if (gameOver) {
            return; // Stop generating food if the game is over
        }
    	
        Random random = new Random();
        int minX = SQUARE_SIZE;
        int maxX = OUTER_PANEL_SIZE - 2 * SQUARE_SIZE;
        int minY = SQUARE_SIZE;
        int maxY = OUTER_PANEL_SIZE - 2 * SQUARE_SIZE;

        int foodX = minX + SQUARE_SIZE * random.nextInt((maxX - minX) / SQUARE_SIZE);
        int foodY = minY + SQUARE_SIZE * random.nextInt((maxY - minY) / SQUARE_SIZE);

        food = new Point(foodX, foodY);
        
        
    }

    private void checkFood() {
        if (Math.abs(squareX- food.x)<=15 && Math.abs(squareY- food.y)<=15) {
            generateFood();
            increaseScore();
        }
    }
    
    private void increaseScore() {
    	
    	 if (gameOver) {
    	        return; // Stop increasing the score if the game is over
    	    }
    	 
        score += 1;
    }



    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (gameOver) {
            return; // Ignore key presses if the game is over
        }

        if (keyCode == KeyEvent.VK_LEFT && directionX != MOVE_SPEED) {
            directionX = -MOVE_SPEED;
            directionY = 0;
            paused = false;
        } else if (keyCode == KeyEvent.VK_RIGHT && directionX != -MOVE_SPEED) {
            directionX = MOVE_SPEED;
            directionY = 0;
            paused = false;
        } else if (keyCode == KeyEvent.VK_UP && directionY != MOVE_SPEED) {
            directionX = 0;
            directionY = -MOVE_SPEED;
            paused = false;
        } else if (keyCode == KeyEvent.VK_DOWN && directionY != -MOVE_SPEED) {
            directionX = 0;
            directionY = MOVE_SPEED;
            paused = false;
        } else if (keyCode == KeyEvent.VK_SPACE) {
            paused = !paused;
        }
        
       
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
}
