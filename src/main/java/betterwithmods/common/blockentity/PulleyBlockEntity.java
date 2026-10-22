package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.PulleyBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.common.registry.BWMBlocks;
import betterwithmods.common.registry.BWMItems;
import betterwithmods.util.MechanicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PulleyBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, IMechanicalPower {
    public static final int SLOTS = 4;
    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

    private int power;
    private int moveTimer;

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> new InvWrapper(this));

    public PulleyBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.PULLEY.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PulleyBlockEntity pulley) {
        pulley.power = pulley.calculateInput();
        boolean isActive = pulley.power > 0;

        if (state.getValue(PulleyBlock.ACTIVE) != isActive) {
            level.setBlock(pos, state.setValue(PulleyBlock.ACTIVE, isActive), 3);
        }

        if (isActive) {
            pulley.moveTimer++;
            if (pulley.moveTimer >= 20) {
                pulley.moveTimer = 0;
                boolean isRedstone = MechanicalUtil.isRedstonePowered(level, pos);
                if (isRedstone) {
                    pulley.raiseRope();
                } else {
                    pulley.lowerRope();
                }
            }
        }
    }

    private void lowerRope() {
        BlockPos below = worldPosition.below();
        while (level.getBlockState(below).is(BWMBlocks.ROPE.get())) {
            below = below.below();
        }

        if (level.getBlockState(below).isAir() || level.getBlockState(below).canBeReplaced()) {
            if (consumeRope()) {
                level.setBlock(below, BWMBlocks.ROPE.get().defaultBlockState(), 3);
            }
        }
    }

    private void raiseRope() {
        BlockPos below = worldPosition.below();
        BlockPos lowestRope = null;
        while (level.getBlockState(below).is(BWMBlocks.ROPE.get())) {
            lowestRope = below;
            below = below.below();
        }

        if (lowestRope != null) {
            level.setBlock(lowestRope, Blocks.AIR.defaultBlockState(), 3);
            addRope(new ItemStack(BWMItems.ROPE.get()));
        }
    }

    private boolean consumeRope() {
        for (ItemStack stack : items) {
            if (stack.is(BWMItems.ROPE.get()) && !stack.isEmpty()) {
                stack.shrink(1);
                setChanged();
                return true;
            }
        }
        return false;
    }

    private void addRope(ItemStack stack) {
        for (int i = 0; i < SLOTS; i++) {
            ItemStack slot = items.get(i);
            if (slot.isEmpty()) {
                items.set(i, stack.copy());
                setChanged();
                return;
            } else if (ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
                slot.grow(1);
                setChanged();
                return;
            }
        }
        Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, items);
        compound.putInt("power", power);
        compound.putInt("moveTimer", moveTimer);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, items);
        this.power = compound.getInt("power");
        this.moveTimer = compound.getInt("moveTimer");
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityMechanical.MECHANICAL_POWER) {
            return powerCap.cast();
        }
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        powerCap.invalidate();
        itemCap.invalidate();
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

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0, 1, 2, 3};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return stack.is(BWMItems.ROPE.get());
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.betterwithmods.pulley");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new betterwithmods.common.inventory.PulleyMenu(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return SLOTS;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return net.minecraft.world.Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }
}
