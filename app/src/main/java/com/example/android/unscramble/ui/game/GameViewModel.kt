package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

//O código de tomada de decisão, como a descoberta da próxima palavra embaralhada,
//e os cálculos da pontuação e da contagem de palavras precisam estar no ViewModel.
class GameViewModel:ViewModel() {

    // List of words used in the game
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    //a palavra embaralhada atual (currentScrambledWord),
    // contagem de palavras (currentWordCount),
    // pontuação (score)


    private var _score = 0
    val score: Int
        get() = _score


    private var _currentWordCount = 0 // dados precisam ser editáveis.(dentro do viewModel)
    val currentWordCount: Int         // leitura, mas não editáveis (fora do viewModel)
        get() = _currentWordCount


    private lateinit  var _currentScrambledWord : String
    val currentScrambledWord: String
    get() = _currentScrambledWord

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

    fun getNextWord(){
        currentWord = allWordsList.random()        //Extraia uma palavra aleatória

        val tempWord = currentWord.toCharArray()   //Embaralhe os caracteres
        tempWord.shuffle()

        //caso em que a palavra embaralhada é igual à palavra não embaralhada
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        //Não mostre a mesma palavra duas vezes durante a partida.
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord = String(tempWord)
            ++_currentWordCount
            wordsList.add(currentWord)
        }
    }



    //valide a palavra do jogador e aumente a pontuação se o palpite estiver correto.
    // Isso atualizará a pontuação final na caixa de diálogo de aviso.
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    //Re-initializes the game data to restart the game.
    fun reinitializeData() {
        _score = 0
        _currentWordCount = 0
        wordsList.clear()
        getNextWord()
    }

    /*
* Retorna TRUE se a contagem de palavras atual for menor que MAX_NO_OF_WORDS.
* Atualiza a próxima palavra.
*/
    fun nextWord(): Boolean {
        return if (currentWordCount < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
    // Aumente a variável score
    private fun increaseScore() {
        _score += SCORE_INCREASE
    }
}

