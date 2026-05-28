package com.example.presentation.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.EmiApplication
import com.example.domain.model.AmortizationItem
import com.example.domain.model.EmiResult
import com.example.domain.model.SavedLoan
import com.example.domain.repository.LoanRepository
import com.example.domain.usecase.CalculateEmiUseCase
import com.example.domain.usecase.GenerateAmortizationScheduleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.data.remote.gemini.GeminiClient
import com.example.data.remote.gemini.GeminiRequest
import com.example.data.remote.gemini.Content
import com.example.data.remote.gemini.Part
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalculatorViewModel(private val repository: LoanRepository) : ViewModel() {

    // Use cases
    private val calculateEmiUseCase = CalculateEmiUseCase()
    private val generateAmortizationScheduleUseCase = GenerateAmortizationScheduleUseCase()

    // --- AI Loan Intelligent Suite States ---
    sealed class AiAnalysisState {
        object Idle : AiAnalysisState()
        object Loading : AiAnalysisState()
        data class Success(val analysis: String) : AiAnalysisState()
        data class Error(val message: String) : AiAnalysisState()
    }

    private val _aiAnalysisState = MutableStateFlow<AiAnalysisState>(AiAnalysisState.Idle)
    val aiAnalysisState = _aiAnalysisState.asStateFlow()

    data class ChatMessage(
        val isUser: Boolean,
        val text: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _chatLoading = MutableStateFlow(false)
    val chatLoading = _chatLoading.asStateFlow()

    // 1. Inputs for the General Calculator Screen
    val amountInput = MutableStateFlow("")
    val interestInput = MutableStateFlow("")
    val tenureInput = MutableStateFlow("")
    val tenureTypeInput = MutableStateFlow("Years") // "Years" or "Months"
    val processingFeeInput = MutableStateFlow("0")
    val prepaymentInput = MutableStateFlow("0")
    val startDateInput = MutableStateFlow("")

    // 2. Error validation states
    val amountError = MutableStateFlow<String?>(null)
    val interestError = MutableStateFlow<String?>(null)
    val tenureError = MutableStateFlow<String?>(null)

    // 3. Current active calculation outputs
    private val _emiResult = MutableStateFlow<EmiResult?>(null)
    val emiResult = _emiResult.asStateFlow()

    private val _amortizationSchedule = MutableStateFlow<List<AmortizationItem>>(emptyList())
    val amortizationSchedule = _amortizationSchedule.asStateFlow()

    // 4. Saved Loans history from Room Database (Reactive Flow)
    val savedLoans: StateFlow<List<SavedLoan>> = repository.getAllLoans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 5. App level configurations from DataStore
    val currencySymbol = repository.getCurrencyPreference()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "₹")

    val appTheme = repository.getThemePreference()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val isOnboarded = repository.isOnboarded()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 6. Comparison Screen Inputs & Results
    // Loan A
    val compareAmountA = MutableStateFlow("1500000")
    val compareInterestA = MutableStateFlow("8.5")
    val compareTenureA = MutableStateFlow("15")
    val compareTenureTypeA = MutableStateFlow("Years")
    // Loan B
    val compareAmountB = MutableStateFlow("1500000")
    val compareInterestB = MutableStateFlow("9.0")
    val compareTenureB = MutableStateFlow("15")
    val compareTenureTypeB = MutableStateFlow("Years")

    sealed class CompareResultState {
        object Idle : CompareResultState()
        data class Calculated(
            val emiA: Double,
            val emiB: Double,
            val interestA: Double,
            val interestB: Double,
            val totalPayableA: Double,
            val totalPayableB: Double,
            val comparisonText: String
        ) : CompareResultState()
    }

    private val _compareResult = MutableStateFlow<CompareResultState>(CompareResultState.Idle)
    val compareResult = _compareResult.asStateFlow()

    init {
        // Set default start date to current month/year
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        startDateInput.value = sdf.format(Calendar.getInstance().time)
        
        // Trigger default comparison calculation
        calculateComparison()
    }

    // --- Onboarding Operations ---
    fun completeOnboarding() {
        viewModelScope.launch {
            repository.setOnboarded(true)
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            repository.setOnboarded(false)
        }
    }

    // --- Settings Preferences Operations ---
    fun setCurrency(symbol: String) {
        viewModelScope.launch {
            repository.setCurrency(symbol)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            repository.setTheme(theme)
        }
    }

    // --- Main EMI Form Actions ---
    fun calculateEmi(): Boolean {
        // Validation check
        var isValid = true

        val amount = amountInput.value.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            amountError.value = "Loan amount must be greater than zero."
            isValid = false
        } else {
            amountError.value = null
        }

        val interest = interestInput.value.toDoubleOrNull()
        if (interest == null || interest < 0.0) {
            interestError.value = "Interest rate cannot be negative or empty."
            isValid = false
        } else {
            interestError.value = null
        }

        val tenure = tenureInput.value.toIntOrNull()
        if (tenure == null || tenure <= 0) {
            tenureError.value = "Tenure must be greater than zero."
            isValid = false
        } else {
            tenureError.value = null
        }

        if (!isValid) return false

        // Perform computation
        val fee = processingFeeInput.value.toDoubleOrNull() ?: 0.0
        val prepay = prepaymentInput.value.toDoubleOrNull() ?: 0.0
        val startD = startDateInput.value

        val result = calculateEmiUseCase(
            loanAmount = amount!!,
            annualInterestRate = interest!!,
            tenure = tenure!!,
            tenureType = tenureTypeInput.value,
            processingFee = fee,
            prepaymentAmount = prepay,
            startDateString = startD
        )

        _emiResult.value = result

        // Generate amortization schedule using resulted months
        _amortizationSchedule.value = generateAmortizationScheduleUseCase(
            loanAmount = result.principalAmount,
            annualInterestRate = result.annualRate,
            tenureMonths = result.tenureMonths,
            prepaymentAmount = result.prepaymentAmount,
            monthlyEmiCalculated = result.monthlyEmi
        )

        return true
    }

    fun resetForm() {
        amountInput.value = ""
        interestInput.value = ""
        tenureInput.value = ""
        tenureTypeInput.value = "Years"
        processingFeeInput.value = "0"
        prepaymentInput.value = "0"
        amountError.value = null
        interestError.value = null
        tenureError.value = null
        _emiResult.value = null
        _amortizationSchedule.value = emptyList()

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        startDateInput.value = sdf.format(Calendar.getInstance().time)
    }

    fun saveCurrentCalculation(title: String) {
        val currentEmiResult = _emiResult.value ?: return
        viewModelScope.launch {
            repository.insertLoan(
                SavedLoan(
                    title = title.ifEmpty { "Loan EMI Calculation" },
                    loanAmount = currentEmiResult.principalAmount,
                    interestRate = currentEmiResult.annualRate,
                    tenure = currentEmiResult.tenureMonths,
                    tenureType = "Months",
                    emi = currentEmiResult.monthlyEmi,
                    totalInterest = currentEmiResult.totalInterest,
                    totalPayment = currentEmiResult.totalPayment,
                    processingFee = currentEmiResult.processingFee,
                    prepaymentAmount = currentEmiResult.prepaymentAmount,
                    startDate = currentEmiResult.formattedEndDate
                )
            )
        }
    }

    fun deleteLoanItem(loan: SavedLoan) {
        viewModelScope.launch {
            repository.deleteLoan(loan)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllLoans()
        }
    }

    fun inspectSavedLoan(loan: SavedLoan) {
        // Load settings to make it active in results screen
        amountInput.value = loan.loanAmount.toString()
        interestInput.value = loan.interestRate.toString()
        tenureInput.value = loan.tenure.toString()
        tenureTypeInput.value = "Months"
        processingFeeInput.value = loan.processingFee.toString()
        prepaymentInput.value = loan.prepaymentAmount.toString()

        calculateEmi()
    }

    // --- Compare Loans Operations ---
    fun calculateComparison() {
        val amountA = compareAmountA.value.toDoubleOrNull() ?: 0.0
        val rateA = compareInterestA.value.toDoubleOrNull() ?: 0.0
        val tenureAVal = compareTenureA.value.toIntOrNull() ?: 0
        val tenureMonthsA = if (compareTenureTypeA.value == "Years") tenureAVal * 12 else tenureAVal

        val amountB = compareAmountB.value.toDoubleOrNull() ?: 0.0
        val rateB = compareInterestB.value.toDoubleOrNull() ?: 0.0
        val tenureBVal = compareTenureB.value.toIntOrNull() ?: 0
        val tenureMonthsB = if (compareTenureTypeB.value == "Years") tenureBVal * 12 else tenureBVal

        if (amountA <= 0.0 || tenureMonthsA <= 0 || amountB <= 0.0 || tenureMonthsB <= 0) {
            _compareResult.value = CompareResultState.Idle
            return
        }

        // Compute results
        val resA = calculateEmiUseCase(loanAmount = amountA, annualInterestRate = rateA, tenure = tenureMonthsA, tenureType = "Months")
        val resB = calculateEmiUseCase(loanAmount = amountB, annualInterestRate = rateB, tenure = tenureMonthsB, tenureType = "Months")

        var recommendation = ""
        if (resA.totalInterest < resB.totalInterest) {
            val diff = resB.totalInterest - resA.totalInterest
            val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
            recommendation = "Loan A saves ${currencySymbol.value} ${formatter.format(diff)} in total interest compared to Loan B."
        } else if (resB.totalInterest < resA.totalInterest) {
            val diff = resA.totalInterest - resB.totalInterest
            val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
            recommendation = "Loan B saves ${currencySymbol.value} ${formatter.format(diff)} in total interest compared to Loan A."
        } else {
            recommendation = "Both loans require the exact same total interest payment."
        }

        _compareResult.value = CompareResultState.Calculated(
            emiA = resA.monthlyEmi,
            emiB = resB.monthlyEmi,
            interestA = resA.totalInterest,
            interestB = resB.totalInterest,
            totalPayableA = resA.totalPayment,
            totalPayableB = resB.totalPayment,
            comparisonText = recommendation
        )
    }

    fun analyzeLoanWithAi() {
        val currentEmi = _emiResult.value ?: return
        _aiAnalysisState.value = AiAnalysisState.Loading

        viewModelScope.launch {
            try {
                val systemPrompt = "You are an expert AI Loan Consultant and financial analyst. Help the user understand their loan structure, decide if the interest premium is too high, and provide structured strategies on prepayment frequencies or refinancing options to minimize loan costs. Be concise, highly professional, encouraging, and use elegant spacing, standard Markdown and clear bullet points."
                
                val prompt = """
                    Here are my loan calculation details:
                    - Principal Loan Amount: ${currentEmi.principalAmount}
                    - Annual Interest Rate: ${currentEmi.annualRate}%
                    - Monthly EMI: ${currentEmi.monthlyEmi}
                    - Total Interest Payable: ${currentEmi.totalInterest}
                    - Total Payment (Principal + Interest): ${currentEmi.totalPayment}
                    - Loan Tenure: ${currentEmi.tenureMonths} months
                    - Processing Fee: ${currentEmi.processingFee}
                    - Prepayment Amount: ${currentEmi.prepaymentAmount}
                    
                    Please provide an expert analysis in 3 short, elegant sections:
                    1. 📊 STRUCTURAL HEALTH SCORE (Analyze the ratio of interest to principal. Warn if interest is excessively high compared to principal)
                    2. 💡 DYNAMIC PREPAYMENT SAVINGS (How much they can save by prepaying a small extra sum periodically)
                    3. 📈 EXPERT REFINANCE RECOMMENDATION (Is this rate/tenure optimal? What are general smart recommendations?)
                """.trimIndent()

                val apiRequest = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
                )

                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                val response = GeminiClient.service.generateContent(apiKey, apiRequest)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "No analysis text found."

                _aiAnalysisState.value = AiAnalysisState.Success(responseText)
                
                // Pre-populate chat history with introductory advisory message
                if (_chatMessages.value.isEmpty()) {
                    _chatMessages.value = listOf(
                        ChatMessage(
                            isUser = false,
                            text = "Hi! I am your AI Loan Advisor. Based on your current calculations, I can help you model extra prepayment scenarios, detail amortization questions, or build strategies. Ask me anything!"
                        )
                    )
                }
            } catch (e: Exception) {
                _aiAnalysisState.value = AiAnalysisState.Error(e.message ?: "Unknown error calling Gemini API")
            }
        }
    }

    fun sendChatMessage(messageText: String) {
        if (messageText.isBlank()) return
        val currentEmi = _emiResult.value
        val userMsg = ChatMessage(isUser = true, text = messageText)
        _chatMessages.value = _chatMessages.value + userMsg
        _chatLoading.value = true

        viewModelScope.launch {
            try {
                val systemPrompt = "You are an expert interactive AI Loan Consultant and financial intelligence advisor. Help the user dynamically solve interest calculations, prepayment schedules, mortgage strategies, and explain technical financial concepts with absolute clarity. Be crisp, professional, and friendly."
                
                val loanContextPrompt = if (currentEmi != null) {
                    """
                    Current active loan context calculated by the user:
                    - Principal: ${currentEmi.principalAmount}
                    - Interest Rate: ${currentEmi.annualRate}%
                    - Tenure: ${currentEmi.tenureMonths} months
                    - EMI: ${currentEmi.monthlyEmi}
                    - Total Interest: ${currentEmi.totalInterest}
                    - Total Payment: ${currentEmi.totalPayment}
                    """
                } else {
                    "No active loan calculated yet."
                }

                // Compile conversation history
                val conversationHistory = _chatMessages.value.takeLast(10).map { msg ->
                    val senderPrefix = if (msg.isUser) "User" else "Advisor"
                    "$senderPrefix: ${msg.text}"
                }.joinToString("\n")

                val fullPrompt = """
                    $loanContextPrompt
                    
                    Conversation History:
                    $conversationHistory
                    
                    Provide a concise, helpful response to the user's latest query.
                """.trimIndent()

                val apiRequest = GeminiRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = fullPrompt)))
                    ),
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
                )

                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                val response = GeminiClient.service.generateContent(apiKey, apiRequest)
                val aiResponseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "I'm sorry, I couldn't process that query."

                _chatMessages.value = _chatMessages.value + ChatMessage(isUser = false, text = aiResponseText)
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(isUser = false, text = "I encountered an issue connecting to the AI brain: ${e.message}")
            } finally {
                _chatLoading.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as EmiApplication
                return CalculatorViewModel(application.container.loanRepository) as T
            }
        }
    }
}
