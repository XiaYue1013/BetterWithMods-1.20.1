package betterwithmods.common.blockentity;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.api.tile.IMechanicalPower;
import betterwithmods.common.blocks.mechanical.MillstoneBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.common.registry.BWMItems;
import betterwithmods.common.registry.BWMSounds;
import betterwithmods.util.MechanicalUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MillstoneBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, IMechanicalPower {
    public static final int SLOTS = 3;
    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

    private int power;
    private int grindCounter;
    public static final int GRIND_MAX = 200;

    private final LazyOptional<IMechanicalPower> powerCap = LazyOptional.of(() -> this);
    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> new InvWrapper(this));

    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.MILLSTONE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MillstoneBlockEntity mill) {
        mill.power = mill.calculateInput();

        boolean isActive = mill.power > 0;
        if (state.getValue(MillstoneBlock.ACTIVE) != isActive) {
            level.setBlock(pos, state.setValue(MillstoneBlock.ACTIVE, isActive), 3);
        }

        if (isActive) {
            if (mill.hasValidRecipe()) {
                mill.grindCounter++;
                if (mill.grindCounter % 40 == 0) {
                    level.playSound(null, pos, BWMSounds.STONE_GRIND.get(), SoundSource.BLOCKS, 0.5f, 1.0f);
                    if (mill.items.get(0).is(Items.NETHERRACK) && level.random.nextInt(3) == 0) {
                        level.playSound(null, pos, BWMSounds.MILLSTONE_NETHERRACK.get(), SoundSource.BLOCKS, 0.8f, 1.0f);
                    }
                }
                if (mill.grindCounter >= GRIND_MAX) {
                    mill.craftItem();
                    mill.grindCounter = 0;
                    mill.setChanged();
                }
            } else {
                mill.grindCounter = 0;
            }
        } else {
            if (mill.grindCounter > 0) {
                mill.grindCounter--;
            }
        }
    }

    private java.util.Optional<betterwithmods.common.recipe.MillingRecipe> getActiveRecipe() {
        if (level == null) return java.util.Optional.empty();
        return level.getRecipeManager().getRecipeFor(betterwithmods.common.registry.BWMRecipes.MILLING_TYPE.get(), this, level);
    }

    private boolean hasValidRecipe() {
        java.util.Optional<betterwithmods.common.recipe.MillingRecipe> recipe = getActiveRecipe();
        if (recipe.isPresent()) return planBatch(recipe.get()) != null;
        ItemStack input = items.get(0);
        if (input.isEmpty()) return false;

        // Basic milling conversions (standard recipes)
        if (input.is(Items.WHEAT)) return canOutput(new ItemStack(BWMItems.FLOUR.get(), 1));
        if (input.is(Items.NETHERRACK)) return canOutput(new ItemStack(BWMItems.GROUND_NETHERRACK.get(), 1));
        if (input.is(Items.COAL)) return canOutput(new ItemStack(BWMItems.COAL_DUST.get(), 1));
        if (input.is(Items.CHARCOAL)) return canOutput(new ItemStack(BWMItems.CHARCOAL_DUST.get(), 1));
        if (input.is(BWMItems.HEMP.get().asItem())) return canOutput(new ItemStack(BWMItems.HEMP_FIBERS.get(), 3));
        if (input.is(Items.BONE)) return canOutput(new ItemStack(Items.BONE_MEAL, 6));
        return false;
    }

    private boolean canOutput(ItemStack output) {
        ItemStack slot1 = items.get(1);
        if (slot1.isEmpty()) return true;
        if (ItemStack.isSameItemSameTags(slot1, output) && slot1.getCount() + output.getCount() <= slot1.getMaxStackSize()) {
            return true;
        }
        ItemStack slot2 = items.get(2);
        if (slot2.isEmpty()) return true;
        return ItemStack.isSameItemSameTags(slot2, output) && slot2.getCount() + output.getCount() <= slot2.getMaxStackSize();
    }

    /** Simulate on copies. A full input batch may free the very cells its output needs. */
    private record BatchPlan(NonNullList<ItemStack> inventory, java.util.List<ItemStack> ejected) {}

    private BatchPlan planBatch(betterwithmods.common.recipe.MillingRecipe recipe) {
        if (recipe.getResults().isEmpty()) return null;
        NonNullList<ItemStack> next = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        for (int slot = 0; slot < SLOTS; slot++) next.set(slot, items.get(slot).copy());
        for (net.minecraft.world.item.crafting.Ingredient ingredient : recipe.getIngredients()) {
            boolean found = false;
            for (ItemStack stack : next) {
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    stack.shrink(1);
                    found = true;
                    break;
                }
            }
            if (!found) return null;
        }
        java.util.List<ItemStack> ejected = new java.util.ArrayList<>();
        for (ItemStack result : recipe.getResults()) {
            ItemStack remaining = result.copy();
            // Merge first so empty cells remain available for distinct byproducts.
            for (ItemStack target : next) {
                if (!target.isEmpty() && ItemStack.isSameItemSameTags(target, remaining)) {
                    int count = Math.min(remaining.getCount(), Math.min(getMaxStackSize(), target.getMaxStackSize()) - target.getCount());
                    if (count > 0) { target.grow(count); remaining.shrink(count); }
                }
                if (remaining.isEmpty()) break;
            }
            for (int slot = 0; slot < SLOTS && !remaining.isEmpty(); slot++) {
                if (!next.get(slot).isEmpty()) continue;
                int count = Math.min(remaining.getCount(), Math.min(getMaxStackSize(), remaining.getMaxStackSize()));
                next.set(slot, remaining.copyWithCount(count));
                remaining.shrink(count);
            }
            if (!remaining.isEmpty()) ejected.add(remaining);
        }
        return new BatchPlan(next, ejected);
    }

    private void craftItem() {
        java.util.Optional<betterwithmods.common.recipe.MillingRecipe> recipeOpt = getActiveRecipe();
        if (recipeOpt.isPresent()) {
            BatchPlan plan = planBatch(recipeOpt.get());
            if (plan == null) return;
            for (int slot = 0; slot < SLOTS; slot++) items.set(slot, plan.inventory().get(slot));
            for (ItemStack output : plan.ejected()) {
                Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, output);
            }
            setChanged();
            return;
        }

        ItemStack input = items.get(0);
        ItemStack result = ItemStack.EMPTY;
        if (input.is(Items.WHEAT)) result = new ItemStack(BWMItems.FLOUR.get(), 1);
        else if (input.is(Items.NETHERRACK)) result = new ItemStack(BWMItems.GROUND_NETHERRACK.get(), 1);
        else if (input.is(Items.COAL)) result = new ItemStack(BWMItems.COAL_DUST.get(), 1);
        else if (input.is(Items.CHARCOAL)) result = new ItemStack(BWMItems.CHARCOAL_DUST.get(), 1);
        else if (input.is(BWMItems.HEMP.get().asItem())) result = new ItemStack(BWMItems.HEMP_FIBERS.get(), 3);
        else if (input.is(Items.BONE)) result = new ItemStack(Items.BONE_MEAL, 6);

        if (!result.isEmpty()) {
            input.shrink(1);
            insertOutput(result);
        }
    }

    private void insertOutput(ItemStack stack) {
        for (int i = 1; i < SLOTS; i++) {
            ItemStack slot = items.get(i);
            if (slot.isEmpty()) {
                items.set(i, stack.copy());
                return;
            } else if (ItemStack.isSameItemSameTags(slot, stack)) {
                int add = Math.min(stack.getCount(), slot.getMaxStackSize() - slot.getCount());
                slot.grow(add);
                stack.shrink(add);
                if (stack.isEmpty()) return;
            }
        }
        if (!stack.isEmpty() && level != null) {
            Containers.dropItemStack(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, stack);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, items);
        compound.putInt("power", power);
        compound.putInt("grindCounter", grindCounter);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, items);
        this.power = compound.getInt("power");
        this.grindCounter = compound.getInt("grindCounter");
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
        return new int[]{0, 1, 2};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return index >= 0 && index < SLOTS;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        if (direction != Direction.DOWN || level == null) return false;
        // Shared batch cells: extract known recipe outputs, not every raw input.
        return level.getRecipeManager().getAllRecipesFor(betterwithmods.common.registry.BWMRecipes.MILLING_TYPE.get())
                .stream().anyMatch(recipe -> recipe.getResults().stream()
                        .anyMatch(result -> ItemStack.isSameItemSameTags(result, stack)));
    }

    private final net.minecraft.world.inventory.ContainerData dataAccess = new net.minecraft.world.inventory.ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> grindCounter;
                case 1 -> GRIND_MAX;
                case 2 -> power > 0 ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> grindCounter = value;
                case 2 -> power = value;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.betterwithmods.mill");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new betterwithmods.common.inventory.MillMenu(id, inventory, this, this.dataAccess);
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
