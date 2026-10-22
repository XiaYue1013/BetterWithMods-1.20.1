package betterwithmods.common.registry;

import betterwithmods.BWMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BWMCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BWMod.MODID);

    public static final RegistryObject<CreativeModeTab> BWM_TAB = CREATIVE_TABS.register("bwm_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.betterwithmods"))
                    .icon(() -> new ItemStack(BWMItems.AXLE_WOODEN.get()))
                    .displayItems((parameters, output) -> {
                        BWMItems.ITEMS.getEntries().forEach(itemReg -> output.accept(itemReg.get()));
                    })
                    .build());
}
