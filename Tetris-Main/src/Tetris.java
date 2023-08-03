package src;// Tetris.java

import ch.aplu.jgamegrid.*;

import java.security.Key;
import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class Tetris extends JFrame implements GGActListener {

    private Actor currentBlock = null;  // Currently active block
    private Actor blockPreview = null;   // block in preview window
    private int score = 0;
    private int slowDown = 5;
    private Random random = new Random(0);
    private int blockBound = 7; //how many different blocks
    private double speedRatio = 1; // for speed variation in medium mode
    private boolean rotationLock = false; // for locking up rotation function in madness mode
    private DifficultyHandler difficultyHandler;

    private TetrisGameCallback gameCallback;

    private boolean isAuto = false;

    private int seed = 30006;
    // For testing mode, the block will be moved automatically based on the blockActions.
    // L is for Left, R is for Right, T is for turning (rotating), and D for down
    private String [] blockActions = new String[10];
    private int blockActionIndex = 0;

    // Difficulty variable -- change by Mabrur (6/9/22)
    private String difficulty;
    private boolean extendedPieces = false;


    // 2022/9/6 change by brian
    GameStats stats = new GameStats();

    // Initialise object
    private void initWithProperties(Properties properties) {
        this.seed = Integer.parseInt(properties.getProperty("seed", "30006"));
        random = new Random(seed);
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));
        String blockActionProperty = properties.getProperty("autoBlockActions", "");
        blockActions = blockActionProperty.split(",");
        difficulty = properties.getProperty("difficulty", "easy");
    }

    public Tetris(TetrisGameCallback gameCallback, Properties properties) {
        // Initialise value
        initWithProperties(properties);
        this.gameCallback = gameCallback;
        blockActionIndex = 0;

        // Set up the UI components. No need to modify the UI Components
        tetrisComponents = new TetrisComponents();
        tetrisComponents.initComponents(this);
        gameGrid1.addActListener(this);
        gameGrid1.setSimulationPeriod(getSimulationTime());

        // Set difficulty
        difficultyHandler = new DifficultyHandler(this, difficulty);
        difficultyHandler.updateSettings();

        // Add the first block to start
        currentBlock = createRandomTetrisBlock();
        gameGrid1.addActor(currentBlock, new Location(6, 0));
        gameGrid1.doRun();

        // Do not lose keyboard focus when clicking this window
        gameGrid2.setFocusable(false);
        setTitle("SWEN30006 Tetris Madness");
        score = 0;
        showScore(score);
        slowDown = 5;


    }

    // create a block and assign to a preview mode
    Actor createRandomTetrisBlock() {
        if (blockPreview != null)
            blockPreview.removeSelf();

        // If the game is in auto test mode, then the block will be moved according to the blockActions
        String currentBlockMove = "";
        if (blockActions.length > blockActionIndex) {
            currentBlockMove = blockActions[blockActionIndex];
        }

        blockActionIndex++;
        TetroPiece t = null;
        TetroPiece previewPiece = null;
        int rnd = random.nextInt(blockBound);

        switch (rnd) {
            case 0 -> {
                t = new I(this);
                previewPiece = new I(this);
            }
            case 1 -> {
                t = new J(this);
                previewPiece = new J(this);
            }
            case 2 -> {
                t = new L(this);
                previewPiece = new L(this);
            }
            case 3 -> {
                t = new O(this);
                previewPiece = new O(this);
            }
            case 4 -> {
                t = new S(this);
                previewPiece = new S(this);
            }
            case 5 -> {
                t = new T(this);
                previewPiece = new T(this);
            }
            case 6 -> {
                t = new Z(this);
                previewPiece = new Z(this);
            }
            case 7 -> {
                t = new P(this);
                previewPiece = new P(this);
            }
            case 8 -> {
                t = new Plus(this);
                previewPiece = new Plus(this);
            }
            case 9 -> {
                t = new Q(this);
                previewPiece = new Q(this);
            }
        }
        stats.addCount(t.getBlockName());

        if (isAuto) {
            t.setAutoBlockMove(currentBlockMove);
        }
        // Show preview tetrisBlock
        previewPiece.display(gameGrid2, new Location(2, 1));
        blockPreview = previewPiece;

        // Harry 06/09: this only takes int as a variable. Can modify in actor tho?
        // HMB 07/09: Update any settings that need to changed (eg. block speed)
        difficultyHandler.updateSettings();
        t.setSlowDown((int) (slowDown * speedRatio));
        return t;
    }

    void setCurrentTetrisBlock(Actor t) {
        gameCallback.changeOfBlock(currentBlock);
        currentBlock = t;
    }

    // Handle user input to move block. Arrow left to move left, Arrow right to move right, Arrow up to rotate and
    // Arrow down for going down
    private void moveBlock(int keyEvent) {
        switch (keyEvent) {
            case KeyEvent.VK_UP:
                if(!rotationLock){
                    ((TetroPiece) currentBlock).rotate();
                }
                break;
            case KeyEvent.VK_LEFT:
                ((TetroPiece) currentBlock).left();
                break;
            case KeyEvent.VK_RIGHT:
                ((TetroPiece) currentBlock).right();
                break;
            case KeyEvent.VK_DOWN:
                ((TetroPiece) currentBlock).drop();
                break;
            default:
                return;
        }
    }
    public void act() {
        removeFilledLine();
        moveBlock(gameGrid1.getKeyCode());
    }
    private void removeFilledLine() {
        for (int y = 0; y < gameGrid1.nbVertCells; y++) {
            boolean isLineComplete = true;
            TetroBlock[] blocks = new TetroBlock[gameGrid1.nbHorzCells];   // One line
            // Calculate if a line is complete
            for (int x = 0; x < gameGrid1.nbHorzCells; x++) {
                blocks[x] =
                        (TetroBlock) gameGrid1.getOneActorAt(new Location(x, y), TetroBlock.class);
                if (blocks[x] == null) {
                    isLineComplete = false;
                    break;
                }
            }
            if (isLineComplete) {
                // If a line is complete, we remove the component block of the shape that belongs to that line
                for (int x = 0; x < gameGrid1.nbHorzCells; x++)
                    gameGrid1.removeActor(blocks[x]);
                ArrayList<Actor> allBlocks = gameGrid1.getActors(TetroBlock.class);
                for (Actor a : allBlocks) {
                    int z = a.getY();
                    if (z < y)
                        a.setY(z + 1);
                }
                gameGrid1.refresh();
                score++;
                gameCallback.changeOfScore(score);
                showScore(score);
                // Set speed of tetrisBlocks
                if (score > 10)
                    slowDown = 4;
                if (score > 20)
                    slowDown = 3;
                if (score > 30)
                    slowDown = 2;
                if (score > 40)
                    slowDown = 1;
                if (score > 50)
                    slowDown = 0;

            }
        }
    }

    // Show Score and Game Over

    private void showScore(final int score) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                scoreText.setText(score + " points");
            }
        });

    }

    void gameOver() {
        stats.updateScores(score);
        stats.updateFile(difficulty, blockBound);

        gameGrid1.addActor(new Actor("sprites/gameover.gif"), new Location(5, 5));
        gameGrid1.doPause();
        if (isAuto) {
            System.exit(0);
        }
    }

    // Start a new game
    public void startBtnActionPerformed(java.awt.event.ActionEvent evt)
    {
        gameGrid1.doPause();
        gameGrid1.removeAllActors();
        gameGrid2.removeAllActors();
        gameGrid1.refresh();
        gameGrid2.refresh();
        gameGrid2.delay(getDelayTime());
        blockActionIndex = 0;
        currentBlock = createRandomTetrisBlock();
        gameGrid1.addActor(currentBlock, new Location(6, 0));
        gameGrid1.doRun();
        gameGrid1.requestFocus();
        score = 0;
        showScore(score);
        slowDown = 5;
        stats.updateScores(score);
        stats.updateFile(difficulty, blockBound);
    }

    // Different speed for manual and auto mode
    private int getSimulationTime() {
        if (isAuto) {
            return 10;
        } else {
            return 100;
        }
    }

    private int getDelayTime() {
        if (isAuto) {
            return 200;
        } else {
            return 2000;
        }
    }

    public void setBlockBound(int blockBound){
        this.blockBound = blockBound;
    }
    public void setSpeedRatio(double speedRatio){
        this.speedRatio = speedRatio;
    }
    public void setRotationLock(boolean isLock){
        rotationLock = isLock;
    }

    // AUTO GENERATED - do not modify//GEN-BEGIN:variables
    public ch.aplu.jgamegrid.GameGrid gameGrid1;
    public ch.aplu.jgamegrid.GameGrid gameGrid2;
    public javax.swing.JPanel jPanel1;
    public javax.swing.JPanel jPanel2;
    public javax.swing.JPanel jPanel3;
    public javax.swing.JPanel jPanel4;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextArea jTextArea1;
    public javax.swing.JTextField scoreText;
    public javax.swing.JButton startBtn;
    private TetrisComponents tetrisComponents;
    // End of variables declaration//GEN-END:variables

}
