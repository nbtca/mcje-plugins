package space.nbtca.mc.Packet;
import lombok.Getter;

import java.util.Optional;
@Getter
public enum PacketType {
    PLAYER_CHAT("PlayerChat"),
    PLAYER_JOIN("PlayerJoin"),
    PLAYER_QUIT("PlayerQuit"),
    PLAYER_DEATH("PlayerDeath");
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
        switch (this) {
            case PLAYER_CHAT:
                return (Class<T>) PlayerChatPacket.class;
//            case PLAYER_JOIN:
//                return (Class<T>) PlayerJoinPacket.class;
//            case PLAYER_QUIT:
//                return (Class<T>) PlayerQuitPacket.class;
//            case PLAYER_DEATH:
//                return (Class<T>) PlayerDeathPacket.class;
            default:
                return (Class<T>) BasePacket.class;
        }
    }
}
