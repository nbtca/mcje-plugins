package space.nbtca.mc.Packet;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PlayerQuitPacket extends BasePacket {
    @Override
    public PacketType getType() {
        return PacketType.PLAYER_QUIT;
    }
    private final String playerName;
}

