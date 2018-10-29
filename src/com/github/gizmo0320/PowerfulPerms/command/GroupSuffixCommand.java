package com.github.gizmo0320.PowerfulPerms.command;

import com.github.gizmo0320.PowerfulPerms.common.ChatColor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.github.gizmo0320.PowerfulPerms.common.ICommand;
import com.github.gizmo0320.PowerfulPermsAPI.Group;
import com.github.gizmo0320.PowerfulPermsAPI.PermissionManager;
import com.github.gizmo0320.PowerfulPermsAPI.PowerfulPermsPlugin;
import com.github.gizmo0320.PowerfulPermsAPI.Response;

public class GroupSuffixCommand extends SubCommand {

    public GroupSuffixCommand(PowerfulPermsPlugin plugin, PermissionManager permissionManager) {
        super(plugin, permissionManager);
        usage.add("/pp group <group> suffix set <suffix> (server) | ... suffix remove (server)");
    }

    @Override
    public CommandResult execute(final ICommand invoker, final String sender, String[] args) throws InterruptedException, ExecutionException {
        if (hasBasicPerms(invoker, sender, "powerfulperms.group.suffix")) {
            if (args != null && args.length >= 2 && args[1].equalsIgnoreCase("suffix")) {
                if (args.length == 3 && !args[2].equalsIgnoreCase("remove")) {
                    sendSender(invoker, sender, getUsage());
                    return CommandResult.success;
                }
                final String groupName = args[0];
                final Group group = permissionManager.getGroup(groupName);
                if (group == null) {
                    sendSender(invoker, sender, "Group does not exist.");
                    return CommandResult.success;
                }
                int groupId = group.getId();

                String server = "";
                if (args.length >= 4 && args[2].equalsIgnoreCase("set")) {
                    String suffix = "";
                    if (args.length >= 5 && args[3].toCharArray()[0] != '"')
                        server = args[4];
                    if (args[3].length() >= 1 && args[3].toCharArray()[0] == '"') {
                        // Input is between quote marks.
                        String result = "";
                        result += args[3].substring(1) + " ";

                        int lastArg = 3;
                        if (!result.endsWith("\" ")) {
                            if (args.length >= 5) {
                                for (int i = 4; i < args.length; i++) {
                                    result += args[i] + " ";
                                    if (args[i].endsWith("\"")) {
                                        lastArg = i;
                                        break;
                                    }
                                }
                            }
                        }

                        // If server is specified set server to argument after
                        if (args.length >= lastArg + 2) {
                            server = args[lastArg + 1];
                        }

                        // remove '" '
                        if (result.toCharArray()[result.length() - 1] == ' ')
                            result = result.substring(0, result.length() - 1);
                        if (result.toCharArray()[result.length() - 1] == '"')
                            result = result.substring(0, result.length() - 1);

                        suffix = result;
                    } else
                        suffix = args[3];

                    Response response = permissionManager.setGroupSuffixBase(groupId, suffix, server);
                    sendSender(invoker, sender, response.getResponse());
                } else if (args.length >= 3 && args[2].equalsIgnoreCase("remove")) {
                    Response response = permissionManager.setGroupSuffixBase(groupId, "", (args.length >= 4 ? args[3] : ""));
                    sendSender(invoker, sender, response.getResponse());
                } else {
                    HashMap<String, String> prefix = permissionManager.getGroupServerSuffix(groupId);
                    if (prefix != null) {
                        String output = "";
                        Iterator<Entry<String, String>> it = prefix.entrySet().iterator();
                        while (it.hasNext()) {
                            Entry<String, String> entry = it.next();
                            output += ChatColor.WHITE + "\"" + entry.getValue() + "\":" + (entry.getKey().isEmpty() ? ChatColor.RED + "ALL" + ChatColor.WHITE : entry.getKey());
                            if (it.hasNext())
                                output += ", ";
                        }
                        sendSender(invoker, sender, "Suffixes for group " + groupName + ": " + output);
                    } else
                        sendSender(invoker, sender, "Group does not exist.");
                }
                return CommandResult.success;
            } else
                return CommandResult.noMatch;
        } else
            return CommandResult.noPermission;
    }

    @Override
    public List<String> tabComplete(ICommand invoker, String sender, String[] args) {
        if (args.length == 1 && "suffix".startsWith(args[0].toLowerCase())) {
            List<String> output = new ArrayList<>();
            output.add("suffix");
            return output;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("suffix")) {
            List<String> output = new ArrayList<>();
            if ("set".startsWith(args[1].toLowerCase()))
                output.add("set");
            if ("remove".startsWith(args[1].toLowerCase()))
                output.add("remove");
            return output;
        }
        return null;
    }
}
