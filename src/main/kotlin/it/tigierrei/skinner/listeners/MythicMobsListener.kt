package it.tigierrei.skinner.listeners

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent
import it.tigierrei.skinner.Skinner
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class MythicMobsListener(private val pl: Skinner) : Listener{

    @EventHandler(priority = EventPriority.LOWEST)
    fun onMythicMobSpawn(event: MythicMobSpawnEvent){
        pl.dataManager.mythicMobsAlive[event.entity] = event.mobType
    }

    @EventHandler
    fun onMythicMobDeath(event: MythicMobDeathEvent){
        if(pl.dataManager.mythicMobsAlive.containsKey(event.entity)){
            pl.dataManager.mythicMobsAlive.remove(event.entity)
        }
    }
}