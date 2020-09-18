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

package net.miniaturepets.npcs;

import com.kirelcodes.miniaturepets.loader.PetLoader;
import com.kirelcodes.miniaturepets.mob.Mob;
import com.kirelcodes.miniaturepets.mob.MobContainer;
import net.citizensnpcs.api.event.NPCTeleportEvent;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MiniatureNpcTrait extends Trait {
    final MiniatureNPCs plugin;
    private Mob mob;
    private LivingEntity npcEntity;

    @Persist("pet_type") private String petType = "";

    public MiniatureNpcTrait() {
        super("miniature_npc");
        plugin = JavaPlugin.getPlugin(MiniatureNPCs.class);
    }

    @EventHandler
    public void onTeleport(NPCTeleportEvent e) {
        mob.teleport(e.getTo());
    }

    @Override
    public void onSpawn() {
        // Create mob
        MobContainer c = new MobContainer(PetLoader.getPet(petType));
        mob = new Mob(npc.getStoredLocation(), c);
        LivingEntity mobNavigator = mob.getNavigator();
        mobNavigator.setAI(false);
        mobNavigator.setGravity(false);
        mobNavigator.setInvulnerable(true);
        plugin.registerMob(mob);

        // Turn invisible
        npcEntity = npc.getEntity() instanceof LivingEntity ? ((LivingEntity) npc.getEntity()) : null;
        if (npcEntity == null) {
            npc.removeTrait(MiniatureNpcTrait.class);
            throw new RuntimeException("MiniatureNPCs trait attached to non-living entity");
        }
        npcEntity.setAI(false);
        npcEntity.setGravity(false);
        npcEntity.setCollidable(false);

        // Integer.MAX_VALUE is enough for 68 years of being continuously loaded
        npcEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true, false));

        // Copy properties from npc into mob
        mob.setCustomName(npcEntity.getName());
    }

    @Override
    public void onDespawn() {
        mob.remove();
    }

    // Called on de-attach
    @Override
    public void onRemove() {
        mob.remove();
        npcEntity.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public void setPetType(String petType) {
        this.petType = petType;
        if (npc.isSpawned()) {
            Location l = npc.getEntity().getLocation();
            npc.despawn();
            npc.spawn(l);
        }
    }
}
