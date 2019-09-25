package it.tigierrei.skinner.api.disguise

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import it.tigierrei.configapi.ConfigFile
import it.tigierrei.skinner.Skinner
import net.minecraft.server.v1_14_R1.EntityPlayer
import net.minecraft.server.v1_14_R1.PacketPlayOutNamedEntitySpawn
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_14_R1.PlayerInteractManager
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_14_R1.CraftServer
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class DisguiseManager(val plugin : Skinner) {
    val disguisedEntities = HashMap<Int, List<Player>>()
    val fakeEntities = HashMap<Int,EntityPlayer>()
    val map = HashMap<String, Disguise>()

    init {
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

        val gameProfile = GameProfile(UUID.randomUUID(),displayName)
        gameProfile.properties.removeAll("textures")
        gameProfile.properties.put("textures", Property("textures", texture, signature))

        val fake = EntityPlayer(server, nmsWorld, gameProfile, PlayerInteractManager(nmsWorld))
        fake.setLocation(location.x, location.y, location.z, location.yaw, location.pitch)
        fakeEntities[entity.entityId as Int] = fake

        val pi = PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,fake)
        val spawn = PacketPlayOutNamedEntitySpawn(fake)

        players.forEach {
            val player = (it as CraftPlayer).handle
            player.playerConnection.sendPacket(pi)
            player.playerConnection.sendPacket(spawn)
        }

        val fakePlayer = EntityPlayer(server,nmsWorld,gameProfile, PlayerInteractManager(nmsWorld))
        fakePlayer.setLocation(location.x,location.y,location.z,location.yaw,location.pitch)
    }

    fun disguiseToAll(entity: Entity, disguise: Disguise){
        disguise(entity,disguise, *Bukkit.getServer().onlinePlayers.toTypedArray())
    }

    fun disguiseToPlayer(entity: Entity, disguise: Disguise, player: Player){
        disguise(entity,disguise,player)
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
            map.put(key,disguise)
        }

    }

}