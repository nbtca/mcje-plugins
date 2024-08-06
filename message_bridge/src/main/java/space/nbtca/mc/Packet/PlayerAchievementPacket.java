package space.nbtca.mc.Packet;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PlayerAchievementPacket extends BasePacket {
    @Override
    public PacketType getType() {
        return PacketType.PLAYER_ACHIEVEMENT;
    }
    private final String playerName;
    private final String name;
    private final String description;
    private final String[] criteria;
}
