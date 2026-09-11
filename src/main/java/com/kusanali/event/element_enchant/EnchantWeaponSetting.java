package com.kusanali.event.element_enchant;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.TypedActionResult;

import java.util.Set;

import static com.kusanali.Kusanali.MOD_ID;

public class EnchantWeaponSetting {

    private static final Set<String> ELEMENTAL_WEAPON_IDS = Set.of(
            MOD_ID + ":anemo_enchant",
            MOD_ID + ":geo_enchant",
            MOD_ID + ":electro_enchant",
            MOD_ID + ":dendro_enchant",
            MOD_ID + ":hydro_enchant",
            MOD_ID + ":pyro_enchant",
            MOD_ID + ":cyro_enchant"
    );

    private static final Set<String> ELEMENTAL_BOW_IDS = Set.of(
            MOD_ID + ":anemo_enchant_bow",
            MOD_ID + ":geo_enchant_bow",
            MOD_ID + ":electro_enchant_bow",
            MOD_ID + ":dendro_enchant_bow",
            MOD_ID + ":hydro_enchant_bow",
            MOD_ID + ":pyro_enchant_bow",
            MOD_ID + ":cyro_enchant_bow"
    );

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));

            ItemStack stack = player.getStackInHand(hand);
            enforceSingleElement(stack, "Enchantments", ELEMENTAL_WEAPON_IDS);
            enforceSingleElement(stack, "Enchantments_Bow", ELEMENTAL_BOW_IDS);
            return TypedActionResult.pass(stack);
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
                    // 第一个元素附魔：保留
                    keptId = id;
                    newList.add(enchant.copy());
                } else if (id.equals(keptId)) {
                    // 同类重复：也保留（一般不会有）
                    newList.add(enchant.copy());
                } else {
                    // 不同类型的元素附魔：移除
                    changed = true;
                }
            } else {
                // 非元素附魔：始终保留
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