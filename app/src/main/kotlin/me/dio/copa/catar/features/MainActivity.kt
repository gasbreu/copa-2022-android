package me.dio.copa.catar.features

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import me.dio.copa.catar.extensions.observe
import me.dio.copa.catar.notification.scheduler.extensions.NotificationMatcherWorker
import me.dio.copa.catar.ui.theme.Copa2022Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeActions()
        setContent {
            Copa2022Theme {
                val state by viewModel.state.collectAsState()
                MainScreen(matches = state.matches, viewModel::toggleNotification)
            }
        }
    }

    private fun observeActions() {
        viewModel.action.observe(this) { action ->
            when (action) {
                is MainUiAction.MatchesNotFound -> handleMatchesNotFound()
                MainUiAction.Unexpected -> handleUnexpectedAction()
                is MainUiAction.DisableNotification ->
                    NotificationMatcherWorker.cancel(applicationContext, action.match)
                is MainUiAction.EnableNotification ->
                    NotificationMatcherWorker.start(applicationContext, action.match)
            }
        }
    }

    private fun handleUnexpectedAction() {
        Log.e("MainActivity", "Ação inesperada!")
        showMessage("Ação inesperada!")

    }

    private fun handleMatchesNotFound() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Nenhuma correspondência encontrada")
            .setMessage("Não foi possível encontrar nenhuma correspondência.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()

    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }

}
