package space.nbtca.mc;
import net.minecraft.server.MinecraftServer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import space.nbtca.mc.Packet.*;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
public abstract class NotificationWsClient extends WebSocketClient {
    private final Logger logger;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public NotificationWsClient(MinecraftServer server, URI serverUri, String token) {
        super(serverUri);
        this.logger = Logger.getLogger("MessageBridge");
        this.addHeader("Authorization", "Bearer " + token);
        this.addHeader("client-type", "minecraft");
        this.addHeader("client-subtype", "java");
        this.addHeader("client-version", server.getVersion());
        this.addHeader("client-name", server.getName());
        this.addHeader("address", serverUri.toString());
    }
    public void start() {
        this.connect();
        scheduler.scheduleAtFixedRate(() -> {
            if (!this.isOpen()) {
                try {
                    logger.info("Reconnecting..." + this.getURI());
                    this.reconnectBlocking();
                } catch (Exception e) {
                    logger.warning("Reconnect failed: " + e.getMessage());
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Connected to " + this.getURI() + " [" + handshakedata.getHttpStatus() + "] " + handshakedata.getHttpStatusMessage());
    }
    @Override
    public void onMessage(String message) {
        logger.info("Received message: " + message);
        processPacket(message);
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("Closed " + code + " " + reason + " " + remote);
    }
    @Override
    public void onError(Exception ex) {
        logger.warning("Error: " + ex.getMessage());
    }
    public <T extends BasePacket> void sendPacket(T packet) {
        //发送消息
        send(packet.toJson());
    }
    public abstract void onGroupMessage(GroupMessagePacket pkt);
    public abstract GetPlayerListResponsePacket.PlayerInfo[] onGetPlayerList();
    public void processPacket(String message) {
        //处理消息
        BasePacket.fromJson(message).ifPresentOrElse(packet -> {
            logger.info("Received packet: " + packet);
            switch (packet.getType()) {
                case GROUP_MESSAGE:
                    onGroupMessage((GroupMessagePacket) packet);
                    break;
                case GET_PLAYER_LIST_REQUEST:
                    sendPacket(new GetPlayerListResponsePacket(((GetPlayerListRequestPacket) packet).getRequestId(), onGetPlayerList()));
                    break;
                case ACTIVE_CLIENTS_CHANGE:
                    logger.info("Active clients changed: " + Arrays.toString(((ActiveBroadcastPacket) packet).getClients()));
                    break;
                default:
                    logger.warning("Unknown packet type: " + packet.getType());
            }
        }, () -> logger.warning("Failed to parse packet: " + message));
    }
}

