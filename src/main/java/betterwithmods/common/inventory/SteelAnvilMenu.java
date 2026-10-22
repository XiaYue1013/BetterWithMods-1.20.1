package betterwithmods.common.inventory;

import betterwithmods.common.registry.BWMMenuTypes;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class SteelAnvilMenu extends AbstractContainerMenu {
    public static final int RESULT_SLOT = 0;
    public static final int CRAFT_SLOT_START = 1;
    public static final int CRAFT_SLOT_END = 17;
    public static final int INV_SLOT_START = 17;
    public static final int INV_SLOT_END = 44;
    public static final int USE_ROW_SLOT_START = 44;
    public static final int USE_ROW_SLOT_END = 53;

    private final TransientCraftingContainer craftSlots;
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public SteelAnvilMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public SteelAnvilMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(BWMMenuTypes.STEEL_ANVIL.get(), containerId);
        this.access = access;
        this.player = playerInventory.player;
        this.craftSlots = new TransientCraftingContainer(this, 4, 4);

        // Result slot at (124, 44)
        this.addSlot(new ResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 124, 44));

        // 4x4 crafting grid (16 slots)
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.addSlot(new Slot(this.craftSlots, j + i * 4, 12 + j * 18, 17 + i * 18));
            }
        }

        // Player Inventory (3 rows of 9)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 102 + i * 18));
            }
        }

        // Player Hotbar (9 slots)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 160));
        }
    }

    protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots) {
        if (!level.isClientSide) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<betterwithmods.common.recipe.SteelAnvilRecipe> anvilRecipe = level.getServer().getRecipeManager().getRecipeFor(betterwithmods.common.registry.BWMRecipes.STEEL_ANVIL_TYPE.get(), craftSlots, level);
            if (anvilRecipe.isPresent()) {
                betterwithmods.common.recipe.SteelAnvilRecipe recipe = anvilRecipe.get();
                if (resultSlots.setRecipeUsed(level, serverPlayer, recipe)) {
                    ItemStack result = recipe.assemble(craftSlots, level.registryAccess());
                    if (result.isItemEnabled(level.enabledFeatures())) {
                        itemstack = result;
                    }
                }
            } else {
                Optional<CraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftSlots, level);
                if (optional.isPresent()) {
                    CraftingRecipe recipe = optional.get();
                    if (resultSlots.setRecipeUsed(level, serverPlayer, recipe)) {
                        ItemStack result = recipe.assemble(craftSlots, level.registryAccess());
                        if (result.isItemEnabled(level.enabledFeatures())) {
                            itemstack = result;
                        }
                    }
                }
            }

            resultSlots.setItem(0, itemstack);
            menu.setRemoteSlot(0, itemstack);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, itemstack));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> {
            slotChangedCraftingGrid(this, level, this.player, this.craftSlots, this.resultSlots);
        });
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> {
            this.clearContainer(player, this.craftSlots);
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, net.minecraft.world.level.block.Blocks.CRAFTING_TABLE)
                || stillValid(this.access, player, betterwithmods.common.registry.BWMBlocks.STEEL_ANVIL.get())
                || access == ContainerLevelAccess.NULL;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == RESULT_SLOT) {
                this.access.execute((level, pos) -> stackInSlot.getItem().onCraftedBy(stackInSlot, level, player));
                if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index >= INV_SLOT_START && index < USE_ROW_SLOT_END) {
                if (!this.moveItemStackTo(stackInSlot, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
                    if (index < INV_SLOT_END) {
                        if (!this.moveItemStackTo(stackInSlot, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, INV_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(stackInSlot, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
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
            if (index == RESULT_SLOT) {
                player.drop(stackInSlot, false);
            }
        }

        return itemstack;
    }
}
