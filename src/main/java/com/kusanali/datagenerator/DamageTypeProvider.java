package com.kusanali.datagenerator;

import com.kusanali.register.ModDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class DamageTypeProvider extends FabricDynamicRegistryProvider {
    public DamageTypeProvider(FabricDataOutput output,
                              CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup, Entries entries) {
        for (int i = 1; i <= 6; i++) {
            Identifier id = new Identifier("kusanali", "reaction_type_" + i);
            RegistryKey<DamageType> key = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id);
            entries.add(key, new DamageType(
                    "reaction",
                    0.0f
            ));
        }

        entries.add(ModDamageTypes.ELEMENT_ANEMO, new DamageType(
                "kusanali.element_anemo",
                0.0f
        ));
        entries.add(ModDamageTypes.ELEMENT_HYDRO, new DamageType(
                "kusanali.element_hydro",
                0.0f
        ));
        entries.add(ModDamageTypes.ELEMENT_ELECTRO, new DamageType(
                "kusanali.element_electro",
                0.0f
        ));
        entries.add(ModDamageTypes.ELEMENT_GEO, new DamageType(
                "kusanali.element_geo",
                0.0f
        ));
        entries.add(ModDamageTypes.ELEMENT_DENDRO, new DamageType(
                "kusanali.element_dendro",
                0.0f
        ));
        entries.add(ModDamageTypes.ELEMENT_CYRO, new DamageType(
                "kusanali.element_cyro",
                0.0f
        ));
        entries.add(ModDamageTypes.ELEMENT_PYRO, new DamageType(
                "kusanali.element_pyro",
                0.0f
        ));
        entries.add(ModDamageTypes.ABYSS, new DamageType(
                "kusanali.abyss",
                0.6f
        ));
    }

    @Override
    public String getName() {
        return "Damage Types";
    }
}
