package betterwithmods.common.registry;

import betterwithmods.BWMod;
import betterwithmods.common.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BWMRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, BWMod.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BWMod.MODID);

    // Recipe Types
    public static final RegistryObject<RecipeType<MillingRecipe>> MILLING_TYPE =
            RECIPE_TYPES.register("mill", () -> RecipeType.simple(new ResourceLocation(BWMod.MODID, "mill")));
    public static final RegistryObject<RecipeType<CookingPotRecipe>> CAULDRON_TYPE =
            RECIPE_TYPES.register("cauldron", () -> RecipeType.simple(new ResourceLocation(BWMod.MODID, "cauldron")));
    public static final RegistryObject<RecipeType<CookingPotRecipe>> CRUCIBLE_TYPE =
            RECIPE_TYPES.register("crucible", () -> RecipeType.simple(new ResourceLocation(BWMod.MODID, "crucible")));
    public static final RegistryObject<RecipeType<KilnRecipe>> KILN_TYPE =
            RECIPE_TYPES.register("kiln", () -> RecipeType.simple(new ResourceLocation(BWMod.MODID, "kiln")));
    public static final RegistryObject<RecipeType<SawRecipe>> SAW_TYPE =
            RECIPE_TYPES.register("saw", () -> RecipeType.simple(new ResourceLocation(BWMod.MODID, "saw")));
    public static final RegistryObject<RecipeType<TurntableRecipe>> TURNTABLE_TYPE =
            RECIPE_TYPES.register("turntable", () -> RecipeType.simple(new ResourceLocation(BWMod.MODID, "turntable")));
    public static final RegistryObject<RecipeType<SteelAnvilRecipe>> STEEL_ANVIL_TYPE =
            RECIPE_TYPES.register("steel_anvil", () -> RecipeType.simple(new ResourceLocation(BWMod.MODID, "steel_anvil")));

    // Recipe Serializers
    public static final RegistryObject<RecipeSerializer<MillingRecipe>> MILLING_SERIALIZER =
            RECIPE_SERIALIZERS.register("mill", () -> MillingRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<CookingPotRecipe>> CAULDRON_SERIALIZER =
            RECIPE_SERIALIZERS.register("cauldron", () -> CookingPotRecipe.CauldronSerializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<CookingPotRecipe>> CRUCIBLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("crucible", () -> CookingPotRecipe.CrucibleSerializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<KilnRecipe>> KILN_SERIALIZER =
            RECIPE_SERIALIZERS.register("kiln", () -> KilnRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<SawRecipe>> SAW_SERIALIZER =
            RECIPE_SERIALIZERS.register("saw", () -> SawRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<TurntableRecipe>> TURNTABLE_SERIALIZER =
            RECIPE_SERIALIZERS.register("turntable", () -> TurntableRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<SteelAnvilRecipe>> STEEL_ANVIL_SERIALIZER =
            RECIPE_SERIALIZERS.register("steel_anvil", () -> SteelAnvilRecipe.Serializer.INSTANCE);
}
