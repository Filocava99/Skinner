package it.tigierrei.skinner.managers

import io.lumine.xikage.mythicmobs.mobs.MythicMob
import it.tigierrei.configapi.ConfigFile
import it.tigierrei.skinner.Skinner
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import java.io.File
import java.nio.file.Paths

class DataManager(pl: Skinner) {

    private val config = ConfigFile(Paths.get(pl.dataFolder.path, "config.yml").toString(), false)

    var mythicMobs: Boolean = false
    var citizens: Boolean = false
    var vanillaMobs: Boolean = false
    lateinit var mythicMobsDisguiseMap : MutableMap<String, String>
    lateinit var citizensDisguiseMap : MutableMap<String, String>
    lateinit var vanillaMobsDisguiseMap : MutableMap<EntityType, String>

    var mythicMobsAlive: MutableMap<Entity, MythicMob> = HashMap()

    init {
        //Loads the config and checks its integrity
        loadConfig()
        //Creates the skins folder if not exists
        val skinsFolder = File("${pl.dataFolder.path}/skins/")
        if(!skinsFolder.exists()){
            skinsFolder.mkdirs()
        }
    }

    fun loadConfig() {
        checkIntegrity()
        mythicMobsDisguiseMap = HashMap()
        citizensDisguiseMap = HashMap()
        vanillaMobsDisguiseMap = HashMap()

        mythicMobs = config.get("MythicMobs") as Boolean
        citizens = config.get("Citizens") as Boolean
        vanillaMobs = config.get("VanillaMobs") as Boolean
        if(mythicMobs){
            val section = config.getSection("MM-Disguises")
            for ( x in section.getKeys(false)){
                val subSection = section.getConfigurationSection(x)
                if (subSection != null) {
                    val disguiseName = subSection.getString("disguise")
                    if(!disguiseName.isNullOrEmpty()) {
                        mythicMobsDisguiseMap[x] = disguiseName
                    }
                }
            }
        }
        if(citizens){
            val section = config.getSection("Citizens-Disguises")
            for ( x in section.getKeys(false)){
                val subSection = section.getConfigurationSection(x)
                if (subSection != null) {
                    val disguiseName = subSection.getString("disguise")
                    if(!disguiseName.isNullOrEmpty()) {
                        citizensDisguiseMap[x] = disguiseName
                    }
                }
            }
        }
        if(vanillaMobs){
            val section = config.getSection("VanillaMobs-Disguises")
            for ( x in section.getKeys(false)){
                val subSection = section.getConfigurationSection(x)
                if (subSection != null) {
                    val disguiseName = subSection.getString("disguise")
                    if(!disguiseName.isNullOrEmpty()) {
                        val entityType = EntityType.fromName(x)
                        if(entityType != null) {
                            vanillaMobsDisguiseMap[entityType] = disguiseName
                        }
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
        if(config.get("VanillaMobs") == null){
            config.set("VanillaMobs", false)
        }
        if(config.getSection("MM-Disguises") == null){
            config.createSection("MM-Disguises")
            val mmSection = config.getSection("MM-Disguises")
            val mmExampleSection = mmSection.createSection("MobInternalNameExample")
            mmExampleSection.set("disguise-name","exampleDisguiseName")
            config.save()
        }
        if(config.getSection("Citizens-Disguises") == null){
            config.createSection("Citizens-Disguises")
            val citizenSection = config.getSection("Citizens-Disguises")
            val citizenNPCExample = citizenSection.createSection("ExampleNPCName")
            citizenNPCExample.set("disguise-name","exampleDisguiseName")
            config.save()
        }
        if(config.getSection("VanillaMobs-Disguises") == null){
            config.createSection("VanillaMobs-Disguises")
            val vanillaMobSection = config.getSection("VanillaMobs-Disguises")
            val vanillaMobExample = vanillaMobSection.createSection("CREEPER")
            vanillaMobExample.set("disguise-name","exampleDisguiseName")
            config.save()
        }
    }
}