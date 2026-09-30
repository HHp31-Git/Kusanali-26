package com.kusanali.event.element_enchant;

import com.kusanali.register.ModEffects;
import com.kusanali.register.ModEnchants;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.TypedActionResult;

import java.util.Map;
import java.util.Set;

import static com.kusanali.Kusanali.MOD_ID;

public class EnchantWeaponSetting {

    private static final Set<String> ELEMENTAL_WEAPON_IDS = Set.of(
            MOD_ID + ":anemo_enchant", MOD_ID + ":geo_enchant", MOD_ID + ":electro_enchant",
            MOD_ID + ":dendro_enchant", MOD_ID + ":hydro_enchant", MOD_ID + ":pyro_enchant", MOD_ID + ":cyro_enchant"
    );

    private static final Set<String> ELEMENTAL_BOW_IDS = Set.of(
            MOD_ID + ":anemo_enchant_bow", MOD_ID + ":geo_enchant_bow", MOD_ID + ":electro_enchant_bow",
            MOD_ID + ":dendro_enchant_bow", MOD_ID + ":hydro_enchant_bow", MOD_ID + ":pyro_enchant_bow", MOD_ID + ":cyro_enchant_bow"
    );

    // 统一管理弓箭附魔与对应元素效果的映射
    private static final Map<Enchantment, StatusEffectInstance> BOW_ENCHANT_EFFECTS = Map.of(
            ModEnchants.ANEMO_ENCHANT_BOW, new StatusEffectInstance(ModEffects.ANEMO, 5, 0, false, false),
            ModEnchants.GEO_ENCHANT_BOW, new StatusEffectInstance(ModEffects.GEO, 5, 0, false, false),
            ModEnchants.ELECTRO_ENCHANT_BOW, new StatusEffectInstance(ModEffects.ELECTRO, 125, 0, true, true),
            ModEnchants.DENDRO_ENCHANT_BOW, new StatusEffectInstance(ModEffects.DENDRO, 125, 0, true, true),
            ModEnchants.HYDRO_ENCHANT_BOW, new StatusEffectInstance(ModEffects.HYDRO, 125, 0, true, true),
            ModEnchants.CYRO_ENCHANT_BOW, new StatusEffectInstance(ModEffects.CYRO, 125, 0, true, true)
    );

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));

            ItemStack stack = player.getStackInHand(hand);
            enforceSingleElement(stack, "Enchantments", ELEMENTAL_WEAPON_IDS);
            enforceSingleElement(stack, "Enchantments_Bow", ELEMENTAL_BOW_IDS);

            // 合并原 ElementBowEnchantEvent 逻辑：拉弓时附着元素
            applyBowElementEffect(stack, player);

            return TypedActionResult.pass(stack);
        });
    }

    /**
     * 弓箭元素附魔效果附着（替代原 ALLOW_DAMAGE 监听）
     */
    private static void applyBowElementEffect(ItemStack stack, PlayerEntity player) {
        if (!stack.hasEnchantments()) return;
        BOW_ENCHANT_EFFECTS.forEach((enchant, effect) -> {
            if (EnchantmentHelper.getLevel(enchant, stack) > 0) {
                player.addStatusEffect(new StatusEffectInstance(effect));
            }
        });
    }

    /**
     * 通用逻辑：确保某个 NBT key 下，同类元素附魔只保留第一个
     */
    private static void enforceSingleElement(ItemStack stack, String nbtKey, Set<String> elementalIds) {
        if (stack.isEmpty() || !stack.hasEnchantments()) return;

        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(nbtKey, 9)) return;

        NbtList oldList = nbt.getList(nbtKey, 10);
        if (oldList.size() <= 1) return;

        NbtList newList = new NbtList();
        String keptId = null;
        boolean changed = false;

        for (int i = 0; i < oldList.size(); i++) {
            NbtCompound enchant = oldList.getCompound(i);
            String id = enchant.getString("id");

            if (elementalIds.contains(id)) {
                if (keptId == null) {
                    keptId = id;
                    newList.add(enchant.copy());
                } else if (id.equals(keptId)) {
                    newList.add(enchant.copy());
                } else {
                    changed = true;
                }
            } else {
                newList.add(enchant.copy());
            }
        }

        if (changed) {
            if (newList.isEmpty()) {
                stack.removeSubNbt(nbtKey);
            } else {
                nbt.put(nbtKey, newList);
            }
        }
    }
}
