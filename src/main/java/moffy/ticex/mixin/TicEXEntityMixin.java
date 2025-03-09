package moffy.ticex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import moffy.ticex.modules.TicEXRegister;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

@Mixin(AttributeSupplier.Builder.class)
public class TicEXEntityMixin {
    @Inject(
        at = {@At("HEAD")},
        method = {"build"},
        cancellable = true
    )
    public void addExtraAttribute(CallbackInfoReturnable<AttributeSupplier> info){
        ((AttributeSupplier.Builder)((Object)this)).add(TicEXRegister.HEALING_RECEIVED.get()).add(TicEXRegister.DAMAGE_TAKEN.get());
    }
}
