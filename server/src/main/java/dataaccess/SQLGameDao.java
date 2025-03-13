package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class SQLGameDao implements GameDAO{

    @Override
    public void clear() throws SQLException {
        String statement = "TRUNCATE games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
                System.out.println("inside of game clear");
                System.out.println(statement);
            }
        } catch (DataAccessException | SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public int create(GameData data) throws SQLException {
        // serialize the statement for storing as a string, SQL cannot store a gameData object
        String chessGameObjectString = new Gson().toJson(data.game());

        String statement;
        // first statement: one for initial creation (dummy ID = 69)
        if (data.gameID() == 0) {
            statement = "INSERT INTO games (game_json, name, whiteUsername, blackUsername) VALUES (?, ?, ?, ?)";
        }
        // second statement: used for insert (join by manual delete and recreate)
        else {
            statement = "INSERT INTO games (game_json, name, whiteUsername, blackUsername, gameID) VALUES (?, ?, ?, ?, ?)";
        }

        // try with resources
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                // set the ? value to be our gameData json string
                preparedStatement.setString(1, chessGameObjectString);
                preparedStatement.setString(2, data.gameName());
                preparedStatement.setString(3, data.whiteUsername());
                preparedStatement.setString(4, data.blackUsername());

                if (data.gameID() != 0) {
                    preparedStatement.setString(5, String.valueOf(data.gameID()));
                }

                preparedStatement.executeUpdate();

                // sanity check / debug
                System.out.println("inside of game create");
                System.out.println(statement);

                // now retrieve the gameID we just made
                try (var resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        int generatedGameID = resultSet.getInt(1);
                        System.out.println("✅ Game created with auto-incremented ID: " + generatedGameID);
                        return resultSet.getInt(1);  // return auto incremented gameID
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return 0;
    }

    @Override
    public List<GameData> findAll() {
        return List.of();
    }

    @Override
    public GameData find(String gameID) throws SQLException {
        // prep the statement
        String statement = "SELECT gameID, game_json, whiteUsername, blackUsername, name FROM games WHERE gameID = ?";

        // try w resources
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {

                // set the value of ? to gameID
                preparedStatement.setString(1, gameID);

                // do the query
                try (var resultSet = preparedStatement.executeQuery()) {
                    // get the result. make sure that a rs.next exists before checking it
                    if (resultSet.next()) {
                        int retrievedGameID = resultSet.getInt("gameID");
                        String gameJson = resultSet.getString("game_json");
                        String whiteUsername = resultSet.getString("whiteUsername");
                        String blackUsername = resultSet.getString("blackUsername");
                        String gameName = resultSet.getString("name");

                        System.out.println("inside of game find");
                        System.out.println("Retrieved gameID: " + retrievedGameID);
                        System.out.println("White Player: " + whiteUsername);
                        System.out.println("Black Player: " + blackUsername);
                        System.out.println("Game Name: " + gameName);

                        // turn chess json into a chess game object before returning
                        ChessGame chessGame = new Gson().fromJson(gameJson, ChessGame.class);
                        return new GameData(retrievedGameID, whiteUsername, blackUsername, gameName, chessGame);

                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return null; // this is reached if the try does not have rs.next (empty response) so we return null
    }

    @Override
    public void remove(String gameID) throws SQLException {
        // prep the statement
        String statement = "DELETE FROM games WHERE gameID = ?";
        // try w resources
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                // set the value of ? to gameID
                preparedStatement.setInt(1, Integer.parseInt(gameID));
                preparedStatement.executeUpdate();
            }
        } catch (DataAccessException | SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }
}
