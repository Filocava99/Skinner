package it.tigierrei.skinner.api.disguise

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import it.tigierrei.skinner.Skinner
import net.minecraft.server.v1_14_R1.PacketPlayOutEntity
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer

class RelEntityMoveLookPacketListener(val plugin: Skinner, val disguiseManager: DisguiseManager) {

    init {
        plugin.protocolManager.addPacketListener(object : PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK){
            override fun onPacketSending(event: PacketEvent?) {
                if (event != null) {
                    if(event.packetType == PacketType.Play.Server.REL_ENTITY_MOVE_LOOK) {
                        val integers = event.packet.integers
                        if(integers != null && integers.size() > 0){
                            val entityID = integers.read(0)
                            if(disguiseManager.isDisguised(entityID)){
                                val fakeEntity = disguiseManager.fakeEntitiesList[entityID]
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

                                    val tempOriginalEntityLocation = disguiseManager.originalEntitiesList[entityID]?.location!!
                                    println(fakeEntity.locX.toString() + "  " + tempOriginalEntityLocation.blockX.toString())
                                    println(fakeEntity.locY.toString() + "  " + tempOriginalEntityLocation.blockY.toString())
                                    println(fakeEntity.locZ.toString() + "  " + tempOriginalEntityLocation.blockZ.toString())
//                                    val tempX = fakeEntity.locX.toShort() - tempOriginalEntityLocation.blockX.toShort()
//                                    val tempY = fakeEntity.locY.toShort() - tempOriginalEntityLocation.blockY.toShort()
//                                    val tempZ = fakeEntity.locZ.toShort() - tempOriginalEntityLocation.blockZ.toShort()
                                    val packet = PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(fakeEntity.id,x,y,z, yaw, pitch,onGround)
                                    disguiseManager.playersWhoSeeDisguiseList[entityID]?.forEach {
                                        (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
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