package net.miniaturepets.npcs;

import com.kirelcodes.miniaturepets.loader.PetLoader;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NpcSubcommand {
    public static boolean onCommand(@NotNull Player player, @NotNull String label, @NotNull String[] args) {
        if(args.length != 2) {
            player.sendMessage("Usage: /" + label + " npc <name> <type>");
            return false;
        }
        String npcName = args[0];
        String petType = args[1];

        if (PetLoader.getPet(petType) == null) {
            player.sendMessage("Pet type not found.");
            return false;
        }

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, npcName);
        npc.addTrait(MiniatureNpcTrait.class);
        npc.getTrait(MiniatureNpcTrait.class).setPetType(petType);

        npc.spawn(player.getLocation());
        ((Citizens) Bukkit.getPluginManager().getPlugin("Citizens")).getNPCSelector().select(player, npc);

        return true;
    }
}
