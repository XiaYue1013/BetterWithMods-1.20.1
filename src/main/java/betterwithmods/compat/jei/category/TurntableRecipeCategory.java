package betterwithmods.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.common.recipe.TurntableRecipe;
import betterwithmods.common.registry.BWMBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TurntableRecipeCategory implements IRecipeCategory<TurntableRecipe> {
    public static final RecipeType<TurntableRecipe> RECIPE_TYPE = RecipeType.create(BWMod.MODID, "turntable", TurntableRecipe.class);
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BWMod.MODID, "textures/gui/jei/turntable.png");

    private final IDrawable background;
    private final IDrawable icon;

    public TurntableRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(GUI_TEXTURE, 0, 0, 76, 50);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BWMBlocks.TURNTABLE.get()));
    }

    @Override
    public RecipeType<TurntableRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("inv.turntable.name");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TurntableRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 9).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 32, 9).addItemStack(recipe.getResultItem(null));
        builder.addSlot(RecipeIngredientRole.CATALYST, 5, 27).addItemStack(new ItemStack(BWMBlocks.TURNTABLE.get()));

        NonNullList<ItemStack> drops = recipe.getDrops();
        for (int i = 0; i < 2; i++) {
            var slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 33 + i * 18, 30);
            if (i < drops.size()) {
                slot.addItemStack(drops.get(i));
            }
        }
    }
}
