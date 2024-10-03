package space.nbtca.mc.Packet;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GroupMessagePacket extends BasePacket {
    @Override
    public PacketType getType() {
        return PacketType.GROUP_MESSAGE;
    }
    private final String groupId;
    private final String groupName;
    private final String senderId;
    private final String senderName;
    private final String message;
}
