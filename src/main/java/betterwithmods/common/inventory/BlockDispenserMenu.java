package betterwithmods.common.inventory;

import betterwithmods.common.registry.BWMMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BlockDispenserMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public BlockDispenserMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(16), new SimpleContainerData(1));
    }

    public BlockDispenserMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(BWMMenuTypes.BLOCK_DISPENSER.get(), containerId);
        checkContainerSize(container, 16);
        checkContainerDataCount(data, 1);
        this.container = container;
        this.data = data;

        container.startOpen(playerInventory.player);

        // 16 dispenser slots (4 rows of 4)
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.addSlot(new Slot(container, j + i * 4, 53 + j * 18, 17 + i * 18));
            }
        }

        // Player Inventory (3 rows of 9)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }

        // Player Hotbar (9 slots)
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 160));
        }

        this.addDataSlots(data);
    }

    public int getNextIndex() {
        return data.get(0);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index < 16) {
                if (!this.moveItemStackTo(stackInSlot, 16, 52, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 16, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }
        return itemstack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
