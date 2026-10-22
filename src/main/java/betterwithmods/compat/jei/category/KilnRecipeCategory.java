package betterwithmods.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.common.recipe.KilnRecipe;
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

public class KilnRecipeCategory implements IRecipeCategory<KilnRecipe> {
    public static final RecipeType<KilnRecipe> RECIPE_TYPE = RecipeType.create(BWMod.MODID, "kiln", KilnRecipe.class);
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BWMod.MODID, "textures/gui/jei/kiln.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated regularFlame;
    private final IDrawableAnimated stokedFlame;

    public KilnRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(GUI_TEXTURE, 0, 0, 145, 80);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BWMBlocks.HIBACHI.get()));

        IDrawableStatic regDrawable = helper.createDrawable(GUI_TEXTURE, 145, 0, 14, 14);
        this.regularFlame = helper.createAnimatedDrawable(regDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);

        IDrawableStatic stokedDrawable = helper.createDrawable(GUI_TEXTURE, 145, 14, 14, 14);
        this.stokedFlame = helper.createAnimatedDrawable(stokedDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public RecipeType<KilnRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("inv.kiln.name");
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
    public void draw(KilnRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        if (recipe.getHeat() > 1) {
            stokedFlame.draw(guiGraphics, 67, 33);
        } else {
            regularFlame.draw(guiGraphics, 67, 33);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, KilnRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 20, 31).addIngredients(recipe.getInput());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 87, 32).addItemStack(recipe.getResultItem(null));

        if (recipe.getHeat() > 1) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 65, 52).addItemStack(new ItemStack(BWMBlocks.HIBACHI.get()));
        } else {
            builder.addSlot(RecipeIngredientRole.CATALYST, 65, 52).addItemStack(new ItemStack(Items.FLINT_AND_STEEL));
        }
    }
}
