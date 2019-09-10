package it.tigierrei.skinner

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import it.tigierrei.skinner.commands.SkinnerCommand
import it.tigierrei.skinner.listeners.EntityListener
import it.tigierrei.skinner.listeners.MythicMobsListener
import it.tigierrei.skinner.listeners.NPCListener
import it.tigierrei.skinner.managers.DataManager
import it.tigierrei.skinner.utils.Scheduler
import it.tigierrei.skinner.holograms.Hologram
import it.tigierrei.skinner.listeners.PacketsListener
import me.libraryaddict.disguise.DisguiseAPI
import net.citizensnpcs.api.CitizensAPI
import net.minecraft.server.v1_14_R1.PacketPlayOutEntity
import org.bukkit.plugin.java.JavaPlugin
import org.mineskin.MineskinClient
import org.inventivetalent.packetlistener.handler.ReceivedPacket
import org.inventivetalent.packetlistener.handler.SentPacket
import org.inventivetalent.packetlistener.handler.PacketHandler
import org.inventivetalent.packetlistener.PacketListenerAPI
import java.lang.IndexOutOfBoundsException

class Skinner : JavaPlugin() {

    lateinit var dataManager: DataManager
    private lateinit var protocolManager: ProtocolManager
    lateinit var mineskinClient: MineskinClient

    override fun onLoad() {
        super.onLoad()
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun onEnable() {
        super.onEnable()

        dataManager = DataManager(this)
        val scheduler = Scheduler(this)
        mineskinClient = MineskinClient()

        //Commands executors
        this.getCommand("skinner")?.setExecutor(SkinnerCommand(this))

        //Listeners
        if (dataManager.mythicMobs) {
            server.pluginManager.registerEvents(MythicMobsListener(this), this)
        }
        if (dataManager.citizens) {
            server.pluginManager.registerEvents(NPCListener(this), this)
        }
        server.pluginManager.registerEvents(EntityListener(this), this)
        PacketsListener(this,dataManager,protocolManager)
    }

    override fun onDisable(){
        //Deletes all the holograms
        for((key,value) in dataManager.holograms){
            if(!value.entity.isDead){
                value.entity.remove()
            }
        }
    }
}