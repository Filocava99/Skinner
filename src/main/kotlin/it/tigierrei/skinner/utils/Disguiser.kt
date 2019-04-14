package it.tigierrei.skinner.utils

import com.sainttx.holograms.api.Hologram
import com.sainttx.holograms.api.line.HologramLine
import com.sainttx.holograms.api.line.TextLine
import it.tigierrei.skinner.Skinner
import me.libraryaddict.disguise.DisguiseAPI
import org.bukkit.entity.Entity


class Disguiser {
    companion object{
        fun disguise(plugin: Skinner, entity: Entity, disguise: SkinnerDisguise){
            DisguiseAPI.disguiseToAll(entity,disguise.disguise)
            if(!disguise.displayName.isNullOrEmpty()){
                val hologram = Hologram(disguise.displayName,entity.location)
                val hologramLine: HologramLine = TextLine(hologram,disguise.displayName)
                hologram.addLine(hologramLine)
                plugin.hologramManager.addActiveHologram(hologram) // Tells the plugin a new Hologram was added
                plugin.dataManager.holograms[entity] = hologram
            }
        }
    }
}