package it.tigierrei.skinner.api.disguise

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import it.tigierrei.configapi.ConfigFile
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.listeners.packets.UseEntityPacketListener
import net.minecraft.server.v1_14_R1.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.craftbukkit.v1_14_R1.CraftServer
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftZombie
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import java.util.*
import kotlin.collections.HashMap

class DisguiseManager(val plugin : Skinner) {
    val playersWhoSeeDisguiseList = HashMap<Int, List<Player>>()
    val fakeEntitiesList = HashMap<Int,EntityPlayer>()
    val originalEntitiesList = HashMap<Int, Entity>()
    private val disguises = HashMap<String, Disguise>()

    init {
        checkIntegrity()
        loadDisguises()
        RelEntityMoveLookPacketListener(plugin,this)
        //UseEntityPacketListener(plugin, this)
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
        fake.playerConnection = PlayerConnection(MinecraftServer.getServer(), NetworkManager(EnumProtocolDirection.CLIENTBOUND), fake);
        fake.health = (entity as LivingEntity).health.toFloat()

        //In questo modo la fake entity iniziera' a triggerare eventi
        nmsWorld.addEntity(fake, CreatureSpawnEvent.SpawnReason.CUSTOM)



        val pi = PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,fake)
        val spawn = PacketPlayOutNamedEntitySpawn(fake)

        players.forEach {
            val player = (it as CraftPlayer).handle
            player.playerConnection.sendPacket(pi)
            player.playerConnection.sendPacket(spawn)
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable {
                player.playerConnection.sendPacket(PacketPlayOutEntityDestroy(entity.entityId))
            },10L)
        }

        fakeEntitiesList[entity.entityId] = fake
        playersWhoSeeDisguiseList[entity.entityId] = players.asList()
        originalEntitiesList[entity.entityId] = entity

    }

    fun disguiseToAll(entity: Entity, disguise: Disguise){
        disguise(entity,disguise, *Bukkit.getServer().onlinePlayers.toTypedArray())
    }

    fun disguiseToPlayer(entity: Entity, disguise: Disguise, player: Player){
        disguise(entity,disguise,player)
    }

    fun undisguise(entity: Entity){
        if(playersWhoSeeDisguiseList.containsKey(entity.entityId)){
            playersWhoSeeDisguiseList.remove(entity.entityId)
        }
        if(originalEntitiesList.containsKey(entity.entityId)){
            originalEntitiesList.remove(entity.entityId)
        }
        if(fakeEntitiesList.containsKey(entity.entityId)){
            val fakeEntity = fakeEntitiesList[entity.entityId]
            //Se la fakeEntity non e' morta allora devo inviare al giocare il pacchetto per eliminarla
            if(fakeEntity != null && fakeEntity.isAlive){
                Bukkit.getOnlinePlayers().forEach {
                    val packet = PacketPlayOutEntityDestroy(1,fakeEntity.id)
                    (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
                }
                fakeEntitiesList.remove(entity.entityId)
                //Uccido l'entity cosi che il server sappia che non esiste piu'. Potrebbe triggerare errori della madonna
                fakeEntity.killEntity()
            }
            //Se l'entity originale non e' morta la faccio riapparire usando i pacchetti
            if(!entity.isDead){
                val server = (Bukkit.getServer() as CraftServer)
                when(entity.type){
                    EntityType.ZOMBIE -> {
                        val craftEntity = CraftZombie(server,EntityZombie((entity.location.world as CraftWorld).handle.minecraftWorld))
                        Bukkit.getOnlinePlayers().forEach {
                            val packet = PacketPlayOutSpawnEntityLiving(entity as EntityLiving)
                            (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
                        }
                    }
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
            val disguiseSection = section.getConfigurationSection(key)
            val displayName = disguiseSection?.getString("displayName")
            val texture = disguiseSection?.getString("texture")
            val signature = disguiseSection?.getString("signature")
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
        return playersWhoSeeDisguiseList.containsKey(entity.entityId) && fakeEntitiesList.containsKey(entity.entityId) && originalEntitiesList.containsKey(entity.entityId)
    }

    fun isDisguised(entityId: Int):Boolean{
        return playersWhoSeeDisguiseList.containsKey(entityId) && fakeEntitiesList.containsKey(entityId) && originalEntitiesList.containsKey(entityId)
    }

    fun isDisguisedToPlayer(entity: Entity,player: Player):Boolean{
        return if(playersWhoSeeDisguiseList.containsKey(entity.entityId)){
            val playersList = playersWhoSeeDisguiseList[entity.entityId]
            playersList?.contains(player) ?: false
        }else false
    }

    fun isFakeEntity(entityId: Int):Boolean{
        fakeEntitiesList.values.forEach {
            if(it.id == entityId) return true
        }
        return false
    }

    fun getFakeEntityByItsId(entityId: Int):EntityPlayer?{
        fakeEntitiesList.values.forEach {
            if(it.id == entityId) return it
        }
        return null
    }

    fun getOriginalEntityIdByFakeEntity(entityId: Int):Entity?{
        var entity : Entity? = null
        fakeEntitiesList.forEach { t, u ->
            if(u.id == entityId) {
                entity = originalEntitiesList[t]!!
            }
        }
        return entity
    }

}