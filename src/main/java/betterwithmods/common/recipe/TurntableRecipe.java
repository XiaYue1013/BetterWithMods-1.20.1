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

public class TurntableRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final int rotations;
    private final ItemStack result;
    private final NonNullList<ItemStack> drops;

    public TurntableRecipe(ResourceLocation id, Ingredient input, int rotations, ItemStack result, NonNullList<ItemStack> drops) {
        this.id = id;
        this.input = input;
        this.rotations = rotations;
        this.result = result;
        this.drops = drops;
    }

    public Ingredient getInput() {
        return input;
    }

    public int getRotations() {
        return rotations;
    }

    public NonNullList<ItemStack> getDrops() {
        return drops;
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
        return BWMRecipes.TURNTABLE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<TurntableRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public TurntableRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient input = Ingredient.fromJson(json.get("input"));
            int rotations = GsonHelper.getAsInt(json, "rotations", 8);
            ItemStack result = CraftingHelper.getItemStack(json.getAsJsonObject("result"), true);

            NonNullList<ItemStack> drops = NonNullList.create();
            if (json.has("drops")) {
                JsonArray dropsJson = json.getAsJsonArray("drops");
                for (int i = 0; i < dropsJson.size(); i++) {
                    drops.add(CraftingHelper.getItemStack(dropsJson.get(i).getAsJsonObject(), true));
                }
            }

            return new TurntableRecipe(recipeId, input, rotations, result, drops);
        }

        @Nullable
        @Override
        public TurntableRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient input = Ingredient.fromNetwork(buffer);
            int rotations = buffer.readVarInt();
            ItemStack result = buffer.readItem();

            int dropsCount = buffer.readVarInt();
            NonNullList<ItemStack> drops = NonNullList.withSize(dropsCount, ItemStack.EMPTY);
            for (int i = 0; i < dropsCount; i++) {
                drops.set(i, buffer.readItem());
            }

            return new TurntableRecipe(recipeId, input, rotations, result, drops);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, TurntableRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeVarInt(recipe.rotations);
            buffer.writeItem(recipe.result);

            buffer.writeVarInt(recipe.drops.size());
            for (ItemStack drop : recipe.drops) {
                buffer.writeItem(drop);
            }
        }
    }
}
