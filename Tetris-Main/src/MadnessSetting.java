package src;
import java.util.Random;

public class MadnessSetting implements ChangeSettings{
    private final int BLOCK_BOUND = 10;
    private final double SPEED_RATIO_MAX = 1;
    private final double SPEED_RATIO_MIN = 0.5;
    Tetris tetris;
    public MadnessSetting(Tetris tetris){
        this.tetris = tetris;
    }

    @Override
    public void update() {
        tetris.setBlockBound(BLOCK_BOUND);
        tetris.setSpeedRatio(generateRandomSpeed());
        tetris.setRotationLock(true);
    }

    // change by harry 06/09
    private double generateRandomSpeed(){
        Random rand = new Random();
        double randomSpeed = SPEED_RATIO_MIN + rand.nextDouble() * (SPEED_RATIO_MAX - SPEED_RATIO_MIN);
        return randomSpeed;
    }



}
