package betterwithmods.common.registry;

import betterwithmods.BWMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import betterwithmods.common.items.AxleGeneratorItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BWMItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BWMod.MODID);

    // ==========================================
    // Food Properties
    // ==========================================
    public static final FoodProperties DONUT_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.25f).build();
    public static final FoodProperties CREEPER_OYSTER_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).effect(() -> new MobEffectInstance(MobEffects.POISON, 100, 0), 1.0f).build();
    public static final FoodProperties RAW_EGG_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f).build();
    public static final FoodProperties COOKED_EGG_FOOD = new FoodProperties.Builder().nutrition(3).saturationMod(0.5f).build();
    public static final FoodProperties RAW_SCRAMBLED_EGG_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f).build();
    public static final FoodProperties COOKED_SCRAMBLED_EGG_FOOD = new FoodProperties.Builder().nutrition(5).saturationMod(0.5f).build();
    public static final FoodProperties RAW_OMELET_FOOD = new FoodProperties.Builder().nutrition(3).saturationMod(0.5f).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f).build();
    public static final FoodProperties COOKED_OMELET_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(1.0f).build();
    public static final FoodProperties HAM_AND_EGGS_FOOD = new FoodProperties.Builder().nutrition(6).saturationMod(1.0f).build();
    public static final FoodProperties TASTY_SANDWICH_FOOD = new FoodProperties.Builder().nutrition(7).saturationMod(0.7f).build();
    public static final FoodProperties BEEF_DINNER_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.6f).build();
    public static final FoodProperties BEEF_POTATOES_FOOD = new FoodProperties.Builder().nutrition(7).saturationMod(0.5f).build();
    public static final FoodProperties CHICKEN_SOUP_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.6f).build();
    public static final FoodProperties CHOCOLATE_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).build();
    public static final FoodProperties CHOWDER_FOOD = new FoodProperties.Builder().nutrition(5).saturationMod(0.5f).build();
    public static final FoodProperties RAW_KEBAB_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f).build();
    public static final FoodProperties COOKED_KEBAB_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.4f).build();
    public static final FoodProperties HEARTY_STEW_FOOD = new FoodProperties.Builder().nutrition(10).saturationMod(0.8f).build();
    public static final FoodProperties PORK_DINNER_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.6f).build();
    public static final FoodProperties WOLF_CHOP_FOOD = new FoodProperties.Builder().nutrition(3).saturationMod(0.3f).meat().build();
    public static final FoodProperties COOKED_WOLF_CHOP_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.3f).meat().build();
    public static final FoodProperties KIBBLE_FOOD = new FoodProperties.Builder().nutrition(3).saturationMod(0.0f).meat().effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3f).build();
    public static final FoodProperties APPLE_PIE_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.3f).build();
    public static final FoodProperties MYSTERY_MEAT_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).meat().build();
    public static final FoodProperties COOKED_MYSTERY_MEAT_FOOD = new FoodProperties.Builder().nutrition(6).saturationMod(0.8f).meat().build();
    public static final FoodProperties BAT_WING_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.3f).build();
    public static final FoodProperties COOKED_BAT_WING_FOOD = new FoodProperties.Builder().nutrition(4).saturationMod(0.6f).build();

    // ==========================================
    // Tier Materials
    // ==========================================
    public static final Tier SOULFORGED_STEEL_TIER = new Tier() {
        @Override public int getUses() { return 2250; }
        @Override public float getSpeed() { return 10.0f; }
        @Override public float getAttackDamageBonus() { return 3.0f; }
        @Override public int getLevel() { return 4; }
        @Override public int getEnchantmentValue() { return 22; }
        @Override public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() {
            return net.minecraft.world.item.crafting.Ingredient.of(STEEL_INGOT.get());
        }
    };

    // ==========================================
    // Materials
    // ==========================================
    public static final RegistryObject<Item> GEAR = registerItem("gear", () -> new Item(new Item.Properties()) {
        @Override public int getBurnTime(ItemStack itemStack, net.minecraft.world.item.crafting.RecipeType<?> recipeType) { return 18; }
    });
    public static final RegistryObject<Item> STEEL_GEAR = registerItem("steel_gear");
    public static final RegistryObject<Item> NETHERCOAL = registerItem("nethercoal", () -> new Item(new Item.Properties()) {
        @Override public int getBurnTime(ItemStack itemStack, net.minecraft.world.item.crafting.RecipeType<?> recipeType) { return 3200; }
    });
    public static final RegistryObject<Item> HEMP = registerItem("hemp");
    public static final RegistryObject<Item> HEMP_FIBERS = registerItem("hemp_fibers");
    public static final RegistryObject<Item> HEMP_CLOTH = registerItem("hemp_cloth");
    public static final RegistryObject<Item> DUNG = registerItem("dung");
    public static final RegistryObject<Item> TANNED_LEATHER = registerItem("tanned_leather");
    public static final RegistryObject<Item> SCOURED_LEATHER = registerItem("scoured_leather");
    public static final RegistryObject<Item> LEATHER_STRAP = registerItem("leather_strap");
    public static final RegistryObject<Item> LEATHER_BELT = registerItem("leather_belt");
    public static final RegistryObject<Item> WOOD_BLADE = registerItem("wood_blade", () -> new Item(new Item.Properties()) {
        @Override public int getBurnTime(ItemStack itemStack, net.minecraft.world.item.crafting.RecipeType<?> recipeType) { return 37; }
    });
    public static final RegistryObject<Item> WINDMILL_BLADE = registerItem("windmill_blade", () -> new Item(new Item.Properties()) {
        @Override public int getBurnTime(ItemStack itemStack, net.minecraft.world.item.crafting.RecipeType<?> recipeType) { return 75; }
    });
    public static final RegistryObject<Item> GLUE = registerItem("glue");
    public static final RegistryObject<Item> TALLOW = registerItem("tallow");
    public static final RegistryObject<Item> STEEL_INGOT = registerItem("steel_ingot");
    public static final RegistryObject<Item> STEEL_NUGGET = registerItem("steel_nugget");
    public static final RegistryObject<Item> STEEL_PLATE = registerItem("steel_plate");
    public static final RegistryObject<Item> GROUND_NETHERRACK = registerItem("ground_netherrack");
    public static final RegistryObject<Item> HELLFIRE_DUST = registerItem("hellfire_dust");
    public static final RegistryObject<Item> CONCENTRATED_HELLFIRE = registerItem("concentrated_hellfire");
    public static final RegistryObject<Item> COAL_DUST = registerItem("coal_dust");
    public static final RegistryObject<Item> CHARCOAL_DUST = registerItem("charcoal_dust");
    public static final RegistryObject<Item> FILAMENT = registerItem("filament");
    public static final RegistryObject<Item> POLISHED_LAPIS = registerItem("polished_lapis");
    public static final RegistryObject<Item> POTASH = registerItem("potash");
    public static final RegistryObject<Item> SAWDUST = registerItem("sawdust", () -> new Item(new Item.Properties()) {
        @Override public int getBurnTime(ItemStack itemStack, net.minecraft.world.item.crafting.RecipeType<?> recipeType) { return 25; }
    });
    public static final RegistryObject<Item> SOUL_DUST = registerItem("soul_dust");
    public static final RegistryObject<Item> SCREW = registerItem("screw");
    public static final RegistryObject<Item> BRIMSTONE = registerItem("brimstone");
    public static final RegistryObject<Item> NITER = registerItem("niter");
    public static final RegistryObject<Item> ELEMENT = registerItem("element");
    public static final RegistryObject<Item> FUSE = registerItem("fuse");
    public static final RegistryObject<Item> BLASTING_OIL = registerItem("blasting_oil");
    public static final RegistryObject<Item> CUT_LEATHER = registerItem("leather_cut");
    public static final RegistryObject<Item> CUT_TANNED_LEATHER = registerItem("tanned_leather_cut");
    public static final RegistryObject<Item> CUT_SCOURED_LEATHER = registerItem("scoured_leather_cut");
    public static final RegistryObject<Item> REDSTONE_LATCH = registerItem("redstone_latch");
    public static final RegistryObject<Item> NETHER_SLUDGE = registerItem("nether_sludge");
    public static final RegistryObject<Item> HAFT = registerItem("haft", () -> new Item(new Item.Properties()) {
        @Override public int getBurnTime(ItemStack itemStack, net.minecraft.world.item.crafting.RecipeType<?> recipeType) { return 150; }
    });
    public static final RegistryObject<Item> SOUL_FLUX = registerItem("soul_flux");
    public static final RegistryObject<Item> ENDER_SLAG = registerItem("ender_slag");
    public static final RegistryObject<Item> ENDER_OCULAR = registerItem("ender_ocular");
    public static final RegistryObject<Item> PADDING = registerItem("padding");
    public static final RegistryObject<Item> ARMOR_PLATE = registerItem("armor_plate");
    public static final RegistryObject<Item> BROADHEAD = registerItem("broadhead");
    public static final RegistryObject<Item> COCOA_POWDER = registerItem("cocoa_powder");
    public static final RegistryObject<Item> DIAMOND_INGOT = registerItem("diamond_ingot");
    public static final RegistryObject<Item> DIAMOND_NUGGET = registerItem("diamond_nugget");
    public static final RegistryObject<Item> CHAIN_MAIL = registerItem("chain_mail");
    public static final RegistryObject<Item> STEEL_SPRING = registerItem("steel_spring");
    public static final RegistryObject<Item> SOAP = registerItem("soap");
    public static final RegistryObject<Item> WITCH_WART = registerItem("witch_wart");
    public static final RegistryObject<Item> MYSTERY_GLAND = registerItem("mystery_gland");
    public static final RegistryObject<Item> POISON_SAC = registerItem("poison_sac");
    public static final RegistryObject<Item> FLOUR = registerItem("flour");

    // ==========================================
    // Barks
    // ==========================================
    public static final RegistryObject<Item> BARK_OAK = registerBark("bark_oak");
    public static final RegistryObject<Item> BARK_SPRUCE = registerBark("bark_spruce");
    public static final RegistryObject<Item> BARK_BIRCH = registerBark("bark_birch");
    public static final RegistryObject<Item> BARK_JUNGLE = registerBark("bark_jungle");
    public static final RegistryObject<Item> BARK_ACACIA = registerBark("bark_acacia");
    public static final RegistryObject<Item> BARK_DARK_OAK = registerBark("bark_dark_oak");
    public static final RegistryObject<Item> BARK_BLOOD = registerBark("bark_blood");

    // ==========================================
    // Foods
    // ==========================================
    public static final RegistryObject<Item> DONUT = registerItem("donut", () -> new Item(new Item.Properties().food(DONUT_FOOD)));
    public static final RegistryObject<Item> CREEPER_OYSTER = registerItem("creeper_oyster", () -> new Item(new Item.Properties().food(CREEPER_OYSTER_FOOD)));
    public static final RegistryObject<Item> RAW_EGG = registerItem("raw_egg", () -> new Item(new Item.Properties().food(RAW_EGG_FOOD)));
    public static final RegistryObject<Item> COOKED_EGG = registerItem("cooked_egg", () -> new Item(new Item.Properties().food(COOKED_EGG_FOOD)));
    public static final RegistryObject<Item> RAW_SCRAMBLED_EGG = registerItem("raw_scrambled_egg", () -> new Item(new Item.Properties().food(RAW_SCRAMBLED_EGG_FOOD)));
    public static final RegistryObject<Item> COOKED_SCRAMBLED_EGG = registerItem("cooked_scrambled_egg", () -> new Item(new Item.Properties().food(COOKED_SCRAMBLED_EGG_FOOD)));
    public static final RegistryObject<Item> RAW_OMELET = registerItem("raw_omelet", () -> new Item(new Item.Properties().food(RAW_OMELET_FOOD)));
    public static final RegistryObject<Item> COOKED_OMELET = registerItem("cooked_omelet", () -> new Item(new Item.Properties().food(COOKED_OMELET_FOOD)));
    public static final RegistryObject<Item> HAM_AND_EGGS = registerItem("ham_and_eggs", () -> new Item(new Item.Properties().food(HAM_AND_EGGS_FOOD)));
    public static final RegistryObject<Item> TASTY_SANDWICH = registerItem("tasty_sandwich", () -> new Item(new Item.Properties().food(TASTY_SANDWICH_FOOD)));
    public static final RegistryObject<Item> BEEF_DINNER = registerItem("beef_dinner", () -> new Item(new Item.Properties().food(BEEF_DINNER_FOOD)));
    public static final RegistryObject<Item> BEEF_POTATOES = registerItem("beef_potatoes", () -> new Item(new Item.Properties().food(BEEF_POTATOES_FOOD)));
    public static final RegistryObject<Item> CHICKEN_SOUP = registerItem("chicken_soup", () -> new BowlFoodItem(new Item.Properties().food(CHICKEN_SOUP_FOOD).stacksTo(64)));
    public static final RegistryObject<Item> CHOCOLATE = registerItem("chocolate", () -> new Item(new Item.Properties().food(CHOCOLATE_FOOD)));
    public static final RegistryObject<Item> CHOWDER = registerItem("chowder", () -> new BowlFoodItem(new Item.Properties().food(CHOWDER_FOOD).stacksTo(64)));
    public static final RegistryObject<Item> RAW_KEBAB = registerItem("raw_kebab", () -> new Item(new Item.Properties().food(RAW_KEBAB_FOOD)));
    public static final RegistryObject<Item> COOKED_KEBAB = registerItem("cooked_kebab", () -> new Item(new Item.Properties().food(COOKED_KEBAB_FOOD)));
    public static final RegistryObject<Item> HEARTY_STEW = registerItem("hearty_stew", () -> new BowlFoodItem(new Item.Properties().food(HEARTY_STEW_FOOD).stacksTo(64)));
    public static final RegistryObject<Item> PORK_DINNER = registerItem("pork_dinner", () -> new Item(new Item.Properties().food(PORK_DINNER_FOOD)));
    public static final RegistryObject<Item> WOLF_CHOP = registerItem("wolf_chop", () -> new Item(new Item.Properties().food(WOLF_CHOP_FOOD)));
    public static final RegistryObject<Item> COOKED_WOLF_CHOP = registerItem("cooked_wolf_chop", () -> new Item(new Item.Properties().food(COOKED_WOLF_CHOP_FOOD)));
    public static final RegistryObject<Item> KIBBLE = registerItem("kibble", () -> new Item(new Item.Properties().food(KIBBLE_FOOD)));
    public static final RegistryObject<Item> APPLE_PIE = registerItem("apple_pie", () -> new Item(new Item.Properties().food(APPLE_PIE_FOOD)));
    public static final RegistryObject<Item> MYSTERY_MEAT = registerItem("mystery_meat", () -> new Item(new Item.Properties().food(MYSTERY_MEAT_FOOD)));
    public static final RegistryObject<Item> COOKED_MYSTERY_MEAT = registerItem("cooked_mystery_meat", () -> new Item(new Item.Properties().food(COOKED_MYSTERY_MEAT_FOOD)));
    public static final RegistryObject<Item> BAT_WING = registerItem("bat_wing", () -> new Item(new Item.Properties().food(BAT_WING_FOOD)));
    public static final RegistryObject<Item> COOKED_BAT_WING = registerItem("cooked_bat_wing", () -> new Item(new Item.Properties().food(COOKED_BAT_WING_FOOD)));

    // ==========================================
    // Dirt, Gravel & Sand Piles
    // ==========================================
    public static final RegistryObject<Item> DIRT_PILE = registerItem("dirt_pile");
    public static final RegistryObject<Item> GRAVEL_PILE = registerItem("gravel_pile");
    public static final RegistryObject<Item> SAND_PILE = registerItem("sand_pile");
    public static final RegistryObject<Item> RED_SAND_PILE = registerItem("red_sand_pile");

    // ==========================================
    // Soulforged Steel Tools & Weapons
    // ==========================================
    public static final RegistryObject<Item> STEEL_AXE = registerItem("steel_axe", () -> new AxeItem(SOULFORGED_STEEL_TIER, 5.0f, -3.0f, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_HOE = registerItem("steel_hoe", () -> new HoeItem(SOULFORGED_STEEL_TIER, -3, 0.0f, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_PICKAXE = registerItem("steel_pickaxe", () -> new PickaxeItem(SOULFORGED_STEEL_TIER, 1, -2.8f, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_SHOVEL = registerItem("steel_shovel", () -> new ShovelItem(SOULFORGED_STEEL_TIER, 1.5f, -3.0f, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_SWORD = registerItem("steel_sword", () -> new SwordItem(SOULFORGED_STEEL_TIER, 3, -2.4f, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_BATTLEAXE = registerItem("steel_battleaxe", () -> new SwordItem(SOULFORGED_STEEL_TIER, 6, -3.2f, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_MATTOCK = registerItem("steel_mattock", () -> new PickaxeItem(SOULFORGED_STEEL_TIER, 2, -2.9f, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_HACKSAW = registerItem("steel_hacksaw", () -> new Item(new Item.Properties().durability(500)));

    // ==========================================
    // Soulforged Steel Armor
    // ==========================================
    public static final ArmorMaterial SOULFORGED_STEEL_ARMOR_MATERIAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) { return new int[]{13, 15, 16, 11}[type.getSlot().getIndex()] * 37; }
        @Override public int getDefenseForType(ArmorItem.Type type) { return new int[]{3, 8, 6, 3}[type.getSlot().getIndex()]; }
        @Override public int getEnchantmentValue() { return 25; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_NETHERITE; }
        @Override public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() { return net.minecraft.world.item.crafting.Ingredient.of(STEEL_INGOT.get()); }
        @Override public String getName() { return BWMod.MODID + ":steel"; }
        @Override public float getToughness() { return 3.0F; }
        @Override public float getKnockbackResistance() { return 0.1F; }
    };

    public static final RegistryObject<Item> STEEL_HELMET = registerItem("steel_helmet",
            () -> new ArmorItem(SOULFORGED_STEEL_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_CHESTPLATE = registerItem("steel_chestplate",
            () -> new ArmorItem(SOULFORGED_STEEL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_LEGGINGS = registerItem("steel_leggings",
            () -> new ArmorItem(SOULFORGED_STEEL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> STEEL_BOOTS = registerItem("steel_boots",
            () -> new ArmorItem(SOULFORGED_STEEL_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ==========================================
    // Tanned Leather Armor
    // ==========================================
    public static final ArmorMaterial TANNED_LEATHER_ARMOR_MATERIAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) { return new int[]{13, 15, 16, 11}[type.getSlot().getIndex()] * 7; }
        @Override public int getDefenseForType(ArmorItem.Type type) { return new int[]{1, 3, 2, 1}[type.getSlot().getIndex()]; }
        @Override public int getEnchantmentValue() { return 15; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER; }
        @Override public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() { return net.minecraft.world.item.crafting.Ingredient.of(TANNED_LEATHER.get()); }
        @Override public String getName() { return BWMod.MODID + ":tanned_leather"; }
        @Override public float getToughness() { return 0.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final RegistryObject<Item> TANNED_HELMET = registerItem("leather_tanned_helmet",
            () -> new ArmorItem(TANNED_LEATHER_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> TANNED_CHESTPLATE = registerItem("leather_tanned_chest",
            () -> new ArmorItem(TANNED_LEATHER_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> TANNED_LEGGINGS = registerItem("leather_tanned_pants",
            () -> new ArmorItem(TANNED_LEATHER_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> TANNED_BOOTS = registerItem("leather_tanned_boots",
            () -> new ArmorItem(TANNED_LEATHER_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ==========================================
    // Wool Armor
    // ==========================================
    public static final ArmorMaterial WOOL_ARMOR_MATERIAL = new ArmorMaterial() {
        @Override public int getDurabilityForType(ArmorItem.Type type) { return new int[]{13, 15, 16, 11}[type.getSlot().getIndex()] * 5; }
        @Override public int getDefenseForType(ArmorItem.Type type) { return new int[]{1, 2, 1, 1}[type.getSlot().getIndex()]; }
        @Override public int getEnchantmentValue() { return 10; }
        @Override public net.minecraft.sounds.SoundEvent getEquipSound() { return net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER; }
        @Override public net.minecraft.world.item.crafting.Ingredient getRepairIngredient() { return net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.tags.ItemTags.WOOL); }
        @Override public String getName() { return BWMod.MODID + ":wool"; }
        @Override public float getToughness() { return 0.0F; }
        @Override public float getKnockbackResistance() { return 0.0F; }
    };

    public static final RegistryObject<Item> WOOL_HELMET = registerItem("wool_helmet",
            () -> new ArmorItem(WOOL_ARMOR_MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
    public static final RegistryObject<Item> WOOL_CHESTPLATE = registerItem("wool_chest",
            () -> new ArmorItem(WOOL_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
    public static final RegistryObject<Item> WOOL_LEGGINGS = registerItem("wool_pants",
            () -> new ArmorItem(WOOL_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS, new Item.Properties()));
    public static final RegistryObject<Item> WOOL_BOOTS = registerItem("wool_boots",
            () -> new ArmorItem(WOOL_ARMOR_MATERIAL, ArmorItem.Type.BOOTS, new Item.Properties()));

    // ==========================================
    // Specialty Items
    // ==========================================
    public static final RegistryObject<Item> COMPOSITE_BOW = registerItem("composite_bow", () -> new BowItem(new Item.Properties().durability(576)));
    public static final RegistryObject<Item> BROADHEAD_ARROW = registerItem("broadhead_arrow", () -> new ArrowItem(new Item.Properties()));
    public static final RegistryObject<Item> ENDER_SPECTACLES = registerItem("ender_spectacles", () -> new Item(new Item.Properties().durability(100)));
    public static final RegistryObject<Item> BREEDING_HARNESS = registerItem("breeding_harness", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DYNAMITE = registerItem("dynamite", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> DYNAMITE_BUNDLE = registerItem("dynamite_bundle", () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> FERTILIZER = registerItem("fertilizer");
    public static final RegistryObject<Item> STUMP_REMOVER = registerItem("stump_remover");
    public static final RegistryObject<Item> ARCANE_SCROLL = registerItem("arcane_scroll", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MANUAL = registerItem("manual", () -> new Item(new Item.Properties().stacksTo(1)));

    // Generator Placer Items
    public static final RegistryObject<Item> WINDMILL = registerItem("windmill",
            () -> new AxleGeneratorItem(new Item.Properties().stacksTo(16), AxleGeneratorItem.GeneratorType.WINDMILL));
    public static final RegistryObject<Item> WATERWHEEL = registerItem("waterwheel",
            () -> new AxleGeneratorItem(new Item.Properties().stacksTo(16), AxleGeneratorItem.GeneratorType.WATERWHEEL));
    public static final RegistryObject<Item> VERTICAL_WINDMILL = registerItem("vertical_windmill",
            () -> new AxleGeneratorItem(new Item.Properties().stacksTo(16), AxleGeneratorItem.GeneratorType.VERTICAL_WINDMILL));

    // Block Items (Forward declared - registered with blocks)
    public static final RegistryObject<Item> AXLE_WOODEN = registerItem("wooden_axle", () -> new BlockItem(BWMBlocks.WOODEN_AXLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> AXLE_STEEL = registerItem("steel_axle", () -> new BlockItem(BWMBlocks.STEEL_AXLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> GEARBOX_WOODEN = registerItem("wooden_gearbox", () -> new BlockItem(BWMBlocks.WOODEN_GEARBOX.get(), new Item.Properties()));
    public static final RegistryObject<Item> GEARBOX_STEEL = registerItem("steel_gearbox", () -> new BlockItem(BWMBlocks.STEEL_GEARBOX.get(), new Item.Properties()));
    public static final RegistryObject<Item> BROKEN_GEARBOX_WOODEN = registerItem("wooden_broken_gearbox", () -> new BlockItem(BWMBlocks.WOODEN_BROKEN_GEARBOX.get(), new Item.Properties()));
    public static final RegistryObject<Item> BROKEN_GEARBOX_STEEL = registerItem("steel_broken_gearbox", () -> new BlockItem(BWMBlocks.STEEL_BROKEN_GEARBOX.get(), new Item.Properties()));
    public static final RegistryObject<Item> HAND_CRANK = registerItem("hand_crank", () -> new BlockItem(BWMBlocks.HAND_CRANK.get(), new Item.Properties()));
    public static final RegistryObject<Item> MILLSTONE = registerItem("millstone", () -> new BlockItem(BWMBlocks.MILLSTONE.get(), new Item.Properties()));
    public static final RegistryObject<Item> SAW = registerItem("saw", () -> new BlockItem(BWMBlocks.SAW.get(), new Item.Properties()));
    public static final RegistryObject<Item> STEEL_SAW = registerItem("steel_saw", () -> new BlockItem(BWMBlocks.STEEL_SAW.get(), new Item.Properties()));
    public static final RegistryObject<Item> TURNTABLE = registerItem("turntable", () -> new BlockItem(BWMBlocks.TURNTABLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> SCREW_PUMP = registerItem("screw_pump", () -> new BlockItem(BWMBlocks.SCREW_PUMP.get(), new Item.Properties()));
    public static final RegistryObject<Item> BELLOWS = registerItem("bellows", () -> new BlockItem(BWMBlocks.BELLOWS.get(), new Item.Properties()));
    public static final RegistryObject<Item> PULLEY = registerItem("pulley", () -> new BlockItem(BWMBlocks.PULLEY.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_DISPENSER = registerItem("block_dispenser", () -> new BlockItem(BWMBlocks.BLOCK_DISPENSER.get(), new Item.Properties()));
    public static final RegistryObject<Item> FILTERED_HOPPER = registerItem("hopper", () -> new BlockItem(BWMBlocks.FILTERED_HOPPER.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAULDRON = registerItem("cauldron", () -> new BlockItem(BWMBlocks.CAULDRON.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRUCIBLE = registerItem("crucible", () -> new BlockItem(BWMBlocks.CRUCIBLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> DRAGON_VESSEL = registerItem("dragon_vessel", () -> new BlockItem(BWMBlocks.DRAGON_VESSEL.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HIBACHI = registerItem("hibachi", () -> new BlockItem(BWMBlocks.HIBACHI.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANCHOR = registerItem("anchor", () -> new BlockItem(BWMBlocks.ANCHOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ROPE = registerItem("rope", () -> new BlockItem(BWMBlocks.ROPE.get(), new Item.Properties()));
    public static final RegistryObject<Item> WICKER = registerItem("wicker", () -> new BlockItem(BWMBlocks.WICKER.get(), new Item.Properties()));
    public static final RegistryObject<Item> GRATE = registerItem("grate", () -> new BlockItem(BWMBlocks.GRATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> SLATS = registerItem("slats", () -> new BlockItem(BWMBlocks.SLATS.get(), new Item.Properties()));
    public static final RegistryObject<Item> PLANTER = registerItem("planter", () -> new BlockItem(BWMBlocks.PLANTER.get(), new Item.Properties()));
    public static final RegistryObject<Item> VASE = registerItem("vase", () -> new BlockItem(BWMBlocks.VASE.get(), new Item.Properties()));
    public static final RegistryObject<Item> URN = registerItem("urn", () -> new BlockItem(BWMBlocks.URN.get(), new Item.Properties()));
    public static final RegistryObject<Item> UNFIRED_POTTERY = registerItem("unfired_pottery", () -> new BlockItem(BWMBlocks.UNFIRED_POTTERY.get(), new Item.Properties()));
    public static final RegistryObject<Item> DETECTOR = registerItem("detector", () -> new BlockItem(BWMBlocks.DETECTOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> BUDDY_BLOCK = registerItem("buddy_block", () -> new BlockItem(BWMBlocks.BUDDY_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> LENS = registerItem("lens", () -> new BlockItem(BWMBlocks.LENS.get(), new Item.Properties()));
    public static final RegistryObject<Item> STEEL_ANVIL = registerItem("steel_anvil", () -> new BlockItem(BWMBlocks.STEEL_ANVIL.get(), new Item.Properties()));
    public static final RegistryObject<Item> INFERNAL_ENCHANTER = registerItem("infernal_enchanter", () -> new BlockItem(BWMBlocks.INFERNAL_ENCHANTER.get(), new Item.Properties()));
    public static final RegistryObject<Item> CREATIVE_GENERATOR = registerItem("creative_generator", () -> new BlockItem(BWMBlocks.CREATIVE_GENERATOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> MINING_CHARGE = registerItem("mining_charge", () -> new BlockItem(BWMBlocks.MINING_CHARGE.get(), new Item.Properties()));
    public static final RegistryObject<Item> FERTILE_FARMLAND = registerItem("fertile_farmland", () -> new BlockItem(BWMBlocks.FERTILE_FARMLAND.get(), new Item.Properties()));
    public static final RegistryObject<Item> STEEL_BLOCK = registerItem("steel_block", () -> new BlockItem(BWMBlocks.STEEL_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_LOG = registerItem("blood_log", () -> new BlockItem(BWMBlocks.BLOOD_LOG.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_LEAVES = registerItem("blood_leaves", () -> new BlockItem(BWMBlocks.BLOOD_LEAVES.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOOD_SAPLING = registerItem("blood_sapling", () -> new BlockItem(BWMBlocks.BLOOD_SAPLING.get(), new Item.Properties()));
    public static final RegistryObject<Item> NETHER_CLAY = registerItem("nether_clay", () -> new BlockItem(BWMBlocks.NETHER_CLAY.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEMP_SEEDS = registerItem("hemp_seeds", () -> new ItemNameBlockItem(BWMBlocks.HEMP.get(), new Item.Properties()));

    // Helpers
    private static RegistryObject<Item> registerItem(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }

    private static RegistryObject<Item> registerItem(String name, java.util.function.Supplier<Item> itemSupplier) {
        return ITEMS.register(name, itemSupplier);
    }

    private static RegistryObject<Item> registerBark(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()) {
            @Override public int getBurnTime(ItemStack itemStack, net.minecraft.world.item.crafting.RecipeType<?> recipeType) { return 25; }
        });
    }
}
