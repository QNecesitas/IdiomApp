package com.estholon.idiomapp

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.PopupMenu
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import com.estholon.idiomapp.adapters.EditSentenceAdapter
import com.estholon.idiomapp.auxiliary.ImageTools
import com.estholon.idiomapp.data.Category
import com.estholon.idiomapp.data.Idioms
import com.estholon.idiomapp.data.Records
import com.estholon.idiomapp.databinding.ActivityEditRecordBinding
import com.estholon.idiomapp.databinding.LiAddCategoryBinding
import com.estholon.idiomapp.viewmodels.EditRecordViewModel
import com.estholon.idiomapp.viewmodels.EditRecordViewModelFactory
import com.google.android.material.chip.Chip
import com.shashank.sony.fancytoastlib.FancyToast
import com.yalantis.ucrop.UCrop

class ActivityEditRecord : AppCompatActivity() {

    private lateinit var binding: ActivityEditRecordBinding

    private lateinit var liCategoryBinding: LiAddCategoryBinding

    //Results launchers
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    private var uriImageCut: Uri? = null

    //View Model
    private val viewModel: EditRecordViewModel by viewModels {
        EditRecordViewModelFactory((application as IdiomApp).database.recordsDao(),(application as IdiomApp).database.translationsDao(),(application as IdiomApp).database.record_categoriesDao(),(application as IdiomApp).database.idiomsDao(),(application as IdiomApp).database.categoriesDao())
    }

    //Recycler
    private lateinit var alRecord: MutableList<Records>
    private lateinit var adapterSentence: EditSentenceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get id from ActivityRecord
        val idRecord=intent.getIntExtra("idRecord",0)

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
                    val intent = Intent(this@ActivityEditRecord, ActivityRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_new_record -> {
                    val intent = Intent(this@ActivityEditRecord, ActivityNewRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_cards -> {
                    val intent = Intent(this@ActivityEditRecord, ActivityCard::class.java)
                    startActivity(intent)
                }

                R.id.menu_match -> {
                    val intent = Intent(this@ActivityEditRecord, ActivityMatch::class.java)
                    startActivity(intent)
                }

                R.id.menu_writing -> {
                    val intent = Intent(this@ActivityEditRecord, ActivityWriting::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        //Recycler
        binding.rvLanguage.setHasFixedSize(true)
        alRecord = mutableListOf()
        adapterSentence = EditSentenceAdapter(this , mutableListOf())
        binding.rvLanguage.adapter = adapterSentence
        adapterSentence.setSpinnerListener(object:EditSentenceAdapter.SpinnerListener{
            override fun onSpinnerClick(idiom: String,position:Int) {
                viewModel.listRecords.value?.get(position)?.idIdiom=idiom
            }
        })
        adapterSentence.setTextChanged(object:EditSentenceAdapter.ITouchTextChanged{
            override fun onTextChanged(text: String , position: Int) {
                viewModel.listRecords.value?.get(position)?.sentence=text
            }

        })

        //Observe
        viewModel.listPhoto.observe(this){
            val image=viewModel.listRecords.value?.get(0)?.image
            val imageUri= Uri.parse(image)
            binding.ivAddimage.setImageURI(imageUri)
            this.uriImageCut=Uri.parse(image)
        }
        viewModel.listRecords.observe(this) {
            viewModel.listIdioms.value?.let { it1 -> adapterSentence.setIdiomList(it1.toList() as MutableList<Idioms>) }
            adapterSentence.submitList(it)

        }

        viewModel.listCategorySelected.observe(this){
            otherRefreshChipGroup(it)
        }



        viewModel.listIdioms.observe(this){
            viewModel.getRecord(idRecord)
        }

        viewModel.listCategory.observe(this) {
            refreshChipGroup(it)
        }

        //Results launchers
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                imageReceivedGallery(result)
            }

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                imageReceivedCamera(result)
            }

        //Listeners
        binding.ivAddimage.setOnClickListener {
            val popupMenu = PopupMenu(applicationContext, binding.cvAddImage)
            popupMenu.menuInflater.inflate(R.menu.menu_add_image, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_add_gallery -> {
                        choiceGalleryImage()
                    }
                    R.id.menu_delete -> {
                        binding.ivAddimage.setImageDrawable(this.getDrawable(R.drawable.outline_image_24))
                        uriImageCut = null
                    }
                    R.id.menu_add_camera -> {
                        val intent = Intent(this, ActivityCamera::class.java)
                        cameraLauncher.launch(intent)
                    }
                }
                false
            }
            popupMenu.show()
        }
        binding.addLanguage.setOnClickListener {
            if((viewModel.listRecords.value?.size ?: 6) < 5) {
                viewModel.addDateLIstSentence()
            }else{
                FancyToast.makeText(
                    this@ActivityEditRecord,
                    getString(R.string.maximo_traducciones),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.WARNING,
                    false
                ).show()
            }
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
                    viewModel.listRecords.value!![0].idIdiom,
                    idRecord

                )
            }
        }
        binding.ivDelete.setOnClickListener {
            alertDeleteRecord(idRecord)
        }
        adapterSentence.setClickDelete(object :EditSentenceAdapter.ITouchDelete{
            override fun onClickDelete(record: Records,position:Int) {
                viewModel.deleteSentence(record.id)
            }

        })

