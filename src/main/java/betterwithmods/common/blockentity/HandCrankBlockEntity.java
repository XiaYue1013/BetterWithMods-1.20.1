package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.HandCrankBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HandCrankBlockEntity extends BlockEntity implements IMechanicalPower {
    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);

    public HandCrankBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.CRANK.get(), pos, state);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityMechanical.MECHANICAL_POWER) {
            return powerCap.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        powerCap.invalidate();
    }

    @Override
    public int getMechanicalOutput(Direction facing) {
        BlockState state = getBlockState();
        if (state.hasProperty(HandCrankBlock.STAGE) && state.getValue(HandCrankBlock.STAGE) > 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getMechanicalInput(Direction facing) {
        return 0;
    }

    @Override
    public int getMaximumInput(Direction facing) {
        return 0;
    }

    @Override
    public int getMinimumInput(Direction facing) {
        return 0;
    }

    @Override
    public Block getBlock() {
        return getBlockState().getBlock();
    }

    @Override
    public Level getBlockWorld() {
        return level;
    }

    @Override
    public BlockPos getBlockPos() {
        return worldPosition;
    }
}
