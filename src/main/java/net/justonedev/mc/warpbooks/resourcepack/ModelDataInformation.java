package net.justonedev.mc.warpbooks.resourcepack;

import java.util.Objects;

public class ModelDataInformation {

    private static final String FORMAT = "[Type: %s, Modelname: %s, Legacy Integer: %d]";

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

    public boolean isNone() {
        return modelDataType == ModelDataType.NONE;
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

    @Override
    public String toString() {
        return FORMAT.formatted(modelDataType, modelName, modelInteger);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ModelDataInformation that = (ModelDataInformation) o;
        return modelInteger == that.modelInteger && Objects.equals(modelName, that.modelName) && modelDataType == that.modelDataType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelName, modelInteger, modelDataType);
    }
}
