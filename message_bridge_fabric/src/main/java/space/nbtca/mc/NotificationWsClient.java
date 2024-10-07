package space.nbtca.mc;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import space.nbtca.mc.Packet.*;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public abstract class NotificationWsClient extends WebSocketClient {
    private final Logger logger = LogManager.getLogger("MessageBridge/NotificationWsClient");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public NotificationWsClient(MinecraftServer server, URI serverUri, String token) {
        super(serverUri);
        this.addHeader("Authorization", "Bearer " + token);
        this.addHeader("client-type", "minecraft");
        this.addHeader("client-subtype", "java");
        this.addHeader("client-version", server.getVersion());
        this.addHeader("client-name", server.getName());
        this.addHeader("address", serverUri.toString());
    }
    public void start() {
//        new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(1000 * 10);
//                    if (!this.isOpen()) {
//                        logger.info("Reconnecting...{}", this.getURI());
//                        this.connectBlocking(5, TimeUnit.SECONDS);
//                    }
//                } catch (Exception e) {
//                    logger.warn("Reconnect failed: {}", e.getMessage());
//                }
//            }
//        }).start();
        this.connect();
        scheduler.scheduleAtFixedRate(() -> {
            if (!this.isOpen()) {
                try {
                    logger.info("Reconnecting...{}", this.getURI());
                    this.reconnect();
                } catch (Exception e) {
                    logger.warn("Reconnect failed: {}", e.getMessage());
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("Connected to {} [{}] {}", this.getURI(), handshakedata.getHttpStatus(), handshakedata.getHttpStatusMessage());
    }
    @Override
    public void onMessage(String message) {
        logger.info("Received message: {}", message);
        processPacket(message);
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("Closed {} {} {}", code, reason, remote);
    }
    @Override
    public void onError(Exception ex) {
        logger.warn("Error: {}", ex.getMessage());
    }
    public <T extends BasePacket> void sendPacket(T packet) {
        //发送消息
        try {
            send(packet.toJson());
        } catch (Exception e) {
            logger.warn("Failed to send packet: {}", e.getMessage());
        }
    }
    public abstract void onGroupMessage(GroupMessagePacket pkt);
    public abstract GetPlayerListResponsePacket.PlayerInfo[] onGetPlayerList();
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();
    public void processPacket(String message) {
        //处理消息
        BasePacket.fromJson(message).ifPresentOrElse(packet -> {
            logger.info("Received packet: {}", packet);
            switch (packet.getType()) {
                case GROUP_MESSAGE:
                    onGroupMessage((GroupMessagePacket) packet);
                    break;
                case GET_PLAYER_LIST_REQUEST:
                    sendPacket(new GetPlayerListResponsePacket(((GetPlayerListRequestPacket) packet).getRequestId(), onGetPlayerList()));
                    break;
                case ACTIVE_CLIENTS_CHANGE:
                    var items = ((ActiveBroadcastPacket) packet).getClients();
                    var json = GSON.toJson(items);
                    logger.info("Active clients changed: {}", json);
                    break;
                default:
                    logger.warn("Unknown packet type: {}", packet.getType());
            }
        }, () -> logger.warn("Failed to parse packet: {}", message));
    }
}

