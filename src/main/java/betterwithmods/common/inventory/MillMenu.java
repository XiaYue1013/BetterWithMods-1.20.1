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

public class MillMenu extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public MillMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(3));
    }

    public MillMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
        super(BWMMenuTypes.MILL.get(), containerId);
        checkContainerSize(container, 3);
        checkContainerDataCount(data, 3);
        this.container = container;
        this.data = data;

        container.startOpen(playerInventory.player);

        // Millstone slots: 3 slots horizontally
        for (int j = 0; j < 3; j++) {
            this.addSlot(new Slot(container, j, 62 + j * 18, 43));
        }

        // Player Inventory (3 rows of 9)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 76 + i * 18));
            }
        }

        // Player Hotbar (9 slots)
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 134));
        }

        this.addDataSlots(data);
    }

    public int getGrindProgress() {
        int counter = data.get(0);
        int max = data.get(1);
        if (max <= 0) max = 200;
        return (counter * 14) / max; // 14 pixels height on arrow
    }

    public boolean isMechanicalActive() {
        return data.get(2) > 0;
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

            if (index < 3) {
                if (!this.moveItemStackTo(stackInSlot, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
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
