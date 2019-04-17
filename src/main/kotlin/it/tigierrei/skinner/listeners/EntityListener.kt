package it.tigierrei.skinner.listeners

import it.tigierrei.skinner.Skinner
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

class EntityListener(private val pl: Skinner) : Listener {

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent){
        if(pl.dataManager.holograms.containsKey(event.entity)){
            pl.hologramManager.deleteHologram(pl.dataManager.holograms[event.entity])
        }
    }

}