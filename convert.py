import tensorflow as tf
# 转换h5文件到SavedMovdel

model = tf.keras.models.load_model('model/aimbot_detection_model-3.h5')
# model.summary()
model.export('model/model')
# tf.saved_model.save(model, export_dir='model/model')