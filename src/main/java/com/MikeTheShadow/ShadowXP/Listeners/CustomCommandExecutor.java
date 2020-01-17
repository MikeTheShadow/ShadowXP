package com.MikeTheShadow.ShadowXP.Listeners;

import com.MikeTheShadow.ShadowXP.ShadowXP;
import com.MikeTheShadow.ShadowXP.Util.CustomUser;
import com.MikeTheShadow.ShadowXP.Util.DBHandler;
import de.leonhard.storage.Json;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CustomCommandExecutor implements CommandExecutor
{
    private final ShadowXP shadowXP;
    public CustomCommandExecutor(ShadowXP shadowXP) { this.shadowXP = shadowXP; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args)
    {
        if (cmd.getName().equalsIgnoreCase("userstats"))
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage("This command can only be run by a player.");
            }
            if(args.length > 1)return false;
            CustomUser user;
            Player player = (Player) sender;
            if(args.length == 0)
            {
                user = DBHandler.getUserByID(player.getUniqueId().toString());
            }
            else
            {
                OfflinePlayer offPlayer = Bukkit.getServer().getOfflinePlayer(args[0]);
                user = DBHandler.getUserByID(offPlayer.getUniqueId().toString());
            }
            if(user == null)
            {
                sender.sendMessage("User " + args[0] + " does not exist!");
                return true;
            }
            StringBuilder builder = new StringBuilder();
            //open stats
            builder.append("\n").append(StringUtils.repeat("§5~",20)).append("\n");
            //add username
            builder.append(StringUtils.repeat(" ",10 - (user.getName().length()/2)));
            builder.append("§6").append(user.getName()).append("\n").append("\n \n");
            //experience information if user is max level do not display information
            if(ShadowXP.config.getInt("levels." + (user.getLevel() + 1))  != 0 )
            {
                builder.append("§3Level: ").append(user.getLevel()).append("\n \n");
                builder.append("§2Current Experience: §a").append(user.getCurrentXP()).append("§e/§a").append(ShadowXP.config.getInt("levels." + user.getLevel())).append("\n \n");
            }
            else
            {
                builder.append("§3Level: ").append(user.getLevel()).append("§5 (MAX)").append("\n \n");
            }
            //finally add total Experience
            builder.append("§9Total Experience: §c").append(user.getTotalXP()).append("\n");
            //close stats
            builder.append(StringUtils.repeat("§5~",20));
            player.sendMessage(builder.toString());
            return true;
        }
        else if(cmd.getName().equalsIgnoreCase("mystats"))
        {
            if (!(sender instanceof Player))
            {
                sender.sendMessage("This command can only be run by a player.");
            }
            if(args.length > 0)return false;
            CustomUser user = null;
            Player player = (Player) sender;
            user = DBHandler.getUserByID(player.getUniqueId().toString());
            StringBuilder builder = new StringBuilder();
            //open stats
            builder.append("\n").append(StringUtils.repeat("§5~",20)).append("\n");
            //add username
            builder.append(StringUtils.repeat(" ",10 - (user.getName().length()/2)));
            builder.append("§6").append(user.getName()).append("\n").append("\n \n");
            //experience information if user is max level do not display information
            if(ShadowXP.config.getInt("levels." + (user.getLevel() + 1))  != 0 )
            {
                builder.append("§3Level: ").append(user.getLevel()).append("\n \n");
                builder.append("§2Current Experience: §a").append(user.getCurrentXP()).append("§e/§a").append(ShadowXP.config.getInt("levels." + user.getLevel())).append("\n \n");
            }
            else
            {
                builder.append("§3Level: ").append(user.getLevel()).append("§5 (MAX)").append("\n \n");
            }
            //finally add total Experience
            builder.append("§9Total Experience: §c").append(user.getTotalXP()).append("\n");
            //close stats
            builder.append(StringUtils.repeat("§5~",20));
            player.sendMessage(builder.toString());
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("fixexperience"))
        {
            List<CustomUser> customUsers = DBHandler.getAllUsers();
            ShadowXP.config.update();
            for (CustomUser user: customUsers)
            {
                updateServerUser(user);
            }
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("setexperience"))
        {
            if(warnUser(args,sender)) return false;
            Player target = (Bukkit.getServer().getPlayer(args[0]));
            if (target != null)
            {
                CustomUser user = DBHandler.getUserByID(target.getUniqueId().toString());
                user.setTotalXP(Integer.parseInt(args[1]));
                target.setLevel(updateServerUser(user));
                return true;
            }
            else
            {
                OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(args[0]);
                CustomUser user = DBHandler.getUserByID(offPlayer.getUniqueId().toString());
                user.setTotalXP(Integer.parseInt(args[1]));
                updateServerUser(user);
            }
            return true;
        }
        else if (cmd.getName().equalsIgnoreCase("setlevel"))
        {
            if(warnUser(args,sender)) return false;
            Json config = ShadowXP.config;
            int levelXP = config.getInt("levels." + args[1]);
            if(levelXP == 0)
            {
                try { sender.sendMessage("Level too high!"); }
                catch (Exception e) { Bukkit.getConsoleSender().sendMessage("Level too high!"); }
                return false;
            }
            Player target = (Bukkit.getServer().getPlayer(args[0]));
            String ID = "";
            boolean isOnline = false;
            if (target != null) {
                ID = target.getUniqueId().toString();
                isOnline = true;
            }
            else { ID = Bukkit.getOfflinePlayer(args[0]).getUniqueId().toString(); }
            CustomUser user = DBHandler.getUserByID(ID);
            updateUserLevel(user,Integer.parseInt(args[1]),isOnline);
            return true;
        }
        //ADD EXPERIENCE
        else if(cmd.getName().equalsIgnoreCase("addexperience"))
        {
            if(warnUser(args,sender)) return false;

            Player target = (Bukkit.getServer().getPlayer(args[0]));
            CustomUser user = DBHandler.getUserByID(target.getUniqueId().toString());
            try
            {
                int xpToAdd = Integer.parseInt(args[1]);
                float bonus = Float.parseFloat(ShadowXP.expansion.onPlaceholderRequest(target,"boost_zero"));
                if(bonus < 2) bonus = 1;
                xpToAdd = Math.round(bonus * xpToAdd);
                user.addTotalXP(xpToAdd);
                user.addXP(xpToAdd);
                updateServerUser(user,xpToAdd);
            }
            catch (Exception e)
            {
                Bukkit.getServer().getConsoleSender().sendMessage("Error adding experience check that values are correct!" + e.getMessage());
                return false;
            }
            return true;
        }
        return false;
    }
    public boolean warnUser(String[] args,CommandSender sender)
    {
        if(args.length != 2)
        {
            if(sender instanceof Player)
            {
                ((Player) sender).getPlayer().sendMessage("Please use correct parameters!");
            }
            else{Bukkit.getConsoleSender().sendMessage("Please Use correct parameters!");}
            return true;
        }
        return false;
    }

    public void updateUserLevel(CustomUser user, int setLevel, boolean isOnline)
    {
        user.setTotalXP(0);
        user.setXP(0);
        Json config = ShadowXP.config;
        int finalXP = 0;
        for(int i = 0; i < setLevel;i++)
        {
             finalXP += config.getInt("levels." + i);
        }
        user.setTotalXP(finalXP);
        updateServerUser(user);
        DBHandler.updateCustomUser(user);
        if(isOnline)
        {
            Bukkit.getServer().getPlayer(user.getName()).setLevel(user.getLevel());
        }
    }

    public int updateServerUser(CustomUser user)
    {
        Json config = ShadowXP.config;
        int totalXP = user.getTotalXP();
        int levelXP = config.getInt("levels.1");
        int level = 1;
        while(levelXP != 0)
        {
            if(totalXP - levelXP < 0)
            {
                user.setXP(totalXP);
                user.setLevel(level);
                break;
            }
            if(totalXP < levelXP)
            {
                user.setXP(totalXP);
                user.setLevel(level);
                break;
            }
            totalXP -= levelXP;
            level++;
            levelXP = config.getInt("levels." + level);
        }
        DBHandler.updateCustomUser(user);
        Player target = (Bukkit.getServer().getPlayer(user.getName()));
        if(target != null)
        {
            target.setLevel(user.getLevel());
            target.setExp(0);
        }
        return level;
    }
    public int updateServerUser(CustomUser user, int amount)
    {
        Json config = ShadowXP.config;
        int totalXP = user.getTotalXP();
        int levelXP = config.getInt("levels.1");
        int level = 1;
        int startingLevel = user.getLevel();
        while(levelXP != 0)
        {
            if(totalXP - levelXP < 0)
            {
                user.setXP(totalXP);
                user.setLevel(level);
                break;
            }
            if(totalXP < levelXP)
            {
                user.setXP(totalXP);
                user.setLevel(level);
                break;
            }
            totalXP -= levelXP;
            level++;
            levelXP = config.getInt("levels." + level);
        }
        DBHandler.updateCustomUser(user);
        Player target = (Bukkit.getServer().getPlayer(user.getName()));
        if(target != null)
        {
            target.setLevel(user.getLevel());
            target.setExp(0);
            target.sendMessage(ShadowXP.config.get("settings.experience").toString().replace("%","" + amount));
            if(user.getLevel() == startingLevel) return level;
            target.sendMessage(ShadowXP.config.get("settings.levelup").toString().toString().replace("%","" + user.getLevel()));
        }
        return level;
    }
}
