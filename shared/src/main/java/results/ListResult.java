package results;

import model.GameData;

import java.util.List;

public record ListResult(String message, List<GameData> games) {
}
