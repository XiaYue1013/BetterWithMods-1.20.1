package betterwithmods.api.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface IMechanicalPower {
    int getMechanicalOutput(Direction facing);
    int getMechanicalInput(Direction facing);
    int getMaximumInput(Direction facing);
    int getMinimumInput(Direction facing);
    Block getBlock();
    Level getBlockWorld();
    BlockPos getBlockPos();

    default int calculateInput() {
        int maxFound = 0;
        boolean overpowered = false;
        for (Direction direction : Direction.values()) {
            int power = getMechanicalInput(direction);
            if (power > maxFound) {
                maxFound = power;
            }
            if (power > getMaximumInput(direction)) {
                overpowered = true;
            }
        }
        if (overpowered && overpowerChance()) {
            overpower();
            return 0;
        }
        return maxFound;
    }

    default boolean overpowerChance() {
        Level world = getBlockWorld();
        return world != null && world.random.nextInt(10) == 0;
    }

    default void overpower() {
    }
}
