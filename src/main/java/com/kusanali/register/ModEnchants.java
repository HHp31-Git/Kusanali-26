package com.kusanali.register;

import com.kusanali.enchantments.bow.*;
import com.kusanali.enchantments.weapon.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.*;
import net.minecraft.util.Identifier;

import static com.kusanali.Kusanali.MOD_ID;

public class ModEnchants {
    //武器类
    public static final Enchantment ANEMO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "anemo_enchant"),
            new Anemo()
    );
    public static final Enchantment GEO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "geo_enchant"),
            new Geo()
    );
    public static final Enchantment ELECTRO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "electro_enchant"),
            new Electro()
    );
    public static final Enchantment DENDRO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "dendro_enchant"),
            new Dendro()
    );
    public static final Enchantment HYDRO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "hydro_enchant"),
            new Hydro()
    );
    public static final Enchantment CYRO_ENCHANT = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "cyro_enchant"),
            new Cyro()
    );
    //弓类
    public static final Enchantment ANEMO_ENCHANT_BOW = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "anemo_enchant_bow"),
            new AnemoBow()
    );
    public static final Enchantment GEO_ENCHANT_BOW = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "geo_enchant_bow"),
            new GeoBow()
    );
    public static final Enchantment ELECTRO_ENCHANT_BOW = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "electro_enchant_bow"),
            new ElectroBow()
    );
    public static final Enchantment DENDRO_ENCHANT_BOW = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "dendro_enchant_bow"),
            new DendroBow()
    );
    public static final Enchantment HYDRO_ENCHANT_BOW = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "hydro_enchant_bow"),
            new HydroBow()
    );
    public static final Enchantment CYRO_ENCHANT_BOW = Registry.register(
            Registries.ENCHANTMENT,
            new Identifier(MOD_ID, "cyro_enchant_bow"),
            new CyroBow()
    );
    public static void register() {}
}
