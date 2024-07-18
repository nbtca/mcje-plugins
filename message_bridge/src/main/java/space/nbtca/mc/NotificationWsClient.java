package space.nbtca.mc;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import space.nbtca.mc.Packet.BasePacket;
import space.nbtca.mc.Packet.GroupMessagePacket;

import java.net.URI;
import java.util.logging.Logger;
public abstract class NotificationWsClient extends WebSocketClient {
    private final Logger logger;
    public NotificationWsClient(Logger logger, URI serverUri, String token) {
        super(serverUri);
        this.logger = logger;
        this.addHeader("Authorization", "Bearer " + token);
    }
    public void start() {
        this.connect();
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 10);
                    if (!this.isOpen()) {
                        logger.info("Reconnecting..." + this.getURI());
                        this.reconnectBlocking();
                    }
                } catch (Exception e) {
                    logger.warning("Reconnect failed: " + e.getMessage());
                }
            }
        }).start();
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
    public void processPacket(String message) {
        //处理消息
        BasePacket.fromJson(message).ifPresentOrElse(
                packet -> {
                    logger.info("Received packet: " + packet);
                    switch (packet.getType()) {
                        case GROUP_MESSAGE:
                            var pkt = (GroupMessagePacket) packet;
                            onGroupMessage(pkt);
                        default:
                            logger.warning("Unknown packet type: " + packet.getType());
                    }
                },
                () -> logger.warning("Failed to parse packet: " + message)
        );
    }
}

