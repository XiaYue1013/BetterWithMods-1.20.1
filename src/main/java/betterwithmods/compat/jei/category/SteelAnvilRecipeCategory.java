package betterwithmods.compat.jei.category;

import betterwithmods.BWMod;
import betterwithmods.common.recipe.SteelAnvilRecipe;
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

public class SteelAnvilRecipeCategory implements IRecipeCategory<SteelAnvilRecipe> {
    public static final RecipeType<SteelAnvilRecipe> RECIPE_TYPE = RecipeType.create(BWMod.MODID, "steel_anvil", SteelAnvilRecipe.class);
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(BWMod.MODID, "textures/gui/jei/steel_anvil.png");

    private final IDrawable background;
    private final IDrawable icon;

    public SteelAnvilRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(GUI_TEXTURE, 0, 0, 134, 72);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BWMBlocks.STEEL_ANVIL.get()));
    }

    @Override
    public RecipeType<SteelAnvilRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("inv.steel_anvil.name");
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
    public void setRecipe(IRecipeLayoutBuilder builder, SteelAnvilRecipe recipe, IFocusGroup focuses) {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                var slot = builder.addSlot(RecipeIngredientRole.INPUT, x * 18, y * 18);
                if (x < recipe.getWidth() && y < recipe.getHeight()) {
                    int idx = y * recipe.getWidth() + x;
                    if (idx < recipe.getIngredients().size()) {
                        slot.addIngredients(recipe.getIngredients().get(idx));
                    }
                }
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 27).addItemStack(recipe.getResultItem(null));
    }
}
