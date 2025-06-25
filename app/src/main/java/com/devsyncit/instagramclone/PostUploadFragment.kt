package com.devsyncit.instagramclone

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

class PostUploadFragment : Fragment() {

    lateinit var btnAddPhoto: Button
    lateinit var back_arrow: ImageButton
    lateinit var btnPost: Button
    lateinit var captionEdittext: TextInputEditText
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var imagePreview: ImageView
    private var selectedBase64Image: String? = null
    lateinit var databaseReference: DatabaseReference

    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val postUploadView = inflater.inflate(R.layout.fragment_post_upload, container, false)

        btnAddPhoto = postUploadView.findViewById(R.id.btnAddPhoto)
        imagePreview = postUploadView.findViewById(R.id.imagePreview)
        captionEdittext = postUploadView.findViewById(R.id.captionEditText)
        btnPost = postUploadView.findViewById(R.id.btnPost)
        back_arrow = postUploadView.findViewById(R.id.back_arrow)
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts")

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    var imageUri = result.data!!.data
                    lifecycleScope.launch {
                        try {

                            val compressedBase64Image = withContext(Dispatchers.IO) {
                                val inputStream = requireContext().contentResolver.openInputStream(imageUri!!)
                                val originalBitmap = BitmapFactory.decodeStream(inputStream)

                                val exif =
                                    ExifInterface(requireContext().contentResolver.openInputStream(imageUri)!!)
                                val orientation = exif.getAttributeInt(
                                    ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_NORMAL
                                )

                                val rotatedBitmap =
                                    rotateBitmapIfRequired(originalBitmap, orientation)
                                val resizedBitmap = resizeBitmap(rotatedBitmap, 800)

                                val stream = ByteArrayOutputStream()
                                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
                                val compressedBytes = stream.toByteArray()

                                Base64.encodeToString(
                                    compressedBytes,
                                    Base64.DEFAULT
                                ) to resizedBitmap
                            }

                            val (base64Image, resizedBitmap) = compressedBase64Image
                            selectedBase64Image = base64Image
                            imagePreview.setImageBitmap(resizedBitmap)

                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }


        btnAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
        }

        btnPost.setOnClickListener {

            var caption: String = captionEdittext.text.toString()

            if (selectedBase64Image == null && caption == null){

                Toast.makeText(requireContext(), "Nothing have", Toast.LENGTH_SHORT).show()

            }else{

                var post: HashMap<String, String> = HashMap()
                if (selectedBase64Image!=null){
                    post.put("image", selectedBase64Image!!)
                }
                post.put("caption", caption)

                databaseReference.child(uid!!).push().setValue(post).addOnSuccessListener {

                    Toast.makeText(requireContext(), "Post Uploaded", Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {

                    Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()

                }
            }

        }



        return postUploadView
    }


    private fun rotateBitmapIfRequired(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun resizeBitmap(original: Bitmap, maxSize: Int): Bitmap {
        var width = original.width
        var height = original.height

        val ratio = width.toFloat() / height

        if (ratio > 1) {
            width = maxSize
            height = (width / ratio).toInt()
        } else {
            height = maxSize
            width = (height * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(original, width, height, true)
    }

}