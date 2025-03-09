package moffy.ticex.event;

import moffy.ticex.modules.TicEXRegister;
import moffy.ticex.utils.TicEXUtils;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class TicEXEvent {

    public static void onEntityHurt(LivingHurtEvent event){
        float damage = event.getAmount();
        if (damage <= 0F || TicEXUtils.isPureDamage(event.getSource(), damage)) {
            return;
        }
        AttributeInstance attributeInstance = event.getEntity().getAttribute(TicEXRegister.DAMAGE_TAKEN.get());
        if(attributeInstance != null){
            double multiplier = attributeInstance.getValue();
            if (multiplier != 1D) {
                float newAmount = Math.max(damage * (float)multiplier, 0F);
                event.setAmount(newAmount);
                if(newAmount < 0.0001){
                    event.setCanceled(true);
                }
            }
        }
    }

    public static void onEntityHeal(LivingHealEvent event){
        float amount = event.getAmount();
        if (amount <= 0F) {
            return;
        }
        AttributeInstance attributeInstance = event.getEntity().getAttribute(TicEXRegister.HEALING_RECEIVED.get());
        if(attributeInstance != null){
            double multiplier = attributeInstance.getValue();
            if (multiplier != 1D) {
                float newAmount = Math.max(amount * (float)multiplier, 0F);
                event.setAmount(newAmount);
                if(newAmount < 0.0001){
                    event.setCanceled(true);
                }
            }
        }
    }
}
