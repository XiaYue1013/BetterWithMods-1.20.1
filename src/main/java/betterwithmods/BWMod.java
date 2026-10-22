package betterwithmods;

import betterwithmods.api.capabilities.CapabilityMechanical;
import betterwithmods.common.config.BWMConfig;
import betterwithmods.common.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BWMod.MODID)
public class BWMod {
    public static final String MODID = "betterwithmods";
    public static final String NAME = "Better With Mods";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public BWMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register Configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BWMConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BWMConfig.CLIENT_SPEC);

        // Register Capabilities
        modEventBus.addListener(this::registerCapabilities);

        // Register Deferred Registers
        BWMBlocks.BLOCKS.register(modEventBus);
        BWMItems.ITEMS.register(modEventBus);
        BWMBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        BWMMenuTypes.MENU_TYPES.register(modEventBus);
        BWMEntities.ENTITIES.register(modEventBus);
        BWMRecipes.RECIPE_TYPES.register(modEventBus);
        BWMRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        BWMSounds.SOUNDS.register(modEventBus);
        BWMCreativeTabs.CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        CapabilityMechanical.register(event);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("[BetterWithMods] Initializing Common Systems & Mechanics...");
        event.enqueueWork(betterwithmods.network.BWNetwork::register);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("[BetterWithMods] Initializing Client Setup...");
    }
}
