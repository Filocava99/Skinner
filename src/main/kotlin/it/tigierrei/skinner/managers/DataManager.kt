package it.tigierrei.skinner.managers

import com.sainttx.holograms.api.Hologram
import io.lumine.xikage.mythicmobs.mobs.MythicMob
import it.tigierrei.configapi.ConfigFile
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.utils.SkinnerDisguise
import me.libraryaddict.disguise.DisguiseAPI
import me.libraryaddict.disguise.disguisetypes.Disguise
import org.bukkit.entity.Entity
import java.io.File
import java.nio.file.Paths

class DataManager(pl: Skinner) {

    private val config = ConfigFile(Paths.get(pl.dataFolder.path, "config.yml").toString(), false)

    var mythicMobs: Boolean = false
    var citizens: Boolean = false
    var mythicMobsDisguiseMap : MutableMap<String, SkinnerDisguise> = HashMap()
    var citizensDisguiseMap : MutableMap<String, SkinnerDisguise> = HashMap()

    var mythicMobsAlive: MutableMap<Entity, MythicMob> = HashMap()
    var holograms: MutableMap<Entity, Hologram> = HashMap()

    init {
        //Loads the config and checks its integrity
        loadConfig()
        //Creates the skins folder if not exists
        val skinsFolder = File("${pl.dataFolder.path}/skins/")
        if(!skinsFolder.exists()){
            skinsFolder.mkdirs()
        }
    }

    private fun loadConfig() {
        checkIntegrity()
        mythicMobs = config.get("MythicMobs") as Boolean
        citizens = config.get("Citizens") as Boolean
        if(mythicMobs){
            val section = config.getSection("MM-Disguises")
            for ( x in section.getKeys(false)){
                val subSection = section.getConfigurationSection(x)
                if (subSection != null) {
                    val disguise = SkinnerDisguise(DisguiseAPI.getCustomDisguise(subSection.get("disguise") as String?),subSection.getString("displayName"))
                    if(disguise.disguise != null) {
                        mythicMobsDisguiseMap[x] = disguise
                    }
                }
            }
        }
        if(citizens){
            val section = config.getSection("Citizens-Disguises")
            for ( x in section.getKeys(false)){
                val subSection = section.getConfigurationSection(x)
                if (subSection != null) {
                    val disguise = SkinnerDisguise(DisguiseAPI.getCustomDisguise(subSection.get("disguise") as String?) as Disguise?,subSection.getString("displayName"))
                    if(disguise.disguise != null) {
                        citizensDisguiseMap[x] = disguise
                    }
                }
            }
        }
    }

    private fun checkIntegrity() {
        if(config.get("MythicMobs") == null){
            config.set("MythicMobs",false)
        }
        if(config.get("Citizens") == null){
            config.set("Citizens", false)
        }
        if(config.getSection("MM-Disguises") == null){
            config.createSection("MM-Disguises")
            val mmSection = config.getSection("MM-Disguises")
            val mmExampleSection = mmSection.createSection("MobInternalNameExample")
            mmExampleSection.set("displayName","&5Example display name")
            mmExampleSection.set("disguise","exampleDisguiseName")
            config.save()
        }
        if(config.getSection("Citizens-Disguises") == null){
            config.createSection("Citizens-Disguises")
            val citizenSection = config.getSection("Citizens-Disguises")
            val citizenNPCExample = citizenSection.createSection("ExampleNPCName")
            citizenNPCExample.set("displayName","Example NPC display name")
            citizenNPCExample.set("disguise","exampleDisguiseName")
            config.save()
        }
    }
}