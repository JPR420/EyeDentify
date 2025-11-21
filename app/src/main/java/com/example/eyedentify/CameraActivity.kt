package com.example.eyedentify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class CameraActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var captureBtn: ImageButton
    private var imageCapture: ImageCapture? = null



    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCamera()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        previewView = findViewById(R.id.previewView)
        captureBtn = findViewById(R.id.captureBtn)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera()
        }

        captureBtn.setOnClickListener { takePhoto() }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()


            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }


            imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()


            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA


            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageCapture
            )

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return


        val photoFile = File(externalCacheDir, "capture_${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Log.e("CameraX", "Error: ${error.message}")
                }

                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    val uri = Uri.fromFile(photoFile)

                    Log.d("CameraActivity", "File exists: ${photoFile.exists()}, size: ${photoFile.length()}")


                    lifecycleScope.launch {
                        val result = ApiTest.identifyImage(photoFile)
                        Log.d("CameraActivity", "Result: $result")





                            val intent = Intent(this@CameraActivity, ResultActivity::class.java)
                            intent.putExtra("imageUri", uri?.toString() ?: R.drawable.imagenotfound)
                            intent.putExtra("name", result?.name ?: "Unknown Object")
                            intent.putExtra("confidence", result?.confidence ?: "0.00%" )
                            intent.putExtra("description",  result?.description ?: "No description available")
                            startActivity(intent)

                    }
                }
            }
        )
    }




}

data class ApiResult(
    val name: String,
    val confidence: String,
    val description: String
)

suspend fun <T> Task<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
    }

