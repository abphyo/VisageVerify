package com.biho.visageverify.data.model

import com.biho.visageverify.data.utils.Processor
import com.biho.visageverify.data.utils.TfLite
import kotlinx.serialization.Serializable

@Serializable
data class TfLitePreferences(
    val threadCount: Int = 2,
    val processorDelegate: Processor = Processor.DELEGATE_CPU,
    val model: TfLite = TfLite.FaceNet,
    val maxResult: Int = 2,
    val threshold: Float = 0.5f
)
