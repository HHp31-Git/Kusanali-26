package com.kusanali.entity.client.renderer;

import com.kusanali.entity.client.model.HypostasisDendroModel;
import com.kusanali.entity.custom.HypostasisDendroEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HypostasisDendroRenderer extends GeoEntityRenderer<HypostasisDendroEntity> {
    public HypostasisDendroRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HypostasisDendroModel());
        this.shadowRadius = 0.5f;
    }
}
