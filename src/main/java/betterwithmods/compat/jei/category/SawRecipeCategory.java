package betterwithmods.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.common.recipe.SawRecipe;
import betterwithmods.common.registry.BWMBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SawRecipeCategory implements IRecipeCategory<SawRecipe> {
    public static final RecipeType<SawRecipe> RECIPE_TYPE = RecipeType.create(BWMod.MODID, "saw", SawRecipe.class);
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BWMod.MODID, "textures/gui/jei/saw.png");

    private final IDrawable background;
    private final IDrawable icon;

    public SawRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(GUI_TEXTURE, 0, 0, 117, 36);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BWMBlocks.SAW.get()));
    }

    @Override
    public RecipeType<SawRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("inv.saw.name");
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
    public void setRecipe(IRecipeLayoutBuilder builder, SawRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 9).addIngredients(recipe.getInput());

        for (int i = 0; i < 3; i++) {
            var slot = builder.addSlot(RecipeIngredientRole.OUTPUT, 58 + i * 18, 10);
            if (i < recipe.getResults().size()) {
                slot.addItemStack(recipe.getResults().get(i));
            }
        }
    }
}
