package betterwithmods.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.common.recipe.CookingPotRecipe;
import betterwithmods.common.registry.BWMBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CookingPotRecipeCategory implements IRecipeCategory<CookingPotRecipe> {
    public static final RecipeType<CookingPotRecipe> CAULDRON = RecipeType.create(BWMod.MODID, "cauldron", CookingPotRecipe.class);
    public static final RecipeType<CookingPotRecipe> CRUCIBLE = RecipeType.create(BWMod.MODID, "crucible", CookingPotRecipe.class);

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BWMod.MODID, "textures/gui/jei/cooking.png");

    private final RecipeType<CookingPotRecipe> recipeType;
    private final String titleKey;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated regularFlame;
    private final IDrawableAnimated stokedFlame;

    public CookingPotRecipeCategory(IGuiHelper helper, RecipeType<CookingPotRecipe> recipeType, String titleKey, ItemStack iconStack) {
        this.recipeType = recipeType;
        this.titleKey = titleKey;
        this.background = helper.createDrawable(GUI_TEXTURE, 0, 0, 165, 57);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, iconStack);

        IDrawableStatic regDrawable = helper.createDrawable(GUI_TEXTURE, 166, 0, 14, 14);
        this.regularFlame = helper.createAnimatedDrawable(regDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);

        IDrawableStatic stokedDrawable = helper.createDrawable(GUI_TEXTURE, 166, 14, 14, 14);
        this.stokedFlame = helper.createAnimatedDrawable(stokedDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public RecipeType<CookingPotRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(titleKey);
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
    public void draw(CookingPotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (recipe.getHeat() > 1) {
            stokedFlame.draw(guiGraphics, 77, 22);
        } else if (recipe.getHeat() == 1) {
            regularFlame.draw(guiGraphics, 77, 22);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CookingPotRecipe recipe, IFocusGroup focuses) {
        // 3x3 input grid
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int idx = row * 3 + col;
                var slot = builder.addSlot(RecipeIngredientRole.INPUT, 8 + col * 18, 3 + row * 18);
                if (idx < recipe.getIngredients().size()) {
                    slot.addIngredients(recipe.getIngredients().get(idx));
                }
            }
        }

        // 3x3 output grid
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int idx = row * 3 + col;
                var slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 107 + col * 18, 4 + row * 18);
                if (idx < recipe.getResults().size()) {
                    slot.addItemStack(recipe.getResults().get(idx));
                }
            }
        }

        // Heat source slot at (76, 39)
        if (recipe.getHeat() > 1) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 76, 39).addItemStack(new ItemStack(BWMBlocks.HIBACHI.get()));
        } else if (recipe.getHeat() == 1) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 76, 39).addItemStack(new ItemStack(Items.FLINT_AND_STEEL));
        }
    }
}
