package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.BellowsBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.common.registry.BWMBlocks;
import betterwithmods.common.registry.BWMSounds;
import betterwithmods.util.MechanicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BellowsBlockEntity extends BlockEntity implements IMechanicalPower {
    private int power;
    private boolean wasCompressed;

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);

    public BellowsBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.BELLOWS.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BellowsBlockEntity bellows) {
        bellows.power = bellows.calculateInput();
        boolean isCompressed = bellows.power > 0;

        if (isCompressed != bellows.wasCompressed) {
            bellows.wasCompressed = isCompressed;
            level.setBlock(pos, state.setValue(BellowsBlock.COMPRESSED, isCompressed), 3);

            if (isCompressed) {
                // Blow air forward
                Direction facing = state.getValue(BellowsBlock.FACING);
                level.playSound(null, pos, BWMSounds.WOOD_BELLOW.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                bellows.blowAir(facing);
            }
        }
    }

    private void blowAir(Direction facing) {
        for (int i = 1; i <= 3; i++) {
            BlockPos targetPos = worldPosition.relative(facing, i);
            BlockState targetState = level.getBlockState(targetPos);

            if (targetState.is(Blocks.FIRE) || targetState.is(BWMBlocks.STOKED_FLAME.get())) {
                level.setBlock(targetPos, BWMBlocks.STOKED_FLAME.get().defaultBlockState(), 3);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.putInt("power", power);
        compound.putBoolean("wasCompressed", wasCompressed);
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.power = compound.getInt("power");
        this.wasCompressed = compound.getBoolean("wasCompressed");
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
        BlockState state = getBlockState();
        if (state.hasProperty(BellowsBlock.FACING) && facing == state.getValue(BellowsBlock.FACING)) {
            return 0; // Front face blows air
        }
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
