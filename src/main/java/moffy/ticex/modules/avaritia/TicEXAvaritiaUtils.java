package moffy.ticex.modules.avaritia;

import committee.nova.mods.avaritia.init.registry.ModDamageTypes;
import net.minecraft.world.damagesource.DamageSource;

public class TicEXAvaritiaUtils {
    public static boolean isInfinityDamage(DamageSource source){
        return source.is(ModDamageTypes.INFINITY);
    }
}
