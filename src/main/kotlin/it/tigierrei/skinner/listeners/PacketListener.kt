package it.tigierrei.skinner.listeners

import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn
import com.comphenix.packetwrapper.WrapperPlayServerRelEntityMoveLook
import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntity
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.sainttx.holograms.api.Hologram
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.utils.Disguiser
import me.libraryaddict.disguise.DisguiseAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType

class PacketListener(private val pl: Skinner, listenerPriority: ListenerPriority?, vararg types: PacketType?) :
    PacketAdapter(pl, listenerPriority, *types) {


    override fun onPacketSending(event: PacketEvent) {
        var entity: Entity? = null//NPC of player type is bugged
        //Updates the hologram location
        //Checks if the entity as a custom name (it so there must be an hologram in the hashmap)
        //Checks if the entity is already disguised
        when {
            event.packetType == PacketType.Play.Server.REL_ENTITY_MOVE_LOOK -> {
                val wrapper = WrapperPlayServerRelEntityMoveLook(event.packet)
                entity = wrapper.getEntity(event)
            }
            event.packetType == PacketType.Play.Server.SPAWN_ENTITY -> {
                val wrapper = WrapperPlayServerSpawnEntity(event.packet)
                entity = wrapper.getEntity(event)
            }
            event.packetType == PacketType.Play.Server.NAMED_ENTITY_SPAWN -> {
                val wrapper = WrapperPlayServerNamedEntitySpawn(event.packet)
                entity = wrapper.getEntity(event)
            }
        }
        if (entity != null) {
            //Checks if the entity is already disguised
            if (DisguiseAPI.isDisguised(entity)) {
                //Checks if the entity as a custom name (it so there must be an hologram in the hashmap)
                if (pl.dataManager.holograms.containsKey(entity)) {
                    //Updates the hologram location
                    (pl.dataManager.holograms[entity] as Hologram).teleport(entity.location.add(0.0, 2.2, 0.0))
                }
            } else {
                if (pl.dataManager.mythicMobs) {
                    if (pl.dataManager.mythicMobsAlive.containsKey(entity)) {
                        val mythicMob = pl.dataManager.mythicMobsAlive[entity]
                        if (mythicMob != null) {
                            if (pl.dataManager.mythicMobsDisguiseMap.containsKey(mythicMob.internalName)) {
                                val disguise = pl.dataManager.mythicMobsDisguiseMap[mythicMob.internalName]
                                if (disguise != null) {
                                    Disguiser.disguise(pl, entity, disguise)
                                    return
                                }
                            }
                        }
                    }
                }
                if (pl.dataManager.citizens) {
                    //NPC of player type is bugged
                    if (entity.hasMetadata("NPC") && entity.type != EntityType.PLAYER) {
                        if (pl.dataManager.citizensDisguiseMap.containsKey(entity.customName)) {
                            val disguise = pl.dataManager.citizensDisguiseMap[entity.customName]
                            if (disguise != null) {
                                Bukkit.getScheduler().runTaskLater(pl, Runnable {
                                    Disguiser.disguise(pl, entity, disguise)
                                }, 1L)
                                return
                            }
                        }

                    }
                }
                if(pl.dataManager.vanillaMobs){
                    if(pl.dataManager.vanillaMobsDisguiseMap.containsKey(entity.type)){
                        val disguise = pl.dataManager.vanillaMobsDisguiseMap[entity.type]
                        if(disguise != null){
                            Disguiser.disguise(pl,entity,disguise)
                            return
                        }
                    }
                }
            }
        }

    }
}