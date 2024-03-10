package com.biho.visageverify.data

import android.content.Context
import android.graphics.Bitmap
import com.biho.visageverify.data.repo.PersonInterpreter
import com.biho.visageverify.data.utils.Processor
import com.biho.visageverify.data.utils.TfLite
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class TfLiteInterpreter(
    private val context: Context
): PersonInterpreter {

    private var interpreter: Interpreter? = null
    private var tfLiteModel: TfLite = TfLite.FaceNet
    init {
        setUpInterpreter(
            2,
            Processor.DELEGATE_CPU,
            model = TfLite.FaceNet
        )
    }
    fun setUpInterpreter(
        threadCount: Int,
        processorDelegate: Processor,
        model: TfLite
    ) {
        tfLiteModel = model

        val baseOptions = Interpreter.Options().apply {
            setNumThreads(threadCount)
        }

        // Use the specified hardware for running the model. Default to CPU
        when (processorDelegate) {
            Processor.DELEGATE_CPU -> {
                // Default
                baseOptions.apply {
                    setUseNNAPI(false)
                }
            }
            Processor.DELEGATE_GPU -> {
                if (CompatibilityList().isDelegateSupportedOnThisDevice) {
                    // Gpu doesn't support for Interpreter
                    baseOptions.apply {
                        setUseNNAPI(false)
                    }
                } else {
                    throw IllegalStateException("GPU is not supported on this device")
                }
            }
            Processor.DELEGATE_NNAPI -> {
                baseOptions.apply {
                    setUseNNAPI(true)
                }
            }
        }

        try {
            interpreter = Interpreter(FileUtil.loadMappedFile(context, tfLiteModel.fileName), baseOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun interpret(bitmap: Bitmap): Array<FloatArray> {

        if (interpreter == null)
            throw IllegalStateException("Interpreter hasn't set up yet")

        val imageSize = when (tfLiteModel) {
            TfLite.FaceNet -> 160
            TfLite.FaceNetHiroki -> 160
            TfLite.MobileFaceNet -> 112
        }

        val embeddingDim = when(tfLiteModel) {
            TfLite.FaceNet -> 128
            TfLite.FaceNetHiroki -> 128
            TfLite.MobileFaceNet -> 192
        }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(imageSize, imageSize, ResizeOp.ResizeMethod.BILINEAR))
            .add(CastOp( DataType.FLOAT32 ))
            .build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap)).buffer

        val faceNetModelOutputs: MutableMap<Int, Any> = HashMap()
        faceNetModelOutputs[0] = Array(1) { FloatArray(embeddingDim) }

        interpreter?.runForMultipleInputsOutputs(arrayOf(tensorImage), faceNetModelOutputs)

        // Perform a safe cast with null check
        val outputArray = faceNetModelOutputs[0] as? Array<FloatArray>

        // Check if the cast was successful
        if (outputArray != null) {
            return outputArray
        } else {
            throw IllegalStateException("Unexpected output format")
        }

    }

//    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
//        return when(rotation) {
//            Surface.ROTATION_270 -> ImageProcessingOptions.Orientation.BOTTOM_RIGHT
//            Surface.ROTATION_90 -> ImageProcessingOptions.Orientation.TOP_LEFT
//            Surface.ROTATION_180 -> ImageProcessingOptions.Orientation.RIGHT_BOTTOM
//            else -> ImageProcessingOptions.Orientation.RIGHT_TOP
//        }
//    }
}
