package it.tigierrei.skinner.listeners

import it.tigierrei.skinner.Skinner
import net.citizensnpcs.api.CitizensAPI
import net.minecraft.server.v1_14_R1.PacketPlayOutAnimation
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityStatus
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer
import org.bukkit.entity.LivingEntity
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
        if(pl.disguiseManager.isFakeEntity(entityId = entity.entityId)){
            val disguisedEntity = pl.disguiseManager.getOriginalEntityIdByFakeEntity(entityId = entity.entityId)
            if(disguisedEntity != null && !disguisedEntity.isDead){
                disguisedEntity.remove()
                pl.disguiseManager.undisguise(disguisedEntity)
            }
            val packet = PacketPlayOutEntityStatus(pl.disguiseManager.fakeEntitiesList[entity.entityId],3)
            pl.disguiseManager.playersWhoSeeDisguiseList[entity.entityId]?.forEach {
                (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
            }
            pl.disguiseManager.undisguise(event.entity)
        }
    }

    @EventHandler
    fun onEntityDamaged(event: EntityDamageByEntityEvent){
        val damager = event.damager
        //Se l'entity che sta attaccando e' quella nascosta devo inviare i pacchetti di attacco per la fake entity
        if(pl.disguiseManager.isDisguised(damager.entityId)){
            val fakeEntity = pl.disguiseManager.fakeEntitiesList[damager.entityId]
            val attackAnimationPacket = PacketPlayOutAnimation(fakeEntity,0)
            Bukkit.getOnlinePlayers().forEach {
                (it as CraftPlayer).handle.playerConnection.sendPacket(attackAnimationPacket)
            }
        }

        val entityDamaged = event.entity
        if(pl.disguiseManager.isDisguised(entityId = entityDamaged.entityId)){
            if(entityDamaged is LivingEntity){
                (entityDamaged as LivingEntity).damage(event.damage)
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onEntitySpawn(event: EntitySpawnEvent){
        if(pl.disguiseManager.isFakeEntity(event.entity.entityId)){
            return
        }
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