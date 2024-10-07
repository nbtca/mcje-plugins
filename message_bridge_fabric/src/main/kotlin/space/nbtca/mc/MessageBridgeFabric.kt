package space.nbtca.mc

import de.exlll.configlib.YamlConfigurations
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.mixin.event.interaction.ServerPlayerEntityMixin
import net.minecraft.advancement.Advancement
import net.minecraft.entity.damage.DamageSource
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import space.nbtca.mc.Packet.*
import java.io.File
import java.net.URI
import java.nio.file.Paths

class MessageBridgeFabric : ModInitializer {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: MessageBridgeFabric
    }

    private lateinit var wsClient: NotificationWsClient
    private lateinit var config: UserConfiguration
    private val logger: Logger = LogManager.getLogger("MessageBridge");
    private fun setServerInformation(server: MinecraftServer) {
        var serverName = server.getName()
        var serverVersion = server.getVersion()
        var info = BasePacket.SenderInformation(config.getDisplayName(), serverName, serverVersion)
        BasePacket.setServerInfo(info)
    }

    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register { server: MinecraftServer ->
            var configFile = Paths.get(getDataFolder().getAbsolutePath(), "config.yml")
            logger.info("Config file: $configFile")
            if (configFile.toFile().exists()) {
                config = YamlConfigurations.load(configFile, UserConfiguration::class.java)
            } else {
                config = UserConfiguration()
            }
            YamlConfigurations.save(configFile, UserConfiguration::class.java, config)
            logger.info("Loaded config: $config")
            setServerInformation(server)
            startWebsocket(server)
        }
//        ServerLifecycleEvents.SERVER_STOPPING.register { server: MinecraftServer ->
//             logger.info("ServerStopping")
//        }
        ServerMessageEvents.CHAT_MESSAGE.register { message, sender, params ->
            val message = message.content.string
            val playerName = sender.name.string
            var packet = PlayerChatPacket(playerName, message)
            wsClient.sendPacket(packet)
        }
//        ServerMessageEvents.GAME_MESSAGE.register { server, message, overlay ->
//            val message = message.string
//             logger.info("[Message Bridge DEBUG] GameMessage: $message")
//        }
        ServerPlayConnectionEvents.JOIN.register { handler, sender, server ->
            val player = handler.player
            val playerName = player.name.string
            var packet = PlayerJoinPacket(playerName)
            wsClient.sendPacket(packet)
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, server ->
            val player = handler.player
            val playerName = player.name.string
            var packet = PlayerQuitPacket(playerName)
            wsClient.sendPacket(packet)
        }
        //PlayerAdvancementTracker
    }

    fun onPlayerDeath(player: ServerPlayerEntity, damageSource: DamageSource) {
        val name = player.name.string
        val deathMessage = damageSource.getDeathMessage(player).string
        val packet = PlayerDeathPacket(name, deathMessage)
        wsClient.sendPacket(packet)
    }

    fun onAdvancementAchieved(owner: ServerPlayerEntity, title: String, description: String, criteria: Array<String>) {
        val playerName = owner.name.string
        val packet =
            PlayerAchievementPacket(playerName, title, description, criteria)
        wsClient.sendPacket(packet)
    }


    fun startWebsocket(server: MinecraftServer) {
        wsClient = object : NotificationWsClient(
            server,
            URI.create(config.notificationCenterWsAddress),
            config.notificationCenterToken
        ) {
            override fun onGroupMessage(pkt: GroupMessagePacket) {
                val msg = "[${pkt.groupName}] <${pkt.senderName}> ${pkt.message}"
                val text = Text.of(msg)
                server.playerManager.broadcast(text, false)
            }

            override fun onGetPlayerList(): Array<GetPlayerListResponsePacket.PlayerInfo> {
                return server.playerManager.playerList.map { player ->
                    val pos = player.blockPos
                    val world = player.world?.registryKey?.value?.toString() ?: "null"
                    GetPlayerListResponsePacket.PlayerInfo(
                        player.name.string, player.uuidAsString,
                        player.networkHandler.latency,
                        intArrayOf(pos.x, pos.y, pos.z),
                        world
                    )
                }.toTypedArray()
            }
        }
        wsClient.start()
    }
}

private fun MessageBridgeFabric.getDataFolder(): File {
    val dataFolder = File("config", "message-bridge")
    if (!dataFolder.exists()) {
        dataFolder.mkdirs()
    }
    return dataFolder
}