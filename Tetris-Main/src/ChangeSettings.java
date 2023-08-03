package src;

public interface ChangeSettings {
    // "default" because it is directly called, avoid crash

    default void update() {
        return;
    }
}
