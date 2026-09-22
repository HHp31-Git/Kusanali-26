package com.kusanali.entity.client.renderer;

import com.kusanali.entity.original.GiantZombieEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.util.Identifier;

public class GiantZombieRenderer extends MobEntityRenderer<GiantZombieEntity, ZombieEntityModel<GiantZombieEntity>> {

    private static final float TARGET_HEIGHT = 3.25f;
    private static final float ORIGINAL_HEIGHT = 2.0f;
    private static final float SCALE = TARGET_HEIGHT / ORIGINAL_HEIGHT;

    public GiantZombieRenderer(EntityRendererFactory.Context context) {
        super(context, new ZombieEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.65f);
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
