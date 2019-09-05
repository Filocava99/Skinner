package it.tigierrei.skinner.utils

import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.holograms.Hologram
import me.libraryaddict.disguise.DisguiseAPI
import org.bukkit.entity.Entity


class Disguiser {
    companion object{
        fun disguise(plugin: Skinner, entity: Entity, disguise: SkinnerDisguise){
            DisguiseAPI.disguiseToAll(entity,disguise.disguise)
            if(!disguise.displayName.isNullOrEmpty()){
                val hologram = Hologram(disguise.displayName,entity.location.add(0.0,-0.2,0.0))
                plugin.dataManager.holograms[entity] = hologram
            }
        }
    }
}