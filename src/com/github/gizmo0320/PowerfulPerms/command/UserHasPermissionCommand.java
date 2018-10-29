package com.github.gizmo0320.PowerfulPerms.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.github.gizmo0320.PowerfulPerms.common.ChatColor;
import com.github.gizmo0320.PowerfulPerms.common.ICommand;
import com.github.gizmo0320.PowerfulPermsAPI.PermissionManager;
import com.github.gizmo0320.PowerfulPermsAPI.PowerfulPermsPlugin;
import com.google.common.util.concurrent.ListenableFuture;

public class UserHasPermissionCommand extends SubCommand {

    public UserHasPermissionCommand(PowerfulPermsPlugin plugin, PermissionManager permissionManager) {
        super(plugin, permissionManager);
        usage.add("/pp user <user> haspermission <permission> (server) (world)");
    }

    @Override
    public CommandResult execute(final ICommand invoker, final String sender, String[] args) throws InterruptedException, ExecutionException {
        if (hasBasicPerms(invoker, sender, "powerfulperms.user.haspermission") || (args != null && args.length >= 3 && hasPermission(invoker, sender, "powerfulperms.user.haspermission." + args[2]))) {
            if (args != null && args.length >= 2 && args[1].equalsIgnoreCase("haspermission")) {
                if (args.length < 3) {
                    sendSender(invoker, sender, getUsage());
                    return CommandResult.success;
                }
                final String playerName = args[0];
                final String permission = args[2];
                String server = "";
                if (args.length >= 4)
                    server = args[3];
                String world = "";
                if (args.length >= 5)
                    world = args[4];

                final String serverFinal = server;
                final String worldFinal = world;

                UUID uuid = permissionManager.getConvertUUIDBase(playerName);
                if (uuid == null) {
                    sendSender(invoker, sender, "Could not find player UUID.");
                } else {
                    permissionManager.getScheduler().runAsync(() -> {
                        ListenableFuture<Boolean> second = _permissionManager.playerHasPermission(uuid, permission, worldFinal, serverFinal);
                        Boolean has;
                        try {
                            has = second.get();
                            if (has != null) {
                                if (has)
                                    sendSender(invoker, sender, playerName + " has the permission \"" + permission + "\".");
                                else
                                    sendSender(invoker, sender, playerName + ChatColor.RED + " does not " + ChatColor.WHITE + "have the permission \"" + permission + "\".");
                            } else
                                sendSender(invoker, sender, "The permission \"" + permission + "\" is not set.");
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }, false);
                }
                return CommandResult.success;
            } else
                return CommandResult.noMatch;
        } else
            return CommandResult.noPermission;
    }

    @Override
    public List<String> tabComplete(ICommand invoker, String sender, String[] args) {
        if ("haspermission".startsWith(args[0].toLowerCase())) {
            List<String> output = new ArrayList<>();
            output.add("haspermission");
            return output;
        }
        return null;
    }
}
