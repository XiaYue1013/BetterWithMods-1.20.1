package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.util.MechanicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ScrewPumpBlockEntity extends BlockEntity implements IMechanicalPower {
    private int power;
    private int pumpTimer;

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);

    public ScrewPumpBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.PUMP.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ScrewPumpBlockEntity pump) {
        pump.power = pump.calculateInput();

        if (pump.power > 0) {
            pump.pumpTimer++;
            if (pump.pumpTimer >= 20) {
                pump.pumpTimer = 0;
                pump.pumpFluid();
            }
        }
    }

    private void pumpFluid() {
        BlockPos belowPos = worldPosition.below();
        BlockState belowState = level.getBlockState(belowPos);
        BlockPos abovePos = worldPosition.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (belowState.getFluidState().is(Fluids.WATER) && (aboveState.isAir() || aboveState.canBeReplaced())) {
            level.setBlock(abovePos, Blocks.WATER.defaultBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.putInt("power", power);
        compound.putInt("pumpTimer", pumpTimer);
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.power = compound.getInt("power");
        this.pumpTimer = compound.getInt("pumpTimer");
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
        return 0;
    }

    @Override
    public int getMechanicalInput(Direction facing) {
        BlockPos offset = worldPosition.relative(facing);
        return MechanicalUtil.getPowerOutput(level, offset, facing.getOpposite());
    }

    @Override
    public int getMaximumInput(Direction facing) {
        return 50;
    }

    @Override
    public int getMinimumInput(Direction facing) {
        return 1;
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
