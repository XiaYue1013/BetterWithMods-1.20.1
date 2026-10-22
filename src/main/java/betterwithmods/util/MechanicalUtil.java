package betterwithmods.util;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IAxle;
import betterwithmods.api.tile.IMechanicalPower;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MechanicalUtil {
    public static boolean isRedstonePowered(Level level, BlockPos pos) {
        return level.hasNeighborSignal(pos);
    }

    public static IMechanicalPower getMechanicalPower(Level level, BlockPos pos, Direction facing) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            return be.getCapability(CapabilityMechanical.MECHANICAL_POWER, facing).orElse(null);
        }
        return null;
    }

    public static boolean canInput(Level level, BlockPos pos, Direction facing) {
        IMechanicalPower power = getMechanicalPower(level, pos, facing);
        return power != null && power.getMaximumInput(facing) > 0;
    }

    public static boolean isAxle(BlockGetter level, BlockPos pos, Direction facing) {
        IAxle axle = getAxle(level, pos, facing);
        return axle != null && axle.getAxis() == facing.getAxis();
    }

    public static IAxle getAxle(BlockGetter level, BlockPos pos, Direction facing) {
        if (level instanceof Level lvl) {
            BlockEntity be = lvl.getBlockEntity(pos);
            if (be != null) {
                return be.getCapability(CapabilityMechanical.AXLE, facing).orElse(null);
            }
        }
        return null;
    }

    public static int getPowerOutput(Level level, BlockPos pos, Direction facing) {
        IMechanicalPower power = getMechanicalPower(level, pos, facing);
        if (power != null) {
            return power.getMechanicalOutput(facing);
        }
        return 0;
    }
}
