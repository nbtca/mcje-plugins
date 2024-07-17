package space.nbtca.mc;
import de.exlll.configlib.YamlConfigurations;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import space.nbtca.mc.Packet.BasePacket;
import space.nbtca.mc.Packet.PlayerChatPacket;

import java.net.URI;
import java.nio.file.Paths;
public final class MessageBridge extends JavaPlugin implements Listener {
    private NotificationWsClient wsClient;
    private UserConfiguration config = new UserConfiguration();
    @Override
    public void onEnable() {
        var configFile = Paths.get(getDataFolder().getAbsolutePath(), "config.yml");
        if (configFile.toFile().exists()) {
            config = YamlConfigurations.load(configFile, UserConfiguration.class);
        }
        YamlConfigurations.save(configFile, UserConfiguration.class, config);
        getLogger().info("Loaded config: " + config.toString());
        getServer().getPluginManager().registerEvents(this, this);
//        wsClient = new NotificationWsClient(
//                getLogger(),
//                URI.create(config.getNotificationCenterWsAddress()),
//                config.getNotificationCenterToken());
//        wsClient.connect();
        var json = new PlayerChatPacket("test", "test").toJson();
        getLogger().info(json);
        var packet = BasePacket.fromJson(json);
        getLogger().info(packet.toString());

        //auto reconnect
//        new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(1000 * 60);
//                    if (!wsClient.isOpen()) {
//                        getLogger().info("Reconnecting...");
//                        wsClient.reconnect();
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
    @Override
    public void onDisable() {
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        String name = event.getPlayer().getName();
    }
}
