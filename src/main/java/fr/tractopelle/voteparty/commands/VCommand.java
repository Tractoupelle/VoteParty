package fr.tractopelle.voteparty.commands;

import fr.tractopelle.voteparty.CorePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class VCommand implements CommandExecutor {

    private final String commandName;
    private final boolean consoleCanExecute;
    private final CorePlugin corePlugin;
    private final String permission;

    public VCommand(CorePlugin corePlugin, String commandName, boolean consoleCanExecute, String permission) {
        this.commandName = commandName;
        this.consoleCanExecute = consoleCanExecute;
        this.corePlugin = corePlugin;
        this.permission = permission;
        corePlugin.getCommand(commandName).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String prefix = corePlugin.getConfiguration().getString("PREFIX");

        if (!command.getLabel().equalsIgnoreCase(commandName))
            return true;

        if (!consoleCanExecute && !(sender instanceof Player)) {
            sender.sendMessage(prefix + corePlugin.getConfiguration().getString("NOT-PLAYER"));
            return true;
        }

        if(!(permission.equals("NONE"))) {
            if (!sender.hasPermission(permission)) {
                sender.sendMessage(prefix + corePlugin.getConfiguration().getString("NO-PERMISSION"));
                return true;
            }
        }

        return execute(sender, args);
    }


    public abstract boolean execute(CommandSender sender, String[] args);
}