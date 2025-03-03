package results;

public record LoginResult(String message, String username, String authToken) {
    // NOTE: this class is shared for login and registration because they have the same fields
}
