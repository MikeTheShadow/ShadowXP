package com.MikeTheShadow.ShadowXP.Listeners;

import com.MikeTheShadow.ShadowXP.Util.CustomUser;
import com.MikeTheShadow.ShadowXP.Util.DBHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void playerJoinEvent(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        //Add player to database if they don't exist
        CustomUser user = DBHandler.getUserByID(player.getUniqueId().toString());
        if(user == null)
        {
            DBHandler.insertNewUser(player.getName(),player.getUniqueId().toString(),1,0,0,(int)player.getHealth());
            user = DBHandler.getUserByID(player.getUniqueId().toString());
            player.setLevel(user.getLevel());
            player.setExp(0);
        }
        else
        {
            player.setLevel(user.getLevel());
            player.setExp(0);
            player.setHealth(user.getLastHP());
        }

    }

    @EventHandler(priority =  EventPriority.HIGHEST)
    public void playerLeaveEvent(PlayerQuitEvent event)
    {
        CustomUser user = DBHandler.getUserByID(event.getPlayer().getUniqueId().toString());
        user.setLastHP((int)event.getPlayer().getHealth());
        DBHandler.updateCustomUser(user);
    }

}
