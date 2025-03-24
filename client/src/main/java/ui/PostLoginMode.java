package ui;

import model.GameData;
import requests.CreateRequest;
import requests.JoinRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ui.EscapeSequences.RESET_TEXT_UNDERLINE;
import static ui.EscapeSequences.SET_TEXT_UNDERLINE;

public class PostLoginMode implements ClientMode {
    private final ServerFacade facade;
    private final String username;
    private List<GameData> currentGamesList = new ArrayList<>();


    public PostLoginMode(ServerFacade facade, String username) {
        this.facade = facade;
        this.username = username;
        System.out.println(help());  // as soon as we enter, the user sees the help text
    }

    @Override
    public String help() {
        return String.format("""
            create <NAME>     - %sto create a game%s
            list              - %sto list games%s
            join <ID> <color> - %sto join a game%s
            observe <ID>      - %sa game%s
            logout            - %sreturn to login screen%s
            quit              - %sexit the game%s
            help              - %sshow commands%s
            """,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
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
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (cmd) {
            case "logout":
                facade.logout();
                System.out.println("Goodbye, " + username + "!");
                return new PreLoginMode(facade);
            case "join":
                return join(params);
            case "list":
                return list();
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

    private ClientMode list() {
        var result = facade.list();
        if (result.message() != null && result.message().contains("Error")) {
            System.out.println("Failed to list games. " + result.message());
            return this;
        }

        var games = result.games();
        if (games == null || games.isEmpty()) {
            System.out.println("There are no games yet! Try creating your own \uD83D\uDC51");
            System.out.println("Usage: create <game name>");
            return this;
        }

        // this means that we have a legit games list
        currentGamesList = games; // keep it for joining and observing later

        System.out.println("Available games:");
        // make a key to help understand
        String listKey = String.format("%sID. \"name\"  |  White user:  |  Black user:%s",
                SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE);
        System.out.println(listKey);
        for (int i = 0; i < games.size(); i++) {
            var game = games.get(i);
            String userViewID = String.valueOf(i + 1);
            String whiteUsername = usernameCheck(game.whiteUsername());
            String blackUsername = usernameCheck(game.blackUsername());
            System.out.println(userViewID + ". " + "\"" + game.gameName() + "\"  |  White: " +
                    whiteUsername + "  |  Black: " + blackUsername);
        }

        return this;
    }

    // helper for list games. makes life easier with games that are not filled with players
    private String usernameCheck(String name) {
        if (name == null || name.isEmpty()) {
            return "(empty)";
        }
        else {
            return name;
        }
    }

    private ClientMode join(String... params) {
        // check params
        if (params.length > 2) {
            System.out.println("Unable to join game. Too many parameters entered.");
            System.out.println("Usage: join <ID> <color>");
            return this;
        }
        if (params.length < 2) {
            System.out.println("Unable to join game. Not enough parameters entered.");
            System.out.println("Usage: join <ID> <color>");
            return this;
        }
        // make sure ID is valid
        if (!isNumber(params[0])) {
            System.out.println("Unable to join game. Game ID must be a number.");
            System.out.println("Usage: list");
            return this;
        }
        if (Integer.parseInt(params[0]) < 1) {
            System.out.println("Unable to join game. Game ID must be positive.");
            System.out.println("Usage: join <ID> <color>");
            return this;
        }
        if (Integer.parseInt(params[0]) > (currentGamesList.size())) {
            System.out.println("Unable to join game. Game ID does not match any existing games.");
            System.out.println("Usage: list");
            return this;
        }
        // try black and white join
        String uC = params[1];
        String gameID = String.valueOf(Integer.parseInt(params[0]) - 1);
        int intGameID = Integer.parseInt(params[0]) - 1;
        String gameName = currentGamesList.get(intGameID).gameName();

        if (uC.equalsIgnoreCase("black")) {
            facade.join(new JoinRequest("BLACK", gameID));
            return new GameMode(this.facade, this.username, gameID, gameName, "BLACK");
        }
        else if (uC.equalsIgnoreCase("white")) {
            facade.join(new JoinRequest("WHITE", gameID));
            return new GameMode(this.facade, this.username, gameID, gameName, "WHITE");
        }
        else {
            String helpText = String.format("Unable to join game. Color must be either %sblack%s or %swhite%s.",
                    SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE,
                    SET_TEXT_UNDERLINE, RESET_TEXT_UNDERLINE
            );
            System.out.println(helpText);
            System.out.println("Usage: join <ID> <color>");
            return this;
        }
    }

    // helper for join. makes sure the game ID from user is a number
    private boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}