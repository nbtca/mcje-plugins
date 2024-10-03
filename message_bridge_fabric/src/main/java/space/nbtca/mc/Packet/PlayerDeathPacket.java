package space.nbtca.mc.Packet;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PlayerDeathPacket extends BasePacket {
    @Override
    public PacketType getType() {
        return PacketType.PLAYER_DEATH;
    }
    private final String playerName;
    private final String deathMessage;
}
