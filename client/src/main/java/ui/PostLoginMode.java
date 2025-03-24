package ui;

import requests.CreateRequest;
import requests.LoginRequest;

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
                quit              - playing chess
                help              - with possible commands
                """;
    }

    @Override
    public ClientMode eval(String input) {
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (cmd) {
            case "logout":
                facade.logout();
                System.out.println("Goodbye, " + username + "!");
                return new PreLoginMode(facade);

            case "create":
                return create(params);
            case "quit":
                return null;
            default:
                System.out.println("Command not recognized: " + cmd);
                System.out.println(help());
                return this;
        }
    }


    private ClientMode create(String... params) {
        if (params.length == 1) {
            var result = facade.create(new CreateRequest(params[0]));
            if (result.message()!= null && result.message().contains("Error")) {
                System.out.println("Game creation failed. " + result.message());
                System.out.println("Usage: create <game name>");
                return this;
            }
            System.out.println("New chess game \"" + params[0] + "\" successfully created! Good job " + username + "!");
            return this;
        } else if (params.length < 1) {
            System.out.println("Unable to create game. Not enough parameters entered.");
            System.out.println("Usage: create <game name>");
        } else {
            System.out.println("Unable to create game. Too many parameters entered.");
            System.out.println("Usage: create <game name>");
        }
        return this;
    }

}