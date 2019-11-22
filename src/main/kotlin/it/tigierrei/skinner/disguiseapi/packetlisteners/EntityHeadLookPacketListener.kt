package it.tigierrei.skinner.disguiseapi.packetlisteners

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.disguiseapi.DisguiseManager
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityHeadRotation
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer

class EntityHeadLookPacketListener(plugin: Skinner, disguiseManager: DisguiseManager) :
    PacketListener(plugin, disguiseManager) {

    init {
        plugin.protocolManager.addPacketListener(object : PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_HEAD_ROTATION){
            override fun onPacketSending(event: PacketEvent?) {
                if (event != null) {
                    if (event.packetType == PacketType.Play.Server.ENTITY_HEAD_ROTATION) {
                        val integers = event.packet.integers
                        if (integers != null && integers.size() > 0) {
                            val entityID = integers.read(0)
                            if (disguiseManager.isDisguised(entityID)) {
                                val fakeEntity = disguiseManager.fakeEntitiesList[entityID]
                                if (fakeEntity != null) {
                                    val shorts = event.packet.shorts
                                    val bytes = event.packet.bytes
                                    val booleans = event.packet.booleans
                                    //println(booleans.size())
                                    if (bytes.size() < 1) return

                                    val yaw = bytes.read(0)

                                    val packet = PacketPlayOutEntityHeadRotation(
                                        fakeEntity,
                                        yaw
                                    )
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