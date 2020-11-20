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
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class MiniatureNPCs extends JavaPlugin {

    @Getter
    MobManager mobManager;

    @Override
    public void onEnable() {
        Plugin citizens = getServer().getPluginManager().getPlugin("Citizens");
        if (citizens == null || !citizens.isEnabled()) {
            getLogger().log(Level.SEVERE, "Citizens is not found or not enabled. Please make sure you have Citizens 2.x or higher.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        mobManager = new MobManager();

        CommandLoader.getMajorCommand().getCommandManager().addCommand(new NpcCommand());

        // Register trait with Citizens
        CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(MiniatureNpcTrait.class).withName("miniature_npc"));
    }

    @Override
    public void onDisable() {
        mobManager.removeAll();
    }

}
