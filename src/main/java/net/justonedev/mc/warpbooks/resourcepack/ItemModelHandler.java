package net.justonedev.mc.warpbooks.resourcepack;

import org.bukkit.inventory.ItemStack;

public final class ItemModelHandler {
    private ItemModelHandler() { }

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
        return true;
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
        return ModelDataInformation.none();
    }
}
