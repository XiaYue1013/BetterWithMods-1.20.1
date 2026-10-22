package betterwithmods.common.registry;

import betterwithmods.BWMod;
import betterwithmods.common.inventory.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BWMMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, BWMod.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = MENU_TYPES;

    public static final RegistryObject<MenuType<MillMenu>> MILL =
            MENUS.register("mill", () -> IForgeMenuType.create((windowId, inv, data) -> new MillMenu(windowId, inv)));

    public static final RegistryObject<MenuType<CookingPotMenu>> COOKING_POT =
            MENUS.register("cooking_pot", () -> IForgeMenuType.create((windowId, inv, data) -> new CookingPotMenu(windowId, inv)));

    public static final RegistryObject<MenuType<FilteredHopperMenu>> FILTERED_HOPPER =
            MENUS.register("filtered_hopper", () -> IForgeMenuType.create((windowId, inv, data) -> new FilteredHopperMenu(windowId, inv)));

    public static final RegistryObject<MenuType<BlockDispenserMenu>> BLOCK_DISPENSER =
            MENUS.register("block_dispenser", () -> IForgeMenuType.create((windowId, inv, data) -> new BlockDispenserMenu(windowId, inv)));

    public static final RegistryObject<MenuType<PulleyMenu>> PULLEY =
            MENUS.register("pulley", () -> IForgeMenuType.create((windowId, inv, data) -> new PulleyMenu(windowId, inv)));

    public static final RegistryObject<MenuType<SteelAnvilMenu>> STEEL_ANVIL =
            MENUS.register("steel_anvil", () -> IForgeMenuType.create((windowId, inv, data) -> new SteelAnvilMenu(windowId, inv)));
}
