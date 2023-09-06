package com.customgit.presentation.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.customgit.R
import com.customgit.core.utils.launchAndCollectIn
import com.customgit.core.utils.toast
import com.customgit.data.auth.app_auth.TokenStorage
import com.customgit.databinding.FragmentAuthorizationBinding
import com.customgit.presentation.user.repositories.FragmentUserRepos
import com.customgit.viewModels.auth.AuthViewModel
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse

class FragmentAuth : Fragment() {

    private lateinit var binding: FragmentAuthorizationBinding
    private val authViewModel: AuthViewModel by viewModels()

    //открыв-я общий интент для обработки customTabsIntent и authRequest
    private val getAuthResponse =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val dataIntent = it.data ?: return@registerForActivityResult
            handleAuthResponseIntent(dataIntent)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorizationBinding.inflate(inflater)

        Glide.with(requireContext())
            .load("https://www.dropbox.com/scl/fi/upwcorulo28exvrm6qnor/GITBlogo.jpg?rlkey=84kc0s42s3dlxd9fxzp6ptsu6&raw=1")
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.gitLogo)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authorization()
    }

    override fun onResume() {
        super.onResume()
        editUserName()
    }

    private fun authorization() = with(authViewModel) {

        binding.getToken.setOnClickListener {
            this.openLoginPage()
        }
        loadingFlow.launchAndCollectIn(viewLifecycleOwner) {
            updateIsLoading(it)
        }
        openAuthPageFlow.launchAndCollectIn(viewLifecycleOwner) {
            openAuthPage(it)
        }
        toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            toast(it)
        }
        authSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {

            requireActivity().supportFragmentManager
                .beginTransaction().replace(R.id.frame, FragmentUserRepos.newInstance2())
                .addToBackStack(null).commit()
        }
    }

    //добавл-е, измен-е имени пользователя
    private fun editUserName() = with(binding) {

        val usernameFromUser = gitUsername
        val saveButton = save
        val deleteButton = delete

        // обработчик для кнопки SAVE NAME
        saveButton.setOnClickListener {
            val enteredUsername = usernameFromUser.text.toString()

            when (enteredUsername.isEmpty()) {
                true -> Toast.makeText(requireContext(), "Enter your name", Toast.LENGTH_SHORT)
                    .show()

                else -> {
                    TokenStorage.username = enteredUsername
                    saveButton.visibility = View.GONE
                    usernameFromUser.isEnabled = false
                    usernameFromUser.setTextColor(Color.parseColor("#FFFF0090"))
                    deleteButton.visibility = View.VISIBLE
                }
            }

            // обработчик для кнопки delete
            deleteButton.setOnClickListener {
                saveButton.visibility = View.VISIBLE
                usernameFromUser.isEnabled = true
                usernameFromUser.setTextColor(Color.parseColor("#B8A0AD"))
                deleteButton.visibility = View.GONE
            }

            // Проверка для delete изначально
            if (usernameFromUser.text.isNotEmpty()) {
                deleteButton.visibility = View.VISIBLE
            } else {
                deleteButton.visibility = View.GONE
            }
        }
    }

    private fun updateIsLoading(isLoading: Boolean) = with(binding) {
        getToken.isVisible = !isLoading
        loginProgress.isVisible = isLoading
    }

    private fun openAuthPage(intent: Intent) {
        getAuthResponse.launch(intent)
    }

    //обмен кода пришедшего из getAuthResponse на токен
    private fun handleAuthResponseIntent(intent: Intent) {
        // ошибка из ответа. null - если все ок
        val exception =
            AuthorizationException.fromIntent(intent) //библ-й метод для обр-ки сообщ-я ошибок

        // запрос для обмена кода на токен, если произошла ошибка то null
        val tokenExchangeRequest =
            AuthorizationResponse.fromIntent(intent)//библ-й метод для обмена кода на токен
                ?.createTokenExchangeRequest()
        when {
            // авториз-я завершались ошибкой
            exception != null -> authViewModel.onAuthCodeFailed(exception)

            // авториз-я успешна, меняем код на токен
            tokenExchangeRequest != null ->
                authViewModel.onAuthCodeReceived(tokenExchangeRequest)
        }
    }
    companion object {
        fun newInstance() = FragmentAuth()
    }
}