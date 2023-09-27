package com.estholon.idiomapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.estholon.idiomapp.adapters.AddSentenceAdapter
import com.estholon.idiomapp.auxiliary.ImageTools
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.ActivityNewRecordsBinding
import com.estholon.idiomapp.databinding.LiAddCategoryBinding
import com.estholon.idiomapp.viewmodels.NewRecordViewModel
import com.estholon.idiomapp.viewmodels.NewRecordViewModelFactory
import com.google.android.material.chip.Chip
import com.shashank.sony.fancytoastlib.FancyToast
import com.yalantis.ucrop.UCrop

class ActivityNewRecords : AppCompatActivity() {

    private lateinit var binding: ActivityNewRecordsBinding
    private lateinit var liCategoryBinding:LiAddCategoryBinding

    //Results launchers
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private var uriImageCut: Uri? = null



    //View Model
    private val viewModel: NewRecordViewModel by viewModels {
        NewRecordViewModelFactory((application as IdiomApp).database.idiomsDao(),(application as IdiomApp).database.categoriesDao(),(application as IdiomApp).database.record_categoriesDao(),(application as IdiomApp).database.recordsDao(),(application as IdiomApp).database.translationsDao())
    }

    //Recycler
    private lateinit var alRecord: MutableList<Records>
    private lateinit var adapterSentence: AddSentenceAdapter


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("imageUri")

