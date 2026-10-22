package betterwithmods.common.recipe;

import betterwithmods.common.registry.BWMRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;

public class SteelAnvilRecipe implements CraftingRecipe {
    private final ResourceLocation id;
    private final int width;
    private final int height;
    private final NonNullList<Ingredient> recipeItems;
    private final ItemStack result;

    public SteelAnvilRecipe(ResourceLocation id, int width, int height, NonNullList<Ingredient> recipeItems, ItemStack result) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.recipeItems = recipeItems;
        this.result = result;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        for (int i = 0; i <= inv.getWidth() - this.width; ++i) {
            for (int j = 0; j <= inv.getHeight() - this.height; ++j) {
                if (this.checkMatch(inv, i, j, true)) {
                    return true;
                }
                if (this.checkMatch(inv, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkMatch(CraftingContainer inv, int startX, int startY, boolean mirrored) {
        for (int i = 0; i < inv.getWidth(); ++i) {
            for (int j = 0; j < inv.getHeight(); ++j) {
                int k = i - startX;
                int l = j - startY;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
                    if (mirrored) {
                        ingredient = this.recipeItems.get(this.width - k - 1 + l * this.width);
                    } else {
                        ingredient = this.recipeItems.get(k + l * this.width);
                    }
                }

                if (!ingredient.test(inv.getItem(i + j * inv.getWidth()))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.recipeItems;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return BWMRecipes.STEEL_ANVIL_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SteelAnvilRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public SteelAnvilRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            int width = GsonHelper.getAsInt(json, "width", 4);
            int height = GsonHelper.getAsInt(json, "height", 4);

            NonNullList<Ingredient> ingredients = NonNullList.create();
            JsonArray pattern = GsonHelper.getAsJsonArray(json, "pattern");
            JsonObject key = GsonHelper.getAsJsonObject(json, "key");

            for (int i = 0; i < pattern.size(); i++) {
                String line = pattern.get(i).getAsString();
                for (int j = 0; j < line.length(); j++) {
                    String charStr = String.valueOf(line.charAt(j));
                    if (" ".equals(charStr)) {
                        ingredients.add(Ingredient.EMPTY);
                    } else if (key.has(charStr)) {
                        ingredients.add(Ingredient.fromJson(key.get(charStr)));
                    } else {
                        ingredients.add(Ingredient.EMPTY);
                    }
                }
            }

            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            return new SteelAnvilRecipe(recipeId, width, height, ingredients, result);
        }

        @Nullable
        @Override
        public SteelAnvilRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            int width = buffer.readVarInt();
            int height = buffer.readVarInt();
            int count = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);
            for (int i = 0; i < count; i++) {
                ingredients.set(i, Ingredient.fromNetwork(buffer));
            }
            ItemStack result = buffer.readItem();
            return new SteelAnvilRecipe(recipeId, width, height, ingredients, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SteelAnvilRecipe recipe) {
            buffer.writeVarInt(recipe.width);
            buffer.writeVarInt(recipe.height);
            buffer.writeVarInt(recipe.recipeItems.size());
            for (Ingredient in : recipe.recipeItems) {
                in.toNetwork(buffer);
            }
            buffer.writeItem(recipe.result);
        }
    }
}
