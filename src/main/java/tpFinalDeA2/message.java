package tpFinalDeA2;

import java.util.Map;

public class message {
    public String type;
    public Integer playerId;
    public Map<String, Object> payload;

    public message() {}
    public message(String type, Integer playerId, Map<String, Object> payload) {
        this.type = type;
        this.playerId = playerId;
        this.payload = payload;
    }
}