package ui;


import java.util.Arrays;

public class preLoginClient {
    private String currentUser = null;
    private final ServerFacade facade;
    private State state = State.SIGNEDOUT;

    public preLoginClient(ServerFacade facade) {
        this.facade = facade;
    }

    public String help() {
        return """
        register <USERNAME> <PASSWORD> <EMAIL> - to create an account AND login
        login <USERNAME> <PASSWORD>            - to play chess
        quit                                   - playing chess
        help                                   - with possible commands
        """;
    }


    public String eval(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "quit" -> "Thanks for playing!";
            default -> help();
        };
    }


    public String login(String... params) {
        return "login";
    }

    public String register(String... params) {
        return "register";
    }

}