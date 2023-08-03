package src;

public class DifficultyHandler {
    ChangeSettings Setting;

    public DifficultyHandler(Tetris tetris, String difficulty){
        switch (difficulty) {
            case "easy": {
                Setting = new EasySetting(tetris);
                break;
            }
            case "medium": {
                Setting = new MediumSetting(tetris);
                break;
            }
            case "madness":
                Setting = new MadnessSetting(tetris);
                break;
        }
    }
    public void updateSettings(){
        Setting.update();
    }

}
