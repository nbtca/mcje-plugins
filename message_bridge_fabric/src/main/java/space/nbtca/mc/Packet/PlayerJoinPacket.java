package space.nbtca.mc.Packet;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PlayerJoinPacket extends BasePacket {
    @Override
    public PacketType getType() {
        return PacketType.PLAYER_JOIN;
    }
    private final String playerName;
}
