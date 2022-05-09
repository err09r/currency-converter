package com.app.currencyconverter.presentation.screens

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.SpinnerAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.currencyconverter.constants.CommonConstants.DATE_PATTERN
import com.app.currencyconverter.constants.CommonConstants.DEFAULT_CURRENCY_CODE
import com.app.currencyconverter.constants.CommonConstants.UPDATE_DATE_KEY
import com.app.currencyconverter.domain.models.CurrencyItem
import com.app.currencyconverter.extensions.formatToString
import com.app.currencyconverter.extensions.hide
import com.app.currencyconverter.extensions.show
import com.app.currencyconverter.helpers.ErrorType
import com.app.currencyconverter.helpers.Mode
import com.app.currencyconverter.helpers.UiState
import com.app.currencyconverter.presentation.viewmodel.MainViewModel
import com.app.feature_converter.R
import com.app.feature_converter.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var preferences: SharedPreferences

    private var usdItemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListeners()
        refreshEffectiveDate()
        setUpViewModel()
    }

    private fun initListeners() {
        binding.etAmountToConvert.addTextChangedListener {
            performConversation()
        }

        preferences.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == UPDATE_DATE_KEY) {
                refreshEffectiveDate()
            }
        }
    }

    private fun refreshEffectiveDate() {
        val defaultDate = Calendar.getInstance().time.formatToString(DATE_PATTERN)
        val date = preferences.getString(UPDATE_DATE_KEY, defaultDate) ?: defaultDate
        binding.tvEffectiveDate.text = getString(R.string.effective_date, date)
    }

    private fun performConversation() {
        val amount = binding.etAmountToConvert.text.toString()
        val fromCurrencyCode = binding.topSpinner.selectedItem as String
        val toCurrencyCode = binding.bottomSpinner.selectedItem as String
        mainViewModel.convert(
            amountString = amount,
            fromCurrencyCode = fromCurrencyCode,
            toCurrencyCode = toCurrencyCode
        )
    }

    private fun setUpViewModel() {
        mainViewModel.loadData()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    mainViewModel.uiState.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                binding.errorLayout.hide()
                                binding.progressBar.hide()
                                binding.mainLayout.show()

                                handleConnectionMode(mode = state.mode)
                                val data = state.content
                                setMainCurrencies(data)
                                setUpSpinners(data)
                            }
                            is UiState.Error -> {
                                binding.progressBar.hide()
                                binding.errorLayout.apply {
                                    text = handleErrorMessage(error = state.error)
                                    show()
                                }
                            }
                            is UiState.Loading -> {
                                binding.progressBar.show()
                            }
                        }
                    }
                }

                launch {
                    mainViewModel.conversionResult.collectLatest { result ->
                        binding.tvResultAmount.text = String.format(Locale.US, "%.4f", result)
                    }
                }
            }
        }
    }

    private fun handleConnectionMode(mode: Mode) {
        if (mode == Mode.OFFLINE) {
            Snackbar.make(
                binding.root,
                getString(R.string.no_connection_snackbar),
                Snackbar.LENGTH_LONG
            ).apply {
                animationMode = Snackbar.ANIMATION_MODE_SLIDE
                show()
            }
        }
    }

    private fun handleErrorMessage(error: ErrorType): String {
        return when (error) {
            ErrorType.NETWORK -> getString(R.string.error_network)
            ErrorType.DATABASE -> getString(R.string.error_database)
            ErrorType.UNKNOWN -> getString(R.string.error_unknown)
        }
    }

    private fun setMainCurrencies(currencyItems: List<CurrencyItem>) {
        val mainCurrencyRates =
            currencyItems.filter { it.currencyCode in mainCurrencyCodes }.map { it.rate.toString() }

        binding.apply {
            tvDollarRate.text = mainCurrencyRates[0]
            tvEuroRate.text = mainCurrencyRates[1]
            tvPoundRate.text = mainCurrencyRates[2]
            tvYenRate.text = mainCurrencyRates[3]
        }
    }

    private fun setUpSpinners(currencyItems: List<CurrencyItem>) {
        usdItemPosition = currencyItems.indexOfFirst { it.currencyCode == USD_CURRENCY_CODE } + 1

        val spinnerEntries = listOf(DEFAULT_CURRENCY_CODE) + currencyItems.map { it.currencyCode }
        val adapter = ArrayAdapter(
            this@MainActivity,
            android.R.layout.simple_spinner_dropdown_item,
            spinnerEntries
        )

        val titles = currencyItems.map { it.currencyTitle }
        setUpTopSpinner(spinnerAdapter = adapter, data = titles)
        setUpBottomSpinner(spinnerAdapter = adapter, data = titles)
    }

    private fun setUpTopSpinner(spinnerAdapter: SpinnerAdapter, data: List<String>) {
        binding.topSpinner.apply {

            adapter = spinnerAdapter
            setSelection(DEFAULT_CURRENCY_POSITION)

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    binding.tvTopCurrencyTitle.text = handleSpinnerValueChanges(
                        selectedItem = selectedItem as String,
                        data = data,
                        position = position
                    )

                    if (position == binding.bottomSpinner.selectedItemPosition) {
                        binding.bottomSpinner.handleSpinnerSelection()
                    }

                    performConversation()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    setSelection(DEFAULT_CURRENCY_POSITION)
                }
            }
        }
    }

    private fun setUpBottomSpinner(spinnerAdapter: SpinnerAdapter, data: List<String>) {
        binding.bottomSpinner.apply {

            adapter = spinnerAdapter

            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    binding.tvBottomCurrencyTitle.text =
                        handleSpinnerValueChanges(
                            selectedItem = selectedItem as String,
                            data = data,
                            position = position
                        )

                    if (position == binding.topSpinner.selectedItemPosition) {
                        binding.topSpinner.handleSpinnerSelection()
                    }

                    performConversation()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    setSelection(DEFAULT_CURRENCY_POSITION)
                }
            }
        }
    }

    private fun Spinner.handleSpinnerSelection() {
        if (selectedItem == USD_CURRENCY_CODE) {
            setSelection(DEFAULT_CURRENCY_POSITION)
        } else {
            setSelection(usdItemPosition)
        }
    }

    private fun handleSpinnerValueChanges(
        selectedItem: String,
        data: List<String>,
        position: Int
    ): String {
        return if (selectedItem == DEFAULT_CURRENCY_CODE) {
            getString(R.string.zloty_title)
        } else {
            data[position - 1]
        }
    }

    private companion object {
        private const val DEFAULT_CURRENCY_POSITION = 0
        private const val USD_CURRENCY_CODE = "USD"
    }
}

private val mainCurrencyCodes = arrayOf("USD", "EUR", "GBP", "JPY")