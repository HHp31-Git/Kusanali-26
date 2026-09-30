package com.kusanali;

import com.kusanali.define.event.ElementAdhering;
import com.kusanali.entity.ModEntities;
import com.kusanali.entity.custom.DendroSeedEntity;
import com.kusanali.entity.original.GiantZombieEntity;
import com.kusanali.entity.original.ShroomboarEntity;
import com.kusanali.entity.tracker.SeedEffectTracker;
import com.kusanali.event.abyss.AbyssDamageStatus;
import com.kusanali.event.damage.ModDamageImmunity;
import com.kusanali.event.element_adhering.*;
import com.kusanali.event.element_enchant.*;
import com.kusanali.event.entity.ShroomboarSpawn;
import com.kusanali.event.item_getter.EmeraldGetter;
import com.kusanali.event.item_getter.JungleAddition;
import com.kusanali.event.special.item.AjiLeavesDrop;
import com.kusanali.event.special.item.CorollaEvent;
import com.kusanali.event.special.item.TribbleEvent;
import com.kusanali.event.special.item.TribbleReactionEvent;
import com.kusanali.register.*;
import com.kusanali.server.*;
import com.kusanali.specialitem.combats.FloatDream;
import com.kusanali.world.feature.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;
import net.minecraft.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Kusanali implements ModInitializer {

	public static final String MOD_ID = "kusanali";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("For the Lesser Lord Kusanali");
        //注册类
        ModEffects.register();
        ModDamageTypes.register();
        ModItems.initialize();
        ModBlocks.initialize();
        ModItemGroup.initialize();
        ModSounds.register();
        ModPaintings.init();
        ModLootTable.register();
        ModTrades.register();
        ModEnchants.register();
        ModEntities.register();
        ModGameRules.initialize();
        //特殊物品行为类
        FloatDream.registerAttackEvent();
        CorollaEvent.register();
        TribbleEvent.register();
        TribbleReactionEvent.register();
        //元素附着类
        List<ElementAdhering> adheringEvents = List.of(
                new HydroEvent(),
                new CyroSnowEvent(),
                new ElectroEvent(),
                new BerryBushDendroEvent(),
                new PyroEvent(),
                new SlimeRandomEvent(),
                new SnowBallCyroEvent(),
                new DripstoneGeoEvent()
        );
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            for (var entity : world.iterateEntities()) {
                if (!(entity instanceof LivingEntity living)) continue;
                for (var event : adheringEvents) {
                    event.tryAdhere(living, world);
                }
            }
        });
        ElementEntityAttackEvent.register();
        SlimeRandomEvent.register();
        //元素反应类
        ModReactionRegistry.init();
        //附魔设置类
        EnchantWeaponSetting.register();
        ElementConversion.registerAll();
        //世界生成类
        ModWorldGeneration.register();
        AjiLeavesDrop.register();
        //生物行为类
        ShroomboarSpawn.register();
        FabricDefaultAttributeRegistry.register(
                ModEntities.SHROOMBOAR,
                ShroomboarEntity.createShroomboarAttributes()
        );
        FabricDefaultAttributeRegistry.register(
                ModEntities.DENDRO_SEED,
                DendroSeedEntity.createAttributes()
        );
        FabricDefaultAttributeRegistry.register(
                ModEntities.GIANT_ZOMBIE,
                GiantZombieEntity.createAttributes()
        );
        ServerTickEvents.END_WORLD_TICK.register(SeedEffectTracker::tickAll);
        //伤害调用类
        ModDamageImmunity.registerAll();
        //深渊行为类
        AbyssDamageStatus.register();
        //物品类
        EmeraldGetter.register();
        JungleAddition.register();
        //HUD类
        FloatDreamHander.register();
        ClientHander.register();
        ClientOverlayRenderer.register();
        ClientMessageOverlay.register();
        ClientVisionOverlay.register();
        //树木去皮注册
        StrippableBlockRegistry.register(ModBlocks.AJI_LOG, ModBlocks.STRIPPED_AJI_LOG);
        StrippableBlockRegistry.register(ModBlocks.AJI_WOOD, ModBlocks.STRIPPED_AJI_WOOD);
        //可燃烧方块注册
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_PLANKS, 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_LEAVES, 30, 60);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_SAPLING, 60, 100);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_AJI_LOG, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.AJI_WOOD, 5, 5);
        FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_AJI_WOOD, 5, 5);

        LOGGER.info("All settings done");
    }
}