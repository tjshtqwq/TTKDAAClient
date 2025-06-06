package me.tjshawa.ttkdaaclient.ml.djl;

import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.DefaultZooProvider;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.tensorflow.engine.TfEngineProvider;
import ai.djl.translate.Batchifier;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import me.tjshawa.ttkdaaclient.TTKDAAClient;
import me.tjshawa.ttkdaaclient.ml.AimbotResponse;
import me.tjshawa.ttkdaaclient.ml.MLBackend;

import java.nio.file.Paths;
import java.util.List;

public class DJLMLBackend implements MLBackend {
    private final ZooModel<float[][], Float> model;
    private final boolean compatibleMode;

    public DJLMLBackend(String modelPath, boolean compatibleMode) {
        try {
            // zoo/engine provider 由 META-INF/services 定义。虽然插件里有 META-INF/services 文件夹，但Java只认Spigot里的。
            // 所以只得手动创建。
//            ModelZoo.registerModelZoo(new DefaultZooProvider());
//            ModelZoo.registerModelZoo(new TfZooProvider());
//            Engine.registerEngine(new TfEngineProvider());

            model = Criteria.builder()
                    .setTypes(float[][].class, Float.class)
                    .optModelPath(Paths.get(TTKDAAClient.INSTANCE.getDataFolder().getAbsolutePath(), modelPath))
                    .optEngine("TensorFlow")
                    .optTranslator(new FloatArrayTranslator(compatibleMode))
                    .build()
                    .loadModel();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load model", e);
        }
        this.compatibleMode = compatibleMode;
    }

    @Override
    public AimbotResponse predictAimML(List<Double> data) {
        if (data.size() != 100) {
            throw new IllegalArgumentException("input.size() != 100");
        }

        float[] delta = new float[compatibleMode ? 19 : 0];
        float avg = 0;
        if (compatibleMode) {
            for (int i = 1; i < 20; i++) {
                delta[i - 1] = (float) (data.get(80 + i) - data.get(80 + i - 1));
            }

            for (float d : delta) avg += d;
            avg /= 19f;
        }

        float[] fullInput = new float[compatibleMode ? 120 : 100];
        for (int i = 0; i < 100; i++) fullInput[i] = data.get(i).floatValue();
        if (compatibleMode) {
            System.arraycopy(delta, 0, fullInput, 100, 19);
            fullInput[119] = avg;
        }

        try (Predictor<float[][], Float> predictor = model.newPredictor()) {
            return new AimbotResponse(predictor.predict(new float[][]{fullInput}));
        } catch (TranslateException e) {
            throw new RuntimeException("Prediction failed", e);
        }
    }

    private static class FloatArrayTranslator implements Translator<float[][], Float> {
        private final boolean compatibleMode;

        public FloatArrayTranslator(boolean compatibleMode) {
            this.compatibleMode = compatibleMode;
        }

        @Override
        public NDList processInput(TranslatorContext ctx, float[][] input) {
            NDArray array = ctx.getNDManager().create(input);
            array = array.reshape(1, compatibleMode ? 120 : 100, 1); // [B=1, C=120, L=1] god knows why
            return new NDList(array);
        }

        @Override
        public Float processOutput(TranslatorContext ctx, NDList list) {
            return list.singletonOrThrow().toFloatArray()[0];
        }

        @Override
        public Batchifier getBatchifier() {
            return null;
        }
    }
}
