import ui.*;
// we will basically use everything in ui folder

public class ClientMain {
    public static void main(String[] args) {
        // onion: repl has a -> mode which has a -> facade
        // repl(mode(facade))
        int port = 8080;
        var facade = new ServerFacade("http://localhost:" + port);
        var startingMode = new PreLoginMode(facade); // eventually we will switch this mode
        var repl = new Repl(startingMode);
        repl.run();
    }
}
