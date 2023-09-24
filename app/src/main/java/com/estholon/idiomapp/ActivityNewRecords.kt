package com.estholon.idiomapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.estholon.idiomapp.auxiliary.ImageTools
import com.estholon.idiomapp.databinding.ActivityNewRecordsBinding
import com.estholon.idiomapp.viewmodels.NewRecordViewModel
import com.estholon.idiomapp.viewmodels.NewRecordViewModelFactory
import com.shashank.sony.fancytoastlib.FancyToast
import com.yalantis.ucrop.UCrop

class ActivityNewRecords : AppCompatActivity() {

    private lateinit var binding: ActivityNewRecordsBinding

    //Results launchers
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private var uriImageCut: Uri? = null
    //Edit photo
    private var processPhotoInCourse = false;


    //View Model
    private val viewModel: NewRecordViewModel by viewModels {
        NewRecordViewModelFactory((application as IdiomApp).database.idiomsDao())
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("imageUri")

        // Comprobar si la URI no es nula
        if (imageUri != null) {
            // Cargar la imagen en la ImageView
            val imageUriObj = Uri.parse(imageUri)
            binding.ivAddimage.setImageURI(imageUriObj)
        }

        //Results launchers
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                imageReceived(result)
            }

        binding.ivAddimage.setOnClickListener {
            val popupMenu = PopupMenu(applicationContext , binding.cvAddImage)
            popupMenu.menuInflater.inflate(R.menu.menu_add_image , popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.menu_add_gallery) {
                    choiceGalleryImage()
                } else if (menuItem.itemId == R.id.menu_delete) {
                    binding.ivAddimage.setImageDrawable(this.getDrawable(R.drawable.baseline_add_photo_alternate_24))
                    uriImageCut = null
                } else if (menuItem.itemId==R.id.menu_add_camera){
                   val intent=Intent(this,ActivityCamera::class.java)
                    startActivity(intent)
                }
                false
            }
            popupMenu.show()
        }
        binding.addLanguage.setOnClickListener {
            val intent=Intent(this,ActivityCard::class.java)
            startActivity(intent)
        }




    }

    private fun choiceGalleryImage() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun imageReceived(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val contentUri = data?.data
            val file = ImageTools.createTempImageFile(
                this@ActivityNewRecords,
                ImageTools.getHoraActual("yyMMddHHmmss")
            )
            if (contentUri != null) {
                cutImage(contentUri , Uri.fromFile(file))
            } else {
                Toast.makeText(
                    this@ActivityNewRecords ,
                    R.string.error_al_obtener_la_imagen,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this@ActivityNewRecords ,
                R.string.error_al_obtener_la_imagen ,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun cutImage(uri1: Uri, uri2: Uri) {
        try {
            UCrop.of(uri1 , uri2)
                .withAspectRatio(3f , 3f)
                .withMaxResultSize(
                    ImageTools.ANCHO_DE_FOTO_A_SUBIR ,
                    ImageTools.ALTO_DE_FOTO_A_SUBIR
                )
                .start(this)
        } catch (e: Exception) {
            Toast.makeText(
                this@ActivityNewRecords ,
                getString(R.string.error) ,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun imageCropped(data: Intent?) {
        if (data != null) {
            this.uriImageCut = UCrop.getOutput(data)
            binding.ivAddimage.setImageURI(this.uriImageCut)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int , resultCode: Int , data: Intent?) {
        super.onActivityResult(requestCode , resultCode , data)
        if (resultCode == RESULT_CANCELED) {
            return
        }


        //UCrop
        if (requestCode == UCrop.REQUEST_CROP) {


            if (data != null) {
                imageCropped(data)
            } else {
                Toast.makeText(
                    this@ActivityNewRecords ,
                    getString(R.string.error_al_obtener_la_imagen) ,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (requestCode == UCrop.RESULT_ERROR) {
            FancyToast.makeText(
                this@ActivityNewRecords ,
                getString(R.string.error_al_obtener_la_imagen) ,
                FancyToast.LENGTH_SHORT ,
                FancyToast.ERROR ,
                false
            ).show()
        }
    }

}