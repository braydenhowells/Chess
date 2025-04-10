package ui;

import requests.RegisterRequest;
import requests.LoginRequest;
import results.LoginResult;

import java.util.Arrays;

import static ui.EscapeSequences.RESET_TEXT_UNDERLINE;
import static ui.EscapeSequences.SET_TEXT_UNDERLINE;

public class PreLoginMode implements ClientMode {
    private final ServerFacade facade;

    public PreLoginMode(ServerFacade facade) {
        this.facade = facade;
        System.out.println(help());
        // this will print every time we come back to preLoginMode
    }

    @Override
    public String help() {
        return String.format("""
                        Available commands:
                        register <USERNAME> <PASSWORD> <EMAIL> - %sto create an account AND login%s
                        login <USERNAME> <PASSWORD>            - %sto play chess%s
                        quit                                   - %sexit the program%s
                        help                                   - %swith possible commands%s
                        """,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE
        );
    }

    @Override
    public ClientMode eval(String input) {
        var tokens = input.split(" ");
        var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
        // the ? is a way of saying "value if true : value if false"
        // this allows us to go to help if the command is empty
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (cmd) {
            case "login":
                return login(params);
            case "register":
                return register(params);
            case "quit":
                return null;
            default:
                System.out.println("Command not recognized: " + cmd);
                System.out.println(help());
                return this;
        }
    }

    // ...

    private ClientMode login(String... params) {
        if (params.length == 2) {
            var result = facade.login(new LoginRequest(params[0], params[1]));
            if (result.username() == null || result.authToken() == null) {
                System.out.println("Login failed. " + result.message());
                System.out.println("Usage: login <username> <password>");
                return this;
            }
            System.out.println("Welcome back, " + result.username() + "! \uD83D\uDE0E");
            return new PostLoginMode(facade, result.username(), result.authToken());
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
            LoginResult result = facade.register(request);

            if (result.username() == null || result.authToken() == null) {
                System.out.println("Registration failed. " + result.message());
                System.out.println("Usage: register <username> <password> <email>");
                return this;
            }

            System.out.println("Welcome, " + result.username() + "! You are now logged in. \uD83D\uDE0E");
            return new PostLoginMode(facade, result.username(), result.authToken());
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

