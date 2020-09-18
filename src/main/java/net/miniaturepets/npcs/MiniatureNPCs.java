package net.miniaturepets.npcs;

import com.kirelcodes.miniaturepets.commands.CommandLoader;
import com.kirelcodes.miniaturepets.commands.ExtendedCommandBase;
import com.kirelcodes.miniaturepets.loader.PetLoader;
import com.kirelcodes.miniaturepets.mob.Mob;
import com.kirelcodes.miniaturepets.pets.PetContainer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MiniatureNPCs extends JavaPlugin {

    private final Vector<Mob> mobs = new Vector<>();

    @Override
    public void onEnable() {
        //check if Citizens is present and enabled.
        Plugin citizens = getServer().getPluginManager().getPlugin("Citizens");
        if (citizens == null || !citizens.isEnabled()) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerNpcSubcommand();

        // Register trait with Citizens
        CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(MiniatureNpcTrait.class).withName("miniature_npc"));
    }

    private void registerNpcSubcommand() {
        CommandLoader.getMajorCommand().getCommandManager().addCommand(new ExtendedCommandBase("npc", "MiniatureNPCs.AddNpc", "Create a MiniaturePet npc.") {
            @Override
            public boolean executeCommand(CommandSender sender, String label, String[] args, boolean isPlayer) {
                if (!isPlayer) {
                    sender.sendMessage("Only a player can execute this command.");
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
        for (Mob mob : mobs) {
            mob.remove();
        }
        mobs.clear();
    }

    void registerMob(Mob m) {
        mobs.add(m);
    }
}
