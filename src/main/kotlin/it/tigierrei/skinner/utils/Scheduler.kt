package it.tigierrei.skinner.utils

import it.tigierrei.skinner.Skinner
import org.bukkit.Bukkit

class Scheduler(val pl: Skinner) {
    init{
        Bukkit.getScheduler().runTaskTimer(pl,Runnable{
            for((key, value) in pl.dataManager.holograms){
                if(key.isDead){
                    pl.hologramManager.deleteHologram(value)
                    pl.dataManager.holograms.remove(key)
                }
            }
        },20L,20L)
    }
}