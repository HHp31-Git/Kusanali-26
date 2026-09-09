package com.kusanali.datagenerator;

import com.kusanali.Kusanali;
import com.kusanali.register.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class EntityLootTable extends SimpleFabricLootTableProvider {
    public EntityLootTable(FabricDataOutput output) {
        super(output, LootContextTypes.ENTITY);
    }

    public static final Identifier HYPOSTASIS_DENDRO =
            new Identifier(Kusanali.MOD_ID, "entities/hypostasis");


    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> exporter) {
        exporter.accept(HYPOSTASIS_DENDRO, LootTable.builder()
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(Items.DIAMOND)
                                .conditionally(RandomChanceLootCondition.builder(0.1f))))
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(ModItems.EMERALD_CHUCK)
                                .weight(6)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1))))
                        .with(ItemEntry.builder(ModItems.EMERALD_CHUCK)
                                .weight(3)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2))))
                        .with(ItemEntry.builder(ModItems.EMERALD_CHUCK)
                                .weight(1)
                                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(3))))
                )
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(Items.IRON_INGOT)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 4)))))
                .pool(LootPool.builder()
                        .with(ItemEntry.builder(Items.COPPER_INGOT)
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(3, 5)))))
        );
    }
}
