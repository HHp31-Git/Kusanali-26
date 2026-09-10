package com.kusanali.entity.client.renderer;

import com.kusanali.entity.original.ShroomboarEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.util.Identifier;

public class ShroomboarRenderer extends MobEntityRenderer<ShroomboarEntity, PigEntityModel<ShroomboarEntity>> {

    public ShroomboarRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PigEntityModel<>(ctx.getPart(EntityModelLayers.PIG)), 0.5f);
    }

    @Override
    public Identifier getTexture(ShroomboarEntity entity) {
        return new Identifier("kusanali", "textures/entity/shroomboar/shroomboar.png");
    }
}
