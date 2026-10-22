package betterwithmods.common.blockentity;

import betterwithmods.common.blocks.mechanical.AxleGeneratorBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class WaterwheelBlockEntity extends AxleGeneratorBlockEntity {

    public WaterwheelBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.WATERWHEEL.get(), pos, state);
    }

    @Override
    public int getRadius() {
        return 2;
    }

    private boolean isWater(BlockPos checkPos) {
        if (level == null) return false;
        FluidState fluid = level.getFluidState(checkPos);
        return fluid.is(Fluids.WATER) || fluid.is(Fluids.FLOWING_WATER);
    }

    @Override
    public void verifyIntegrity() {
        if (level == null) return;
        Direction.Axis axis = getAxis();
        if (axis == Direction.Axis.Y) {
            isValid = false;
            return;
        }

        boolean isAir = true;
        boolean hasWater = false;

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                int xOffset = (axis == Direction.Axis.Z ? i : 0);
                int zOffset = (axis == Direction.Axis.X ? i : 0);
                BlockPos offset = worldPosition.offset(xOffset, j, zOffset);

                if (j == -2) {
                    if (isWater(offset)) {
                        hasWater = true;
                    }
                }

                if (i == 0 && j == 0) {
                    continue;
                } else if (j > -2) {
                    BlockState state = level.getBlockState(offset);
                    boolean replaceable = state.canBeReplaced() || state.isAir();
                    if (i == -2 || i == 2) {
                        replaceable = replaceable || isWater(offset);
                    }
                    if (!replaceable) {
                        isAir = false;
                        break;
                    }
                }
            }
            if (!isAir) break;
        }

        if (!hasWater) {
            hasWater = sidesHaveWater();
        }

        isValid = isAir && hasWater;
    }

    private boolean sidesHaveWater() {
        if (level == null) return false;
        Direction.Axis axis = getAxis();
        int leftWater = 0;
        int rightWater = 0;
        boolean bottomClear = true;

        for (int i = -2; i <= 2; i++) {
            int xLeft = axis == Direction.Axis.Z ? -2 : 0;
            int xRight = axis == Direction.Axis.Z ? 2 : 0;
            int zLeft = axis == Direction.Axis.X ? -2 : 0;
            int zRight = axis == Direction.Axis.X ? 2 : 0;

            BlockPos leftPos = worldPosition.offset(xLeft, i, zLeft);
            BlockPos rightPos = worldPosition.offset(xRight, i, zRight);

            if (isWater(leftPos)) leftWater++;
            if (isWater(rightPos)) rightWater++;

            int xP = axis == Direction.Axis.Z ? i : 0;
            int zP = axis == Direction.Axis.X ? i : 0;
            BlockPos bPos = worldPosition.offset(xP, -2, zP);
            if (!level.getBlockState(bPos).isAir() && !isWater(bPos)) {
                bottomClear = false;
                break;
            }
        }
        return bottomClear && (leftWater != rightWater) && (leftWater > 0 || rightWater > 0);
    }

    @Override
    public void calculatePower() {
        if (level == null) return;
        int newPower = 0;

        if (isValid()) {
            Direction.Axis axis = getAxis();
            Vec3 flow = Vec3.ZERO;

            for (int i = -1; i <= 1; i++) {
                int xP = axis == Direction.Axis.Z ? i : 0;
                int zP = axis == Direction.Axis.X ? i : 0;
                BlockPos lowPos = worldPosition.offset(xP, -2, zP);
                FluidState fluid = level.getFluidState(lowPos);
                if (!fluid.isEmpty()) {
                    Vec3 vec = fluid.getFlow(level, lowPos);
                    flow = flow.add(vec);
                }
            }

            int leftWater = 0;
            int rightWater = 0;
            for (int i = -1; i <= 2; i++) {
                int xLeft = axis == Direction.Axis.Z ? -2 : 0;
                int xRight = axis == Direction.Axis.Z ? 2 : 0;
                int zLeft = axis == Direction.Axis.X ? -2 : 0;
                int zRight = axis == Direction.Axis.X ? 2 : 0;

                if (isWater(worldPosition.offset(xLeft, i, zLeft))) leftWater++;
                if (isWater(worldPosition.offset(xRight, i, zRight))) rightWater++;
            }

            double relevantFlow = (axis == Direction.Axis.X) ? flow.z : flow.x;

            if (leftWater > rightWater || (relevantFlow > 0.1 && leftWater >= rightWater)) {
                waterMod = -1.0f;
            } else if (rightWater > leftWater || (relevantFlow < -0.1 && rightWater >= leftWater)) {
                waterMod = 1.0f;
            } else if (Math.abs(relevantFlow) > 0.05) {
                waterMod = relevantFlow > 0 ? -1.0f : 1.0f;
            } else {
                waterMod = 0.0f;
            }

            if (waterMod != 0.0f) {
                newPower = 1;
            }
        }

        if (newPower != this.power) {
            setPower(newPower);
        }
    }
}
