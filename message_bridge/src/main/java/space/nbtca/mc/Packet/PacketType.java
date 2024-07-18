package space.nbtca.mc.Packet;
import lombok.Getter;

import java.util.Optional;
@Getter
public enum PacketType {
    PLAYER_CHAT("player_chat"), PLAYER_JOIN("player_join"), PLAYER_QUIT("player_quit"), PLAYER_DEATH("player_death"), GROUP_MESSAGE("group_chat");
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
        };
        //noinspection unchecked
        return (Class<T>) type;
    }
}
