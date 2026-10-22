package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.GearboxBlock;
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

public class GearboxBlockEntity extends BlockEntity implements IMechanicalPower {
    private int power;
    private int maxPower;
    private int unchanged;

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);

    public GearboxBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.GEARBOX.get(), pos, state);
        boolean isSteel = state.is(BWMBlocks.STEEL_GEARBOX.get());
        this.maxPower = isSteel ? 50 : 1;
    }

    public void onChanged() {
        if (level == null || level.isClientSide) return;

        if (MechanicalUtil.isRedstonePowered(level, worldPosition)) {
            setPower(0);
            return;
        }

        Direction facing = getFacing();
        int input = getMechanicalInput(facing);

        if (input != this.power) {
            setPower(input);
            unchanged = 0;
        } else {
            unchanged++;
        }

        if (power > maxPower && unchanged > 10) {
            overpower();
        }
    }

    public Direction getFacing() {
        BlockState state = getBlockState();
        if (state.hasProperty(GearboxBlock.FACING)) {
            return state.getValue(GearboxBlock.FACING);
        }
        return Direction.NORTH;
    }

    public void setPower(int power) {
        this.power = power;
        BlockState state = getBlockState();
        if (state.hasProperty(GearboxBlock.ACTIVE) && state.getValue(GearboxBlock.ACTIVE) != (power > 0)) {
            level.setBlock(worldPosition, state.setValue(GearboxBlock.ACTIVE, power > 0), 3);
        }
        setChanged();
    }

    public int getPower() {
        return power;
    }

    @Override
    public void overpower() {
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.getBlock() instanceof GearboxBlock gb) {
                gb.overpower(level, worldPosition);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        compound.putInt("power", power);
        compound.putInt("maxPower", maxPower);
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.power = compound.getInt("power");
        this.maxPower = compound.getInt("maxPower");
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
        if (facing != getFacing() && MechanicalUtil.isAxle(level, worldPosition.relative(facing), facing.getOpposite())) {
            return Math.min(power, maxPower);
        }
        return 0;
    }

    @Override
    public int getMechanicalInput(Direction facing) {
        if (facing == getFacing()) {
            BlockPos inputPos = worldPosition.relative(facing);
            IMechanicalPower source = MechanicalUtil.getMechanicalPower(level, inputPos, facing.getOpposite());
            if (source != null && !(source instanceof GearboxBlockEntity)) {
                return source.getMechanicalOutput(facing.getOpposite());
            }
        }
        return 0;
    }

    @Override
    public int getMaximumInput(Direction facing) {
        return maxPower;
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
