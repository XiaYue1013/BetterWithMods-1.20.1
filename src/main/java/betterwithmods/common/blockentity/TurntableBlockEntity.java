package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.UnfiredPotteryBlock;
import betterwithmods.common.blocks.mechanical.TurntableBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.common.registry.BWMBlocks;
import betterwithmods.util.MechanicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TurntableBlockEntity extends BlockEntity implements IMechanicalPower {
    private int power;
    private int timer;
    private int potteryStage;

    public static final int[] ROTATION_INTERVALS = {10, 20, 40, 80};

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);

    public TurntableBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.TURNTABLE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TurntableBlockEntity turntable) {
        turntable.power = turntable.calculateInput();

        if (turntable.power > 0) {
            int speedIndex = state.getValue(TurntableBlock.SPEED);
            int interval = ROTATION_INTERVALS[speedIndex % ROTATION_INTERVALS.length];

            turntable.timer++;
            if (turntable.timer >= interval) {
                turntable.timer = 0;
                turntable.rotateTopBlock();
            }
        }
    }

    private void rotateTopBlock() {
        BlockPos topPos = worldPosition.above();
        BlockState topState = level.getBlockState(topPos);
        if (topState.isAir()) return;

        // Check unfired pottery progression
        if (topState.is(BWMBlocks.UNFIRED_POTTERY.get())) {
            potteryStage++;
            level.playSound(null, topPos, SoundEvents.GRAVEL_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
            if (potteryStage >= 8) {
                potteryStage = 0;
                advancePottery(topPos, topState);
            }
            return;
        }

        // Standard block rotation
        BlockState rotated = topState.rotate(level, topPos, Rotation.CLOCKWISE_90);
        if (rotated != topState) {
            level.setBlock(topPos, rotated, 3);
            level.playSound(null, topPos, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.1f, 2.0f);
        }
    }

    private void advancePottery(BlockPos topPos, BlockState state) {
        int currentType = state.getValue(UnfiredPotteryBlock.TYPE);
        if (currentType < UnfiredPotteryBlock.EnumType.values().length - 1) {
            level.setBlock(topPos, state.setValue(UnfiredPotteryBlock.TYPE, currentType + 1), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.putInt("power", power);
        compound.putInt("timer", timer);
        compound.putInt("potteryStage", potteryStage);
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.power = compound.getInt("power");
        this.timer = compound.getInt("timer");
        this.potteryStage = compound.getInt("potteryStage");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
        if (facing == Direction.UP) return 0; // Top is work surface
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
