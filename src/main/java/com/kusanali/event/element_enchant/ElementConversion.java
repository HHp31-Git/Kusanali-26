package com.kusanali.event.element_enchant;

import com.kusanali.register.ModDamageTypes;
import com.kusanali.register.ModEnchants;
import net.minecraft.enchantment.Enchantments;

public class ElementConversion {
    public static void registerAll() {
        DamageChangeEnchants.register(
                ModEnchants.ANEMO_ENCHANT_BOW,
                ModDamageTypes.ELEMENT_ANEMO,
                0.5f
        );
        DamageChangeEnchants.register(
                ModEnchants.ANEMO_ENCHANT,
                ModDamageTypes.ELEMENT_ANEMO,
                0.5f
        );

        DamageChangeEnchants.register(
                Enchantments.FIRE_ASPECT,
                ModDamageTypes.ELEMENT_PYRO,
                0.5f
        );
        DamageChangeEnchants.register(
                Enchantments.FLAME,
                ModDamageTypes.ELEMENT_PYRO,
                0.5f
        );

        DamageChangeEnchants.register(
                ModEnchants.CYRO_ENCHANT_BOW,
                ModDamageTypes.ELEMENT_CYRO,
                0.5f
        );
        DamageChangeEnchants.register(
                ModEnchants.CYRO_ENCHANT,
                ModDamageTypes.ELEMENT_CYRO,
                0.5f
        );

        DamageChangeEnchants.register(
                ModEnchants.ELECTRO_ENCHANT_BOW,
                ModDamageTypes.ELEMENT_ELECTRO,
                0.5f
        );
        DamageChangeEnchants.register(
                ModEnchants.ELECTRO_ENCHANT,
                ModDamageTypes.ELEMENT_ELECTRO,
                0.5f
        );

        DamageChangeEnchants.register(
                ModEnchants.GEO_ENCHANT_BOW,
                ModDamageTypes.ELEMENT_GEO,
                0.5f
        );
        DamageChangeEnchants.register(
                ModEnchants.GEO_ENCHANT,
                ModDamageTypes.ELEMENT_GEO,
                0.5f
        );

        DamageChangeEnchants.register(
                ModEnchants.HYDRO_ENCHANT_BOW,
                ModDamageTypes.ELEMENT_HYDRO,
                0.5f
        );
        DamageChangeEnchants.register(
                ModEnchants.HYDRO_ENCHANT,
                ModDamageTypes.ELEMENT_HYDRO,
                0.5f
        );
    }
}
