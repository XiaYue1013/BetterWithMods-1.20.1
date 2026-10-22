package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.BlockDispenserBlock;
import betterwithmods.common.registry.BWMBlockEntities;
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
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
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

public class BlockDispenserBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, IMechanicalPower {
    public static final int SLOTS = 16;
    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private int power;
    private boolean wasPowered;
    public int nextIndex = 0;

    private final net.minecraft.world.inventory.ContainerData dataAccess = new net.minecraft.world.inventory.ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? nextIndex : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) nextIndex = value;
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> new InvWrapper(this));

    public BlockDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.BLOCK_DISPENSER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlockDispenserBlockEntity dispenser) {
        dispenser.power = dispenser.calculateInput();
        boolean isPowered = dispenser.power > 0;

        if (isPowered && !dispenser.wasPowered) {
            dispenser.wasPowered = true;
            dispenser.triggerAction();
        } else if (!isPowered && dispenser.wasPowered) {
            dispenser.wasPowered = false;
        }
    }

    private void triggerAction() {
        Direction facing = getBlockState().getValue(BlockDispenserBlock.FACING);
        BlockPos targetPos = worldPosition.relative(facing);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.isAir() || targetState.canBeReplaced()) {
            // Place block
            for (int i = 0; i < SLOTS; i++) {
                ItemStack stack = items.get(i);
                if (stack.getItem() instanceof BlockItem blockItem) {
                    Block blockToPlace = blockItem.getBlock();
                    level.setBlock(targetPos, blockToPlace.defaultBlockState(), 3);
                    stack.shrink(1);
                    setChanged();
                    return;
                }
            }
        } else {
            // Absorb / break block
            if (targetState.getDestroySpeed(level, targetPos) >= 0) {
                ItemStack drop = new ItemStack(targetState.getBlock());
                level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
                insertItem(drop);
            }
        }
    }

    private void insertItem(ItemStack stack) {
        for (int i = 0; i < SLOTS; i++) {
            ItemStack slot = items.get(i);
            if (slot.isEmpty()) {
                items.set(i, stack.copy());
                setChanged();
                return;
            } else if (ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
                int add = Math.min(stack.getCount(), slot.getMaxStackSize() - slot.getCount());
                slot.grow(add);
                stack.shrink(add);
                if (stack.isEmpty()) {
                    setChanged();
                    return;
                }
            }
        }
        if (!stack.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, items);
        compound.putInt("power", power);
        compound.putBoolean("wasPowered", wasPowered);
        compound.putInt("nextIndex", nextIndex);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, items);
        this.power = compound.getInt("power");
        this.wasPowered = compound.getBoolean("wasPowered");
        this.nextIndex = compound.getInt("nextIndex");
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
        int[] result = new int[SLOTS];
        for (int i = 0; i < SLOTS; i++) result[i] = i;
        return result;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.betterwithmods.block_dispenser");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new betterwithmods.common.inventory.BlockDispenserMenu(id, inventory, this, this.dataAccess);
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
