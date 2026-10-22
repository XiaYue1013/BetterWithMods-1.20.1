package betterwithmods.common.recipe;

import betterwithmods.common.registry.BWMRecipes;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

public class KilnRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final int heat;
    private final int cookTime;
    private final ItemStack result;

    public KilnRecipe(ResourceLocation id, Ingredient input, int heat, int cookTime, ItemStack result) {
        this.id = id;
        this.input = input;
        this.heat = heat;
        this.cookTime = cookTime;
        this.result = result;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getHeat() {
        return heat;
    }

    public int getCookTime() {
        return cookTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(input);
        return list;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return BWMRecipes.KILN_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<KilnRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public KilnRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("input"));
            int heat = GsonHelper.getAsInt(json, "heat", 2);
            int cookTime = GsonHelper.getAsInt(json, "cook_time", 200);
            ItemStack result = CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);

            return new KilnRecipe(recipeId, input, heat, cookTime, result);
        }

        @Nullable
        @Override
        public KilnRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            int heat = buffer.readVarInt();
            int cookTime = buffer.readVarInt();
            ItemStack result = buffer.readItem();

            return new KilnRecipe(recipeId, input, heat, cookTime, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, KilnRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeVarInt(recipe.heat);
            buffer.writeVarInt(recipe.cookTime);
            buffer.writeItem(recipe.result);
        }
    }
}
