package com.estholon.idiomapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import com.estholon.idiomapp.databinding.ActivityCardBinding

class ActivityCard : AppCompatActivity() {

    private lateinit var binding: ActivityCardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //NavigationDrawer
        binding.ivIconSetting.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        val itemToInvisible = binding.navigationView.menu.findItem(R.id.menu_new_record)
        itemToInvisible.isVisible = false
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_record -> {
                    val intent = Intent(this@ActivityCard, ActivityRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_new_record -> {
                    val intent = Intent(this@ActivityCard, ActivityNewRecords::class.java)
                    startActivity(intent)
                }

                R.id.menu_cards -> {
                    val intent = Intent(this@ActivityCard, ActivityCard::class.java)
                    startActivity(intent)
                }

                R.id.menu_match -> {
                    val intent = Intent(this@ActivityCard, ActivityMatch::class.java)
                    startActivity(intent)
                }

                R.id.menu_writing -> {
                    val intent = Intent(this@ActivityCard, ActivityWriting::class.java)
                    startActivity(intent)
                }
            }
            true
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish()
        }
    }
}