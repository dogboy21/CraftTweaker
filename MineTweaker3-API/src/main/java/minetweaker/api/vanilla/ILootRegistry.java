/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.api.vanilla;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.WeightedItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

/**
 * @author Stan
 */
@ZenClass("vanilla.ILootRegistry")
public interface ILootRegistry {
    @ZenMethod
    void addChestLoot(String name, WeightedItemStack item);
    @ZenMethod
    void addChestLoot(String name, WeightedItemStack item, int min, int max);

    @ZenMethod
    void removeChestLoot(String name, IIngredient ingredient);

    @ZenMethod
    List<LootEntry> getLoot(String name);

    @ZenGetter("lootTypes")
    List<String> getLootTypes();
}
