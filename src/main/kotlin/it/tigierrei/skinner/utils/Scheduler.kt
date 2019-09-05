package it.tigierrei.skinner.utils

import it.tigierrei.skinner.Skinner
import org.bukkit.Bukkit

class Scheduler(private val pl: Skinner) {
    init{
        Bukkit.getScheduler().runTaskTimer(pl,Runnable{
            try{
            for((key, value) in pl.dataManager.holograms){
                if(key.isDead){
                    value.entity.remove()
                    pl.dataManager.holograms.remove(key)
                }
            }}catch(e: ConcurrentModificationException){
            }
        },20L,20L)
    }
}