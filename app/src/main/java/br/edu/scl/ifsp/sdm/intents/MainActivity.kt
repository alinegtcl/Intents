package br.edu.scl.ifsp.sdm.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.edu.scl.ifsp.sdm.intents.Extras.PARAMETER_EXTRA
import br.edu.scl.ifsp.sdm.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    //    Não é mais necessário, devido a retirada da obsolescência
//    companion object{
//        private const val PARAMETER_REQUEST_CODE = 0
//    }
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var parameterArl: ActivityResultLauncher<Intent>
    private lateinit var callPhonePermissionArl: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        parameterArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.getStringExtra(PARAMETER_EXTRA)?.also {
                        activityMainBinding.parameterTv.text = it
                    }
                }
            }

        callPhonePermissionArl =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                if (permissionGranted) {
                    callPhone()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.permission_required_to_call), Toast.LENGTH_SHORT
                    ).show()
                }
            }

        activityMainBinding.apply {
            parameterBt.setOnClickListener {
                val parameterIntent =
                    Intent(this@MainActivity, ParameterActivity::class.java).apply {
                        putExtra(PARAMETER_EXTRA, parameterTv.text)
                    }
                parameterArl.launch(parameterIntent)
                // startActivityForResult(parameterIntent, PARAMETER_REQUEST_CODE) obsoleto, no vídeo foi retirado
            }
        }
    }

//    Forma depreciada, no vídeo foi retirada
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PARAMETER_REQUEST_CODE && resultCode == RESULT_OK) {
//            data?.getStringExtra(PARAMETER_EXTRA)?.also {
//                activityMainBinding.parameterTv.text = it
//            }
//        }
//    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.openActivityMi -> {
                val parameterIntent = Intent("OPEN_PARAMETER_ACTIVITY_ACTION").apply {
                    putExtra(PARAMETER_EXTRA, activityMainBinding.parameterTv.text)
                }
                parameterArl.launch(parameterIntent)
                true
            }

            R.id.viewMi -> {
                val url = Uri.parse(activityMainBinding.parameterTv.text.toString())
                val browserIntent = Intent(ACTION_VIEW, url)
                startActivity(browserIntent)
                true
            }

            R.id.callMi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        callPhone()
                    } else {
                        callPhonePermissionArl.launch(CALL_PHONE)
                    }
                } else {
                    callPhone()
                }
                true
            }

            R.id.dialMi -> {
                true
            }

            R.id.pickMi -> {
                true
            }

            R.id.chooserMi -> {
                true
            }

            else -> {
                false
            }
        }
    }

    private fun callPhone() {
        startActivity(Intent(ACTION_CALL).apply {
            "tel: ${activityMainBinding.parameterTv.text}".also {
                data = Uri.parse(it)
            }
        })
    }
}