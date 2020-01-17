package com.MikeTheShadow.ShadowXP;

import com.MikeTheShadow.ShadowXP.Listeners.CustomCommandExecutor;
import com.MikeTheShadow.ShadowXP.Listeners.PlayerAttacksListener;
import com.MikeTheShadow.ShadowXP.Listeners.PlayerJoinListener;
import com.MikeTheShadow.ShadowXP.Listeners.PlayerXPEvent;
import com.MikeTheShadow.ShadowXP.Util.DBHandler;
import com.MikeTheShadow.ShadowXP.Util.XPBoostExpansion;
import de.leonhard.storage.Json;
import me.realized.duels.api.Duels;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ShadowXP extends JavaPlugin
{
    public static Json config;
    public static String DBName;

    public static Duels duelsApi;
    public static XPBoostExpansion expansion;
    @Override
    public void onEnable()
    {
        //load those sweet deps
        duelsApi = (Duels) Bukkit.getServer().getPluginManager().getPlugin("Duels");
        config = new Json("config", this.getDataFolder().getPath());
        config.setDefault("levels.1", 100);
        config.setDefault("levels.2", 200);
        config.setDefault("levels.3", 300);
        config.setDefault("levels.4", 400);
        config.setDefault("levels.5", 500);
        config.setDefault("levels.6", 600);
        config.setDefault("levels.7", 700);
        config.setDefault("levels.8", 800);

        expansion = new XPBoostExpansion();
        expansion.canRegister();

        config.setDefault("settings.experience","§8You gained §a % §8experience!");
        config.setDefault("settings.levelup","§6You leveled up to level %!");
        config.setDefault("settings.DatabaseName","userBase");
        config.setDefault("settings.attackMessage", "§6The level difference is too high!");
        config.setDefault("settings.levelDifference", 5);
        DBName = config.getString("settings.DatabaseName")+ ".sqlite";
        DBHandler.createDatabase();
        DBHandler.createUserTable();

        //register listeners
        PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerXPEvent(), this);
        pluginManager.registerEvents(new PlayerAttacksListener(),this);
        //command register
        this.getCommand("mystats").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("userstats").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("fixexperience").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("setexperience").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("setlevel").setExecutor(new CustomCommandExecutor(this));
        this.getCommand("addexperience").setExecutor(new CustomCommandExecutor(this));
    }
    @Override
    public void onDisable()
    {

    }


}
