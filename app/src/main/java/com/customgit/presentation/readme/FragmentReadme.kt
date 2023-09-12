package com.customgit.presentation.readme

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.customgit.core.data_classes.ReadmeResponse
import com.customgit.databinding.FragmentReadmeBinding
import com.customgit.viewModels.user_info.UserInfoViewModel
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.nio.charset.Charset

class FragmentReadme : Fragment() {

    private lateinit var binding: FragmentReadmeBinding
    private var repo: String? = null
    private val viewModel: UserInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReadmeBinding.inflate(layoutInflater)
        arguments?.let {
            repo = it.getString("repo") // Получаем репозиторий из аргументов
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getReadme()
        setupBackButton()

        viewModel.currentReadme.observe(viewLifecycleOwner) { readme ->
            displayReadmeHtml(readme!!)
        }
    }

        //получ-е readme.md
        private fun getReadme() {
            viewModel.getReadmeForRepository(repo ?: "")
        }

        @SuppressLint("SetJavaScriptEnabled")
        private fun displayReadmeHtml(readme: ReadmeResponse) {
            val decodedContent = Base64.decode(readme.content, Base64.DEFAULT)
            val markdownContent = String(decodedContent, Charset.forName("UTF-8"))

            // Парс. Markdown-контента
            val parser = Parser.builder().build()
            val document = parser.parse(markdownContent)

            // Извлеч-е текста с переносами строк из документа
            val textContent = extractTextFromMarkdown(document)

            // Стили для изображений
            val htmlWithStyles = """
        <html>
        <head>
            <style>
                /* Стиль для изображений */
                img {
                    max-width: 100%;
                    height: auto;
                }
            </style>
        </head>
        <body>
            $textContent
        </body>
        </html>
    """.trimIndent()

            binding.webview.settings.javaScriptEnabled = true

            // Загрузка текстового контента со стилями в webview
            binding.webview.loadDataWithBaseURL(null, htmlWithStyles, "text/html", "UTF-8", null)
        }

        //использ-е библ. "commonmark-java" для преобразов-я Markdown в HTML
        private fun extractTextFromMarkdown(node: Node): String {
            val renderer = HtmlRenderer.builder().build()
            return renderer.render(node)
        }

        //кнопка назад
        private fun setupBackButton() {
            binding.back.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }