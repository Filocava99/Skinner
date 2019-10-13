package it.tigierrei.skinner.listeners.packets

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import it.tigierrei.skinner.Skinner
import it.tigierrei.skinner.api.disguise.DisguiseManager
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityStatus
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer

class UseEntityPacketListener(val plugin: Skinner, val disguiseManager: DisguiseManager) {
    init {
        plugin.protocolManager.addPacketListener(object :
            PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {

            override fun onPacketReceiving(event: PacketEvent?) {
                if(event != null && event.packetType == PacketType.Play.Client.USE_ENTITY){
                    val packetIntegers = event.packet.integers
                    val interactionType = event.packet.entityUseActions.read(0)
                    if(packetIntegers != null && packetIntegers.size() >= 1 && interactionType == EnumWrappers.EntityUseAction.ATTACK){
                        val entityId = packetIntegers.read(0)
                        println(entityId)
                        if(disguiseManager.isFakeEntity(entityId)){
                            val packet = PacketPlayOutEntityStatus(disguiseManager.getFakeEntityByItsId(entityId),2)
                            val originalEntity = disguiseManager.getOriginalEntityIdByFakeEntity(entityId)
                            disguiseManager.playersWhoSeeDisguiseList[originalEntity?.entityId]?.forEach {
                                (it as CraftPlayer).handle.playerConnection.sendPacket(packet)
                            }
                        }
                    }
                }
            }

        })
    }
}