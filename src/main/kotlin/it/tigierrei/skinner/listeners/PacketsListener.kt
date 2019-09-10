package it.tigierrei.skinner.listeners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.holograms.Hologram
import it.tigierrei.skinner.managers.DataManager
import me.libraryaddict.disguise.DisguiseAPI
import net.citizensnpcs.api.CitizensAPI

class PacketsListener(val plugin: Skinner, val dataManager: DataManager, val protocolManager: ProtocolManager) {

    init {
        protocolManager.addPacketListener(object :
            PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
            override fun onPacketSending(event: PacketEvent) {
                try {
                    if (event.packetType == PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
                        //println("check2")
                        if(event.packet.integers == null || event.packet.integers.size() == 0){
                            return
                        }

                        val entityID = event.packet.integers.read(0)
                        event.isCancelled = false
                        //println("check3")
                        if(event.player != null && entityID != null){
                            //println("check4")
                            val iterator = event.player.world.entities.iterator()

                            while(iterator.hasNext()){
                                val entity = iterator.next()
                                //System.out.println("check5")
                                if (entity != null && !entity.isDead) {
                                    //System.out.println(it.entityId.toString() + " == " + entityID)
                                    if (entity.entityId == entityID) {
                                        if (DisguiseAPI.isDisguised(entity)) {
                                            if(dataManager.holograms.containsKey(entity)){
                                                dataManager.holograms[entity]?.entity?.teleport(entity.location.add(0.0, -0.2, 0.0))
                                            }
                                        }
                                        if (!DisguiseAPI.isDisguised(event.player, entity)) {
                                            if (dataManager.mythicMobs) {
                                                if (dataManager.mythicMobsAlive.containsKey(entity)) {
                                                    val mythicMob = dataManager.mythicMobsAlive[entity]
                                                    val disguise = dataManager.mythicMobsDisguiseMap[mythicMob?.internalName]
                                                    DisguiseAPI.disguiseEntity(entity, disguise?.disguise)
                                                    if (!dataManager.holograms.containsKey(entity)) {
                                                        val hologram = Hologram(
                                                            if (disguise?.displayName == null) "" else disguise.displayName,
                                                            entity.location
                                                        )
                                                        dataManager.holograms[entity] = hologram
                                                    }
                                                    return
                                                }
                                            }
                                            if (dataManager.citizens) {
                                                if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
                                                    if (dataManager.citizensDisguiseMap.containsKey(entity.customName)) {
                                                        val disguise = dataManager.citizensDisguiseMap[entity.customName]
                                                        DisguiseAPI.disguiseEntity(entity, disguise?.disguise)
                                                        if (!dataManager.holograms.containsKey(entity)) {
                                                            val hologram = Hologram(
                                                                if (disguise?.displayName == null) "" else disguise.displayName,
                                                                entity.location
                                                            )
                                                            dataManager.holograms[entity] = hologram
                                                        }
                                                        return
                                                    }
                                                }
                                            }
                                            if (dataManager.vanillaMobs) {
                                                if (dataManager.vanillaMobsDisguiseMap.containsKey(entity.type)) {
                                                    val disguise = dataManager.vanillaMobsDisguiseMap[entity.type]
                                                    DisguiseAPI.disguiseEntity(entity, disguise?.disguise)
                                                    System.out.println("disguising " + entity.type.toString())
                                                    if (!dataManager.holograms.containsKey(entity)) {
                                                        val hologram = Hologram(
                                                            if (disguise?.displayName == null) "" else disguise.displayName,
                                                            entity.location
                                                        )
                                                        dataManager.holograms[entity] = hologram
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