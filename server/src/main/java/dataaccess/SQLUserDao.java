package dataaccess;

import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLUserDao implements UserDAO{
    @Override
    public UserData getUser(String username) throws SQLException {
        // prep the statement
        // We need to retrieve user details based on the given username
        String statement = "SELECT username, password, email FROM users WHERE username = ?";

        // try with resources
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {

                // set the ? value to be the username
                preparedStatement.setString(1, username);

                // do the query
                try (var resultSet = preparedStatement.executeQuery()) {
                    // get the result and make sure that it exists before checking it
                    if (resultSet.next()) {
                        String retrievedUsername = resultSet.getString("username");
                        String retrievedPassword = resultSet.getString("password");
                        String retrievedEmail = resultSet.getString("email");

                        System.out.println("inside of getUser");
                        System.out.println("Retrieved username: " + retrievedUsername);
                        System.out.println("Retrieved email: " + retrievedEmail);

                        // make into object before return
                        return new UserData(retrievedUsername, retrievedPassword, retrievedEmail);
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new SQLException(e.getMessage());
        }

        return null; // this is reached if the try does not have rs.next (empty response) so we return null
    }


    @Override
    public void createUser(UserData data) throws SQLException {
        // prep the statement
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        // try with resources
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                // set the ? values to be the user details
                preparedStatement.setString(1, data.username());
                preparedStatement.setString(2, data.password());
                preparedStatement.setString(3, data.email());

                preparedStatement.executeUpdate();

                // sanity check / debug
                System.out.println("✅ User created with username: " + data.username());
            }
        } catch (DataAccessException | SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }


    @Override
    public void clear() {

    }

    @Override
    public ArrayList<UserData> getAllUsers() {
        return null;
    }
}
