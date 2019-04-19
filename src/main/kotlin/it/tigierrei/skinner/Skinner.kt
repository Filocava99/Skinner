package it.tigierrei.skinner

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.sainttx.holograms.api.HologramManager
import com.sainttx.holograms.api.HologramPlugin
import it.tigierrei.skinner.commands.SkinnerCommand
import it.tigierrei.skinner.listeners.EntityListener
import it.tigierrei.skinner.listeners.MythicMobsListener
import it.tigierrei.skinner.listeners.NPCListener
import it.tigierrei.skinner.listeners.PacketListener
import it.tigierrei.skinner.managers.DataManager
import it.tigierrei.skinner.utils.Scheduler
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
        val scheduler = Scheduler(this)
        hologramManager = getPlugin(HologramPlugin::class.java).hologramManager
        mineskinClient = MineskinClient()

        //Commands executors
        this.getCommand("skinner")?.setExecutor(SkinnerCommand(this))

        //Listeners
        if(dataManager.mythicMobs) {
            server.pluginManager.registerEvents(MythicMobsListener(this), this)
        }
        if(dataManager.citizens){
            server.pluginManager.registerEvents(NPCListener(this),this)
        }
        server.pluginManager.registerEvents(EntityListener(this),this)

        //Packets listeners
        protocolManager.addPacketListener(PacketListener(this,ListenerPriority.NORMAL,PacketType.Play.Server.SPAWN_ENTITY))
        protocolManager.addPacketListener(PacketListener(this,ListenerPriority.NORMAL,PacketType.Play.Server.NAMED_ENTITY_SPAWN))
        protocolManager.addPacketListener(PacketListener(this,ListenerPriority.NORMAL,PacketType.Play.Server.REL_ENTITY_MOVE_LOOK))
    }
}