        //NavigationDrawer
        binding.ivIconSetting.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        val itemToInvisible = binding.navigationView.menu.findItem(R.id.menu_new_record)
        itemToInvisible.isVisible = false
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_record -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_new_record -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityNewRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_cards -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityCard::class.java)
                    startActivity(intent)
                }

                R.id.menu_match -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityMatch::class.java)
                    startActivity(intent)
                }

                R.id.menu_writing -> {
                    val intent = Intent(this@ActivityNewRecords, ActivityWriting::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        //Recycler
        binding.rvLanguage.setHasFixedSize(true)
        alRecord = mutableListOf()
        adapterSentence = AddSentenceAdapter(this , mutableListOf())
        binding.rvLanguage.adapter = adapterSentence
        adapterSentence.setSpinnerListener(object:AddSentenceAdapter.SpinnerListener{
            override fun onSpinnerClick(idiom: String,position:Int) {
                viewModel.listRecords.value?.get(position)?.idIdiom=idiom
            }
        })
        adapterSentence.setTextChanged(object:AddSentenceAdapter.ITouchTextChanged{
            override fun onTextChanged(text: String , position: Int) {
                viewModel.listRecords.value?.get(position)?.sentence=text
            }

        })


        //Observe
        viewModel.listRecords.observe(this) {
            viewModel.listIdioms.value?.let { it1 -> adapterSentence.setIdiomList(it1.toList()) }
            adapterSentence.submitList(it)
        }

        viewModel.listIdioms.observe(this){
            viewModel.getAllSentences()
        }
        //Observe
        viewModel.listCategory.observe(this) {
            refreshChipGroup(it)
        }
        viewModel.selectedCategory.observe(this){
            otherRefreshChipGroup(it)
        }


        // Comprobar si la URI no es nula
        if (imageUri != null) {
            // Cargar la imagen en la ImageView
            this.uriImageCut = Uri.parse(imageUri)
            binding.ivAddimage.setImageURI(this.uriImageCut)
            Log.e("YYY","${this.uriImageCut}")
        }

        //Results launchers
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                imageReceived(result)
            }

        binding.ivAddimage.setOnClickListener {
            val popupMenu = PopupMenu(applicationContext, binding.cvAddImage)
            popupMenu.menuInflater.inflate(R.menu.menu_add_image, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.menu_add_gallery) {
                    choiceGalleryImage()
                } else if (menuItem.itemId == R.id.menu_delete) {
                    binding.ivAddimage.setImageDrawable(this.getDrawable(R.drawable.baseline_add_photo_alternate_24))
                    uriImageCut = null
                } else if (menuItem.itemId == R.id.menu_add_camera) {
                    val intent = Intent(this, ActivityCamera::class.java)
                    startActivity(intent)
                }
                false
            }
            popupMenu.show()
        }
        binding.addLanguage.setOnClickListener {
            viewModel.addDateLIstSentence()
        }
        binding.addLabel.setOnClickListener {
            liAddCategory()
        }
        binding.addRecord.setOnClickListener {
            if (viewModel.listRecords.value!=null) {
                if(this.uriImageCut==null){
                    this.uriImageCut= Uri.parse("no")
                }
                addRecordToBD(
                    this.uriImageCut.toString() ,
                    viewModel.listRecords.value!![0].sentence ,
                    viewModel.listRecords.value!![0].idIdiom

                )
                finish()
            }
        }
        adapterSentence.setClickDelete(object :AddSentenceAdapter.ITouchDelete{
            override fun onClickDelete(record: Records,position:Int) {
                viewModel.deleteSentence(record.id)
            }

        })

        viewModel.getAllIdioms()


    }

    private fun choiceGalleryImage() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                cutImage(contentUri, Uri.fromFile(file))
            } else {
                Toast.makeText(
                    this@ActivityNewRecords,
                    R.string.error_al_obtener_la_imagen,
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this@ActivityNewRecords,
                R.string.error_al_obtener_la_imagen,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun cutImage(uri1: Uri, uri2: Uri) {
        try {
            UCrop.of(uri1, uri2)
                .withAspectRatio(3f, 3f)
                .withMaxResultSize(
                    ImageTools.ANCHO_DE_FOTO_A_SUBIR,
                    ImageTools.ALTO_DE_FOTO_A_SUBIR
                )
                .start(this)
        } catch (e: Exception) {
            Toast.makeText(
                this@ActivityNewRecords,
                getString(R.string.error),
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }

        //UCrop
        if (requestCode == UCrop.REQUEST_CROP) {


            if (data != null) {
                imageCropped(data)
            } else {
                Toast.makeText(
                    this@ActivityNewRecords,
                    getString(R.string.error_al_obtener_la_imagen),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        if (requestCode == UCrop.RESULT_ERROR) {
            FancyToast.makeText(
                this@ActivityNewRecords,
                getString(R.string.error_al_obtener_la_imagen),
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }

    private fun liAddCategory(){
        val inflater = LayoutInflater.from(binding.root.context)
        liCategoryBinding = LiAddCategoryBinding.inflate(inflater)
        val builder = AlertDialog.Builder(binding.root.context)
        builder.setView(liCategoryBinding.root)
        val alertDialog = builder.create()

        viewModel.refreshCategories()

        liCategoryBinding.ivAdd.setOnClickListener{
            if(liCategoryBinding.tiet.text.toString().isNotEmpty()){
                viewModel.addCategory(liCategoryBinding.tiet.text.toString())
                liCategoryBinding.tiet.setText("")
            }
        }

        liCategoryBinding.ivClose.setOnClickListener{
            alertDialog.dismiss()
        }

        //Finished
        alertDialog.setCancelable(false)
        alertDialog.window!!.setGravity(Gravity.CENTER)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

    }

    private fun refreshChipGroup(list : MutableList<Category>){
        liCategoryBinding.chipGroup.removeAllViews()
        for (e in list){
            val chip = Chip(this)
            chip.text = e.categories
            chip.isCheckable = true
            chip.isCloseIconVisible = true
            if(viewModel.selectedCategory.value.toString().contains(e.categories)){
                chip.isChecked=true
            }
            chip.setOnCloseIconClickListener{
                alert_borrar(chip,e.id)
            }
            chip.setOnClickListener {
                if (chip.isChecked){
                viewModel.selectedCategory(e.id,e.categories)
                } else{
                    viewModel.deleteCategory(e.id)
                }
            }
            liCategoryBinding.chipGroup.addView(chip)

        }
    }

    private fun alert_borrar(chip: Chip,id:Int) {
        //init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.Borrar))
        builder.setMessage(R.string.al_borrar)
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.Aceptar) { dialog , _ ->
            //finish the activity
            borrar_chip(chip,id)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.Cancelar){ dialog , _ ->
            //finish the activity
            dialog.dismiss()
        }

        //create the alert dialog and show it
        builder.create().show()
    }

    private fun borrar_chip(chip: Chip,id:Int){
        liCategoryBinding.chipGroup.removeView(chip)
        viewModel.deleteCategoryRecord(id)
        viewModel.deleteCategories(id)
        viewModel.deleteCategory(id)
    }

    private fun otherRefreshChipGroup(list : MutableList<Category>){
        binding.chipGroup.removeAllViews()
        for (e in list){
            val chip = Chip(this)
            chip.text = e.categories
            chip.isCheckable = true
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener{
               viewModel.deleteCategory(e.id)
            }

            binding.chipGroup.addView(chip)

        }
    }
    fun addRecordToBD(image:String,sentence:String,idIdiom:String){
        viewModel.insertRecord(image,sentence,idIdiom)

    }

}