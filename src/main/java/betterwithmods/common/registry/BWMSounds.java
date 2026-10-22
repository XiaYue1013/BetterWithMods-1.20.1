package betterwithmods.common.registry;

import betterwithmods.BWMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BWMSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BWMod.MODID);

    public static final RegistryObject<SoundEvent> WOOD_CREAK = register("block.wood.creak");
    public static final RegistryObject<SoundEvent> STONE_GRIND = register("block.stone.grind");
    public static final RegistryObject<SoundEvent> WOOD_BELLOW = register("block.wood.bellow");
    public static final RegistryObject<SoundEvent> WOOD_CHIME = register("block.wood.chime");
    public static final RegistryObject<SoundEvent> METAL_CHIME = register("block.metal.chime");
    public static final RegistryObject<SoundEvent> METAL_HACKSAW = register("block.metal.hacksaw");
    public static final RegistryObject<SoundEvent> PLAYER_OOF = register("entity.player.oof");
    public static final RegistryObject<SoundEvent> MILLSTONE_NETHERRACK = register("block.millstone.netherrack");
    public static final RegistryObject<SoundEvent> SAW_CUT = register("block.saw.cut");
    public static final RegistryObject<SoundEvent> MECHANICAL_OVERPOWER = register("block.mechanical.overpower");
    public static final RegistryObject<SoundEvent> BLOODWOOD_BREAK = register("block.bloodwood.break");
    public static final RegistryObject<SoundEvent> AMBIENT_GLOOM = register("ambient.gloom");
    public static final RegistryObject<SoundEvent> AMBIENT_SPOOK = register("ambient.spook");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BWMod.MODID, name)));
    }
}
