package betterwithmods.common.recipe;

import betterwithmods.common.registry.BWMRecipes;
import com.google.gson.JsonArray;
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

public class SawRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final NonNullList<ItemStack> results;

    public SawRecipe(ResourceLocation id, Ingredient input, NonNullList<ItemStack> results) {
        this.id = id;
        this.input = input;
        this.results = results;
    }

    public Ingredient getInput() {
        return input;
    }

    public NonNullList<ItemStack> getResults() {
        return results;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack stack = container.getItem(0);
        return input.test(stack);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0).copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0);
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
        return BWMRecipes.SAW_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SawRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public SawRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("input"));

            NonNullList<ItemStack> results = NonNullList.create();
            JsonArray resultsJson = GsonHelper.getAsJsonArray(json, "results");
            for (int i = 0; i < resultsJson.size(); i++) {
                results.add(CraftingHelper.getItemStack(resultsJson.get(i).getAsJsonObject(), true));
            }

            return new SawRecipe(recipeId, input, results);
        }

        @Nullable
        @Override
        public SawRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);

            int outCount = buffer.readVarInt();
            NonNullList<ItemStack> results = NonNullList.withSize(outCount, ItemStack.EMPTY);
            for (int i = 0; i < outCount; i++) {
                results.set(i, buffer.readItem());
            }

            return new SawRecipe(recipeId, input, results);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SawRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeVarInt(recipe.results.size());
            for (ItemStack out : recipe.results) {
                buffer.writeItem(out);
            }
        }
    }
}
