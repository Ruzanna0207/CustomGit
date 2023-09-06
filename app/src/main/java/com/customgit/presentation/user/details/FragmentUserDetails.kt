package com.customgit.presentation.user.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.customgit.core.data_classes.RemoteGithubUser
import com.customgit.databinding.FragmentUserDetailsBinding

class FragmentUserDetails : Fragment() {

    private lateinit var binding: FragmentUserDetailsBinding
    private var user: RemoteGithubUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserDetailsBinding.inflate(layoutInflater)
        arguments?.let {
            user = it.getParcelable("user") // Получаем пользователя из аргументов
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() = with(binding) {

        Glide.with(requireContext())
            .load(user?.avatarUrl)
            .circleCrop()
            .into(image)

        userName.text = user?.name
        userLocation.text = user?.location
        userLogin.text = user?.login

        //при нажатии на задний фон или крестик - фрагмент закроется
        clickInterceptor.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        //карточка остается без изменений
        card.setOnClickListener {}
    }
}
