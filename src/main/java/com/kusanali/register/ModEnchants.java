package com.kusanali.register;

import com.kusanali.enchantments.bow.*;
import com.kusanali.enchantments.weapon.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;

import static com.kusanali.Kusanali.MOD_ID;

public class ModEnchants {
    //武器类
    public static Enchantment ANEMO_ENCHANT;
    public static Enchantment GEO_ENCHANT;
    public static Enchantment ELECTRO_ENCHANT;
    public static Enchantment DENDRO_ENCHANT;
    public static Enchantment HYDRO_ENCHANT;
    public static Enchantment CYRO_ENCHANT;

    //弓类
    public static Enchantment ANEMO_ENCHANT_BOW;
    public static Enchantment GEO_ENCHANT_BOW;
    public static Enchantment ELECTRO_ENCHANT_BOW;
    public static Enchantment DENDRO_ENCHANT_BOW;
    public static Enchantment HYDRO_ENCHANT_BOW;
    public static Enchantment CYRO_ENCHANT_BOW;

    public static void register() {
        ANEMO_ENCHANT = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "anemo_enchant"), new Anemo());
        GEO_ENCHANT = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "geo_enchant"), new Geo());
        ELECTRO_ENCHANT = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "electro_enchant"), new Electro());
        DENDRO_ENCHANT = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "dendro_enchant"), new Dendro());
        HYDRO_ENCHANT = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "hydro_enchant"), new Hydro());
        CYRO_ENCHANT = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "cyro_enchant"), new Cyro());

        ANEMO_ENCHANT_BOW = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "anemo_enchant_bow"), new AnemoBow());
        GEO_ENCHANT_BOW = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "geo_enchant_bow"), new GeoBow());
        ELECTRO_ENCHANT_BOW = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "electro_enchant_bow"), new ElectroBow());
        DENDRO_ENCHANT_BOW = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "dendro_enchant_bow"), new DendroBow());
        HYDRO_ENCHANT_BOW = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "hydro_enchant_bow"), new HydroBow());
        CYRO_ENCHANT_BOW = Registry.register(Registries.ENCHANTMENT,
                new Identifier(MOD_ID, "cyro_enchant_bow"), new CyroBow());
    }
}
