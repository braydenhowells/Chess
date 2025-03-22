import ui.ServerFacade;
import ui.preLoginClient;
import ui.repl;

public class ClientMain {
    public static void main(String[] args) {
        repl r = new repl(new preLoginClient(new ServerFacade("http://localhost:8080")));
        r.run();
    }
}