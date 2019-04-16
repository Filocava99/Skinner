package it.tigierrei.skinner.commands

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import it.tigierrei.configapi.ConfigFile
import it.tigierrei.skinner.Skinner
import me.libraryaddict.disguise.DisguiseAPI
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.mineskin.SkinOptions
import org.mineskin.data.SkinCallback
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.logging.Level


class SkinnerCommand(val pl: Skinner) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("skinner")){
            sender.sendMessage("${ChatColor.RED}You don't have the permission to use that command!")
            return true
        }
        if(args[1].equals("test",ignoreCase = true)){
            DisguiseAPI.disguiseToAll((sender as Player).getNearbyEntities(2.0,2.0,2.0)[0],DisguiseAPI.getCustomDisguise("ogre"))
            return true
        }
        //sk upload fileName fileExtensione disguiseName <displayName>
        if (args.size < 4) {
            sender.sendMessage("${ChatColor.RED}You must pass more arguments!Type /sk help for the list of commands")
            return true
        }
        uploadSkin(args[1], args[2], sender, args[3], if (args.size == 4) null else args[4])
        return true
    }

    private fun uploadSkin(
        skinName: String,
        extension: String,
        sender: CommandSender,
        disguiseName: String,
        displayName: String?
    ) {
        try {
            val skinFile = File(pl.dataFolder.path + "/skins", "$skinName.$extension")

            if (!skinFile.exists()) {
                sender.sendMessage("§c'$skinName' doesn't exist. Please be sure that the name and the extension are correct")
                return
            } else {
                skinFile.createNewFile()
            }


            pl.mineskinClient.generateUpload(skinFile, SkinOptions.name(skinName), object : SkinCallback {
                override fun done(skin: org.mineskin.data.Skin?) {
                    sender.sendMessage("§aSkin data generated.")
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("id", skin?.data?.uuid.toString())
                    jsonObject.addProperty("name", "")

                    val property = JsonObject()
                    property.addProperty("name", "textures")
                    property.addProperty("value", skin?.data?.texture?.value)
                    property.addProperty("signature", skin?.data?.texture?.signature)

                    val propertiesArray = JsonArray()
                    propertiesArray.add(property)

                    jsonObject.add("properties", propertiesArray)

                    val disguise = ConfigFile("${pl.dataFolder.parentFile.path}/LibsDisguises/disguises.yml",false)
                    disguise.getSection("Disguises").set(disguiseName,"player ${if(displayName == null) "." else displayName} setSkin {\"id\":\"a149f81bf7844f8987c554afdd4db533\",\"name\":\"libraryaddict\",\"properties\":[{\"signature\":\"${skin?.data?.texture?.signature}\",\"name\":\"textures\",\"value\":\"${skin?.data?.texture?.value}\"}]}")
                    disguise.save()

                    try {
                        FileWriter(skinFile).use({ writer -> Gson().toJson(jsonObject, writer) })
                    } catch (e: IOException) {
                        sender.sendMessage("§cFailed to save skin to file: " + e.message)
                        pl.logger.log(Level.SEVERE, "Failed to save skin", e)
                    }
                }

                override fun waiting(l: Long) {
                    sender.sendMessage("§7Waiting " + l / 1000.0 + "s to upload skin...")
                }

                override fun uploading() {
                    sender.sendMessage("§eUploading skin...")
                }

                override fun error(s: String) {
                    sender.sendMessage("§cError while generating skin: $s")
                    sender.sendMessage("§cPlease make sure the image is a valid skin texture and try again.")

                    skinFile.delete()
                }

                override fun exception(exception: Exception) {
                    sender.sendMessage("§cException while generating skin, see console for details: " + exception.message)
                    sender.sendMessage("§cPlease make sure the image is a valid skin texture and try again.")

                    skinFile.delete()

                    pl.logger.log(Level.WARNING, "Exception while generating skin", exception)
                }
            })
        } catch (e: MalformedURLException) {
            sender.sendMessage("§cInvalid URL")
            return
        } catch (e: IOException) {
            sender.sendMessage("§cUnexpected IOException: " + e.message)
            pl.logger.log(
                Level.SEVERE,
                "Unexpected IOException while creating skin '$skinName' with source '${pl.dataFolder.path}/skins/$skinName.$extension'",
                e
            )
        }

    }
}