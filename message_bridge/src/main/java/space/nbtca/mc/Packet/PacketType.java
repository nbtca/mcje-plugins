package space.nbtca.mc.Packet;
import lombok.Getter;

import java.util.Optional;
@Getter
public enum PacketType {
    PLAYER_CHAT("player_chat"),//PlayerChat
    PLAYER_JOIN("player_join"),//PlayerJoin
    PLAYER_QUIT("player_quit"),//PlayerQuit
    PLAYER_DEATH("player_death"),//PlayerDeath
    GROUP_MESSAGE("group_message"),//GroupMessage
    GET_PLAYER_LIST_REQUEST("get_player_list_request"),//GetPlayerListRequest
    GET_PLAYER_LIST_RESPONSE("get_player_list_response"),//GetPlayerListResponse
    ACTIVE_CLIENTS_CHANGE("active_clients_change")//ActiveClientsChange
    ;
    private final String name;
    PacketType(String name) {
        this.name = name;
    }
    public static Optional<PacketType> fromName(String name) {
        for (PacketType value : values()) {
            if (value.getName().equals(name)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
    public <T extends BasePacket> Class<T> getPacketClass() {
        var type = switch (this) {
            case PLAYER_CHAT -> PlayerChatPacket.class;
            case PLAYER_JOIN -> PlayerJoinPacket.class;
            case PLAYER_QUIT -> PlayerQuitPacket.class;
            case PLAYER_DEATH -> PlayerDeathPacket.class;
            case GROUP_MESSAGE -> GroupMessagePacket.class;
            case GET_PLAYER_LIST_REQUEST -> GetPlayerListRequestPacket.class;
            case GET_PLAYER_LIST_RESPONSE -> GetPlayerListResponsePacket.class;
            case ACTIVE_CLIENTS_CHANGE -> ActiveBroadcastPacket.class;
        };
        //noinspection unchecked
        return (Class<T>) type;
    }
}
