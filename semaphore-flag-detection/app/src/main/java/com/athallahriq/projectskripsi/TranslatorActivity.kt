package com.athallahriq.projectskripsi

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import android.util.Log
import com.athallahriq.projectskripsi.databinding.ActivityTranslatorBinding
import com.athallahriq.projectskripsi.ml.ModelsultanV1

class TranslatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTranslatorBinding
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra("IMAGE_PATH")
        if (imagePath != null) {
            val originalBitmap = BitmapFactory.decodeFile(imagePath)
            bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            binding.imageScanned.setImageBitmap(bitmap)
        }

        binding.openGallery.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        binding.btnToCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, 200)
        }

        binding.btnToAlphabet.setOnClickListener {
            val intent = Intent(this, AlphabetActivity::class.java)
            startActivity(intent)
        }

        binding.predictImage.setOnClickListener {
            detectPose(bitmap)
        }
    }

    private fun detectPose(bitmap: Bitmap) {
        val labels = application.assets.open("label.txt").bufferedReader().use { it.readLines() }

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(114, 114, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0.0f, 255.0f))
            .build()

        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

        tensorImage = imageProcessor.process(tensorImage)

        val model = ModelsultanV1.newInstance(this)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 114, 114, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

        var maxIdx = 0
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[maxIdx] < fl) {
                maxIdx = index
            }
        }

        binding.txtPrediction.text = labels[maxIdx]

        model.close()

        Log.d("Prediction", "Predicted class: ${labels[maxIdx]}")
        Log.d("Prediction", "Confidence scores: ${outputFeature0.joinToString(", ")}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            val originalBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            binding.imageScanned.setImageBitmap(bitmap)
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            val imageUriString = data?.getStringExtra("image_uri")
            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                val originalBitmap = if (android.os.Build.VERSION.SDK_INT >= 29) {
                    val source = ImageDecoder.createSource(this.contentResolver, imageUri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                }
                bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
                binding.imageScanned.setImageBitmap(bitmap)
            }
        }
    }
}
