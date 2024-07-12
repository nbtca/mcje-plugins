package space.nbtca.mc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
public final class MessageBridge extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
//        saveResource("config.yml", /* replace */ false);
        saveDefaultConfig();
//        getConfig().
    }
    @Override
    public void onDisable() {
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();

    }
}
