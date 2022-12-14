package com.thurainx.googlemlkit.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.thurainx.googlemlkit.R
import com.thurainx.googlemlkit.utils.loadBitmapFromUri
import com.thurainx.googlemlkit.utils.scaleToRatio
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_face_recognition.*
import kotlinx.android.synthetic.main.activity_text_recognition.*
import kotlinx.android.synthetic.main.activity_text_recognition.btnPickImage

class TextRecognitionActivity : BaseActivity(){
    var mChosenImageBitmap: Bitmap? = null

    companion object{
        fun getIntent(context: Context) : Intent {
            val intent = Intent(context, TextRecognitionActivity::class.java)
            return intent
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_recognition)

        setUpListeners()
    }

    private fun setUpListeners() {
        btnPickImage.setOnClickListener {
            selectImageFromGallery()
        }

        btnFindText.setOnClickListener {
            detectTextAndUpdateUI()
        }
    }

    private fun detectTextAndUpdateUI() {
        mChosenImageBitmap?.let {
            val inputImage = InputImage.fromBitmap(it, 0)
            val optionsInterface = TextRecognizerOptions.DEFAULT_OPTIONS
            val recognizer = TextRecognition.getClient(optionsInterface)

            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->

                    // Update text in the screen
                    val detectedTextsString = StringBuilder("")
                    visionText.textBlocks.forEach { block ->
                        detectedTextsString.append("${block.text}\n")
                    }

                    tvRecognizedText.text = ""
                    tvRecognizedText.text = detectedTextsString.toString()

                    // Draw bounding boxes
                    val paint = Paint()
                    paint.color = Color.GREEN
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 2.0f

                    visionText.textBlocks.forEach { block ->
                        val imageCanvas = Canvas(it)
                        block.boundingBox?.let { boundingBox -> imageCanvas.drawRect(boundingBox, paint) }
                    }

                }
                .addOnFailureListener { e ->
                    showSnackBar(
                        e.localizedMessage ?: ("Cannot Detect Text")
                    )
                }
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == BaseActivity.INTENT_REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY) {
            val imageUri = data?.data
            imageUri?.let { image ->

                Observable.just(image)
                    .map { it.loadBitmapFromUri(applicationContext) }
                    .map { it.scaleToRatio(0.35) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mChosenImageBitmap = it
                        ivTextRecognition.setImageBitmap(mChosenImageBitmap)
                    }

            }
        }
    }

}