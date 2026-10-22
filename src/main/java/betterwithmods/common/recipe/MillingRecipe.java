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

import java.util.ArrayList;
import java.util.List;

public class MillingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final NonNullList<ItemStack> results;
    private final int grindTime;

    public MillingRecipe(ResourceLocation id, NonNullList<Ingredient> ingredients, NonNullList<ItemStack> results, int grindTime) {
        this.id = id;
        this.ingredients = ingredients;
        this.results = results;
        this.grindTime = grindTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (ingredients.isEmpty()) return false;
        // Check if all ingredients can be matched from container items
        List<ItemStack> containerItems = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack s = container.getItem(i);
            if (!s.isEmpty()) {
                containerItems.add(s.copy());
            }
        }

        for (Ingredient ingredient : ingredients) {
            boolean matched = false;
            for (ItemStack s : containerItems) {
                if (ingredient.test(s) && !s.isEmpty()) {
                    s.shrink(1);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return results.isEmpty() ? ItemStack.EMPTY : results.get(0).copy();
    }

    public NonNullList<ItemStack> getResults() {
        return results;
    }

    public int getGrindTime() {
        return grindTime;
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
        return ingredients;
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
        return BWMRecipes.MILLING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<MillingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public MillingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            NonNullList<Ingredient> ingredients = NonNullList.create();
            JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
            for (int i = 0; i < ingredientsJson.size(); i++) {
                ingredients.add(Ingredient.fromJson(ingredientsJson.get(i)));
            }

            NonNullList<ItemStack> results = NonNullList.create();
            JsonArray resultsJson = GsonHelper.getAsJsonArray(json, "results");
            for (int i = 0; i < resultsJson.size(); i++) {
                results.add(CraftingHelper.getItemStack(resultsJson.get(i).getAsJsonObject(), true));
            }

            int grindTime = GsonHelper.getAsInt(json, "grind_time", 200);
            return new MillingRecipe(recipeId, ingredients, results, grindTime);
        }

        @Nullable
        @Override
        public MillingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int inCount = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(inCount, Ingredient.EMPTY);
            for (int i = 0; i < inCount; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }

            int outCount = buffer.readVarInt();
            NonNullList<ItemStack> results = NonNullList.withSize(outCount, ItemStack.EMPTY);
            for (int i = 0; i < outCount; i++) {
                results.set(i, buffer.readItem());
            }

            int grindTime = buffer.readVarInt();
            return new MillingRecipe(recipeId, ingredients, results, grindTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MillingRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient in : recipe.ingredients) {
                in.toNetwork(buffer);
            }
            buffer.writeVarInt(recipe.results.size());
            for (ItemStack out : recipe.results) {
                buffer.writeItem(out);
            }
            buffer.writeVarInt(recipe.grindTime);
        }
    }
}
