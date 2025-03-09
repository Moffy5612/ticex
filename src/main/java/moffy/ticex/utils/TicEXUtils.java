package moffy.ticex.utils;

import moffy.ticex.modules.avaritia.TicEXAvaritiaUtils;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.fml.ModList;

public class TicEXUtils {
    public static boolean isPureDamage(DamageSource source, float damage){
        boolean isInfinityDamage = false;

        if(ModList.get().isLoaded("avaritia")){
            isInfinityDamage = TicEXAvaritiaUtils.isInfinityDamage(source);
        }

        return source.is(DamageTypes.FELL_OUT_OF_WORLD) || isInfinityDamage || (damage == Float.MAX_VALUE && source.is(DamageTypeTags.BYPASSES_ARMOR) && source.is(DamageTypeTags.BYPASSES_INVULNERABILITY));
    }
}
