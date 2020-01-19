package com.MikeTheShadow.ShadowXP.Listeners;

import com.MikeTheShadow.ShadowXP.ShadowXP;
import com.MikeTheShadow.ShadowXP.Util.CustomUser;
import com.MikeTheShadow.ShadowXP.Util.DBHandler;
import de.leonhard.storage.Json;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PlayerXPEvent implements Listener
{

    @EventHandler
    public void playerGainsXPEvent(PlayerExpChangeEvent event)
    {
        Player player = event.getPlayer();
        CustomUser user = DBHandler.getUserByID(player.getUniqueId().toString());
        if(user == null) { Bukkit.getConsoleSender().sendMessage("Error! Cannot get user: " + player.getName());return; }
        Json config = ShadowXP.levelConfig;
        int amount = event.getAmount();
        event.setAmount(0);
        //set player data
        int currentLevel = user.getLevel();
        //if the user is max level ignore
        if(config.getInt("levels." + (currentLevel + 1)) == 0)
        {
            user.setXP(user.getCurrentXP() + amount);
            user.setTotalXP(user.getTotalXP() + amount);
        }
        else
        {
            //get the xp required for the next level
            int nextLevelXP = config.getInt("levels." + currentLevel);
            //tell player they gained XP
            player.sendMessage(ShadowXP.levelConfig.get("settings.experience").toString().replace("%","" + amount));
            //check xp events
            if(user.getCurrentXP() + amount >= nextLevelXP)
            {
                //add overXP and update total
                user.setXP((user.getCurrentXP() + amount) - nextLevelXP);
                user.setTotalXP(user.getTotalXP() + amount);
                //update level
                user.addLevel(1);
                //save user
                player.sendMessage(ShadowXP.levelConfig.get("settings.levelup").toString().toString().replace("%","" + user.getLevel()));
                player.setLevel(user.getLevel());
                CustomCommandExecutor.runLevelUpCommands(user,player.getLevel());
            }
            else
            {
                user.setXP(user.getCurrentXP() + amount);
                user.setTotalXP(user.getTotalXP() + amount);
            }
        }
        //update user
        DBHandler.updateCustomUser(user);
    }

}
