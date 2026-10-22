package betterwithmods.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.common.recipe.MillingRecipe;
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

public class MillRecipeCategory implements IRecipeCategory<MillingRecipe> {
    public static final RecipeType<MillingRecipe> RECIPE_TYPE = RecipeType.create(BWMod.MODID, "mill", MillingRecipe.class);
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BWMod.MODID, "textures/gui/jei/mill.png");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated gear;

    public MillRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(GUI_TEXTURE, 0, 0, 149, 32);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BWMBlocks.MILLSTONE.get()));
        IDrawableStatic gearDrawable = helper.createDrawable(GUI_TEXTURE, 150, 0, 14, 14);
        this.gear = helper.createAnimatedDrawable(gearDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
    }

    @Override
    public RecipeType<MillingRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("inv.mill.name");
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
    public void draw(MillingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        gear.draw(guiGraphics, 68, 8);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MillingRecipe recipe, IFocusGroup focuses) {
        for (int i = 0; i < 3; i++) {
            var slot = builder.addSlot(RecipeIngredientRole.INPUT, 8 + i * 18, 8);
            if (i < recipe.getIngredients().size()) {
                slot.addIngredients(recipe.getIngredients().get(i));
            }
        }
        for (int i = 0; i < 3; i++) {
            var slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 91 + i * 18, 9);
            if (i < recipe.getResults().size()) {
                slot.addItemStack(recipe.getResults().get(i));
            }
        }
    }
}
