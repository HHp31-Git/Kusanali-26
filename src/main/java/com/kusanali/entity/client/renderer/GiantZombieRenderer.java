package com.kusanali.entity.client.renderer;

import com.kusanali.entity.original.GiantZombieEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.util.Identifier;

public class GiantZombieRenderer extends MobEntityRenderer<GiantZombieEntity, ZombieEntityModel<GiantZombieEntity>> {

    private static final float SCALE = 3.7f / 2.0f;

    public GiantZombieRenderer(EntityRendererFactory.Context context) {
        super(context, new ZombieEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.8f * SCALE);
    }

    @Override
    public Identifier getTexture(GiantZombieEntity entity) {
        return new Identifier("minecraft", "textures/entity/zombie/zombie.png");
    }

    @Override
    protected void scale(GiantZombieEntity entity,
                         net.minecraft.client.util.math.MatrixStack matrices,
                         float tickDelta) {
        matrices.scale(SCALE, SCALE, SCALE);
    }
}
