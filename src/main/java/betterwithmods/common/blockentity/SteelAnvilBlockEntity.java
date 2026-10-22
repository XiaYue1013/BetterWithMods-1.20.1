package betterwithmods.common.blockentity;

import betterwithmods.common.registry.BWMBlockEntities;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class SteelAnvilBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int SLOTS = 17; // 16 craft + 1 result
    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

    public SteelAnvilBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.STEEL_ANVIL.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, items);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, items);
    }

    @Override
    public int[] getSlotsForFace(net.minecraft.core.Direction side) {
        int[] slots = new int[SLOTS];
        for (int i = 0; i < SLOTS; i++) slots[i] = i;
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable net.minecraft.core.Direction direction) {
        return index < 16;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, net.minecraft.core.Direction direction) {
        return index == 16;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.betterwithmods.steel_anvil");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new betterwithmods.common.inventory.SteelAnvilMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.create(level, worldPosition));
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
