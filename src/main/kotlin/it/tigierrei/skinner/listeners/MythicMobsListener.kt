package it.tigierrei.skinner.listeners

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.utils.Disguiser
import me.libraryaddict.disguise.DisguiseAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class MythicMobsListener(private val pl: Skinner) : Listener{

    @EventHandler
    fun onMythicMobSpawn(event: MythicMobSpawnEvent){
        pl.dataManager.mythicMobsAlive[event.entity] = event.mobType
        if(pl.dataManager.mythicMobsDisguiseMap.containsKey(event.mobType.internalName) && !DisguiseAPI.isDisguised(event.entity)){
            val disguise = pl.dataManager.mythicMobsDisguiseMap[event.mobType.internalName]
            if(disguise != null){
                Disguiser.disguise(pl,event.entity,disguise)
            }
        }
    }

    @EventHandler
    fun onMythicMobDeath(event: MythicMobDeathEvent){
        if(pl.dataManager.mythicMobsAlive.containsKey(event.entity)){
            pl.dataManager.mythicMobsAlive.remove(event.entity)
            //Removes hologram related to the entity if it exists
            if(pl.dataManager.holograms.containsKey(event.entity)){
                pl.hologramManager.deleteHologram(pl.dataManager.holograms[event.entity])
                pl.dataManager.holograms.remove(event.entity)
            }
        }
    }
}