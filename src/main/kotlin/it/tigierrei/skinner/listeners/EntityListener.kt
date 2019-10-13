package it.tigierrei.skinner.listeners

import it.tigierrei.skinner.Skinner
import net.citizensnpcs.api.CitizensAPI
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityStatus
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent

class EntityListener(private val pl: Skinner) : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent){
        val entity = event.entity
        if(pl.disguiseManager.playersWhoSeeDisguiseList.containsKey(entity.entityId) && pl.disguiseManager.fakeEntitiesList.containsKey(entity.entityId)){
            val packet = PacketPlayOutEntityStatus(pl.disguiseManager.fakeEntitiesList[entity.entityId],3)
            pl.disguiseManager.playersWhoSeeDisguiseList[entity.entityId]?.forEach {
                (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
            }
            pl.disguiseManager.undisguise(event.entity)
        }
    }

    @EventHandler
    fun onEntityDamaged(event: EntityDamageByEntityEvent){
        val entity = event.damager
        if(pl.disguiseManager.playersWhoSeeDisguiseList.containsKey(entity.entityId) && pl.disguiseManager.fakeEntitiesList.containsKey(entity.entityId)){
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onEntitySpawn(event: EntitySpawnEvent){
        if(!pl.disguiseManager.isDisguised(event.entity)){
            if(pl.dataManager.citizens){
                if(CitizensAPI.getNPCRegistry().isNPC(event.entity)){
                    val npc = CitizensAPI.getNPCRegistry().getNPC(event.entity)
                    if(npc != null){
                        if(pl.dataManager.citizensDisguiseMap.containsKey(npc.name)){
                            val disguiseName = pl.dataManager.citizensDisguiseMap[npc.name]
                            if(!disguiseName.isNullOrBlank()){
                                val disguise = pl.disguiseManager.getDisguise(disguiseName)
                                if(disguise != null){
                                    pl.disguiseManager.disguiseToAll(event.entity,disguise)
                                }
                            }
                        }
                    }
                    return
                }
            }
            if(pl.dataManager.mythicMobs){
                if(event.entity.customName != null && pl.dataManager.mythicMobsAlive.containsKey(event.entity)){
                    val mythicMob = pl.dataManager.mythicMobsAlive[event.entity]
                    if(mythicMob != null && pl.dataManager.mythicMobsDisguiseMap.containsKey(mythicMob.internalName)){
                        val disguiseName = pl.dataManager.mythicMobsDisguiseMap[mythicMob.internalName]
                        if(!disguiseName.isNullOrBlank()){
                            val disguise = pl.disguiseManager.getDisguise(disguiseName)
                            if(disguise != null){
                                pl.disguiseManager.disguiseToAll(event.entity,disguise)
                            }
                        }
                    }
                    return
                }
            }
            if(pl.dataManager.vanillaMobs){
                val disguiseName = pl.dataManager.vanillaMobsDisguiseMap[event.entityType]
                if(disguiseName != null){
                    val disguise = pl.disguiseManager.getDisguise(disguiseName)
                    if(disguise != null) {
                        pl.disguiseManager.disguiseToAll(event.entity,disguise)
                    }
                }
            }
        }
    }

}