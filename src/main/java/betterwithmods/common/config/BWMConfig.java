package betterwithmods.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BWMConfig {
    public static class Common {
        public final ForgeConfigSpec.BooleanValue debug;
        public final ForgeConfigSpec.IntValue maxPlatformBlocks;
        public final ForgeConfigSpec.DoubleValue crankExhaustion;
        public final ForgeConfigSpec.DoubleValue cauldronNormalSpeed;
        public final ForgeConfigSpec.DoubleValue cauldronStokedSpeed;
        public final ForgeConfigSpec.DoubleValue cauldronMultipleFiresFactor;

        // Hemp
        public final ForgeConfigSpec.DoubleValue hempGrowthChance;
        public final ForgeConfigSpec.DoubleValue hempFertileModifier;
        public final ForgeConfigSpec.DoubleValue hempLampModifier;
        public final ForgeConfigSpec.DoubleValue hempNeighborModifier;
        public final ForgeConfigSpec.BooleanValue dropHempSeeds;

        // Dynamite
        public final ForgeConfigSpec.BooleanValue dynamiteNeedsOffhand;
        public final ForgeConfigSpec.BooleanValue dynamiteBloodThrow;
        public final ForgeConfigSpec.BooleanValue dynamiteNewtonianThrow;
        public final ForgeConfigSpec.BooleanValue dynamiteDispenseLit;

        // Hardcore toggles
        public final ForgeConfigSpec.BooleanValue hcHunger;
        public final ForgeConfigSpec.BooleanValue hcInjury;
        public final ForgeConfigSpec.BooleanValue hcGloom;
        public final ForgeConfigSpec.BooleanValue hcMovement;
        public final ForgeConfigSpec.BooleanValue hcLumber;
        public final ForgeConfigSpec.BooleanValue hcStumping;
        public final ForgeConfigSpec.BooleanValue hcOres;
        public final ForgeConfigSpec.BooleanValue hcStrata;
        public final ForgeConfigSpec.BooleanValue hcTorches;
        public final ForgeConfigSpec.BooleanValue hcSpawn;
        public final ForgeConfigSpec.BooleanValue hcBeds;
        public final ForgeConfigSpec.BooleanValue hcBuckets;
        public final ForgeConfigSpec.BooleanValue hcBeacons;
        public final ForgeConfigSpec.BooleanValue hcTools;
        public final ForgeConfigSpec.BooleanValue hcHardness;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("general");
            debug = builder.comment("Enable debug logging").define("debug", false);
            maxPlatformBlocks = builder.comment("Maximum blocks a platform can lift via pulley").defineInRange("maxPlatformBlocks", 128, 1, 1024);
            crankExhaustion = builder.comment("How much exhaustion turning the crank inflicts on player").defineInRange("crankExhaustion", 6.0, 0.0, 20.0);
            cauldronNormalSpeed = builder.comment("Speed multiplier for unstoked cauldron cooking").defineInRange("cauldronNormalSpeed", 1.0, 0.1, 10.0);
            cauldronStokedSpeed = builder.comment("Speed multiplier for stoked cauldron/crucible cooking").defineInRange("cauldronStokedSpeed", 1.0, 0.1, 10.0);
            cauldronMultipleFiresFactor = builder.comment("Multiple heat sources cooking speed factor").defineInRange("cauldronMultipleFiresFactor", 1.0, 0.1, 10.0);
            builder.pop();

            builder.push("hemp");
            hempGrowthChance = builder.comment("Hemp growth divisor chance (1/X)").defineInRange("growthChance", 15.0, 1.0, 100.0);
            hempFertileModifier = builder.comment("Growth rate multiplier on Fertile Farmland").defineInRange("fertileModifier", 1.33, 0.1, 10.0);
            hempLampModifier = builder.comment("Growth rate multiplier with Light Block above").defineInRange("lampModifier", 1.5, 0.1, 10.0);
            hempNeighborModifier = builder.comment("Growth rate multiplier per adjacent crop").defineInRange("neighborModifier", 1.1, 0.1, 10.0);
            dropHempSeeds = builder.comment("Drop hemp seeds from grass").define("dropHempSeeds", true);
            builder.pop();

            builder.push("dynamite");
            dynamiteNeedsOffhand = builder.comment("Must hold flint and steel in offhand to ignite dynamite").define("needsOffhand", false);
            dynamiteBloodThrow = builder.comment("Hold ignited dynamite in hand before throwing").define("bloodThrow", true);
            dynamiteNewtonianThrow = builder.comment("Dynamite velocity inherits player velocity").define("newtonianThrow", true);
            dynamiteDispenseLit = builder.comment("Dynamite dispensed by dispenser is automatically lit").define("dispenseLit", true);
            builder.pop();

            builder.push("hardcore");
            hcHunger = builder.comment("Enable Hardcore Hunger").define("hcHunger", true);
            hcInjury = builder.comment("Enable Hardcore Injury (movement penalty when injured)").define("hcInjury", true);
            hcGloom = builder.comment("Enable Hardcore Gloom (terror in total darkness)").define("hcGloom", true);
            hcMovement = builder.comment("Enable Hardcore Movement (terrain speed penalties)").define("hcMovement", true);
            hcLumber = builder.comment("Enable Hardcore Lumber (axes needed for wood, drops bark)").define("hcLumber", true);
            hcStumping = builder.comment("Enable Hardcore Stumping (felling trees leaves stump)").define("hcStumping", true);
            hcOres = builder.comment("Enable Hardcore Ores (ores drop piles)").define("hcOres", true);
            hcStrata = builder.comment("Enable Hardcore Strata (rock hardness increases with depth)").define("hcStrata", true);
            hcTorches = builder.comment("Enable Hardcore Torches (torches extinguish)").define("hcTorches", false);
            hcSpawn = builder.comment("Enable Hardcore Spawn (respawn spread radius)").define("hcSpawn", false);
            hcBeds = builder.comment("Enable Hardcore Beds (beds only set spawn/nightmares)").define("hcBeds", true);
            hcBuckets = builder.comment("Enable Hardcore Buckets (water cannot be carried infinitely)").define("hcBuckets", true);
            hcBeacons = builder.comment("Enable Hardcore Beacons (extended beacon mechanics)").define("hcBeacons", true);
            hcTools = builder.comment("Enable Hardcore Tools (tool tier rebalancing)").define("hcTools", true);
            hcHardness = builder.comment("Enable Hardcore Hardness (block hardness tweaks)").define("hcHardness", true);
            builder.pop();
        }
    }

    public static class Client {
        public final ForgeConfigSpec.IntValue statusOffsetX;
        public final ForgeConfigSpec.IntValue statusOffsetY;
        public final ForgeConfigSpec.BooleanValue kidFriendly;
        public final ForgeConfigSpec.DoubleValue generatorRenderDistance;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("gui");
            statusOffsetX = builder.comment("X Offset for Hunger/Injury/Gloom status icons").defineInRange("statusOffsetX", 0, -1000, 1000);
            statusOffsetY = builder.comment("Y Offset for Hunger/Injury/Gloom status icons").defineInRange("statusOffsetY", 0, -1000, 1000);
            kidFriendly = builder.comment("Make animal slaughter and mechanics more kid friendly").define("kidFriendly", false);
            generatorRenderDistance = builder.comment("Render distance for windmill and waterwheel structures").defineInRange("generatorRenderDistance", 256.0, 16.0, 1024.0);
            builder.pop();
        }
    }

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = commonPair.getLeft();
        COMMON_SPEC = commonPair.getRight();

        Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT = clientPair.getLeft();
        CLIENT_SPEC = clientPair.getRight();
    }
}
