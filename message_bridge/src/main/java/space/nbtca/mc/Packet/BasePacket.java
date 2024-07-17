package space.nbtca.mc.Packet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;
@Data
public abstract class BasePacket {
    @Expose(serialize = false, deserialize = false)
    private ServerInformation source;
    @Data
    @AllArgsConstructor
    public static class ServerInformation {
        public String serverName;
        public String serverVersion;
//        public String serverIp;
//        public String serverPort;
    }
    private static JsonElement serverInfo;
    public static void setServerInfo(ServerInformation serverInfo) {
        BasePacket.serverInfo = GSON.toJsonTree(serverInfo);
    }
    public abstract PacketType getType();
    private static final Gson GSON = new Gson();
    public String toJson() {
        var data = GSON.toJsonTree(this);
        var jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType().getName());
        jsonObject.add("data", data);
        if (serverInfo != null) {
            jsonObject.add("source", serverInfo);
        }
        return jsonObject.toString();
    }
    public static Optional<BasePacket> fromJson(String json) {
        var jsonObject = GSON.fromJson(json, JsonObject.class);
        var typeOptional = PacketType.fromName(jsonObject.get("type").getAsString());
        var data = jsonObject.get("data");
        var source = jsonObject.get("source");
        if (typeOptional.isEmpty() || data == null) {
            return Optional.empty();
        }
        var result = GSON.fromJson(data, typeOptional.get().getPacketClass());
        if (source != null) {
            result.source = GSON.fromJson(source, ServerInformation.class);
        }
        return Optional.of(result);
    }
}
