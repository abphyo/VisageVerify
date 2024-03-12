package com.biho.visageverify.presentation.screens

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.biho.visageverify.domain.usecases.ValidatePersonUseCase
import com.biho.visageverify.presentation.utils.cropBitmapRec
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val validatePersonUseCase: ValidatePersonUseCase
): ViewModel() {

    private val _persons = MutableStateFlow<List<Pair<String?, Face>>>(emptyList())
    val persons: StateFlow<List<Pair<String?, Face>>> get() = _persons.asStateFlow()

    fun validateFaces(frame: Bitmap, faces: List<Face>) {
        viewModelScope.launch {
            _persons.update {
                faces.map { face ->
                    val box = face.boundingBox
                    val croppedFace = frame.cropBitmapRec(boundingBox = box)
                    val name = validatePersonUseCase.invoke(croppedBitmap = croppedFace)
                    println("AI name: $name")
                    Pair(name, face)
                }
            }
        }
    }

    fun clearPersons() {
        viewModelScope.launch {
            _persons.update { emptyList() }
        }
    }

}