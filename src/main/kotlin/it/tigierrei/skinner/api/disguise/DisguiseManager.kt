package it.tigierrei.skinner.api.disguise

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import it.tigierrei.skinner.Skinner
import net.minecraft.server.v1_14_R1.EntityPlayer
import net.minecraft.server.v1_14_R1.PlayerInteractManager
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_14_R1.CraftServer
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.util.*

class DisguiseManager(val plugin : Skinner) {


    init {
        
    }


    fun disguise(entity: Entity, vararg players: Player) {
        //Get entity location
        val location = entity.location

        val server = (Bukkit.getServer() as CraftServer).server
        val nmsWorld = (location.world as CraftWorld).handle

        val gameProfile = GameProfile(UUID.randomUUID(),displayName)
        gameProfile.properties.removeAll("textures")
        gameProfile.properties.put("textures", Property("textures", texture, signature))

        val fakePlayer = EntityPlayer(server,nmsWorld,gameProfile, PlayerInteractManager(nmsWorld))
        fakePlayer.setLocation(location.x,location.y,location.z,location.yaw,location.pitch)
    }

}