package betterwithmods.common.blocks.mechanical;

import net.minecraft.world.level.block.Block;

public class BrokenGearboxBlock extends Block {
    private final boolean isSteel;

    public BrokenGearboxBlock(Properties properties, boolean isSteel) {
        super(properties);
        this.isSteel = isSteel;
    }

    public boolean isSteel() {
        return isSteel;
    }
}
