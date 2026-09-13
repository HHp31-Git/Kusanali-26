package com.kusanali;

import com.kusanali.entity.ModEntities;
import com.kusanali.entity.client.*;
import com.kusanali.entity.client.model.DendroSeedModel;
import com.kusanali.entity.client.model.SeedProjectileModel;
import com.kusanali.entity.client.renderer.DendroSeedRenderer;
import com.kusanali.entity.client.renderer.HypostasisDendroRenderer;
import com.kusanali.entity.client.renderer.SeedProjectileRenderer;
import com.kusanali.entity.client.renderer.ShroomboarRenderer;
import com.kusanali.register.ModBlocks;
import com.kusanali.register.ModKeySet;
import com.kusanali.server.ClientVisionOverlay;
import com.kusanali.server.FloatDreamAbilityClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class KusanaliModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //资源包
        Identifier packId = new Identifier(Kusanali.MOD_ID, "kusanali_texture");
        FabricLoader.getInstance().getModContainer(Kusanali.MOD_ID).ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                packId,
                container,
                Text.literal("§a[吉祥草之音]附属材质包"),
                ResourcePackActivationType.NORMAL
        ));

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUMIRU_ROSE, RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTTED_SUMIRU_ROSE, RenderLayer.getCutout());
            BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PADISARAH, RenderLayer.getCutout());

            ClientVisionOverlay.init();
        });

        ModKeySet.register();

        FloatDreamAbilityClient.register();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.AJI_SAPLING, RenderLayer.getCutout());


        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.DENDRO_SEED,
                DendroSeedModel::getTexturedModelData
        );
        EntityRendererRegistry.register(
                ModEntities.DENDRO_SEED,
                DendroSeedRenderer::new
        );

        EntityModelLayerRegistry.registerModelLayer(
                ModModelLayers.SEED_PROJECTILE,
                SeedProjectileModel::getTexturedModelData
        );
        EntityRendererRegistry.register(
                ModEntities.SEED_PROJECTILE,
                SeedProjectileRenderer::new
        );
        EntityRendererRegistry.register(
                ModEntities.SHROOMBOAR,
                ShroomboarRenderer::new
        );

        ClientVisionOverlay.register();

        EntityRendererRegistry.register(ModEntities.HYPOSTASIS_DENDRO, HypostasisDendroRenderer::new);
    }
}
