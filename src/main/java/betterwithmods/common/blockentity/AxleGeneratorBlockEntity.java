package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IAxle;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.AxleGeneratorBlock;
import betterwithmods.common.registry.BWMSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AxleGeneratorBlockEntity extends BlockEntity implements IAxle {
    protected int power = 0;
    protected boolean isValid = false;
    protected float waterMod = 1.0f;
    protected float currentRotation = 0.0f;
    protected float previousRotation = 0.0f;
    protected final float runningSpeed = 0.4f;
    protected int tickCount = 0;

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);
    private final LazyOptional<IAxle> axleCap = LazyOptional.of(() -> this);

    public AxleGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public abstract void verifyIntegrity();
    public abstract void calculatePower();
    public abstract int getRadius();

    public static void tick(Level level, BlockPos pos, BlockState state, AxleGeneratorBlockEntity blockEntity) {
        blockEntity.tickCount++;
        if (blockEntity.tickCount % 20 == 0 && !level.isClientSide) {
            blockEntity.verifyIntegrity();
            blockEntity.calculatePower();
        }

        if (blockEntity.isValid() && blockEntity.power > 0) {
            blockEntity.previousRotation = blockEntity.power * blockEntity.runningSpeed * blockEntity.waterMod;
            blockEntity.currentRotation += (blockEntity.power * blockEntity.power) * blockEntity.runningSpeed * blockEntity.waterMod;
            blockEntity.currentRotation %= 360;

            if (!level.isClientSide && level.random.nextInt(100) == 0) {
                level.playSound(null, pos, BWMSounds.WOOD_CREAK.get(), SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.25f + 0.25f);
            }
        } else {
            blockEntity.previousRotation = 0;
            blockEntity.currentRotation = 0;
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public void setPower(int power) {
        this.power = power;
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.hasProperty(AxleGeneratorBlock.ACTIVE) && state.getValue(AxleGeneratorBlock.ACTIVE) != (power > 0)) {
                level.setBlock(worldPosition, state.setValue(AxleGeneratorBlock.ACTIVE, power > 0), 3);
            }
            setChanged();
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    public float getCurrentRotation() {
        return currentRotation;
    }

    public float getPreviousRotation() {
        return previousRotation;
    }

    public float getWaterMod() {
        return waterMod;
    }

    @Override
    public int getMechanicalOutput(Direction facing) {
        if (facing.getAxis() == getAxis()) {
            return power;
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
    public byte getSignal() {
        return getMaximumSignal();
    }

    @Override
    public byte getMaximumSignal() {
        boolean steel = getBlockState().hasProperty(AxleGeneratorBlock.STEEL) && getBlockState().getValue(AxleGeneratorBlock.STEEL);
        return steel ? (byte) 6 : (byte) 4;
    }

    @Override
    public int getMaximumInput() {
        return 0;
    }

    @Override
    public int getMinimumInput() {
        return 0;
    }

    @Override
    public Direction.Axis getAxis() {
        BlockState state = getBlockState();
        if (state.hasProperty(AxleGeneratorBlock.AXIS)) {
            return state.getValue(AxleGeneratorBlock.AXIS);
        }
        return Direction.Axis.Z;
    }

    @Override
    public Direction[] getDirections() {
        return switch (getAxis()) {
            case X -> new Direction[]{Direction.WEST, Direction.EAST};
            case Y -> new Direction[]{Direction.DOWN, Direction.UP};
            case Z -> new Direction[]{Direction.NORTH, Direction.SOUTH};
        };
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityMechanical.MECHANICAL_POWER) {
            return powerCap.cast();
        }
        if (cap == CapabilityMechanical.AXLE) {
            return axleCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        powerCap.invalidate();
        axleCap.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Power", power);
        tag.putBoolean("IsValid", isValid);
        tag.putFloat("WaterMod", waterMod);
        tag.putFloat("CurrentRotation", currentRotation);
        tag.putFloat("PreviousRotation", previousRotation);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.power = tag.getInt("Power");
        this.isValid = tag.getBoolean("IsValid");
        if (tag.contains("WaterMod")) this.waterMod = tag.getFloat("WaterMod");
        if (tag.contains("CurrentRotation")) this.currentRotation = tag.getFloat("CurrentRotation");
        if (tag.contains("PreviousRotation")) this.previousRotation = tag.getFloat("PreviousRotation");
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

    @Override
    public void onDataPacket(net.minecraft.network.Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            load(tag);
        }
    }

    @Override
    public net.minecraft.world.phys.AABB getRenderBoundingBox() {
        int r = getRadius() + 1;
        return new net.minecraft.world.phys.AABB(worldPosition).inflate(r);
    }

    @Override
    public net.minecraft.world.level.block.Block getBlock() {
        return getBlockState().getBlock();
    }

    @Override
    public Level getBlockWorld() {
        return level;
    }
}
