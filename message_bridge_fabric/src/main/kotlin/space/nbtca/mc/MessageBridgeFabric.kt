package space.nbtca.mc

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer

class MessageBridgeFabric : ModInitializer {
//    private NotificationWsClient wsClient;
    //    private UserConfiguration config = new UserConfiguration();
    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register { server: MinecraftServer ->
            println("ServerStarting")
        }
//        PlayerAdvancementTracker

        ServerLifecycleEvents.SERVER_STOPPING.register { server: MinecraftServer ->
            println("ServerStopping")
        }
        ServerMessageEvents.CHAT_MESSAGE.register { message, sender, params ->
            val message = message.content.string
            val playerName = sender.name
            val dimension = sender.serverWorld.dimension.effects.toString()
            println("ChatMessage: $playerName in $dimension: $message")
        }
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            val player = handler.player
            val playerName = player.name.string
            val dimension = player.serverWorld.dimension.effects.toString()
            println("PlayerJoin: $playerName in $dimension")
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            val player = handler.player
            val playerName = player.name.string
            val dimension = player.serverWorld.dimension.effects.toString()
            println("PlayerLeave: $playerName in $dimension")
        }
    }
}
