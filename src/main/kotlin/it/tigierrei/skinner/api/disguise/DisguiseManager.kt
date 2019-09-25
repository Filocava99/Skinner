package it.tigierrei.skinner.api.disguise

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import it.tigierrei.configapi.ConfigFile
import it.tigierrei.skinner.Skinner
import net.minecraft.server.v1_14_R1.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.craftbukkit.v1_14_R1.CraftServer
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class DisguiseManager(val plugin : Skinner) {
    val disguisedEntities = HashMap<Int, List<Player>>()
    val fakeEntities = HashMap<Int,EntityPlayer>()
    private val disguises = HashMap<String, Disguise>()

    init {
        checkIntegrity()
        loadDisguises()
    }

    private fun disguise(entity: Entity, disguise: Disguise, vararg players: Player) {
        val displayName = disguise.displayName
        val texture = disguise.texture
        val signature = disguise.signature

        //Get entity location
        val location = entity.location

        val server = (Bukkit.getServer() as CraftServer).server
        val nmsWorld = (location.world as CraftWorld).handle

        val gameProfile = GameProfile(UUID.randomUUID(), ChatColor.translateAlternateColorCodes('&',displayName))
        gameProfile.properties.removeAll("textures")
        gameProfile.properties.put("textures", Property("textures", texture, signature))

        val fake = EntityPlayer(server, nmsWorld, gameProfile, PlayerInteractManager(nmsWorld))
        fake.setLocation(location.x, location.y, location.z, location.yaw, location.pitch)

        val pi = PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,fake)
        val spawn = PacketPlayOutNamedEntitySpawn(fake)
        val edp = PacketPlayOutEntityDestroy(1,entity.entityId)

        players.forEach {
            val player = (it as CraftPlayer).handle
            player.playerConnection.sendPacket(pi)
            player.playerConnection.sendPacket(spawn)
            player.playerConnection.sendPacket(edp)
        }

        fakeEntities[entity.entityId as Int] = fake
        disguisedEntities[entity.entityId] = players.asList()

    }

    fun disguiseToAll(entity: Entity, disguise: Disguise){
        disguise(entity,disguise, *Bukkit.getServer().onlinePlayers.toTypedArray())
    }

    fun disguiseToPlayer(entity: Entity, disguise: Disguise, player: Player){
        disguise(entity,disguise,player)
    }

    fun undisguise(entity: Entity){
        if(disguisedEntities.containsKey(entity.entityId)){
            disguisedEntities.remove(entity.entityId)
        }
        if(fakeEntities.containsKey(entity.entityId)){
            val fakeEntity = fakeEntities[entity.entityId]
            if(fakeEntity != null){
                Bukkit.getOnlinePlayers().forEach {
                    val packet = PacketPlayOutEntityDestroy(1,fakeEntity.id)
                    (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
                }
                fakeEntities.remove(entity.entityId)
            }
            if(entity is LivingEntity){
                Bukkit.getOnlinePlayers().forEach {
                    val packet = PacketPlayOutSpawnEntityLiving(entity as EntityLiving)
                    (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
                }
            }
        }
    }

    private fun loadDisguises(){
        val config = ConfigFile(plugin.dataFolder.path+"/disguises.yml",false)
        val section = config.getSection("disguises")
        val iterator = section.getKeys(false).iterator()
        while(iterator.hasNext()){
            val key = iterator.next()
            val disguiseSection = config.getSection(key)
            val displayName = disguiseSection.getString("displayName")
            val texture = disguiseSection.getString("texture")
            val signature = disguiseSection.getString("signature")
            //Se una delle stringhe è vuota o non esiste passo al ciclo successivo
            if(displayName.isNullOrEmpty() || texture.isNullOrEmpty() || signature.isNullOrEmpty()){
                continue
            }
            val disguise = Disguise(displayName,texture,signature)
            disguises[key] = disguise
        }

    }

    private fun checkIntegrity(){
        val config = ConfigFile(plugin.dataFolder.path+"/disguises.yml",false)
        val section = config.getSection("disguises")
        if(section == null) {
            config.createSection("disguises")
            val section2 = config.getSection("disguises") as ConfigurationSection
            val disguiseSection = section2.createSection("disguise-example")
            disguiseSection.set("displayName","exampleDisplayName")
            disguiseSection.set("texture","texture-string")
            disguiseSection.set("signature","signature-string")
            config.save()
        }
    }

    fun getDisguise(disguiseName: String) : Disguise? {
        return if(disguises.containsKey(disguiseName)) {
            disguises[disguiseName]
        }else{
            null
        }
    }

    fun isDisguised(entity: Entity):Boolean{
        return disguisedEntities.containsKey(entity.entityId)
    }

    fun isDisguisedToPlayer(entity: Entity,player: Player):Boolean{
        return if(disguisedEntities.containsKey(entity.entityId)){
            val playersList = disguisedEntities[entity.entityId]
            playersList?.contains(player) ?: false
        }else false
    }

}