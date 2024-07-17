package space.nbtca.mc.Packet;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class PlayerChatPacket extends BasePacket {
    @Override
    public PacketType getType() {
        return PacketType.PLAYER_CHAT;
    }
    private final String playerName;
    private final String message;
}
