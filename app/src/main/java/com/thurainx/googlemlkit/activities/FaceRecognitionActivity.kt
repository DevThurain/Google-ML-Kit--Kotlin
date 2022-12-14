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
import com.thurainx.googlemlkit.R
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_face_recognition.*
import com.thurainx.googlemlkit.utils.loadBitmapFromUri
import com.thurainx.googlemlkit.utils.scaleToRatio


class FaceRecognitionActivity : BaseActivity() {
    var mChosenImageBitmap: Bitmap? = null

    companion object{
        fun getIntent(context: Context) : Intent {
            val intent = Intent(context, FaceRecognitionActivity::class.java)
            return intent
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_recognition)

        setUpListeners()
    }

    private fun setUpListeners() {
        btnPickImage.setOnClickListener {
            selectImageFromGallery()

        }

        btnFindFace.setOnClickListener {
            detectFaceAndDrawRectangle()
        }
    }

    private fun detectFaceAndDrawRectangle() {
        mChosenImageBitmap?.let {
            val inputImage = InputImage.fromBitmap(it, 0)
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()
            val detector =
                FaceDetection.getClient(options)
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    drawRectangleOnFace(it, faces)
                    ivFaceRecognition.setImageBitmap(mChosenImageBitmap)
                }
                .addOnFailureListener { exception ->
                    showSnackBar(
                        exception.localizedMessage
                            ?: "Cannot detect face."
                    )
                }
        }
    }

    private fun drawRectangleOnFace(
        it: Bitmap,
        faces: MutableList<Face>
    ) {
        val imageCanvas = Canvas(it)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 9.0f

        faces.firstOrNull()?.boundingBox?.let {
                boundingBox -> imageCanvas.drawRect(boundingBox, paint)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == INTENT_REQUEST_CODE_SELECT_IMAGE_FROM_GALLERY) {
            val imageUri = data?.data
            imageUri?.let { image ->

                Observable.just(image)
                    .map { it.loadBitmapFromUri(applicationContext) }
                    .map { it.scaleToRatio(0.35) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mChosenImageBitmap = it
                        ivFaceRecognition.setImageBitmap(mChosenImageBitmap)
                    }

            }
        }
    }
}