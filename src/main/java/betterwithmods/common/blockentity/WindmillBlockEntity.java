package betterwithmods.common.blockentity;

import betterwithmods.api.tile.IAxle;
import betterwithmods.common.blocks.mechanical.AxleBlock;
import betterwithmods.common.blocks.mechanical.AxleGeneratorBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.util.MechanicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class WindmillBlockEntity extends AxleGeneratorBlockEntity {
    private int[] bladeColors = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
    private int dyeIndex = 0;

    public WindmillBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.WINDMILL.get(), pos, state);
    }

    public boolean isVertical() {
        return getAxis() == Direction.Axis.Y;
    }

    @Override
    public int getRadius() {
        return isVertical() ? 4 : 6;
    }

    public int getBladeColor(int blade) {
        if (blade >= 0 && blade < bladeColors.length) {
            return bladeColors[blade];
        }
        return 0;
    }

    public boolean dye(DyeColor color) {
        int maxBlades = isVertical() ? 8 : 4;
        if (dyeIndex >= maxBlades) {
            dyeIndex = 0;
        }

        bladeColors[dyeIndex] = color.getId();
        dyeIndex = (dyeIndex + 1) % maxBlades;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return true;
    }

    @Override
    public void verifyIntegrity() {
        if (level == null) return;

        if (isVertical()) {
            verifyVertical();
        } else {
            verifyHorizontal();
        }
    }

    private void verifyHorizontal() {
        Direction.Axis axis = getAxis();
        boolean valid = true;

        for (int vert = -6; vert <= 6; vert++) {
            for (int i = -6; i <= 6; i++) {
                if (vert == 0 && i == 0) continue;

                int xP = (axis == Direction.Axis.Z ? i : 0);
                int zP = (axis == Direction.Axis.X ? i : 0);
                BlockPos offset = worldPosition.offset(xP, vert, zP);

                BlockState state = level.getBlockState(offset);
                if (!state.canBeReplaced() && !state.isAir()) {
                    valid = false;
                    break;
                }
            }
            if (!valid) break;
        }

        isValid = valid && level.canSeeSky(worldPosition) && (level.dimension() == Level.OVERWORLD);
    }

    private void verifyVertical() {
        boolean valid = true;

        // Check vertical axle column from -3 to +3
        for (int i = -3; i <= 3; i++) {
            if (i == 0) continue;
            BlockPos target = worldPosition.above(i);
            BlockState targetState = level.getBlockState(target);
            if (!(targetState.getBlock() instanceof AxleBlock)) {
                valid = false;
                break;
            }
            if (targetState.getValue(AxleBlock.AXIS) != Direction.Axis.Y) {
                valid = false;
                break;
            }
        }

        if (valid) {
            // Check radial air clearance around the cylinder
            for (int yOffset = -3; yOffset <= 3; yOffset++) {
                for (int x = -4; x <= 4; x++) {
                    for (int z = -4; z <= 4; z++) {
                        if (x == 0 && z == 0) continue;
                        if (x * x + z * z > 20) continue; // Approximate cylinder

                        BlockPos checkPos = worldPosition.offset(x, yOffset, z);
                        BlockState state = level.getBlockState(checkPos);
                        if (!state.canBeReplaced() && !state.isAir()) {
                            valid = false;
                            break;
                        }
                    }
                    if (!valid) break;
                }
                if (!valid) break;
            }
        }

        isValid = valid && (level.dimension() != Level.END);
    }

    @Override
    public void calculatePower() {
        if (level == null) return;
        int newPower = 0;

        if (isValid()) {
            if (level.dimension() == Level.OVERWORLD) {
                if (level.isThundering()) {
                    newPower = 3;
                } else if (level.isRaining()) {
                    newPower = 2;
                } else {
                    newPower = 1;
                }
            } else {
                newPower = 1;
            }
        }

        if (newPower != this.power) {
            setPower(newPower);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putIntArray("BladeColors", bladeColors);
        tag.putInt("DyeIndex", dyeIndex);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("BladeColors")) {
            int[] loaded = tag.getIntArray("BladeColors");
            System.arraycopy(loaded, 0, bladeColors, 0, Math.min(loaded.length, bladeColors.length));
        }
        if (tag.contains("DyeIndex")) {
            dyeIndex = tag.getInt("DyeIndex");
        }
    }
}
