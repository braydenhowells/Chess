package exception;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
  private final int statusCode;

  public ResponseException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int statusCode() {
    return statusCode;
  }

  public String toJson() {
    return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
  }

  public static ResponseException fromJson(InputStream stream) {
    Map map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);

    // Debug (optional):
    // System.out.println("Parsed error response: " + map);

    // Safely get the status code
    Object rawStatus = map.get("status");
    int statusCode;
    if (rawStatus instanceof Number) {
      statusCode = ((Number) rawStatus).intValue();
    } else {
      statusCode = 500; // Default to 500 if missing or invalid
    }

    // Safely get the message
    Object rawMessage = map.get("message");
    String message = (rawMessage != null) ? rawMessage.toString() : "Unknown error occurred";

    return new ResponseException(statusCode, message);
  }
}
