package net.justonedev.mc.warpbooks.resourcepack;

public class ModelDataInformation {

    private final String modelName;
    private final int modelInteger;
    private final ModelDataType modelDataType;

    private ModelDataInformation(String modelName, int modelInteger, ModelDataType modelDataType) {
        this.modelName = modelName;
        this.modelDataType = modelDataType;
        this.modelInteger = modelInteger;
        if (this.modelName == null && modelDataType == ModelDataType.MODEL_NAME
            || this.modelInteger == -1 && modelDataType == ModelDataType.MODEL_INTEGER) {
            throw new IllegalArgumentException("Model Data Information Type is not compatible with provided data. (Type was %s, model name was %s, model data was %d)".formatted(modelDataType, modelName, modelInteger));
        }
    }

    public boolean hasModelName() {
        return modelDataType == ModelDataType.MODEL_NAME;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean hasModelInteger() {
        return modelDataType == ModelDataType.MODEL_INTEGER;
    }

    public int getModelInteger() {
        return modelInteger;
    }

    public ModelDataType getModelDataType() {
        return modelDataType;
    }

    public static ModelDataInformation withName(String modelName) {
        return new ModelDataInformation(modelName, -1, ModelDataType.MODEL_NAME);
    }

    public static ModelDataInformation withInteger(int modelInteger) {
        return new ModelDataInformation(null, modelInteger, ModelDataType.MODEL_INTEGER);
    }

    public static ModelDataInformation none() {
        return new ModelDataInformation(null, -1, ModelDataType.NONE);
    }
}
