package space.nbtca.mc;
import de.exlll.configlib.YamlConfigurations;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import space.nbtca.mc.Packet.*;

import java.net.URI;
import java.nio.file.Paths;
public final class MessageBridge extends JavaPlugin implements Listener {
    private NotificationWsClient wsClient;
    private UserConfiguration config = new UserConfiguration();
    private void setServerInformation() {
        var serverName = this.getServer().getName();
        var serverVersion = this.getServer().getVersion();
        var info = new BasePacket.ServerInformation(serverName, serverVersion);
        BasePacket.setServerInfo(info);
    }
    private void startWebsocket() {
        wsClient = new NotificationWsClient(
                getLogger(),
                URI.create(config.getNotificationCenterWsAddress()),
                config.getNotificationCenterToken());
        wsClient.start();
    }
    @Override
    public void onEnable() {
        var configFile = Paths.get(getDataFolder().getAbsolutePath(), "config.yml");
        if (configFile.toFile().exists()) {
            config = YamlConfigurations.load(configFile, UserConfiguration.class);
        }
        YamlConfigurations.save(configFile, UserConfiguration.class, config);
        getLogger().info("Loaded config: " + config.toString());
        getServer().getPluginManager().registerEvents(this, this);
        setServerInformation();
        startWebsocket();
    }
    @Override
    public void onDisable() {
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        var message = event.getMessage();
        var name = event.getPlayer().getName();
        var packet = new PlayerChatPacket(name, message);
        wsClient.sendPacket(packet);
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var name = event.getPlayer().getName();
        var packet = new PlayerJoinPacket(name);
        wsClient.sendPacket(packet);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var name = event.getPlayer().getName();
        var packet = new PlayerQuitPacket(name);
        wsClient.sendPacket(packet);
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        var name = event.getEntity().getName();
        var deathMessage = event.getDeathMessage();
        var packet = new PlayerDeathPacket(name, deathMessage);
        wsClient.sendPacket(packet);
    }
}
