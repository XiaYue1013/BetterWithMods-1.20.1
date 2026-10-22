package betterwithmods.common.registry;

import betterwithmods.BWMod;
import betterwithmods.common.blocks.*;
import betterwithmods.common.blocks.mechanical.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BWMBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BWMod.MODID);

    // ==========================================
    // Mechanical Power Generation & Transmission
    // ==========================================
    public static final RegistryObject<Block> WOODEN_AXLE = BLOCKS.register("wooden_axle",
            () -> new AxleBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.WOOD).noOcclusion(), false));
    public static final RegistryObject<Block> STEEL_AXLE = BLOCKS.register("steel_axle",
            () -> new AxleBlock(BlockBehaviour.Properties.of().strength(100.0f, 4000.0f).sound(SoundType.METAL).noOcclusion(), true));

    public static final RegistryObject<Block> WOODEN_GEARBOX = BLOCKS.register("wooden_gearbox",
            () -> new GearboxBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.WOOD), false));
    public static final RegistryObject<Block> STEEL_GEARBOX = BLOCKS.register("steel_gearbox",
            () -> new GearboxBlock(BlockBehaviour.Properties.of().strength(100.0f, 4000.0f).sound(SoundType.METAL), true));

    public static final RegistryObject<Block> WOODEN_BROKEN_GEARBOX = BLOCKS.register("wooden_broken_gearbox",
            () -> new BrokenGearboxBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.WOOD), false));
    public static final RegistryObject<Block> STEEL_BROKEN_GEARBOX = BLOCKS.register("steel_broken_gearbox",
            () -> new BrokenGearboxBlock(BlockBehaviour.Properties.of().strength(100.0f, 4000.0f).sound(SoundType.METAL), true));

    public static final RegistryObject<Block> HAND_CRANK = BLOCKS.register("hand_crank",
            () -> new HandCrankBlock(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> CREATIVE_GENERATOR = BLOCKS.register("creative_generator",
            () -> new CreativeGeneratorBlock(BlockBehaviour.Properties.of().strength(5.0f).sound(SoundType.METAL)));

    // ==========================================
    // Mechanical Processing Machinery
    // ==========================================
    public static final RegistryObject<Block> MILLSTONE = BLOCKS.register("millstone",
            () -> new MillstoneBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.STONE).noOcclusion()));

    public static final RegistryObject<Block> SAW = BLOCKS.register("saw",
            () -> new SawBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.WOOD).noOcclusion(), false));
    public static final RegistryObject<Block> STEEL_SAW = BLOCKS.register("steel_saw",
            () -> new SawBlock(BlockBehaviour.Properties.of().strength(10.0f).sound(SoundType.METAL).noOcclusion(), true));

    public static final RegistryObject<Block> TURNTABLE = BLOCKS.register("turntable",
            () -> new TurntableBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> SCREW_PUMP = BLOCKS.register("screw_pump",
            () -> new ScrewPumpBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> BELLOWS = BLOCKS.register("bellows",
            () -> new BellowsBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> PULLEY = BLOCKS.register("pulley",
            () -> new PulleyBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> BLOCK_DISPENSER = BLOCKS.register("block_dispenser",
            () -> new BlockDispenserBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.STONE)));

    public static final RegistryObject<Block> FILTERED_HOPPER = BLOCKS.register("hopper",
            () -> new FilteredHopperBlock(BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> STEEL_ANVIL = BLOCKS.register("steel_anvil",
            () -> new SteelAnvilBlock(BlockBehaviour.Properties.of().strength(5.0f, 2000.0f).sound(SoundType.ANVIL).noOcclusion()));

    public static final RegistryObject<Block> INFERNAL_ENCHANTER = BLOCKS.register("infernal_enchanter",
            () -> new InfernalEnchanterBlock(BlockBehaviour.Properties.of().strength(5.0f, 2000.0f).sound(SoundType.STONE).noOcclusion()));

    // ==========================================
    // Cooking Pots & Fire
    // ==========================================
    public static final RegistryObject<Block> CAULDRON = BLOCKS.register("cauldron",
            () -> new CookingPotBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.METAL).noOcclusion(), CookingPotBlock.PotType.CAULDRON));
    public static final RegistryObject<Block> CRUCIBLE = BLOCKS.register("crucible",
            () -> new CookingPotBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.STONE).noOcclusion(), CookingPotBlock.PotType.CRUCIBLE));
    public static final RegistryObject<Block> DRAGON_VESSEL = BLOCKS.register("dragon_vessel",
            () -> new CookingPotBlock(BlockBehaviour.Properties.of().strength(4.0f).sound(SoundType.STONE).noOcclusion(), CookingPotBlock.PotType.DRAGON_VESSEL));

    public static final RegistryObject<Block> HIBACHI = BLOCKS.register("hibachi",
            () -> new HibachiBlock(BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.STONE)));
    public static final RegistryObject<Block> STOKED_FLAME = BLOCKS.register("stoked_flame",
            () -> new StokedFlameBlock(BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(s -> 15).sound(SoundType.WOOL)));

    // ==========================================
    // Utilities, Agriculture & Construction
    // ==========================================
    public static final RegistryObject<Block> ANCHOR = BLOCKS.register("anchor",
            () -> new AnchorBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> ROPE = BLOCKS.register("rope",
            () -> new RopeBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5f).sound(SoundType.WOOL)));

    public static final RegistryObject<Block> WICKER = BLOCKS.register("wicker",
            () -> new Block(BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> GRATE = BLOCKS.register("grate",
            () -> new GrateBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> SLATS = BLOCKS.register("slats",
            () -> new GrateBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> PLANTER = BLOCKS.register("planter",
            () -> new PlanterBlock(BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.STONE)));
    public static final RegistryObject<Block> VASE = BLOCKS.register("vase",
            () -> new VaseBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> URN = BLOCKS.register("urn",
            () -> new UrnBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.STONE).noOcclusion()));
    public static final RegistryObject<Block> UNFIRED_POTTERY = BLOCKS.register("unfired_pottery",
            () -> new UnfiredPotteryBlock(BlockBehaviour.Properties.of().strength(0.6f).sound(SoundType.GRAVEL).noOcclusion()));

    public static final RegistryObject<Block> HEMP = BLOCKS.register("hemp",
            () -> new HempCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP)));

    public static final RegistryObject<Block> DETECTOR = BLOCKS.register("detector",
            () -> new DetectorBlock(BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.STONE)));
    public static final RegistryObject<Block> BUDDY_BLOCK = BLOCKS.register("buddy_block",
            () -> new BuddyBlock(BlockBehaviour.Properties.of().strength(3.0f).sound(SoundType.STONE)));
    public static final RegistryObject<Block> LENS = BLOCKS.register("lens",
            () -> new LensBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.GLASS).noOcclusion()));

    public static final RegistryObject<Block> MINING_CHARGE = BLOCKS.register("mining_charge",
            () -> new MiningChargeBlock(BlockBehaviour.Properties.of().strength(0.0f).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> FERTILE_FARMLAND = BLOCKS.register("fertile_farmland",
            () -> new FertileFarmlandBlock(BlockBehaviour.Properties.of().strength(0.6f).sound(SoundType.GRAVEL)));
    public static final RegistryObject<Block> STEEL_BLOCK = BLOCKS.register("steel_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(10.0f, 1000.0f).sound(SoundType.METAL)));

    public static final RegistryObject<Block> BLOOD_LOG = BLOCKS.register("blood_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> BLOOD_LEAVES = BLOCKS.register("blood_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2f).sound(SoundType.GRASS).noOcclusion().isSuffocating((s, g, p) -> false).isViewBlocking((s, g, p) -> false)));
    public static final RegistryObject<Block> BLOOD_SAPLING = BLOCKS.register("blood_sapling",
            () -> new Block(BlockBehaviour.Properties.of().noCollission().instabreak().sound(SoundType.GRASS)));
    public static final RegistryObject<Block> NETHER_CLAY = BLOCKS.register("nether_clay",
            () -> new Block(BlockBehaviour.Properties.of().strength(0.6f).sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> WATERWHEEL = BLOCKS.register("waterwheel",
            () -> new WaterwheelBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> WINDMILL = BLOCKS.register("windmill",
            () -> new WindmillBlock(BlockBehaviour.Properties.of().strength(2.0f).sound(SoundType.WOOD).noOcclusion()));
}

