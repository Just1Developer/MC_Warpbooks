package net.justonedev.mc.warpbooks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.Objects;

public class Crafting implements Listener {
	
	public static void addCraftingRecipes() {
		ShapelessRecipe craftingRecipe = new ShapelessRecipe(WarpBooks.pageCraftkey, WarpPage.warpPage);
		craftingRecipe.setCategory(CraftingBookCategory.MISC);
		craftingRecipe.addIngredient(Material.PAPER);
		craftingRecipe.addIngredient(Material.ENDER_PEARL);
		
		Bukkit.addRecipe(craftingRecipe);
		WarpBooks.log("Added the warp page crafting recipe");
		Bukkit.broadcastMessage("§eItem: " + craftingRecipe.getResult());
	}
	
	// Layout:
	//  E  E  E
	//  E  B  E
	//  E  E  E
	// where E = Ender pearl and B = book
	// We do it this way because of the associated UUID
	
	@EventHandler
	public void craftWarpBookEvent(PrepareItemCraftEvent e) {
		// Top to Bottom, Left to Right. Null if empty
		ItemStack[] items = e.getInventory().getMatrix();
		if (items.length < 9) return;
		
		// Craft Warp book
		
		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item == null) return;
			
			if (item.getAmount() > 1) return;
			if (i != 4 && item.getType() != Material.ENDER_PEARL) return;
			if (i == 4 && item.getType() != Material.BOOK) return;
			
			if (!item.hasItemMeta()) continue;
			if (!Objects.requireNonNull(item.getItemMeta()).hasCustomModelData()) continue;
			if (Objects.requireNonNull(item.getItemMeta()).getCustomModelData() != 0) return;
		}
		
		// In case anything uses books to be like other stuff
		// make sure it has no custom model data and is actually a book
		// This is important
		// (we don't use it because we don't want to code craft prevention of bookshelves or enchantments with warp books)
		// (clocks are good, compasses too)
		
		e.getInventory().setResult(WarpBook.getNewWarpBook());
	}
	
	@EventHandler
	public void preventCrafting(PrepareItemCraftEvent e) {
		// Top to Bottom, Left to Right. Null if empty
		ItemStack[] items = e.getInventory().getMatrix();
		for (ItemStack item : items) {
			if (item == null || item.getType() != WarpBooks.PLUGIN_MATERIAL) continue;
			
			// Prevent Crafting with Warp Items
			if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasCustomModelData() && item.getItemMeta().getCustomModelData() != 0) {
				e.getInventory().setResult(null);
				return;
			}
		}
	}
	
}
