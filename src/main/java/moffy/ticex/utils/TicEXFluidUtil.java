package moffy.ticex.utils;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import slimeknights.tconstruct.TConstruct;

public class TicEXFluidUtil {
    public static FluidType.Properties hot(String name) {
        return FluidType.Properties.create().density(2000).viscosity(10000).temperature(1000)
                                .descriptionId(TConstruct.makeDescriptionId("fluid", name))
                                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                                // from forge lava type
                                .motionScale(0.0023333333333333335D)
                                .canSwim(false).canDrown(false)
                                .pathType(BlockPathTypes.LAVA).adjacentPathType(null);
    }

    public static FluidType.Properties cool() {
        return FluidType.Properties.create()
                                   .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                                   .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                                   .motionScale(0.0023333333333333335D)
                                   .canExtinguish(true);
    }

    public static FluidType.Properties cool(String name) {
        return cool().descriptionId(TConstruct.makeDescriptionId("fluid", name))
                     .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                     .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY);
    }

    public static FluidType.Properties slime(String name) {
        return cool(name).density(1600).viscosity(1600);
    }
}
