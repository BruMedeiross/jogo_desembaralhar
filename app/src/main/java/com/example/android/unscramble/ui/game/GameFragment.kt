/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {

    // associar o ViewModel ao controlador de IU
    private val viewModel: GameViewModel by viewModels()

    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        //A função onCreateView() infla o XML do layout game_fragment usando o objeto de vinculação.
        binding = GameFragmentBinding.inflate(inflater, container, false)

        Log.d("GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}")

        return binding.root
    }

    //A função onViewCreated() configura os listeners de cliques no botão e atualiza a IU.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura um ouvinte de clique para os botões Enviar e Ignorar.
        binding.submit.setOnClickListener { onSubmitWord()}
        binding.skip.setOnClickListener { onSkipWord()}

        // Atualiza a  UI
        updateNextWordOnScreen()
        binding.score.text = getString(R.string.score, 0)
        binding.wordCount.text = getString(
            R.string.word_count, 0, MAX_NO_OF_WORDS)
    }

    //O método onSubmitWord() é o listener de clique do botão Submit.
    // Essa função exibe a próxima palavra embaralhada, limpa o campo de texto e aumenta a pontuação
    // e a contagem da palavra sem validar a palavra do jogador.
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (viewModel.nextWord()) {
                updateNextWordOnScreen()
            } else {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    // Pula a palavra atual sem alterar a pontuação.
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
            updateNextWordOnScreen()
        } else {
            showFinalScoreDialog()
        }
    }



    /*
     *  é uma função auxiliar que escolhe uma palavra aleatória da lista de palavras
     *  e embaralha as letras dela.
     */
    private fun getNextScrambledWord(): String {
        val tempWord = allWordsList.random().toCharArray()
        tempWord.shuffle()
        return String(tempWord)
    }

    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }


    /*
     * A usadas para reiniciar o jogo
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
        updateNextWordOnScreen()

    }

    /*
     * sair do jogo.
     */
    private fun exitGame() {
        activity?.finish()
    }


    override fun onDetach() {
        super.onDetach()
        Log.d("GameFragment", "GameFragment destroyed!")
    }


    /*
    * O método setErrorTextField() limpa o conteúdo do campo de texto e redefine o status do erro.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }






    // uma nova palavra embaralhada será exibida na inicialização do app.
    // de onde? -> vinda da view model.
    private fun updateNextWordOnScreen() {
        // antes - binding.textViewUnscrambledWord.text = currentScrambledWord
        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord
    }
}


/*metodos substituidos e reescrtos para a regra de negocio permanecer na viewmodel
 /*
    * O método onSubmitWord() é o listener de clique do botão Submit.
    * Essa função exibe a próxima palavra embaralhada, limpa o campo de texto e
    * aumenta a pontuação e a contagem da palavra sem validar a palavra do jogador.
    */
    private fun onSubmitWord() {
        currentScrambledWord = getNextScrambledWord()
        currentWordCount++
        score += SCORE_INCREASE
        binding.wordCount.text = getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
        binding.score.text = getString(R.string.score, score)
        setErrorTextField(false)
        updateNextWordOnScreen()
    }

    /*
     * O método onSkipWord() é o listener de clique do botão Skip.
     * Essa função atualiza a IU de forma semelhante ao método onSubmitWord(), mas não aumenta a pontuação.
     */
    private fun onSkipWord() {
        currentScrambledWord = getNextScrambledWord()
        currentWordCount++
        binding.wordCount.text = getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
        setErrorTextField(false)
        updateNextWordOnScreen()
    }
 */