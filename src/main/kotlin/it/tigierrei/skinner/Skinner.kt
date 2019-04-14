package it.tigierrei.skinner

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.sainttx.holograms.api.HologramManager
import com.sainttx.holograms.api.HologramPlugin
import it.tigierrei.skinner.listeners.MythicMobsListener
import it.tigierrei.skinner.listeners.PacketListener
import it.tigierrei.skinner.managers.DataManager
import org.bukkit.plugin.java.JavaPlugin
import org.mineskin.MineskinClient

class Skinner : JavaPlugin() {

    lateinit var dataManager: DataManager
    private lateinit var protocolManager: ProtocolManager
    lateinit var hologramManager: HologramManager
    lateinit var mineskinClient: MineskinClient

    override fun onLoad() {
        super.onLoad()
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun onEnable() {
        super.onEnable()

        dataManager = DataManager(this)
        hologramManager = getPlugin(HologramPlugin::class.java).hologramManager
        mineskinClient = MineskinClient()

        //Commands executors
        //this.getCommand().setExecutor()

        //Listeners
        server.pluginManager.registerEvents(MythicMobsListener(this),this)

        //Packets listeners
        protocolManager.addPacketListener(PacketListener(this,ListenerPriority.NORMAL,PacketType.Play.Server.SPAWN_ENTITY))
        protocolManager.addPacketListener(PacketListener(this,ListenerPriority.NORMAL,PacketType.Play.Server.REL_ENTITY_MOVE_LOOK))
    }
}