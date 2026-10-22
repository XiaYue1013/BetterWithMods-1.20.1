package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IAxle;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.AxleBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.common.registry.BWMBlocks;
import betterwithmods.util.MechanicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AxleBlockEntity extends BlockEntity implements IAxle {
    private byte maxSignal;
    private int maxPower;
    private int minPower;

    private byte signal;
    private int power;

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);
    private final LazyOptional<IAxle> axleCap = LazyOptional.of(() -> this);

    public AxleBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.AXLE.get(), pos, state);
        boolean isSteel = state.is(BWMBlocks.STEEL_AXLE.get());
        this.maxPower = isSteel ? 50 : 1;
        this.minPower = 1;
        this.maxSignal = isSteel ? (byte) 6 : (byte) 4;
    }

    public AxleBlockEntity(BlockPos pos, BlockState state, int maxPower, int minPower, byte maxSignal) {
        super(BWMBlockEntities.AXLE.get(), pos, state);
        this.maxPower = maxPower;
        this.minPower = minPower;
        this.maxSignal = maxSignal;
    }

    public void onChanged() {
        if (level == null || level.isClientSide) return;

        byte findSignal = 0;
        int findPower = 0;
        int sources = 0;

        for (Direction direction : getDirections()) {
            BlockPos offset = worldPosition.relative(direction);
            Direction opposite = direction.getOpposite();
            IMechanicalPower mech = MechanicalUtil.getMechanicalPower(level, offset, opposite);
            if (mech != null) {
                IAxle axle = MechanicalUtil.getAxle(level, offset, opposite);
                if (axle != null) {
                    if (isFacing(axle)) {
                        byte next = axle.getSignal();
                        if (next > findSignal) {
                            findSignal = next;
                        }
                    }
                }

                int p = mech.getMechanicalOutput(opposite);
                if (p > 0) {
                    if (p > findPower) {
                        sources++;
                        if (axle != null) {
                            if (axle.getSignal() >= findSignal) {
                                findPower = p;
                            }
                        } else {
                            findPower = p;
                        }
                    }
                    if (axle == null) {
                        findSignal = getMaximumSignal();
                    }
                }
            }
        }

        setPower(findPower);

        if (sources >= 2) {
            overpower();
            return;
        }

        byte newSignal = 0;
        if (findSignal > signal) {
            if (findSignal == 1) {
                overpower();
                return;
            }
            if (power > 0) {
                newSignal = (byte) (findSignal - 1);
            }
        } else {
            newSignal = 0;
            setPower(0);
        }

        if (newSignal != this.signal) {
            setSignal(newSignal);
        }

        BlockState state = getBlockState();
        if (state.hasProperty(AxleBlock.ACTIVE) && state.getValue(AxleBlock.ACTIVE) != (power > 0)) {
            level.setBlock(worldPosition, state.setValue(AxleBlock.ACTIVE, power > 0), 3);
        }
        setChanged();
    }

    @Override
    public void overpower() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof AxleBlock axleBlock) {
                axleBlock.overpower(level, worldPosition);
            } else {
                level.destroyBlock(worldPosition, true);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.putByte("signal", signal);
        compound.putInt("power", power);
        compound.putByte("maxSignal", maxSignal);
        compound.putInt("maxPower", maxPower);
        compound.putInt("minPower", minPower);
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.signal = compound.getByte("signal");
        this.maxSignal = compound.getByte("maxSignal");
        this.power = compound.getInt("power");
        this.maxPower = compound.getInt("maxPower");
        this.minPower = compound.getInt("minPower");
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
        if (capability == CapabilityMechanical.AXLE) {
            return axleCap.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        powerCap.invalidate();
        axleCap.invalidate();
    }

    @Override
    public int getMechanicalOutput(Direction facing) {
        if (facing.getAxis() == getAxis()) {
            IAxle axle = MechanicalUtil.getAxle(level, worldPosition.relative(facing), facing.getOpposite());
            if (axle != null && axle.getSignal() > this.getSignal()) {
                return 0;
            }
            return power;
        }
        return 0;
    }

    @Override
    public int getMechanicalInput(Direction facing) {
        return maxPower;
    }

    @Override
    public int getMaximumInput(Direction facing) {
        return maxPower;
    }

    @Override
    public int getMinimumInput(Direction facing) {
        return minPower;
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
    public byte getSignal() {
        return signal;
    }

    public void setSignal(byte signal) {
        this.signal = signal;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public byte getMaximumSignal() {
        return maxSignal;
    }

    @Override
    public int getMaximumInput() {
        return maxPower;
    }

    @Override
    public int getMinimumInput() {
        return minPower;
    }

    @Override
    public Direction[] getDirections() {
        Direction.Axis axis = getAxis();
        return new Direction[]{
                Direction.get(Direction.AxisDirection.POSITIVE, axis),
                Direction.get(Direction.AxisDirection.NEGATIVE, axis)
        };
    }

    @Override
    public Direction.Axis getAxis() {
        BlockState state = getBlockState();
        if (state.hasProperty(AxleBlock.AXIS)) {
            return state.getValue(AxleBlock.AXIS);
        }
        return Direction.Axis.Y;
    }
}