        viewModel.getAllIdioms()
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
    //Image logic
    private fun choiceGalleryImage() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun imageReceivedGallery(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val contentUri = data?.data

            val file = ImageTools.createTempImageFile(
                this@ActivityEditRecord,
                ImageTools.getHoraActual("yyMMddHHmmss")
            )

            if (contentUri != null) {
                cutImage(contentUri, Uri.fromFile(file))
            } else {
                FancyToast.makeText(
                    this@ActivityEditRecord,
                    getString(R.string.error_al_obtener_la_imagen,),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            }

        } else {
            FancyToast.makeText(
                this@ActivityEditRecord,
                getString(R.string.error_al_obtener_la_imagen,),
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
    }

    private fun imageReceivedCamera(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val contentUri = data?.extras?.getString("imageUri")

            if (contentUri != null) {
                this.uriImageCut = Uri.parse(contentUri)
                binding.ivAddimage.setImageURI(this.uriImageCut)
            } else {
                FancyToast.makeText(
                    this@ActivityEditRecord,
                    getString(R.string.maximo_traducciones),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.WARNING,
                    false
                ).show()
                FancyToast.makeText(
                    this@ActivityEditRecord,
                    getString(R.string.error_al_obtener_la_imagen,),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.WARNING,
                    false
                ).show()
            }

        } else {
            FancyToast.makeText(
                this@ActivityEditRecord,
                getString(R.string.error_al_obtener_la_imagen,),
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
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
            FancyToast.makeText(
                this@ActivityEditRecord,
                getString(R.string.error_al_obtener_la_imagen),
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
    }

    private fun imageCropped(data: Intent?) {
        if (data != null) {
            this.uriImageCut = UCrop.getOutput(data)
            binding.ivAddimage.setImageURI(this.uriImageCut)
        }
    }
    //Categories logic
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
//TODO los chips no se estan dando cuenta que hay una lista de categoria y no se ponen checkeados
    private fun refreshChipGroup(list : MutableList<Category>){
        liCategoryBinding.chipGroup.removeAllViews()
        val selectCategory= viewModel.listCategorySelected.value?: emptyList()
        for (e in list){
            val chip = Chip(this)
            chip.text = e.categories
            chip.isCheckable = true
            chip.isCloseIconVisible = true
            chip.isChecked = selectCategory.any { it.categories == e.categories }
            chip.setOnCloseIconClickListener{
                alertDelete(chip,e.id)
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

    private fun alertDelete(chip: Chip, id:Int) {
        //init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.Esta_seguro_borrar))
        builder.setMessage(R.string.al_borrar)
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.Aceptar) { dialog , _ ->
            //finish the activity
            deleteChip(chip,id)
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.Cancelar){ dialog , _ ->
            //finish the activity
            dialog.dismiss()
        }

        //create the alert dialog and show it
        builder.create().show()
    }

    private fun deleteChip(chip: Chip, id:Int){
        liCategoryBinding.chipGroup.removeView(chip)
        viewModel.deleteCategoryRecord(id)
        viewModel.deleteCategories(id)
        viewModel.deleteCategory(id)
    }

    //Accept button
    private fun addRecordToBD(image:String,sentence:String,idIdiom:String,idRecord:Int){
        if(isInformationGood()) {
            viewModel.deleteRecord(idRecord)
            viewModel.insertRecord(image, sentence, idIdiom)
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
    private fun isInformationGood(): Boolean{
        var emptyElement = false
        var repeatedElements = false
        val idiomsSelected = mutableListOf<String>()

        if(viewModel.listRecords.value != null) {
            for (element in viewModel.listRecords.value!!) {
                if(element.sentence.isBlank()){
                    emptyElement = true
                }
                if(idiomsSelected.contains(element.idIdiom)){
                    repeatedElements = true
                }else{
                    idiomsSelected.add(element.idIdiom)
                }
            }
        }

        if(emptyElement){
            FancyToast.makeText(
                this@ActivityEditRecord,
                getString(R.string.no_debe_espacios_vacios),
                FancyToast.LENGTH_LONG,
                FancyToast.ERROR,
                false).show()
        }else if(repeatedElements){
            FancyToast.makeText(
                this@ActivityEditRecord,
                getString(R.string.no_debe_idiomas),
                FancyToast.LENGTH_LONG,
                FancyToast.ERROR,
                false).show()
        }

        return (!emptyElement) && (!repeatedElements)
    }//Activity utils
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
                FancyToast.makeText(
                    this@ActivityEditRecord,
                    getString(R.string.error_al_obtener_la_imagen),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        }
        if (requestCode == UCrop.RESULT_ERROR) {
            FancyToast.makeText(
                this@ActivityEditRecord,
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
    private fun alertDeleteRecord(idRecord: Int) {
        //init alert dialog
        val builder = android.app.AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.Esta_seguro_borrar_record))
        builder.setMessage(R.string.al_borrar_record)
        //set listeners for dialog buttons
        builder.setPositiveButton(R.string.Aceptar) { dialog , _ ->
            //finish the activity
           viewModel.deleteRecord(idRecord)
            dialog.dismiss()
            finish()
        }
        builder.setNegativeButton(R.string.Cancelar){ dialog , _ ->
            //finish the activity
            dialog.dismiss()
        }

        //create the alert dialog and show it
        builder.create().show()
    }


}