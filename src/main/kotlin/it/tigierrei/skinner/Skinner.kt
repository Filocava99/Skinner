package it.tigierrei.skinner

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import it.tigierrei.skinner.disguiseapi.DisguiseManager
import it.tigierrei.skinner.commands.SkinnerCommand
import it.tigierrei.skinner.commands.TabCompleter
import it.tigierrei.skinner.listeners.EntityListener
import it.tigierrei.skinner.listeners.MythicMobsListener
import it.tigierrei.skinner.listeners.NPCListener
import it.tigierrei.skinner.managers.DataManager
import org.bukkit.plugin.java.JavaPlugin
import org.mineskin.MineskinClient

class Skinner : JavaPlugin() {

    lateinit var dataManager: DataManager
    lateinit var protocolManager: ProtocolManager
    lateinit var disguiseManager: DisguiseManager
    lateinit var mineskinClient: MineskinClient

    override fun onLoad() {
        super.onLoad()
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun onEnable() {
        super.onEnable()

        dataManager = DataManager(this)
        disguiseManager = DisguiseManager(this)
        mineskinClient = MineskinClient()

        //Commands executors
        getCommand("skinner")?.setExecutor(SkinnerCommand(this))
        getCommand("skinner")?.tabCompleter = TabCompleter()

        getCommand("sktest")?.setExecutor(Test())

        //Listeners
        if (dataManager.mythicMobs) {
            server.pluginManager.registerEvents(MythicMobsListener(this), this)
        }
        if (dataManager.citizens) {
            server.pluginManager.registerEvents(NPCListener(this), this)
        }
        server.pluginManager.registerEvents(EntityListener(this), this)
    }

    override fun onDisable(){
    }
}