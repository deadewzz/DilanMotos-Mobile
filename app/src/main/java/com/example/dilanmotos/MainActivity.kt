package com.example.dilanmotos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dilanmotos.ui.UsuarioActivity
import com.example.dilanmotos.ui.ProductoActivity
import com.example.dilanmotos.ui.MarcaActivity
import com.example.dilanmotos.ui.MotoActivity
import com.example.dilanmotos.ui.ChatIaActivity
import com.example.dilanmotos.ui.CotizacionActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(R.layout.main_menu)

        val rootLayout = findViewById<View>(R.id.mainMenuRoot)
        rootLayout?.let { view ->
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // 3. Configuración de clics para los módulos clásicos
        findViewById<View>(R.id.btnSeccionUsuarios)?.setOnClickListener {
            startActivity(Intent(this, UsuarioActivity::class.java))
        }

        findViewById<View>(R.id.btnSeccionProducto)?.setOnClickListener {
            startActivity(Intent(this, ProductoActivity::class.java))
        }

        findViewById<View>(R.id.btnSeccionCotizacion)?.setOnClickListener {
            startActivity(Intent(this, CotizacionActivity::class.java))
        }

        findViewById<View>(R.id.btnSeccionMarca)?.setOnClickListener {
            startActivity(Intent(this, MarcaActivity::class.java))
        }

        findViewById<View>(R.id.btnSeccionMoto)?.setOnClickListener {
            startActivity(Intent(this, MotoActivity::class.java))
        }

        findViewById<View>(R.id.btnRecomendacionesIA)?.setOnClickListener {
            val intent = Intent(this, ChatIaActivity::class.java).apply {
                putExtra("MODELO_MOTO", "Moto General")
            }
            startActivity(intent)
        }
    }
}