package betterwithmods.common.blockentity;

import betterwithmods.common.blocks.CookingPotBlock;
import betterwithmods.common.registry.BWMBlockEntities;
import betterwithmods.common.registry.BWMBlocks;
import betterwithmods.common.registry.BWMItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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

public class CookingPotBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int SLOTS = 27;
    private NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);

    private int cookTimer;
    private int heatLevel;
    public static final int COOK_TIME = 200;

    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> new InvWrapper(this));

    public CookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(BWMBlockEntities.COOKING_POT.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CookingPotBlockEntity pot) {
        pot.heatLevel = pot.checkHeatLevel();

        if (pot.heatLevel > 0) {
            if (pot.hasRecipe()) {
                pot.cookTimer++;
                if (pot.cookTimer % 20 == 0) {
                    level.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_UPWARDS_INSIDE, SoundSource.BLOCKS, 0.4f, 1.0f);
                }
                if (pot.cookTimer >= COOK_TIME) {
                    pot.craftRecipe();
                    pot.cookTimer = 0;
                    pot.setChanged();
                }
            } else {
                pot.cookTimer = 0;
            }
        } else {
            pot.cookTimer = 0;
        }
    }

    public int checkHeatLevel() {
        BlockPos below = worldPosition.below();
        BlockState belowState = level.getBlockState(below);
        if (belowState.is(BWMBlocks.STOKED_FLAME.get())) return 2;
        if (belowState.is(Blocks.FIRE) || belowState.is(Blocks.CAMPFIRE) || belowState.is(Blocks.LAVA)) return 1;
        return 0;
    }

    private java.util.Optional<betterwithmods.common.recipe.CookingPotRecipe> getActiveRecipe() {
        if (level == null) return java.util.Optional.empty();
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof CookingPotBlock potBlock)) return java.util.Optional.empty();
        boolean isCrucible = potBlock.getType() == CookingPotBlock.PotType.CRUCIBLE;
        net.minecraft.world.item.crafting.RecipeType<betterwithmods.common.recipe.CookingPotRecipe> type =
                isCrucible ? betterwithmods.common.registry.BWMRecipes.CRUCIBLE_TYPE.get() : betterwithmods.common.registry.BWMRecipes.CAULDRON_TYPE.get();
        java.util.Optional<betterwithmods.common.recipe.CookingPotRecipe> recipe = level.getRecipeManager().getRecipeFor(type, this, level);
        if (recipe.isPresent() && heatLevel >= recipe.get().getHeat()) {
            return recipe;
        }
        return java.util.Optional.empty();
    }

    private boolean hasRecipe() {
        if (getActiveRecipe().isPresent()) return true;

        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof CookingPotBlock potBlock)) return false;

        boolean isCrucible = potBlock.getType() == CookingPotBlock.PotType.CRUCIBLE;
        if (isCrucible) {
            if (heatLevel < 2) return false;
            boolean hasIron = hasItem(Items.IRON_INGOT);
            boolean hasCoalDust = hasItem(BWMItems.COAL_DUST.get()) || hasItem(BWMItems.CHARCOAL_DUST.get());
            boolean hasSoulDust = hasItem(BWMItems.SOUL_DUST.get());
            return hasIron && hasCoalDust && hasSoulDust;
        } else {
            boolean hasLeather = hasItem(Items.LEATHER);
            boolean hasBark = hasItem(BWMItems.BARK_OAK.get()) || hasItem(BWMItems.BARK_SPRUCE.get()) || hasItem(BWMItems.BARK_BIRCH.get());
            if (hasLeather && hasBark) return true;

            boolean hasBone = hasItem(Items.BONE);
            boolean hasFlesh = hasItem(Items.ROTTEN_FLESH);
            return hasBone && hasFlesh;
        }
    }

    private void consumeIngredient(net.minecraft.world.item.crafting.Ingredient in) {
        for (ItemStack s : items) {
            if (in.test(s) && !s.isEmpty()) {
                s.shrink(1);
                return;
            }
        }
    }

    private boolean hasItem(net.minecraft.world.level.ItemLike item) {
        for (ItemStack stack : items) {
            if (stack.is(item.asItem()) && !stack.isEmpty()) return true;
        }
        return false;
    }

    private void craftRecipe() {
        java.util.Optional<betterwithmods.common.recipe.CookingPotRecipe> recipeOpt = getActiveRecipe();
        if (recipeOpt.isPresent()) {
            betterwithmods.common.recipe.CookingPotRecipe recipe = recipeOpt.get();
            for (net.minecraft.world.item.crafting.Ingredient in : recipe.getIngredients()) {
                consumeIngredient(in);
            }
            for (ItemStack out : recipe.getResults()) {
                insertResult(out.copy());
            }
            level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8f, 1.0f);
            return;
        }

        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof CookingPotBlock potBlock)) return;

        boolean isCrucible = potBlock.getType() == CookingPotBlock.PotType.CRUCIBLE;
        if (isCrucible) {
            consumeItem(Items.IRON_INGOT, 1);
            if (!consumeItem(BWMItems.COAL_DUST.get(), 1)) {
                consumeItem(BWMItems.CHARCOAL_DUST.get(), 1);
            }
            consumeItem(BWMItems.SOUL_DUST.get(), 1);
            insertResult(new ItemStack(BWMItems.STEEL_INGOT.get(), 1));
            level.playSound(null, worldPosition, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.8f, 1.0f);
        } else {
            if (hasItem(Items.LEATHER) && (hasItem(BWMItems.BARK_OAK.get()) || hasItem(BWMItems.BARK_SPRUCE.get()))) {
                consumeItem(Items.LEATHER, 1);
                if (!consumeItem(BWMItems.BARK_OAK.get(), 1)) {
                    consumeItem(BWMItems.BARK_SPRUCE.get(), 1);
                }
                insertResult(new ItemStack(BWMItems.TANNED_LEATHER.get(), 1));
            } else if (hasItem(Items.BONE) && hasItem(Items.ROTTEN_FLESH)) {
                consumeItem(Items.BONE, 1);
                consumeItem(Items.ROTTEN_FLESH, 1);
                insertResult(new ItemStack(BWMItems.GLUE.get(), 1));
            }
        }
    }

    private boolean consumeItem(net.minecraft.world.level.ItemLike item, int amount) {
        for (ItemStack stack : items) {
            if (stack.is(item.asItem()) && !stack.isEmpty()) {
                stack.shrink(amount);
                return true;
            }
        }
        return false;
    }

    private void insertResult(ItemStack result) {
        for (int i = 0; i < SLOTS; i++) {
            ItemStack slot = items.get(i);
            if (slot.isEmpty()) {
                items.set(i, result.copy());
                return;
            } else if (ItemStack.isSameItemSameTags(slot, result) && slot.getCount() < slot.getMaxStackSize()) {
                slot.grow(result.getCount());
                return;
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, items);
        compound.putInt("cookTimer", cookTimer);
        compound.putInt("heatLevel", heatLevel);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, items);
        this.cookTimer = compound.getInt("cookTimer");
        this.heatLevel = compound.getInt("heatLevel");
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        int[] slots = new int[SLOTS];
        for (int i = 0; i < SLOTS; i++) slots[i] = i;
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return true;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    private final net.minecraft.world.inventory.ContainerData dataAccess = new net.minecraft.world.inventory.ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookTimer;
                case 1 -> COOK_TIME;
                case 2 -> heatLevel;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cookTimer = value;
                case 2 -> heatLevel = value;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.betterwithmods.cooking_pot");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new betterwithmods.common.inventory.CookingPotMenu(id, inventory, this, this.dataAccess);
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
