package fr.tractopelle.voteparty;

import fr.tractopelle.voteparty.commands.command.VotePartyCommand;
import fr.tractopelle.voteparty.config.Config;
import fr.tractopelle.voteparty.manager.VotePartyManager;
import fr.tractopelle.voteparty.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CorePlugin extends JavaPlugin {

    private Config configuration;
    private Config saveConfig;
    private VotePartyManager votePartyManager;
    private final Logger log = new Logger(this.getDescription().getFullName());

    @Override
    public void onEnable() {

        init();

    }

    @Override
    public void onDisable() {

        saveConfig.set("CURRENT", votePartyManager.getVotePartyCurrent());
        saveConfig.save();

    }

    public void init() {

        registerListeners();

        registerCommands();

        log.info("=======================================", Logger.LogType.SUCCESS);
        log.info(" Plugin initialization in progress...", Logger.LogType.SUCCESS);
        log.info(" Author: Tractopelle#4020", Logger.LogType.SUCCESS);
        log.info("=======================================", Logger.LogType.SUCCESS);

        this.configuration = new Config(this, "config");

        this.saveConfig = new Config(this, "save");

        this.votePartyManager = new VotePartyManager(
                saveConfig.getInt("CURRENT"),
                configuration.getInt("VOTE-PARTY-MAX")
        );

    }

    private void registerCommands() {

        new VotePartyCommand(this);

    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();

    }

    public Config getConfiguration() {
        return configuration;
    }

    public VotePartyManager getVotePartyManager() {
        return votePartyManager;
    }

}
