package com.kusanali.event.special.item;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEffects;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;

public class TribbleEvent {
    //蕴种印-事件触发
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (entity.hasStatusEffect(ModEffects.TRIBBLE)) {
                // 构建草元素伤害源并造成伤害
                DamageSource dendroSource = ModDamageTypes.dendro(entity.getWorld());
                entity.damage(dendroSource, amount + 2.0f);
                return false;  // 阻止原始伤害继续处理
            }
            return true;
        });
    }
}
