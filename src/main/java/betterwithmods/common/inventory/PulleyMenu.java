package betterwithmods.common.inventory;

import betterwithmods.common.registry.BWMBlocks;
import betterwithmods.common.registry.BWMMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class PulleyMenu extends AbstractContainerMenu {
    private final Container container;

    public PulleyMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(4));
    }

    public PulleyMenu(int containerId, Inventory playerInventory, Container container) {
        super(BWMMenuTypes.PULLEY.get(), containerId);
        checkContainerSize(container, 4);
        this.container = container;

        container.startOpen(playerInventory.player);

        // 4 rope storage slots at (53 + i * 18, 52)
        for (int i = 0; i < 4; i++) {
            this.addSlot(new Slot(container, i, 53 + i * 18, 52) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(BWMBlocks.ROPE.get().asItem());
                }
            });
        }

        // Player Inventory (3 rows of 9)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 93 + i * 18));
            }
        }

        // Player Hotbar (9 slots)
        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 151));
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

            if (index < 4) {
                if (!this.moveItemStackTo(stackInSlot, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stackInSlot.is(BWMBlocks.ROPE.get().asItem())) {
                    if (!this.moveItemStackTo(stackInSlot, 0, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
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
