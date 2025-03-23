package ui;

import requests.RegisterRequest;
import requests.LoginRequest;
import results.LoginResult;

import java.util.Arrays;

public class PreLoginMode implements ClientMode {
    private final ServerFacade facade;

    public PreLoginMode(ServerFacade facade) {
        this.facade = facade;
        System.out.println("\uD83D\uDE0A Welcome to chess, hopefully it works!");
        System.out.println(help());
        // these two^ will print every time we come back to preLoginMode
    }

    @Override
    public String help() {
        return """
        Available commands:
        register <USERNAME> <PASSWORD> <EMAIL> - to create an account AND login
        login <USERNAME> <PASSWORD>            - to play chess
        quit                                   - playing chess
        help                                   - with possible commands
        """;
    }

    @Override
    public ClientMode eval(String input) {
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (cmd) {
            case "login":
                return login(params);
            case "register":
                return register(params);
            case "quit":
                return null;
            default:
                System.out.println(help());
                return this;
        }
    }

    private ClientMode login(String... params) {
        if (params.length == 2) {
            var result = facade.login(new LoginRequest(params[0], params[1]));
            if (result.username() == null || result.authToken() == null) {
                System.out.println("Login failed. " + result.message());
                System.out.println("Usage: login <username> <password>");
                return this;
            }
            System.out.println("Welcome back, " + result.username() + "!");
            return new PostLoginMode(facade, result.username());
        } else if (params.length < 2) {
            System.out.println("Unable to login. Not enough parameters entered.");
            System.out.println("Usage: login <username> <password>");

        } else {
            System.out.println("Unable to login. Too many parameters entered.");
            System.out.println("Usage: login <username> <password>");
        }
        return this;
    }

    private ClientMode register(String... params) {
        if (params.length == 3) {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            System.out.println(request);
            LoginResult result = facade.register(request);

            if (result.username() == null || result.authToken() == null) {
                System.out.println("Registration failed. " + result.message());
                System.out.println("Usage: register <username> <password> <email>");
                return this;
            }

            System.out.println("Welcome, " + result.username() + "! You are now logged in.");
            return new PostLoginMode(facade, result.username());
        } else if (params.length < 3) {
            System.out.println("Unable to register. Not enough parameters entered.");
            System.out.println("Usage: register <username> <password> <email>");

        } else {
            System.out.println("Unable to register. Too many parameters entered.");
            System.out.println("Usage: register <username> <password> <email>");
        }
        return this;
    }
}
