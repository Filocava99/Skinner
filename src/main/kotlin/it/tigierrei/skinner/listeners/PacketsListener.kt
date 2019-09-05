package it.tigierrei.skinner.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.holograms.Hologram
import it.tigierrei.skinner.managers.DataManager
import me.libraryaddict.disguise.DisguiseAPI
import net.citizensnpcs.api.CitizensAPI

class PacketsListener(val plugin: Skinner, val dataManager: DataManager) {

    val protocolManager = ProtocolLibrary.getProtocolManager()

    init {
        protocolManager.addPacketListener(object :
            PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
            override fun onPacketSending(event: PacketEvent) {
                //System.out.println("check1")
                try {
                    if (event.packetType == PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
                        //System.out.println("check2")
                        val entityID = event.packet.integers.read(0)
                        //System.out.println("check3")
                        if(event.player != null && entityID != null){
                            //System.out.println("check4")
                            event.player.getNearbyEntities(512.0, 512.0, 512.0).forEach {
                                //System.out.println("check5")
                                if (it != null) {
                                    //System.out.println(it.entityId.toString() + " == " + entityID)
                                    if (it.entityId == entityID) {
                                        if (DisguiseAPI.isDisguised(it)) {
                                            dataManager.holograms[it]?.entity?.teleport(it.location.add(0.0, -0.2, 0.0))
                                        }
                                        if (!DisguiseAPI.isDisguised(event.player, it)) {
                                            if (dataManager.mythicMobs) {
                                                if (dataManager.mythicMobsAlive.containsKey(it)) {
                                                    val mythicMob = dataManager.mythicMobsAlive[it]
                                                    val disguise = dataManager.mythicMobsDisguiseMap[mythicMob?.internalName]
                                                    DisguiseAPI.disguiseEntity(it, disguise?.disguise)
                                                    if (!dataManager.holograms.containsKey(it)) {
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
                                                        DisguiseAPI.disguiseEntity(it, disguise?.disguise)
                                                        if (!dataManager.holograms.containsKey(it)) {
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
                                            if (dataManager.vanillaMobs) {
                                                if (dataManager.vanillaMobsDisguiseMap.containsKey(it.type)) {
                                                    val disguise = dataManager.vanillaMobsDisguiseMap[it.type]
                                                    DisguiseAPI.disguiseEntity(it, disguise?.disguise)
                                                    System.out.println("disguising " + it.type.toString())
                                                    if (!dataManager.holograms.containsKey(it)) {
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
                } catch (e: Exception) {
                }
            }
        })
    }

}