/*
 *    Copyright 2020 Miniature Pets
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package net.miniaturepets.citizensintegration;

import com.kirelcodes.miniaturepets.commands.subcommands.CommandLoader;
import com.kirelcodes.miniaturepets.commands.ExtendedCommandBase;
import com.kirelcodes.miniaturepets.loader.PetLoader;
import com.kirelcodes.miniaturepets.mob.Mob;
import com.kirelcodes.miniaturepets.pets.PetContainer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MiniatureNPCs extends JavaPlugin {

    private final Set<Mob> mobs = new HashSet<>();

    @Override
    public void onEnable() {
        Plugin citizens = getServer().getPluginManager().getPlugin("Citizens");
        if (citizens == null || !citizens.isEnabled()) {
            getLogger().log(Level.SEVERE, "Citizens is not found or not enabled. Please make sure you have Citizens 2.x or higher.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerNpcSubcommand();

        // Register trait with Citizens
        CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(MiniatureNpcTrait.class).withName("miniature_npc"));
    }

    private void registerNpcSubcommand() {
        CommandLoader.getMajorCommand().getCommandManager().addCommand(new ExtendedCommandBase("npc", "miniaturepets.npc.create", "Create a Miniature Pets NPC") {
            @Override
            public boolean executeCommand(CommandSender sender, String label, String[] args, boolean isPlayer) {
                if (!isPlayer) {
                    sender.sendMessage("This command can only be executed by a player.");
                    return false;
                }
                Player player = (Player) sender;

                return NpcSubcommand.onCommand(player, CommandLoader.getMajorCommand().getLabel(), args);
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String label, String[] args) {
                if (args.length == 2) {
                    return PetLoader.getPets().stream().map(PetContainer::getType).filter((type) -> type.startsWith(args[1])).collect(Collectors.toList());
                } else {
                    return new ArrayList<>();
                }
            }
        });
    }

    @Override
    public void onDisable() {
        mobs.forEach(Mob::remove);
        mobs.clear();
    }

    void registerMob(Mob m) {
        mobs.add(m);
    }
}
