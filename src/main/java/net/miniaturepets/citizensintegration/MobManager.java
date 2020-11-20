package net.miniaturepets.citizensintegration;

import com.kirelcodes.miniaturepets.mob.Mob;

import java.util.HashSet;
import java.util.Set;

/* package-private */ final class MobManager {

    private final Set<Mob> mobs = new HashSet<>();

    /* package-private */ void registerMob(Mob mob) {
        mobs.add(mob);

    }

    /* package-private */ void removeAll() {
        mobs.forEach(Mob::remove);
        mobs.clear();
    }

}
