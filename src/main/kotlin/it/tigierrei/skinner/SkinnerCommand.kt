package it.tigierrei.skinner

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
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
        //sk upload fileName disguiseName <displayName>
        if(args.size < 4){
            sender.sendMessage(ChatColor.RED.toString() + "You must pass more arguments!Type /sk help for the list of commands")
        }
        uploadSkin(args[1],args[2],sender,args[3],if(args.size == 3) null else args[3] )
        return true
    }

    private fun uploadSkin(urlString: String, skinName: String, sender: CommandSender, disguiseName: String, displayName: String?){
        try {
            val url = URL(urlString)
            val skinFile = File(pl.dataFolder.path+"/skins",  skinName)

            if (skinFile.exists()) {
                sender.sendMessage("§cCustom skin '$skinName' already exists. Please choose a different name.")
                return
            } else {
                skinFile.createNewFile()
            }


            pl.mineskinClient.generateUrl(url.toString(), SkinOptions.name(skinName), object : SkinCallback {
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

                    try {
                        FileWriter(skinFile).use({ writer -> Gson().toJson(jsonObject, writer) })
                    } catch (e: IOException) {
                        sender.sendMessage("§cFailed to save skin to file: " + e.message)
                        pl.getLogger().log(Level.SEVERE, "Failed to save skin", e)
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

                    pl.getLogger().log(Level.WARNING, "Exception while generating skin", exception)
                }
            })
        } catch (e: MalformedURLException) {
            sender.sendMessage("§cInvalid URL")
            return
        } catch (e: IOException) {
            sender.sendMessage("§cUnexpected IOException: " + e.message)
            pl.getLogger().log(
                Level.SEVERE,
                "Unexpected IOException while creating skin '$skinName' with source '$urlString'",
                e
            )
        }

    }
}