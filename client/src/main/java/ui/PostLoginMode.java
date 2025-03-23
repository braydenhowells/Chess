package ui;

import java.util.Arrays;

public class PostLoginMode implements ClientMode {
    private final ServerFacade facade;
    private final String username;

    public PostLoginMode(ServerFacade facade, String username) {
        this.facade = facade;
        this.username = username;
        System.out.println(help());  // as soon as we enter, the user sees the help text
    }

    @Override
    public String help() {
        return """
        create <NAME>     - to create a game
        list              - to list games
        join <ID> <color> - to join a game
        logout            - return to login screen
        quit              - exit the game
        help              - show commands
        """;
    }

    @Override
    public ClientMode eval(String input) {
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";

        switch (cmd) {
            case "logout":
                facade.logout();
                System.out.println("Goodbye, " + username + "!");
                return new PreLoginMode(facade);
            case "quit":
                return null;
            default:
                System.out.println("Command not recognized: " + cmd);
                System.out.println(help());
                return this;
        }
    }
}
