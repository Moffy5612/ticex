package moffy.ticex.event;

import moffy.ticex.modules.TicEXRegistry;
import moffy.ticex.modules.avaritia.TicEXAvaritiaUtils;
import moffy.ticex.utils.TicEXUtils;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.ModList;

public class TicEXEvent {

    public static void onEntityHurt(LivingHurtEvent event){
        if(ModList.get().isLoaded("avaritia")){
            TicEXAvaritiaUtils.generatePile(event.getEntity(), event.getEntity().level(), event.getEntity().position());
        }

        //attribute
        float damage = event.getAmount();
        if (damage <= 0F || TicEXUtils.isPureDamage(event.getSource(), damage)) {
            return;
        }
        AttributeInstance attributeInstance = event.getEntity().getAttribute(TicEXRegistry.DAMAGE_TAKEN.get());
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
        AttributeInstance attributeInstance = event.getEntity().getAttribute(TicEXRegistry.HEALING_RECEIVED.get());
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

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if(!player.isCreative()){
            if (TicEXUtils.canPlayerFly(player) && !player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            } else if(!TicEXUtils.canPlayerFly(player) && player.getAbilities().mayfly){
                player.getAbilities().mayfly = false;
                player.onUpdateAbilities();
            }
        }
    }

}
