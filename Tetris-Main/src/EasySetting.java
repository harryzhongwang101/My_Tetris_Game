package src;

public class EasySetting implements ChangeSettings {
    private final int BLOCK_BOUND = 7;
    Tetris tetris;
    public EasySetting(Tetris tetris){
        this.tetris = tetris;
    }

    public void update() {
        tetris.setBlockBound(BLOCK_BOUND);
        tetris.setSpeedRatio(1);
        tetris.setRotationLock(false);
    }
}
