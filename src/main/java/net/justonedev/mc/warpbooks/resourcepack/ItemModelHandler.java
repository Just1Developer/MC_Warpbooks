package net.justonedev.mc.warpbooks.resourcepack;

import net.justonedev.mc.warpbooks.WarpBooks;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;

@SuppressWarnings({"UnstableApiUsage", "UtilityClass"})   // Marked as experimental in earliest version
public final class ItemModelHandler {
    private static final int DEFAULT_LEGACY_CUSTOM_MODEL_DATA = 0;

    private ItemModelHandler() { }

    /**
     * Applies a model to an ItemStack. Automatically determines what to apply
     * based on the minecraft version the server is running. If the Server is
     * >= 1.21.4, the method will use the new system with modelName. If the
     * server is below 1.21.4, the method will use the old customModelData system.
     *
     * @param itemStack The ItemStack to apply the model to.
     * @param modelInformation The information for the model.
     */
    public static void applyModelData(ItemStack itemStack, ModelDataInformation modelInformation) {
        applyModelData(itemStack, modelInformation.getModelName(), modelInformation.getModelInteger());
    }

    /**
     * Applies a model to an ItemStack. Automatically determines what to apply
     * based on the minecraft version the server is running. If the Server is
     * >= 1.21.4, the method will use the new system with modelName. If the
     * server is below 1.21.4, the method will use the old customModelData system.
     *
     * @param itemStack The ItemStack to apply the model to.
     * @param modelName The model name for newer versions
     * @param customModelData The custom model data for older versions.
     */
    public static void applyModelData(ItemStack itemStack, String modelName, int customModelData) {
        if (itemStack == null) return;
        ResourceHandler resourceHandler = WarpBooks.getResourceHandler();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        if (resourceHandler.shouldUseNewModels()) {
            String resourceDomain = resourceHandler.getResourceDomain();
            applyModelDataNew(resourceDomain, modelName, itemMeta);
        } else {
            applyModelDataLegacy(customModelData, itemMeta);
        }
        itemStack.setItemMeta(itemMeta);
    }

    private static void applyModelDataNew(String resourceDomain, String modelName, @Nonnull ItemMeta itemMeta) {
        itemMeta.setItemModel(new NamespacedKey(resourceDomain, modelName.toLowerCase()));
    }

    private static void applyModelDataLegacy(int customModelData, @Nonnull ItemMeta itemMeta) {
        itemMeta.setCustomModelData(customModelData);
    }

    /**
     * Checks if an ItemStack has a custom model applied to it. Automatically
     * determines what to apply based on the minecraft version the server is running.
     * If the Server is >= 1.21.4, the method will use the new system with modelName.
     * If the server is below 1.21.4, the method will use the old customModelData system.
     *
     * @param itemStack The ItemStack to check.
     */
    public static boolean hasModelData(ItemStack itemStack) {
        if (itemStack == null) return false;
        ResourceHandler resourceHandler = WarpBooks.getResourceHandler();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return false;
        if (resourceHandler.shouldUseNewModels()) {
            String resourceDomain = resourceHandler.getResourceDomain();
            return hasModelDataNew(resourceDomain, itemMeta);
        } else {
            return hasModelDataLegacy(itemMeta);
        }
    }

    private static boolean hasModelDataNew(String resourceDomain, @Nonnull ItemMeta itemMeta) {
        var customModel = itemMeta.getItemModel();
        if (customModel == null) return false;
        return resourceDomain.equals(customModel.getKey());
    }

    private static boolean hasModelDataLegacy(@Nonnull ItemMeta itemMeta) {
        if (!itemMeta.hasCustomModelData()) return false;
        return itemMeta.getCustomModelData() != DEFAULT_LEGACY_CUSTOM_MODEL_DATA;
    }

    /**
     * Removes any custom model data from an ItemStack. Automatically determines what
     * to remove based on the minecraft version the server is running. If the Server is
     * >= 1.21.4, the method will use the new system with modelName. If the server
     * is below 1.21.4, the method will use the old customModelData system.
     *
     * @param itemStack The ItemStack to clear the model from.
     */
    public static void removeModelData(ItemStack itemStack) {
        if (itemStack == null) return;
        ResourceHandler resourceHandler = WarpBooks.getResourceHandler();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;
        if (resourceHandler.shouldUseNewModels()) {
            removeModelDataNew(itemMeta);
        } else {
            removeModelDataLegacy(itemMeta);
        }
        itemStack.setItemMeta(itemMeta);
    }

    private static void removeModelDataNew(@Nonnull ItemMeta itemMeta) {
        itemMeta.setItemModel(null);
    }

    private static void removeModelDataLegacy(@Nonnull ItemMeta itemMeta) {
        if (!itemMeta.hasCustomModelData()) return;
        itemMeta.setCustomModelData(null);
    }

    /**
     * Gets the custom model data from an ItemStack. Automatically determines what
     * to get based on the minecraft version the server is running. If the Server is
     * >= 1.21.4, the method will use the new system with modelName. If the server
     * is below 1.21.4, the method will use the old customModelData system.
     *
     * @param itemStack The ItemStack to read the model from.
     */
    public static ModelDataInformation getModelData(ItemStack itemStack) {
        if (itemStack == null) return ModelDataInformation.none();
        ResourceHandler resourceHandler = WarpBooks.getResourceHandler();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return ModelDataInformation.none();
        if (resourceHandler.shouldUseNewModels()) {
            return getModelDataNew(itemMeta);
        } else {
            return getModelDataLegacy(itemMeta);
        }
    }

    private static ModelDataInformation getModelDataNew(@Nonnull ItemMeta itemMeta) {
        var customModel = itemMeta.getItemModel();
        if (customModel == null) return ModelDataInformation.none();
        return ModelDataInformation.withName(customModel.getKey());
    }

    private static ModelDataInformation getModelDataLegacy(@Nonnull ItemMeta itemMeta) {
        if (!itemMeta.hasCustomModelData()) return ModelDataInformation.none();
        if (itemMeta.getCustomModelData() == DEFAULT_LEGACY_CUSTOM_MODEL_DATA) return ModelDataInformation.none();
        return ModelDataInformation.withInteger(itemMeta.getCustomModelData());
    }
}
