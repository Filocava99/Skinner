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
import me.libraryaddict.disguise.DisguiseAPI
import net.citizensnpcs.api.CitizensAPI
import net.minecraft.server.v1_14_R1.PacketPlayOutEntity
import org.bukkit.plugin.java.JavaPlugin
import org.mineskin.MineskinClient
import org.inventivetalent.packetlistener.handler.ReceivedPacket
import org.inventivetalent.packetlistener.handler.SentPacket
import org.inventivetalent.packetlistener.handler.PacketHandler
import org.inventivetalent.packetlistener.PacketListenerAPI

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

        PacketListenerAPI.addPacketHandler(object : PacketHandler(this) {

            override fun onSend(packet: SentPacket) {
                if (packet is PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook) {
                    System.out.println("trigger2")
                    val playerLocation = packet.player.location
                    packet.player.getNearbyEntities(512.0, 512.0, 512.0).forEach {
                        if (it != null) {
                            if (it.entityId == (packet.getPacketValue("a") as Int)) {
                                if (DisguiseAPI.isDisguised(it)) {
                                    dataManager.holograms[it]?.entity?.teleport(it.location.add(0.0, 2.4, 0.0))
                                }
                                if(DisguiseAPI.isDisguised(packet.player,it)){
                                    if (dataManager.mythicMobs) {
                                        if (dataManager.mythicMobsAlive.containsKey(it)) {
                                            val mythicMob = dataManager.mythicMobsAlive[it]
                                            val disguise = dataManager.mythicMobsDisguiseMap[mythicMob?.internalName]
                                            DisguiseAPI.disguiseEntity(it, disguise?.disguise)
                                            if(!dataManager.holograms.containsKey(it)){
                                                val hologram = Hologram(
                                                    if (disguise?.displayName == null) "" else disguise.displayName,
                                                    it.location
                                                )
                                                dataManager.holograms[it] = hologram
                                            }
                                            return
                                        }
                                    }
                                    if (dataManager.citizens) {
                                        if (CitizensAPI.getNPCRegistry().isNPC(it)) {
                                            if (dataManager.citizensDisguiseMap.containsKey(it.customName)) {
                                                val disguise = dataManager.citizensDisguiseMap[it.customName]
                                                DisguiseAPI.disguiseEntity(it,disguise?.disguise)
                                                if(!dataManager.holograms.containsKey(it)){
                                                    val hologram = Hologram(
                                                        if (disguise?.displayName == null) "" else disguise.displayName,
                                                        it.location
                                                    )
                                                    dataManager.holograms[it] = hologram
                                                }
                                                return
                                            }
                                        }
                                    }
                                    if(dataManager.vanillaMobs){
                                        if(dataManager.vanillaMobsDisguiseMap.containsKey(it.type)){
                                            val disguise = dataManager.vanillaMobsDisguiseMap[it.type]
                                            DisguiseAPI.disguiseEntity(it,disguise?.disguise)
                                            if(!dataManager.holograms.containsKey(it)){
                                                val hologram = Hologram(
                                                    if (disguise?.displayName == null) "" else disguise.displayName,
                                                    it.location
                                                )
                                                dataManager.holograms[it] = hologram
                                            }
                                            return
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }

            override fun onReceive(packet: ReceivedPacket) {}

        })
    }
}