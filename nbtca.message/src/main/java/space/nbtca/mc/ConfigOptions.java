package space.nbtca.mc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
@Data
@AllArgsConstructor
public class ConfigOptions implements ConfigurationSerializable {
    private int chunkX;
    private int chunkZ;
    private String name;
    public static ConfigOptions deserialize(Map<String, Object> args) {
        return new ConfigOptions(
                (int) args.get("chunk-x"),
                (int) args.get("chunk-z"),
                (String) args.get("name")
        );
    }
    @NonNull
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("chunk-x", this.chunkX);
        data.put("chunk-z", this.chunkZ);
        data.put("name", this.name);
        return data;
    }
}
