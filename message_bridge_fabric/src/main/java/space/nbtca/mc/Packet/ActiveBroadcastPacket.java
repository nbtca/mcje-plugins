package space.nbtca.mc.Packet;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ActiveBroadcastPacket extends BasePacket {
    @Override
    public PacketType getType() {
        return PacketType.ACTIVE_CLIENTS_CHANGE;
    }
    private ClientInfo[] Clients;
    @Data
    public static class ClientInfo {
        private String Address;
        private java.util.Map<String, String[]> Headers;
    }
}