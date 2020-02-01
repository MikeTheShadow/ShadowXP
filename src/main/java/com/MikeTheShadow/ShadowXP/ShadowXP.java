package com.MikeTheShadow.ShadowXP;

import com.MikeTheShadow.ShadowXP.Listeners.*;
import com.MikeTheShadow.ShadowXP.Util.CustomUser;
import com.MikeTheShadow.ShadowXP.Util.DBHandler;
import com.MikeTheShadow.ShadowXP.Util.XPBoostExpansion;
import de.leonhard.storage.Json;
import me.realized.duels.api.Duels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ShadowXP extends JavaPlugin
{
    public static Json levelConfig;
    public static String DBName;

    public static Duels duelsApi;
    public static XPBoostExpansion expansion;
    @Override
    public void onEnable()
    {
        loadLevelConfig();
        //load those sweet deps
        duelsApi = (Duels) Bukkit.getServer().getPluginManager().getPlugin("Duels");
        DBName = levelConfig.getString("settings.DatabaseName")+ ".sqlite";
        DBHandler.createDatabase();
        DBHandler.createUserTable();
        //register listeners
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerXPEvent(), this);
        pluginManager.registerEvents(new PlayerAttacksListener(),this);
        pluginManager.registerEvents(new ModifyPlayerDamageEvent(),this);
        //command register
        registerCommands();
    }
    @Override
    public void onDisable()
    {
        for (Player player: Bukkit.getOnlinePlayers())
        {
            CustomUser user = DBHandler.getUserByID(player.getUniqueId().toString());
            user.setLastHP((int)player.getHealth());
            DBHandler.updateCustomUser(user);
        }
    }
    public void registerCommands()
    {
        this.getCommand("mystats").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("userstats").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("fixexperience").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("setexperience").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("setlevel").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("addexperience").setExecutor(new CustomCommandExecutor(this));
    }

    public void loadLevelConfig()
    {
        levelConfig = new Json("config", this.getDataFolder().getPath());
        //basic level stuff
        levelConfig.setDefault("levels.1", 100);
        levelConfig.setDefault("levels.2", 200);
        levelConfig.setDefault("levels.3", 300);
        levelConfig.setDefault("levels.4.", 400);
        levelConfig.setDefault("levels.5.", 500);

        levelConfig.setDefault("2.1","broadcast leveled %username% to level %level%");
        levelConfig.setDefault("2.2","broadcast you need more experience to level up!");
        levelConfig.setDefault("3.1","broadcast leveled %username% to level 2");
        levelConfig.setDefault("3.2","broadcast this is a second command woot!");
        expansion = new XPBoostExpansion();
        expansion.canRegister();

        levelConfig.setDefault("settings.experience","§8You gained §a % §8experience!");
        levelConfig.setDefault("settings.levelup","§6You leveled up to level %!");
        levelConfig.setDefault("settings.DatabaseName","userBase");
        levelConfig.setDefault("settings.attackMessage", "§6The level difference is too high!");
        levelConfig.setDefault("settings.levelDifference", 5);
    }


}
