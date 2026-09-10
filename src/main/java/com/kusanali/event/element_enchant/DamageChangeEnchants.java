package com.kusanali.event.element_enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class DamageChangeEnchants {
    private static final Map<Enchantment, Pair<RegistryKey<DamageType>, Float>> CONVERSIONS = new HashMap<>();

    public static void register(Enchantment enchantment, RegistryKey<DamageType> damageTypeKey, float conversionRatio) {
        CONVERSIONS.put(enchantment, new Pair<>(damageTypeKey, conversionRatio));
    }

    public static Pair<RegistryKey<DamageType>, Float> getConversion(Enchantment enchantment) {
        return CONVERSIONS.get(enchantment);
    }

    public static boolean hasConversion(Enchantment enchantment) {
        return CONVERSIONS.containsKey(enchantment);
    }

    public static Map<Enchantment, Pair<RegistryKey<DamageType>, Float>> getAllConversions() {
        return CONVERSIONS;
    }
}
