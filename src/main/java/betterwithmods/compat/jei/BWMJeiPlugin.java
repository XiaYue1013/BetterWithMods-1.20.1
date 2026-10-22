package betterwithmods.compat.jei;

import betterwithmods.BWMod;
import betterwithmods.compat.jei.category.*;
import betterwithmods.common.recipe.*;
import betterwithmods.common.registry.BWMBlocks;
import betterwithmods.common.registry.BWMRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

@JeiPlugin
public class BWMJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = new ResourceLocation(BWMod.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
            new MillRecipeCategory(helper),
            new CookingPotRecipeCategory(helper, CookingPotRecipeCategory.CAULDRON, "inv.cauldron.name", new ItemStack(BWMBlocks.CAULDRON.get())),
            new CookingPotRecipeCategory(helper, CookingPotRecipeCategory.CRUCIBLE, "inv.crucible.name", new ItemStack(BWMBlocks.CRUCIBLE.get())),
            new SawRecipeCategory(helper),
            new TurntableRecipeCategory(helper),
            new KilnRecipeCategory(helper),
            new SteelAnvilRecipeCategory(helper)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        RecipeManager recipeManager = minecraft.level.getRecipeManager();

        registration.addRecipes(MillRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(BWMRecipes.MILLING_TYPE.get()));
        registration.addRecipes(CookingPotRecipeCategory.CAULDRON, recipeManager.getAllRecipesFor(BWMRecipes.CAULDRON_TYPE.get()));
        registration.addRecipes(CookingPotRecipeCategory.CRUCIBLE, recipeManager.getAllRecipesFor(BWMRecipes.CRUCIBLE_TYPE.get()));
        registration.addRecipes(SawRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(BWMRecipes.SAW_TYPE.get()));
        registration.addRecipes(TurntableRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(BWMRecipes.TURNTABLE_TYPE.get()));
        registration.addRecipes(KilnRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(BWMRecipes.KILN_TYPE.get()));
        registration.addRecipes(SteelAnvilRecipeCategory.RECIPE_TYPE, recipeManager.getAllRecipesFor(BWMRecipes.STEEL_ANVIL_TYPE.get()));
    }

    @Override
    public void registerRecipeTransferHandlers(mezz.jei.api.registration.IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(
            betterwithmods.common.inventory.SteelAnvilMenu.class,
            betterwithmods.common.registry.BWMMenuTypes.STEEL_ANVIL.get(),
            mezz.jei.api.constants.RecipeTypes.CRAFTING,
            1, 16, 17, 36
        );
        registration.addRecipeTransferHandler(
            betterwithmods.common.inventory.SteelAnvilMenu.class,
            betterwithmods.common.registry.BWMMenuTypes.STEEL_ANVIL.get(),
            SteelAnvilRecipeCategory.RECIPE_TYPE,
            1, 16, 17, 36
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BWMBlocks.MILLSTONE.get()), MillRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BWMBlocks.CAULDRON.get()), CookingPotRecipeCategory.CAULDRON);
        registration.addRecipeCatalyst(new ItemStack(BWMBlocks.CRUCIBLE.get()), CookingPotRecipeCategory.CRUCIBLE);
        registration.addRecipeCatalyst(new ItemStack(BWMBlocks.SAW.get()), SawRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BWMBlocks.TURNTABLE.get()), TurntableRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BWMBlocks.HIBACHI.get()), KilnRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(BWMBlocks.STEEL_ANVIL.get()), SteelAnvilRecipeCategory.RECIPE_TYPE);
    }
}
