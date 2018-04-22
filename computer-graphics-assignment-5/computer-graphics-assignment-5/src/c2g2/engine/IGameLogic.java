package c2g2.engine;

public interface IGameLogic {

    void init(Window window, Timer timer) throws Exception;
    
    void input(Window window, MouseInput mouseInput);

    void update(Window window, float interval, MouseInput mouseInput);
    
    void render(Window window);
    
    void cleanup();
}