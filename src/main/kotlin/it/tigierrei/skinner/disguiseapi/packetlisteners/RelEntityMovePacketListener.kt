package it.tigierrei.skinner.disguiseapi.packetlisteners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.disguiseapi.DisguiseManager
import net.minecraft.server.v1_14_R1.PacketPlayOutEntity
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer

class RelEntityMovePacketListener(plugin: Skinner, disguiseManager: DisguiseManager) :
    PacketListener(plugin, disguiseManager) {

    init {
        plugin.protocolManager.addPacketListener(object : PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK){
            override fun onPacketSending(event: PacketEvent?) {
                if (event != null) {
                    if(event.packetType == PacketType.Play.Server.REL_ENTITY_MOVE){
                        val integers = event.packet.integers
                        if(integers != null && integers.size() > 0){
                            val entityID = integers.read(0)
                            if(disguiseManager.isDisguised(entityID)){
                                val fakeEntity = disguiseManager.fakeEntitiesList[entityID]
                                if(fakeEntity != null){
                                    val shorts = event.packet.shorts
                                    val booleans = event.packet.booleans
                                    //println(booleans.size())
                                    if(shorts.size() < 3 || booleans.size()<1) return

                                    val x = shorts.read(0)
                                    val y = shorts.read(1)
                                    val z = shorts.read(2)
                                    val onGround = booleans.read(0)
                                    //println("X: $x Y: $y Z: $z yaw: $yaw pitch: $pitch onground $onGround")

                                    val packet = PacketPlayOutEntity.PacketPlayOutRelEntityMove(fakeEntity.id,x,y,z, onGround)
                                    disguiseManager.playersWhoSeeDisguiseList[entityID]?.forEach {
                                        (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
                                    }
                                    event.isCancelled = true
                                }
                            }
                        }
                    }
                }
            }
        })
    }

}