package it.tigierrei.skinner.api.disguise

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import it.tigierrei.skinner.Skinner
import net.minecraft.server.v1_14_R1.PacketPlayOutEntity
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer

class EntityPacketsListener(val plugin: Skinner, val disguiseManager: DisguiseManager) {

    init {
        plugin.protocolManager.addPacketListener(object : PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK){
            override fun onPacketSending(event: PacketEvent?) {
                if (event != null) {
                    if(event.packetType == PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
                        val integers = event.packet.integers
                        if(integers != null && integers.size() > 0){
                            val entityID = integers.read(0)

                            if(disguiseManager.disguisedEntities.containsKey(entityID) && disguiseManager.fakeEntities.containsKey(entityID)){
                                val fakeEntity = disguiseManager.fakeEntities[entityID]
                                if(fakeEntity != null){
                                    val shorts = event.packet.shorts
                                    val bytes = event.packet.bytes
                                    val booleans = event.packet.booleans
                                    //println(booleans.size())
                                    if(shorts.size() < 3 || bytes.size() < 2 || booleans.size()<1) return
                                    if(booleans.size()<1) return

                                    val x = shorts.read(0)
                                    val y = shorts.read(1)
                                    val z = shorts.read(2)
                                    val yaw = bytes.read(0)
                                    val pitch = bytes.read(1)
                                    val onGround = booleans.read(0)
                                    //println("X: $x Y: $y Z: $z yaw: $yaw pitch: $pitch onground $onGround")
                                    val packet = PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(fakeEntity.id,x,y,z, yaw, pitch,onGround)

                                    if(disguiseManager.disguisedEntities.containsKey(entityID)){
                                        disguiseManager.disguisedEntities[entityID]?.forEach {
                                            (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
                                        }
                                    }
                                }
                            }
                        }
                    }else if(event.packetType == PacketType.Play.Server.ENTITY_TELEPORT){

                    }
                }
            }
        })
    }

}