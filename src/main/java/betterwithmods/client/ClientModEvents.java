package betterwithmods.client;

import betterwithmods.BWMod;
import betterwithmods.client.gui.*;
import betterwithmods.common.registry.BWMMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BWMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(BWMMenuTypes.MILL.get(), MillScreen::new);
            MenuScreens.register(BWMMenuTypes.COOKING_POT.get(), CookingPotScreen::new);
            MenuScreens.register(BWMMenuTypes.FILTERED_HOPPER.get(), FilteredHopperScreen::new);
            MenuScreens.register(BWMMenuTypes.BLOCK_DISPENSER.get(), BlockDispenserScreen::new);
            MenuScreens.register(BWMMenuTypes.PULLEY.get(), PulleyScreen::new);
            MenuScreens.register(BWMMenuTypes.STEEL_ANVIL.get(), SteelAnvilScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(betterwithmods.common.registry.BWMBlockEntities.WATERWHEEL.get(), betterwithmods.client.renderer.WaterwheelRenderer::new);
        event.registerBlockEntityRenderer(betterwithmods.common.registry.BWMBlockEntities.WINDMILL.get(), betterwithmods.client.renderer.WindmillRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(betterwithmods.client.renderer.WaterwheelRenderer.LAYER_LOCATION, betterwithmods.client.renderer.WaterwheelRenderer::createBodyLayer);
        event.registerLayerDefinition(betterwithmods.client.renderer.WindmillRenderer.HORIZONTAL_LAYER, betterwithmods.client.renderer.WindmillRenderer::createHorizontalLayer);
        event.registerLayerDefinition(betterwithmods.client.renderer.WindmillRenderer.VERTICAL_LAYER, betterwithmods.client.renderer.WindmillRenderer::createVerticalLayer);
    }

    @SubscribeEvent
    public static void registerRecipeBookCategories(net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent event) {
        event.registerRecipeCategoryFinder(betterwithmods.common.registry.BWMRecipes.MILLING_TYPE.get(), r -> net.minecraft.client.RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(betterwithmods.common.registry.BWMRecipes.CAULDRON_TYPE.get(), r -> net.minecraft.client.RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(betterwithmods.common.registry.BWMRecipes.CRUCIBLE_TYPE.get(), r -> net.minecraft.client.RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(betterwithmods.common.registry.BWMRecipes.KILN_TYPE.get(), r -> net.minecraft.client.RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(betterwithmods.common.registry.BWMRecipes.SAW_TYPE.get(), r -> net.minecraft.client.RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(betterwithmods.common.registry.BWMRecipes.TURNTABLE_TYPE.get(), r -> net.minecraft.client.RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(betterwithmods.common.registry.BWMRecipes.STEEL_ANVIL_TYPE.get(), r -> net.minecraft.client.RecipeBookCategories.UNKNOWN);
    }
}
