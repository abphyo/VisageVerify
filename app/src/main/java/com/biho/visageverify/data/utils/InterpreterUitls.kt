package com.biho.visageverify.data.utils

enum class Processor {
    DELEGATE_CPU,
    DELEGATE_GPU,
    DELEGATE_NNAPI,
}

enum class TfLite(val fileName: String) {
    FaceNet("facenet.tflite"),
    FaceNetHiroki("facenet_hiroki.tflite"),
    MobileFaceNet("mobile_facenet.tflite"),
}
