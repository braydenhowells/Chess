package ui;


import requests.RegisterRequest;
import results.LoginResult;

import java.util.Arrays;

public class preLoginClient {
    private final ServerFacade facade;

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
        if (params.length == 3) {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            System.out.println(request);
            LoginResult result = facade.register(request);
            String username = result.username();
            return "Good job registering, " + username + "!";
        }
        else if (params.length < 3) {
            return "Unable to register. Not enough parameters entered.";
        }
        else {
            return "Unable to register. Too many parameters entered.";
        }
    }

}