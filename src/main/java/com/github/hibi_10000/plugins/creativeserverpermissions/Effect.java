package com.github.hibi_10000.plugins.creativeserverpermissions;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Effect implements CommandExecutor, TabCompleter {

    CreativeServerPermissions plugin;
    Effect(@NotNull CreativeServerPermissions instance) {this.plugin = instance;}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("effect")) {
            // 設定ミスかあるいは...?
            sender.sendMessage(ChatColor.RED + "An unknown error has occurred");
            return false;
        }
        if (!(sender instanceof Player)
                || sender.hasPermission("minecraft.command.effect")) {
            // senderがPlayerではないもしくはminecraft:effectを実行する権限を持っている
            StringBuilder sb = new StringBuilder("minecraft:effect");
            for (String str : args) {
                sb.append(" ").append(str);
            }
            plugin.getServer().dispatchCommand(sender, sb.toString());
            return true;
        }


        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Unknown or incomplete command");
            return false;
        }
        Player senderP = (Player) sender;
        if (args.length >= 2) {
            try {
                List<Entity> listE = plugin.getServer().selectEntities(sender, args[1]);
                if (listE.size() == 0) {
                    // 当てはまるエンティティがいなかった
                    sender.sendMessage(ChatColor.RED + "No entity was found");
                    return false;
                }
                if (listE.size() > 1 || !listE.get(0).getUniqueId().equals(senderP.getUniqueId())) {
                    // 自分以外を(も)指定した
                    sender.sendMessage(ChatColor.RED + "You are not authorized to specify a player");
                    return false;
                }
            } catch (IllegalArgumentException e) {
                // '/effect [give|clear] @[!(a|e|p|r|s)] ~'
                sender.sendMessage(ChatColor.RED + "Unknown selector type '" + args[1] + "'");
                return false;
            }
        }
        if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 3) {
                // '/effect give @s' or '/effect give'
                sender.sendMessage(ChatColor.RED + "Unknown or incomplete command");
                return false;
            }
            NamespacedKey key = NamespacedKey.minecraft(args[2].replace("minecraft:", ""));
            PotionEffectType effectType = PotionEffectType.getByKey(key);
            if (effectType == null) {
                sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
                return false;
            }
            int args3 = 30;
            int args4 = 0;
            boolean args5 = false;
            PotionEffect effect;
            if (args.length >= 4) {
                if (!args[3].chars().allMatch(Character::isDigit)) {
                    if (args[3].replace("-", "").chars().allMatch(Character::isDigit)) {
                        sender.sendMessage(ChatColor.RED
                                + "Integer must not be less than 1, found " + args[3]);
                        return false;
                    }
                    sender.sendMessage(ChatColor.RED + "Expected integer");
                    return false;
                }
                if (args[3].length() > 7) {
                    sender.sendMessage(ChatColor.RED
                            + "The duration integer must not be more than 1000000, found " + args[3]);
                    return false;
                }
                args3 = Integer.parseInt(args[3]);
                if (args3 == 0) {
                    sender.sendMessage(ChatColor.RED + "Integer must not be less than 1, found 0");
                    return false;
                }
                if (args3 > 1000000) {
                    sender.sendMessage(ChatColor.RED
                            + "The duration integer must not be more than 1000000, found " + args3);
                    return false;
                }
            }
            if (args.length >= 5) {
                if (!args[4].chars().allMatch(Character::isDigit)) {
                    if (args[4].replace("-", "").chars().allMatch(Character::isDigit)) {
                        sender.sendMessage(ChatColor.RED
                                + "Integer must not be less than 0, found " + args[3]);
                        return false;
                    }
                    sender.sendMessage(ChatColor.RED + "Expected integer");
                    return false;
                }
                if (args[3].length() > 3) {
                    sender.sendMessage(ChatColor.RED
                            + "The duration integer must not be more than 255, found " + args[3]);
                    return false;
                }
                args4 = Integer.parseInt(args[4]);
                if (args4 > 255) {
                    sender.sendMessage(ChatColor.RED + "Integer must not be more than 255, found " + args4);
                    return false;
                }
            }
            if (args.length == 6) {
                if (!args[5].equalsIgnoreCase("true")
                        && !args[5].equalsIgnoreCase("false")) {
                    sender.sendMessage(ChatColor.RED
                            + "Invalid boolean, expected 'true' or 'false' but found '"
                            + args[5] + "'");
                    return false;
                }
                args5 = Boolean.parseBoolean(args[5]);
            }
            if (args.length >= 7) {
                sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
                return false;
            }
            effect = new PotionEffect(effectType, args3 * 20, args4, false, !args5, !args5);
            senderP.addPotionEffect(effect);
            sender.sendMessage("Applied effect "
                    + Joiner.on(" ").join(CaseFormat.LOWER_UNDERSCORE
                    .to(CaseFormat.UPPER_CAMEL, effectType.getKey().getKey()).split("(?=[A-Z])"))
                    + " to " + sender.getName());
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + sender.getName()
                    + ": Applied effect " + Joiner.on(" ").join(CaseFormat.LOWER_UNDERSCORE
                    .to(CaseFormat.UPPER_CAMEL, effectType.getKey().getKey()).split("(?=[A-Z])"))
                    + " to " + sender.getName() + "]");
            // \u007f7 =? §7
            return true;
        }
        if (args[0].equalsIgnoreCase("clear")) {
            if (args.length == 1 || args.length == 2) {
                // '/effect clear' or '/effect clear @s' or '/effect clear [MCID]'
                Collection<PotionEffect> pEffects = senderP.getActivePotionEffects();
                if (pEffects.size() == 0) {
                    sender.sendMessage(ChatColor.RED + "Target has no effects to remove");
                    return false;
                }
                for (PotionEffect p : pEffects) {
                    senderP.removePotionEffect(p.getType());
                }
                sender.sendMessage("Removed every effect from " + sender.getName());
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "[" + sender.getName()
                        + ": Removed every effect from " + sender.getName() + "]");
                return true;
            }
            if (args.length != 3) {
                // 引数が多すぎる
                sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
                return false;
            }
            // '/effect clear [selector] [effectType]'
            NamespacedKey key = NamespacedKey.minecraft(args[2].replace("minecraft:", ""));
            PotionEffectType effectType = PotionEffectType.getByKey(key);
            if (effectType == null) {
                // [effectType]が間違っていた
                sender.sendMessage(ChatColor.RED + "Can't find element '" + args[2]
                        + "' of type 'minecraft:mod_effect'");
                return false;
            }
            Collection<PotionEffect> pEffects = senderP.getActivePotionEffects();
            if (pEffects.size() == 0) {
                // senderがeffectを持っていなかった
                sender.sendMessage(ChatColor.RED + "Target doesn't have the requested effect");
                return false;
            }
            for (PotionEffect p : pEffects) {
                if (p.getType().equals(effectType)) {
                    // senderが[effectType]の効果を持っていた
                    senderP.removePotionEffect(effectType);
                    sender.sendMessage("Removed effect "
                            + effectType.getKey().getKey().replace("_", " ")
                            + " from " + sender.getName());
                    plugin.getServer().getConsoleSender().sendMessage(ChatColor.GRAY + "["
                            + sender.getName() + ": Removed effect "
                            + Joiner.on(" ").join(CaseFormat.LOWER_UNDERSCORE
                            .to(CaseFormat.UPPER_CAMEL, effectType.getKey().getKey()).split("(?=[A-Z])"))
                            + " from " + sender.getName() + "]");
                    return true;
                }
            }
            // senderが[effectType]の効果を持っていなかった
            sender.sendMessage(ChatColor.RED + "Target doesn't have the requested effect");
            return false;
        }
        sender.sendMessage(ChatColor.RED + "Incorrect argument for command");
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("effect")) return null;
        List<String> list = new ArrayList<>();
        if (args.length > 1) {
            if (args.length == 2) {
                if (!(sender instanceof Player)
                        || sender.hasPermission("minecraft.command.effect")) {
                    list.add("@a");
                    list.add("@e");
                    list.add("@p");
                    list.add("@r");
                    list.add("@s");
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                } else {
                    list.add("@s");
                    list.add(sender.getName());
                }
                return list;
            }
            if (args.length == 3) {
                for (PotionEffectType e : PotionEffectType.values()) {
                    list.add("minecraft:" + e.getKey().getKey());
                }
                return list;
            }
            if ((args.length == 4 || args.length == 5) && args[0].equalsIgnoreCase("give")) {
                return null;
            } else
            if (args.length == 6 && args[0].equalsIgnoreCase("give")) {
                list.add("true");
                list.add("false");
                return list;
            }
            return null;
        }
        list.add("give");
        list.add("clear");
        return list;
    }
}
