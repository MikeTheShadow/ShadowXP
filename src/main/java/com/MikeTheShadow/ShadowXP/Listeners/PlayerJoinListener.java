package com.MikeTheShadow.ShadowXP.Listeners;

import com.MikeTheShadow.ShadowXP.Util.CustomUser;
import com.MikeTheShadow.ShadowXP.Util.DBHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener
{
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        //Add player to database if they don't exist
        CustomUser user = DBHandler.GetUserByID(player.getUniqueId().toString());
        if(user == null)
        {
            DBHandler.InsertNewUser(player.getName(),player.getUniqueId().toString(),1,0,0);
            user = DBHandler.GetUserByID(player.getUniqueId().toString());
        }
        player.setLevel(user.getLevel() + 1);
        player.setLevel(user.getLevel());
        player.setExp(0);
    }

}
