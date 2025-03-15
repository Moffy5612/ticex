package moffy.ticex.modules.avaritia;

import committee.nova.mods.avaritia.init.registry.ModDamageTypes;
import committee.nova.mods.avaritia.init.registry.ModItems;
import moffy.ticex.TicEX;
import moffy.ticex.modifier.ModifierCelestial;
import moffy.ticex.modifier.ModifierCondensing;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;

public class TicEXAvaritiaUtils {
    public static boolean isInfinityDamage(DamageSource source){
        return source.is(ModDamageTypes.INFINITY);
    }

    public static boolean hasPlayerCelestial(Player player){
        return ArmorLevelModule.getLevel(player, ModifierCelestial.CELESTIAL_KEY) > 0;
    }

    public static void generatePile(LivingEntity entity, Level level, Vec3 positions){
        if(ArmorLevelModule.getLevel(entity, ModifierCondensing.CONDENSING) > 0){
            RandomSource random = level.getRandom();
            if(random.nextFloat() < TicEX.CONDENSING_DROP_PROBABILITY.get()){
                ItemEntity pile = new ItemEntity(level, positions.x, positions.y, positions.z, new ItemStack(ModItems.neutron_pile.get()));
                level.addFreshEntity(pile);
            }
        }
    }
}
