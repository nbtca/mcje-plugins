package space.nbtca.mc.Packet;
import com.google.gson.*;
import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;
@Data
public abstract class BasePacket {
    @Expose(serialize = false, deserialize = false)
    private SenderInformation source;
    @Data
    @AllArgsConstructor
    public static class SenderInformation {
        public String displayName;
        public String name;
        public String version;
    }
    private static JsonElement serverInfo;
    public static void setServerInfo(SenderInformation serverInfo) {
        BasePacket.serverInfo = GSON.toJsonTree(serverInfo);
    }
    public abstract PacketType getType();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
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
            result.source = GSON.fromJson(source, SenderInformation.class);
        }
        return Optional.of(result);
    }
}
