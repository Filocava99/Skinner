package it.tigierrei.skinner.api.disguise

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import it.tigierrei.skinner.Skinner
import net.minecraft.server.v1_14_R1.PacketPlayOutEntity
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
                                    val doubles = event.packet.doubles
                                    if(doubles.size() < 5) return

                                    val booleans = event.packet.booleans
                                    if(booleans.size()<1) return

                                    val x = doubles.read(0)
                                    val y = doubles.read(1)
                                    val z = doubles.read(2)
                                    val yaw = doubles.read(3)
                                    val pitch = doubles.read(4)
                                    val onGround = booleans.read(0)

                                    event.isCancelled = true

                                    val packet = PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(fakeEntity.id,x.toShort(),y.toShort(),z.toShort(),
                                        yaw.toByte(),
                                        pitch.toByte(),onGround)

                                    disguiseManager.disguisedEntities.forEach { t, u ->
                                        (u as CraftPlayer).handle.playerConnection.sendPacket(packet)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

}