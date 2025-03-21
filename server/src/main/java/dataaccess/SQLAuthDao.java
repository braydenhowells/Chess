package dataaccess;

import model.AuthData;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLAuthDao implements AuthDAO {

    @Override
    public void createAuth(AuthData data) throws SQLException {
        // prep the statement
        // We need to insert a new auth record into the auths table
        String statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";

        // try with resources to ensure the connection closes properly
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            // set the ? values to be the auth data details
            preparedStatement.setString(1, data.authToken());
            preparedStatement.setString(2, data.username());

            preparedStatement.executeUpdate();
        } catch (DataAccessException | SQLException e) {
            throw new SQLException("Error creating auth", e);
        }
    }

    @Override
    public void clear() throws SQLException {
        // prep the statement
        // TRUNCATE removes all auth records
        String statement = "TRUNCATE auths";

        // try with resources to ensure the connection closes properly
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            // execute the truncate statement
            preparedStatement.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new SQLException("Error clearing auth table", e);
        }
    }

    @Override
    public AuthData findAuthData(String authToken) throws SQLException {
        // prep the statement
        // We need to find the auth data that matches the given authToken
        String statement = "SELECT authToken, username FROM auths WHERE authToken = ?";

        // try with resources to ensure the connection closes properly
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            // set the ? value to be the auth token
            preparedStatement.setString(1, authToken);

            // do the query
            try (var resultSet = preparedStatement.executeQuery()) {
                // get the result. make sure that a rs.next exists before checking it
                if (resultSet.next()) {
                    String retrievedAuthToken = resultSet.getString("authToken");
                    String retrievedUsername = resultSet.getString("username");
                    // return an AuthData object with the retrieved values
                    return new AuthData(retrievedAuthToken, retrievedUsername);
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new SQLException("Error finding auth data", e);
        }

        return null; // this is reached if no auth record is found
    }

    @Override
    public void deleteAuthData(AuthData data) throws SQLException {
        // prep the statement
        // We need to delete the auth record for the given authToken
        String statement = "DELETE FROM auths WHERE authToken = ?";

        // try with resources to ensure the connection closes properly
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement)) {

            // set the ? value to be the auth token
            preparedStatement.setString(1, data.authToken());

            preparedStatement.executeUpdate();

        } catch (DataAccessException | SQLException e) {
            throw new SQLException("Error deleting auth data", e);
        }
    }

    @Override
    public ArrayList<AuthData> getAllAuth() throws SQLException {
        // prep the statement
        // We need to retrieve all auth records from the auths table
        String statement = "SELECT authToken, username FROM auths";

        // list to store all retrieved AuthData objects
        ArrayList<AuthData> authList = new ArrayList<>();

        // try with resources to ensure the connection closes properly
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement);
             var resultSet = preparedStatement.executeQuery()) {

            // iterate through the result set and collect auth data
            while (resultSet.next()) {
                String retrievedAuthToken = resultSet.getString("authToken");
                String retrievedUsername = resultSet.getString("username");

                // create a new AuthData object and add it to the list
                authList.add(new AuthData(retrievedAuthToken, retrievedUsername));
            }

        } catch (DataAccessException | SQLException e) {
            throw new SQLException("Error retrieving all auths", e);
        }

        return authList; // return the list of all auth records
    }
}

