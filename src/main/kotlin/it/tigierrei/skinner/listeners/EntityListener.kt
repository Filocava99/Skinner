package it.tigierrei.skinner.listeners

import io.lumine.xikage.mythicmobs.MythicMobs
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.utils.Disguiser
import me.libraryaddict.disguise.DisguiseAPI
import net.citizensnpcs.api.CitizensAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent

class EntityListener(private val pl: Skinner) : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent){
        if(pl.dataManager.holograms.containsKey(event.entity)){
            pl.dataManager.holograms[event.entity]?.entity?.remove()
        }
    }

    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent){
        if(!DisguiseAPI.isDisguised(event.entity)){
            if(pl.dataManager.citizens){
                if(CitizensAPI.getNPCRegistry().isNPC(event.entity)){
                    return
                }
            }
            if(pl.dataManager.mythicMobs){
                if(event.entity.customName != null){
                    return
                }
            }
            if(pl.dataManager.vanillaMobs){
                val disguise = pl.dataManager.vanillaMobsDisguiseMap[event.entityType]
                System.out.println(disguise?.displayName)
                if(disguise != null){
                    Disguiser.disguise(pl,event.entity,disguise)
                }
            }
        }
    }

}