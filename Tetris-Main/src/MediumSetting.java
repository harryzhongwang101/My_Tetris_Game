package src;

public class MediumSetting implements ChangeSettings{
    private final int BLOCK_BOUND = 10;
    private final double SPEED_RATIO = 0.8;
    Tetris tetris;
    public MediumSetting(Tetris tetris){
        this.tetris = tetris;
    }

    public void update(){
        tetris.setBlockBound(BLOCK_BOUND);
        tetris.setSpeedRatio(SPEED_RATIO);
        tetris.setRotationLock(false);
    }
}
