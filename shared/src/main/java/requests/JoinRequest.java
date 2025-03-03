package requests;

public record JoinRequest(String playerColor, String gameID) {
    // possibly the gameID should be int, but we can try string now and convert later
}
