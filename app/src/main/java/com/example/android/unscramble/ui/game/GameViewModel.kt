package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

//O código de tomada de decisão, como a descoberta da próxima palavra embaralhada,
//e os cálculos da pontuação e da contagem de palavras precisam estar no ViewModel.
class GameViewModel:ViewModel() {



    //a palavra embaralhada atual (currentScrambledWord),
    // contagem de palavras (currentWordCount),
    // pontuação (score)


    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score


    private val _currentWordCount =  MutableLiveData(0) // dados precisam ser editáveis.(dentro do viewModel)
    val currentWordCount:  LiveData<Int>      // leitura, mas não editáveis (fora do viewModel)
        get() = _currentWordCount


    //antes
    // private lateinit  var _currentScrambledWord : String
    // val currentScrambledWord: String


    private val _currentScrambledWord = MutableLiveData<String>() //somente os dados armazenados no objeto mudarão.
    //ANTES - val currentScrambledWord: LiveData<String> //é imutável
    // ANTES - get() = _currentScrambledWord
    //TALKBACK IMPLEMENTADO
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }


    // List of words used in the game
    private var wordsList: MutableList<String> = mutableListOf()
    private lateinit var currentWord: String

    init {
        getNextWord()
    }

    private fun getNextWord(){
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
            //acessar os dados em um objeto LiveData, use a propriedade .value
            _currentScrambledWord.value = String(tempWord)
            //antes ++_currentWordCount > use a função Kotlin inc() para aumentar o valor em um com segurança de tipo nulo.
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordsList.add(currentWord)
        }
    }

    //Re-initializes the game data to restart the game.
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }


    // Aumente a variável score
    private fun increaseScore() {
        _score.value = _score.value?.plus(SCORE_INCREASE)
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



    /*
* Retorna TRUE se a contagem de palavras atual for menor que MAX_NO_OF_WORDS.
* Atualiza a próxima palavra.
*/
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }





    //exemplificação - ciclo de vida
    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }
}

