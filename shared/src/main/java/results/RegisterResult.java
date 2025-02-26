package results;

public record RegisterResult(String message, String username, String authToken) {
    // include a message, this will not appear on success
    // however, only message appears on fail
}
