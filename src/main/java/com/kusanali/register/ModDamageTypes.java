package com.kusanali.register;

import com.kusanali.Kusanali;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> ELEMENT_ANEMO =
            //风元素伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "element_anemo"));
    public static final RegistryKey<DamageType> ELEMENT_HYDRO =
            //水元素伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "element_hydro"));
    public static final RegistryKey<DamageType> ELEMENT_ELECTRO =
            //雷元素伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "element_electro"));
    public static final RegistryKey<DamageType> ELEMENT_GEO =
            //岩元素伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "element_geo"));
    public static final RegistryKey<DamageType> ELEMENT_CYRO =
            //冰元素伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "element_cyro"));
    public static final RegistryKey<DamageType> ELEMENT_DENDRO =
            //草元素伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "element_dendro"));
    public static final RegistryKey<DamageType> ELEMENT_PYRO =
            //火元素伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "element_pyro"));

    public static final RegistryKey<DamageType> ELEMENT =
            //元素伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "element"));

    public static final RegistryKey<DamageType> REACTION_TYPE_1 =
            //增幅反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_1"));

    public static final RegistryKey<DamageType> REACTION_TYPE_2 =
            //剧变反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_2"));

    public static final RegistryKey<DamageType> REACTION_TYPE_3 =
            //结晶反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_3"));

    public static final RegistryKey<DamageType> REACTION_TYPE_4 =
            //激化反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_4"));

    public static final RegistryKey<DamageType> REACTION_TYPE_5 =
            //月反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_5"));

    public static final RegistryKey<DamageType> REACTION_TYPE_6 =
            //星反应伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "reaction_type_6"));

    public static final RegistryKey<DamageType> ABYSS =
            //深渊伤害
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE,
                    new Identifier(Kusanali.MOD_ID, "abyss"));

    public static DamageSource anemo(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ELEMENT_ANEMO));
    }
    public static DamageSource hydro(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ELEMENT_HYDRO));
    }

    public static DamageSource electro(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ELEMENT_ELECTRO));
    }
    public static DamageSource geo(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ELEMENT_GEO));
    }
    public static DamageSource cyro(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ELEMENT_CYRO));
    }
    public static DamageSource dendro(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ELEMENT_DENDRO));
    }
    public static DamageSource pyro(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ELEMENT_PYRO));
    }

    public static DamageSource element(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ELEMENT));
    }
    public static DamageSource reaction_type_1(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_1));
    }
    public static DamageSource reaction_type_2(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_2));
    }
    public static DamageSource reaction_type_3(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_3));
    }
    public static DamageSource reaction_type_4(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_4));
    }
    public static DamageSource reaction_type_5(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_5));
    }
    public static DamageSource reaction_type_6(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(REACTION_TYPE_6));
    }
    public static DamageSource abyss(World world) {
        return new DamageSource(world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(ABYSS));
    }

    public static void register() {}
}
