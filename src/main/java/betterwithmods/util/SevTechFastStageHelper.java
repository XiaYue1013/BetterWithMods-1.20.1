package betterwithmods.util;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public final class SevTechFastStageHelper {
    private SevTechFastStageHelper() {
    }

    /**
     * Creates a thread-safe Multimap where each key gets its OWN distinct concurrent Set bucket.
     * Fixes the shared-bucket duplication bug when replacing ItemStages' internal multimaps.
     */
    public static <K, V> Multimap<K, V> createConcurrentMultimap() {
        return Multimaps.newSetMultimap(new ConcurrentHashMap<>(), ConcurrentHashMap::newKeySet);
    }

    /**
     * Creates an O(1) identity-hashed ItemStack predicate for a collection of item registry IDs.
     * Replaces linear O(N) Ingredient scans across thousands of items.
     */
    public static Predicate<ItemStack> createItemSetPredicate(Collection<?> itemIds) {
        Set<Item> items = Collections.newSetFromMap(new IdentityHashMap<>(Math.max(16, itemIds.size() * 2)));
        for (Object rawId : itemIds) {
            if (rawId == null) continue;
            ResourceLocation rl = ResourceLocation.tryParse(rawId.toString());
            if (rl != null) {
                Item item = ForgeRegistries.ITEMS.getValue(rl);
                if (item != null) {
                    items.add(item);
                }
            }
        }
        return stack -> stack != null && !stack.isEmpty() && items.contains(stack.getItem());
    }

    /**
     * Creates an O(1) item + fast NBT check for Tinkers' Construct modifiers/upgrades.
     */
    public static Predicate<ItemStack> createTinkerModifierPredicate(Collection<?> itemIds, String listKey, String modifierId) {
        Predicate<ItemStack> base = createItemSetPredicate(itemIds);
        return stack -> {
            if (!base.test(stack) || !stack.hasTag()) return false;
            CompoundTag tag = stack.getTag();
            if (tag == null || !tag.contains(listKey, Tag.TAG_LIST)) return false;
            ListTag list = tag.getList(listKey, Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                if (modifierId.equals(entry.getString("name"))) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Creates an O(1) item + fast NBT check for Tinkers' Construct tool materials.
     */
    public static Predicate<ItemStack> createTinkerMaterialPredicate(Collection<?> itemIds, String materialId) {
        Predicate<ItemStack> base = createItemSetPredicate(itemIds);
        return stack -> {
            if (!base.test(stack) || !stack.hasTag()) return false;
            CompoundTag tag = stack.getTag();
            if (tag == null || !tag.contains("tic_materials", Tag.TAG_LIST)) return false;
            ListTag list = tag.getList("tic_materials", Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                if (materialId.equals(list.getString(i))) {
                    return true;
                }
            }
            return false;
        };
    }
}
