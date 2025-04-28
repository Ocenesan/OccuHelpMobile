package com.example.occuhelp.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.occuhelp.databinding.DashboardMainBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: DashboardMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DashboardMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.menuButton.setOnClickListener{
            (binding.drawerLayout as DrawerLayout).openDrawer(GravityCompat.START)
        }
    }
}