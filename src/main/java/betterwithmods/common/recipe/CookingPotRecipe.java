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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CookingPotRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final RecipeType<?> recipeType;
    private final NonNullList<Ingredient> ingredients;
    private final NonNullList<ItemStack> results;
    private final int heat;
    private final int cookTime;

    public CookingPotRecipe(ResourceLocation id, RecipeType<?> recipeType, NonNullList<Ingredient> ingredients, NonNullList<ItemStack> results, int heat, int cookTime) {
        this.id = id;
        this.recipeType = recipeType;
        this.ingredients = ingredients;
        this.results = results;
        this.heat = heat;
        this.cookTime = cookTime;
    }

    public int getHeat() {
        return heat;
    }

    public int getCookTime() {
        return cookTime;
    }

    public NonNullList<ItemStack> getResults() {
        return results;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (ingredients.isEmpty()) return false;
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
        if (recipeType == BWMRecipes.CRUCIBLE_TYPE.get()) {
            return CrucibleSerializer.INSTANCE;
        }
        return CauldronSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return recipeType;
    }

    public static class CauldronSerializer implements RecipeSerializer<CookingPotRecipe> {
        public static final CauldronSerializer INSTANCE = new CauldronSerializer();

        @Override
        public CookingPotRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return parse(recipeId, BWMRecipes.CAULDRON_TYPE.get(), json);
        }

        @Nullable
        @Override
        public CookingPotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return read(recipeId, BWMRecipes.CAULDRON_TYPE.get(), buffer);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CookingPotRecipe recipe) {
            write(buffer, recipe);
        }
    }

    public static class CrucibleSerializer implements RecipeSerializer<CookingPotRecipe> {
        public static final CrucibleSerializer INSTANCE = new CrucibleSerializer();

        @Override
        public CookingPotRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            return parse(recipeId, BWMRecipes.CRUCIBLE_TYPE.get(), json);
        }

        @Nullable
        @Override
        public CookingPotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return read(recipeId, BWMRecipes.CRUCIBLE_TYPE.get(), buffer);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CookingPotRecipe recipe) {
            write(buffer, recipe);
        }
    }

    private static CookingPotRecipe parse(ResourceLocation recipeId, RecipeType<?> type, JsonObject json) {
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

        int heat = GsonHelper.getAsInt(json, "heat", 1);
        int cookTime = GsonHelper.getAsInt(json, "cook_time", 200);
        return new CookingPotRecipe(recipeId, type, ingredients, results, heat, cookTime);
    }

    private static CookingPotRecipe read(ResourceLocation recipeId, RecipeType<?> type, FriendlyByteBuf buffer) {
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

        int heat = buffer.readVarInt();
        int cookTime = buffer.readVarInt();
        return new CookingPotRecipe(recipeId, type, ingredients, results, heat, cookTime);
    }

    private static void write(FriendlyByteBuf buffer, CookingPotRecipe recipe) {
        buffer.writeVarInt(recipe.ingredients.size());
        for (Ingredient in : recipe.ingredients) {
            in.toNetwork(buffer);
        }
        buffer.writeVarInt(recipe.results.size());
        for (ItemStack out : recipe.results) {
            buffer.writeItem(out);
        }
        buffer.writeVarInt(recipe.heat);
        buffer.writeVarInt(recipe.cookTime);
    }
}
