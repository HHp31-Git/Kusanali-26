package com.kusanali.event.special.block;

import net.minecraft.block.Block;

public class AlterBrick extends Block {
    public AlterBrick(Settings settings) {
        super(settings);
    }
    @Override
    public float getHardness() {
        return -1.0f;
    }
}
