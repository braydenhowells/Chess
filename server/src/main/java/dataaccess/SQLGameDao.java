package dataaccess;

import model.GameData;
import results.SimpleResult;

import java.sql.SQLException;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDao implements GameDAO{
    public SQLGameDao() {
        configureDatabase();
    }


    private final String[] createStatements = {
           // games
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) NOT NULL,
              `blackUsername` varchar(256) NOT NULL,
              `name` varchar(256) NOT NULL,
              `game_json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(`name`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            // users
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            // auths
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `username` varchar(256) NOT NULL,
              `authToken` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private final String[] usersStatements = {

    };

    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (var statement : createStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                        System.out.println("inside of config");
                    }
                }
            }
        }

        catch (SQLException | DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    private int executeUpdate(String statement, Object... params) {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof GameData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            System.out.println(String.format("unable to update database: %s, %s", statement, e.getMessage()));
            return 1;
        }
    }


    @Override
    public void clear() throws SQLException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public void create(GameData data) {

    }

    @Override
    public List<GameData> findAll() {
        return List.of();
    }

    @Override
    public GameData find(String gameID) {
        return null;
    }

    @Override
    public void remove(String gameID) {

    }
}
