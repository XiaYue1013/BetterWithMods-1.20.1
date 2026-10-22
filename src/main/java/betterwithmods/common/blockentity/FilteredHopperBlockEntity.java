package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.FilteredHopperBlock;
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
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class FilteredHopperBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, IMechanicalPower {
    public static final int INVENTORY_SIZE = 19; // 18 storage + 1 filter (slot 18)
    private NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private int power;
    private int transferTimer;

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> new InvWrapper(this));

    public FilteredHopperBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.FILTERED_HOPPER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FilteredHopperBlockEntity hopper) {
        hopper.power = hopper.calculateInput();

        hopper.transferTimer++;
        if (hopper.transferTimer >= 8) {
            hopper.transferTimer = 0;
            if (hopper.power > 0) hopper.tryFilterConversion();
            hopper.tryOutputItem();
        }
    }

    /**
     * 1.12 hopper filter: a powered hopper with a Soul Sand filter turns Ground Netherrack into
     * Hellfire Dust (1.12 docs items/hellfire_dust.md), one item per transfer cycle.
     */
    private void tryFilterConversion() {
        if (!getFilter().is(Blocks.SOUL_SAND.asItem())) return;
        for (int i = 0; i < 18; i++) {
            ItemStack stack = items.get(i);
            if (!stack.is(BWMItems.GROUND_NETHERRACK.get())) continue;
            ItemStack dust = new ItemStack(BWMItems.HELLFIRE_DUST.get());
            if (!insertStorage(dust, true)) return;
            stack.shrink(1);
            insertStorage(dust, false);
            setChanged();
            return;
        }
    }

    private boolean insertStorage(ItemStack stack, boolean simulate) {
        for (int i = 0; i < 18; i++) {
            ItemStack dest = items.get(i);
            if (!dest.isEmpty() && ItemStack.isSameItemSameTags(dest, stack) && dest.getCount() < dest.getMaxStackSize()) {
                if (!simulate) dest.grow(1);
                return true;
            }
        }
        for (int i = 0; i < 18; i++) {
            if (items.get(i).isEmpty()) {
                if (!simulate) items.set(i, stack.copy());
                return true;
            }
        }
        return false;
    }

    private void tryOutputItem() {
        BlockPos below = worldPosition.below();
        if (level.getBlockEntity(below) instanceof WorldlyContainer container) {
            for (int i = 0; i < 18; i++) {
                ItemStack stack = items.get(i);
                if (!stack.isEmpty()) {
                    // Try to insert into container below
                    for (int slot : container.getSlotsForFace(Direction.UP)) {
                        if (container.canPlaceItemThroughFace(slot, stack, Direction.UP)) {
                            ItemStack dest = container.getItem(slot);
                            if (dest.isEmpty()) {
                                container.setItem(slot, stack.split(1));
                                setChanged();
                                return;
                            } else if (ItemStack.isSameItemSameTags(dest, stack) && dest.getCount() < dest.getMaxStackSize()) {
                                dest.grow(1);
                                stack.shrink(1);
                                setChanged();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    public ItemStack getFilter() {
        return items.get(18);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, items);
        compound.putInt("power", power);
        compound.putInt("transferTimer", transferTimer);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, items);
        this.power = compound.getInt("power");
        this.transferTimer = compound.getInt("transferTimer");
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
        if (side == Direction.DOWN) {
            int[] slots = new int[18];
            for (int i = 0; i < 18; i++) slots[i] = i;
            return slots;
        }
        int[] all = new int[19];
        for (int i = 0; i < 19; i++) all[i] = i;
        return all;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        if (index == 18) {
            return stack.is(BWMBlocks.WICKER.get().asItem()) ||
                    stack.is(BWMBlocks.GRATE.get().asItem()) ||
                    stack.is(BWMBlocks.SLATS.get().asItem()) ||
                    stack.is(Blocks.SOUL_SAND.asItem());
        }
        ItemStack filter = getFilter();
        if (filter.isEmpty()) return true;
        // Simple filter separation
        if (filter.is(BWMBlocks.WICKER.get().asItem())) {
            return stack.is(BWMItems.HEMP_FIBERS.get()) || stack.is(Items.SUGAR) || stack.is(Items.GUNPOWDER) || stack.is(BWMItems.COCOA_POWDER.get());
        }
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index < 18;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.betterwithmods.filtered_hopper");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new betterwithmods.common.inventory.FilteredHopperMenu(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
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
