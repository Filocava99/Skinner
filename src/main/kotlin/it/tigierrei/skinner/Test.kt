package it.tigierrei.skinner

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.server.v1_14_R1.EntityPlayer
import net.minecraft.server.v1_14_R1.PacketPlayOutNamedEntitySpawn
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerInfo
import net.minecraft.server.v1_14_R1.PlayerInteractManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_14_R1.CraftServer
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*



class Test : CommandExecutor{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
/*
        val player = (Bukkit.getPlayer("tigierrei") as CraftPlayer).handle
        val server = (Bukkit.getServer() as CraftServer).server

        val nmsWorld = ((sender as Player).world as CraftWorld).handle


        val gp = GameProfile(UUID.randomUUID(),"Provaaaa")
        gp.getProperties().removeAll("textures")
        val texture = "eyJ0aW1lc3RhbXAiOjE0ODA1MjA3NjAxNTksInByb2ZpbGVJZCI6ImExNDlmODFiZjc4NDRmODk4N2M1NTRhZmRkNGRiNTMzIiwicHJvZmlsZU5hbWUiOiJsaWJyYXJ5YWRkaWN0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZTQ5NDVkMzZjZjVhNjI1OGZjOGY4ZTM5NmZlZWYzMzY1ZjM2MjgyYjE2MjY0OWI2M2NmZWQzNzNmNzY1OSJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWZkNjFjM2M0YWM4OGYxYTM0NjhmYmRlZWY0NWNlYzg5ZTVhZmI4N2I5N2ExYTg0NWJmYjNjNjRmZDBiODgzIn19fQ=="
        val signature = "afoGOO45t3iGvTyQ732AlugPOvj13/RNjM0/utYlD4PZ4ab4Jopbzr8Px75+ALdkyegoKNcfaH4aXzylMvL6mIwaRdL0af7pfGibMMCMJ8F1RAMl2WqRslKBKXHGS1OXxMweoXW+RRatGgZsUC1BjxHMwd4RuXxrV9ZZ7x1r4xouUXmMzn19wqNO9EeG2q8AgF/hZdrnJPdTTrqJs04r4vCQiFiQsTWiY/B5CBOTh6fw4NpOHeeiJwHOLvN+6xKnAm77nKawaKCSciDwt54EeZoE/Q5ReQUEFgj++jdyHb5PJbhGytr//mazpTVzvlDnO06CZqigbiueV2/ush2gKSXQeimCXeNZzcj/CFgqAmMSEZQW3qHp+DgoqqtBNabJa0FBzpbQQ/jQWzoHfmUC/hTf0A0+hgOe4NqDc+xXYf4A9M/6/0JHz0voWhQJi8QriM699DeeUa31bVdTdKjcyK6Zw6/HIOJt++eFnkf++/zKt0fMiqfdRamSqR/K3w+Kk7cs2D345BNubl5L83YWmLbebUcAPKaza5gi17lUW+h/FitzfKAJZ+xsfSdj27nQLa24xYsyB3Fi5DcFLI2oQt5BYAvViT37sabGOXbDBsrijS4t3++mIbC+pCDiKi0hwZzvy0TPRTle2RMhJ6D66DmpykwqBOxzD73fEsieWX4="
        gp.properties.put("textures", Property("textures", texture, signature))

        val fake = EntityPlayer(server, nmsWorld, gp, PlayerInteractManager(nmsWorld))

        fake.setLocation(sender.getLocation().x, sender.getLocation().y, sender.getLocation().z, sender.getLocation().yaw, sender.getLocation().pitch)

        val pi = PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,fake)
        val spawn = PacketPlayOutNamedEntitySpawn(fake)

        player.playerConnection.sendPacket(pi)
        player.playerConnection.sendPacket(spawn)

        /*
        1) com.comphenix.protocol.WrapperPlayServerPlayerInfo (REMOVE)
        2) WrapperPlayServerSpawnPosition
        3) WrapperPlayServerRespawn
        4) WrapperPlayServerPosition
        5) com.comphenix.protocol.WrapperPlayServerPlayerInfo (ADD)

        To avoid glitches teleport player using player.teleport(player) one tick later.
         */
*/
        return true
    }


}