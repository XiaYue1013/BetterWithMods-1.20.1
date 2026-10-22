package betterwithmods.common.inventory;

import betterwithmods.common.registry.BWMMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilteredHopperMenu extends AbstractContainerMenu {
    private final Container container;

    public FilteredHopperMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(19));
    }

    public FilteredHopperMenu(int containerId, Inventory playerInventory, Container container) {
        super(BWMMenuTypes.FILTERED_HOPPER.get(), containerId);
        checkContainerSize(container, 19);
        this.container = container;

        container.startOpen(playerInventory.player);

        // 18 inventory slots (2 rows of 9)
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(container, j + i * 9, 8 + j * 18, 60 + i * 18));
            }
        }

        // 1 filter slot at (80, 37)
        this.addSlot(new Slot(container, 18, 80, 37));

        // Player Inventory (3 rows of 9)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 111 + i * 18));
            }
        }

        // Player Hotbar (9 slots)
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 169));
        }
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

            if (index < 19) {
                if (!this.moveItemStackTo(stackInSlot, 19, 55, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, 18, false)) {
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
