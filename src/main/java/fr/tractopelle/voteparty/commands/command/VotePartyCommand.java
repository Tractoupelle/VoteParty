package fr.tractopelle.voteparty.commands.command;

import fr.tractopelle.voteparty.CorePlugin;
import fr.tractopelle.voteparty.commands.VCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.awt.*;

public class VotePartyCommand extends VCommand {

    private CorePlugin corePlugin;

    public VotePartyCommand(CorePlugin corePlugin) {
        super(corePlugin, "voteparty", true, "NONE");
        this.corePlugin = corePlugin;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] args) {

        String prefix = corePlugin.getConfiguration().getString("PREFIX");

        if (args.length == 0 || args.length > 2 || args.length == 1 && (!args[0].equalsIgnoreCase("info"))) {

            if (commandSender.hasPermission("VOTEPARTY.ADMIN")) {

                corePlugin.getConfiguration().getStringList("USAGE-ADMIN").forEach(commandSender::sendMessage);
                return false;

            } else {

                commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("USAGE"));
                return false;

            }

        } else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {

            commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("INFO-VOTE")
                    .replace("%current%", String.valueOf(corePlugin.getVotePartyManager().getVotePartyCurrent()))
                    .replace("%currentmax%", String.valueOf(corePlugin.getVotePartyManager().getVotePartyMax())));

        } else if (args.length == 2) {

            if (!(commandSender.hasPermission("VOTEPARTY.ADMIN"))) {
                commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("NO-PERMISSION"));
                return false;
            }

            if (!(isInt(args[1]))) {
                commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("USAGE-ADMIN"));
                return false;
            }

            int i = Integer.parseInt(args[1]);

            switch (args[0]) {

                case "add":

                    if (corePlugin.getVotePartyManager().getVotePartyCurrent() + i >= corePlugin.getVotePartyManager().getVotePartyMax()) {

                        // REMISE A ZERO + GIVE + MESSAGE
                        corePlugin.getVotePartyManager().setVotePartyCurrent(0);

                        for (String s : corePlugin.getConfiguration().getStringList("VOTE-PARTY-REACHED-TYPE")) {

                            switch (s) {

                                case "CHAT":

                                    corePlugin.getConfiguration().getStringList("VOTE-PARTY-REACHED-CHAT.MESSAGE").forEach(Bukkit::broadcastMessage);

                                    break;

                                case "TITLE":

                                    String title = corePlugin.getConfiguration().getString("VOTE-PARTY-REACHED-TITLE.TITLE");
                                    String subtitle = corePlugin.getConfiguration().getString("VOTE-PARTY-REACHED-TITLE.SUBTITLE");
                                    int fadeinT = corePlugin.getConfiguration().getInt("VOTE-PARTY-REACHED-TITLE.FADE-IN");
                                    int stayT = corePlugin.getConfiguration().getInt("VOTE-PARTY-REACHED-TITLE.STAY");
                                    int fadeoutT = corePlugin.getConfiguration().getInt("VOTE-PARTY-REACHED-TITLE.FADE-OUT");

                                    for (Player player : Bukkit.getOnlinePlayers()) {

                                        sendTitle(player, title, subtitle, fadeinT, stayT, fadeoutT);

                                    }


                                    break;

                                case "ACTIOBAR":

                                    String message = corePlugin.getConfiguration().getString("VOTE-PARTY-REACHED-ACTIONBAR.MESSAGE");


                                    for (Player player : Bukkit.getOnlinePlayers()) {

                                        sendActionBar(player, message);

                                    }

                                    break;

                            }

                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {

                            corePlugin.getConfiguration().getStringList("VOTE-PARTY-REACHED-COMMAND")
                                    .forEach(line -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), line.replace("%player%", player.getName())));

                        }


                    } else {

                        corePlugin.getVotePartyManager().setVotePartyCurrent(corePlugin.getVotePartyManager().getVotePartyCurrent() + i);

                    }

                    commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("ADD-VOTE").replace("%value%", String.valueOf(i)));

                    break;
                case "remove":

                    if (corePlugin.getVotePartyManager().getVotePartyCurrent() - i < 0) {

                        corePlugin.getVotePartyManager().setVotePartyCurrent(0);

                        commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("RESET-VOTE"));

                    } else {

                        corePlugin.getVotePartyManager().setVotePartyCurrent(corePlugin.getVotePartyManager().getVotePartyCurrent() - i);

                        commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("REMOVE-VOTE").replace("%value%", String.valueOf(i)));

                    }

                    break;
                default:
                    commandSender.sendMessage(prefix + corePlugin.getConfiguration().getString("USAGE"));
                    break;

            }
        }

        return false;
    }

    public static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    public void sendTitle(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {

        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\",color:" + ChatColor.YELLOW.name().toLowerCase() + "}");
        IChatBaseComponent chatSubTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\",color:" + ChatColor.GRAY.name().toLowerCase() + "}");

        PacketPlayOutTitle t = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle s = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubTitle);
        PacketPlayOutTitle length = new PacketPlayOutTitle(fadein, stay, fadeout);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(t);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(s);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
    }

    public void sendActionBar(Player player, String message) {

        CraftPlayer craftPlayer = (CraftPlayer) player;
        IChatBaseComponent cbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
        craftPlayer.getHandle().playerConnection.sendPacket(ppoc);

    }
}
