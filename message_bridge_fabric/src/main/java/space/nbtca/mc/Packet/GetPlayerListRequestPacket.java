package space.nbtca.mc.Packet;
import lombok.*;
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class GetPlayerListRequestPacket extends BasePacket {
    @Override
    public PacketType getType() {
        return PacketType.GET_PLAYER_LIST_REQUEST;
    }
    private String requestId;
}
