import ui.*;
// we will basically use everything in ui folder

public class ClientMain {
    public static void main(String[] args) {
        // onion: repl has a -> mode which has a -> facade
        // repl(mode(facade))
        var facade = new ServerFacade("http://localhost:8080");
        var startingMode = new PreLoginMode(facade); // eventually we will switch this mode
        var repl = new Repl(startingMode);
        repl.run();
    }
}
