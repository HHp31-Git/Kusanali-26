package com.kusanali.entity.client.model;

import com.kusanali.Kusanali;
import com.kusanali.entity.custom.HypostasisDendroEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HypostasisDendroModel extends GeoModel<HypostasisDendroEntity> {
    @Override
    public Identifier getModelResource(HypostasisDendroEntity object) {
        return new Identifier(Kusanali.MOD_ID, "geo/entity/hypostasis.geo.json");
    }

    @Override
    public Identifier getTextureResource(HypostasisDendroEntity object) {
        return new Identifier(Kusanali.MOD_ID, "textures/entity/hypostasis.png");
    }

    @Override
    public Identifier getAnimationResource(HypostasisDendroEntity animatable) {
        return new Identifier(Kusanali.MOD_ID, "animations/entity/hypostasis.animation.json");
    }
}
