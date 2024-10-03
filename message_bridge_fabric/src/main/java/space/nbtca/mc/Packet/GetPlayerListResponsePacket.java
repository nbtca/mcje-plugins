package space.nbtca.mc.Packet;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetPlayerListResponsePacket extends BasePacket {
    @Override
    public PacketType getType() {
        return PacketType.GET_PLAYER_LIST_RESPONSE;
    }
    private String requestId;
    private PlayerInfo[] players;
    public record PlayerInfo(String name, String uuid, int ping, int[] position, String world) {}
}

