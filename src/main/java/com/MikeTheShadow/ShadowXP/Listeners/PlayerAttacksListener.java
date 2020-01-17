package com.MikeTheShadow.ShadowXP.Listeners;

import com.MikeTheShadow.ShadowXP.ShadowXP;
import com.MikeTheShadow.ShadowXP.Util.CustomUser;
import com.MikeTheShadow.ShadowXP.Util.DBHandler;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import me.realized.duels.api.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerAttacksListener implements Listener
{
    @EventHandler
    public void onEntityAttacksEntity(EntityDamageByEntityEvent event)
    {
        if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player)) return;

        int levelDif = ShadowXP.config.getInt("settings.levelDifference");
        //WORLDGUARD BS
        Location loc = event.getEntity().getLocation();
        WorldGuardPlugin worldGuardPlugin =  WorldGuardPlugin.getPlugin(WorldGuardPlugin.class);
        RegionContainer worldguard = WorldGuardPlugin.getPlugin(WorldGuardPlugin.class).getRegionContainer();
        RegionQuery query = worldguard.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        try { if(!set.allows(DefaultFlag.PVP,worldGuardPlugin.wrapPlayer((Player) event.getEntity()))) { return; } }
        catch (Exception e) { e.printStackTrace(); }
        //END OF WORLDGUARD BS
        Player att = (Player) event.getDamager();
        Player def = (Player) event.getEntity();
        CustomUser attacker = DBHandler.getUserByID(att.getUniqueId().toString());
        CustomUser defender = DBHandler.getUserByID(def.getUniqueId().toString());
        //make sure they're not dueling. If they are we don't care
        if(playersInDuel(att,def)) return;
        if(Math.abs(attacker.getLevel() - defender.getLevel()) >= levelDif)
        {
            att.sendMessage(ShadowXP.config.getString("settings.attackMessage"));
            event.setDamage(0);
            event.setCancelled(true);
        }
    }
    public boolean playersInDuel(Player attacker, Player defender)
    {
        Arena arena = ShadowXP.duelsApi.getArenaManager().get(attacker);
        if(arena == null) return false;
        if(arena.has(attacker) && arena.has(defender))return true;
        return false;
    }
}
