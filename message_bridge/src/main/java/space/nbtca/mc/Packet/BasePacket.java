package space.nbtca.mc.Packet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Optional;
public abstract class BasePacket {
    public abstract PacketType getType();
    private static final Gson GSON = new Gson();
    public String toJson() {
        var data = GSON.toJsonTree(this);
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType().getName());
        jsonObject.add("data", data);
        return jsonObject.toString();
    }
    public static Optional<BasePacket> fromJson(String json) {
        var jsonObject = GSON.fromJson(json, JsonObject.class);
        var typeOptional = PacketType.fromName(jsonObject.get("type").getAsString());
        var data = jsonObject.get("data");
        if (typeOptional.isEmpty() || data == null) {
            return Optional.empty();
        }
        return GSON.fromJson(data, typeOptional.get().getPacketClass());
    }
}
