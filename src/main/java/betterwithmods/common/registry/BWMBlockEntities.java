package betterwithmods.common.registry;

import betterwithmods.BWMod;
import betterwithmods.common.blockentity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BWMBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BWMod.MODID);

    public static final RegistryObject<BlockEntityType<AxleBlockEntity>> AXLE =
            BLOCK_ENTITIES.register("axle", () -> BlockEntityType.Builder.of(
                    AxleBlockEntity::new,
                    BWMBlocks.WOODEN_AXLE.get(),
                    BWMBlocks.STEEL_AXLE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<GearboxBlockEntity>> GEARBOX =
            BLOCK_ENTITIES.register("gearbox", () -> BlockEntityType.Builder.of(
                    GearboxBlockEntity::new,
                    BWMBlocks.WOODEN_GEARBOX.get(),
                    BWMBlocks.STEEL_GEARBOX.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<HandCrankBlockEntity>> CRANK =
            BLOCK_ENTITIES.register("crank", () -> BlockEntityType.Builder.of(
                    HandCrankBlockEntity::new,
                    BWMBlocks.HAND_CRANK.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<CreativeGeneratorBlockEntity>> CREATIVE_GEN =
            BLOCK_ENTITIES.register("creative_generator", () -> BlockEntityType.Builder.of(
                    CreativeGeneratorBlockEntity::new,
                    BWMBlocks.CREATIVE_GENERATOR.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<MillstoneBlockEntity>> MILLSTONE =
            BLOCK_ENTITIES.register("millstone", () -> BlockEntityType.Builder.of(
                    MillstoneBlockEntity::new,
                    BWMBlocks.MILLSTONE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<SawBlockEntity>> SAW =
            BLOCK_ENTITIES.register("saw", () -> BlockEntityType.Builder.of(
                    SawBlockEntity::new,
                    BWMBlocks.SAW.get(),
                    BWMBlocks.STEEL_SAW.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<TurntableBlockEntity>> TURNTABLE =
            BLOCK_ENTITIES.register("turntable", () -> BlockEntityType.Builder.of(
                    TurntableBlockEntity::new,
                    BWMBlocks.TURNTABLE.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<ScrewPumpBlockEntity>> PUMP =
            BLOCK_ENTITIES.register("pump", () -> BlockEntityType.Builder.of(
                    ScrewPumpBlockEntity::new,
                    BWMBlocks.SCREW_PUMP.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<BellowsBlockEntity>> BELLOWS =
            BLOCK_ENTITIES.register("bellows", () -> BlockEntityType.Builder.of(
                    BellowsBlockEntity::new,
                    BWMBlocks.BELLOWS.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<PulleyBlockEntity>> PULLEY =
            BLOCK_ENTITIES.register("pulley", () -> BlockEntityType.Builder.of(
                    PulleyBlockEntity::new,
                    BWMBlocks.PULLEY.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<BlockDispenserBlockEntity>> BLOCK_DISPENSER =
            BLOCK_ENTITIES.register("block_dispenser", () -> BlockEntityType.Builder.of(
                    BlockDispenserBlockEntity::new,
                    BWMBlocks.BLOCK_DISPENSER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<FilteredHopperBlockEntity>> FILTERED_HOPPER =
            BLOCK_ENTITIES.register("hopper", () -> BlockEntityType.Builder.of(
                    FilteredHopperBlockEntity::new,
                    BWMBlocks.FILTERED_HOPPER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<CookingPotBlockEntity>> COOKING_POT =
            BLOCK_ENTITIES.register("cooking_pot", () -> BlockEntityType.Builder.of(
                    CookingPotBlockEntity::new,
                    BWMBlocks.CAULDRON.get(),
                    BWMBlocks.CRUCIBLE.get(),
                    BWMBlocks.DRAGON_VESSEL.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<SteelAnvilBlockEntity>> STEEL_ANVIL =
            BLOCK_ENTITIES.register("steel_anvil", () -> BlockEntityType.Builder.of(
                    SteelAnvilBlockEntity::new,
                    BWMBlocks.STEEL_ANVIL.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<InfernalEnchanterBlockEntity>> INFERNAL_ENCHANTER =
            BLOCK_ENTITIES.register("infernal_enchanter", () -> BlockEntityType.Builder.of(
                    InfernalEnchanterBlockEntity::new,
                    BWMBlocks.INFERNAL_ENCHANTER.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<WaterwheelBlockEntity>> WATERWHEEL =
            BLOCK_ENTITIES.register("waterwheel", () -> BlockEntityType.Builder.of(
                    WaterwheelBlockEntity::new,
                    BWMBlocks.WATERWHEEL.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<WindmillBlockEntity>> WINDMILL =
            BLOCK_ENTITIES.register("windmill", () -> BlockEntityType.Builder.of(
                    WindmillBlockEntity::new,
                    BWMBlocks.WINDMILL.get()
            ).build(null));
}

