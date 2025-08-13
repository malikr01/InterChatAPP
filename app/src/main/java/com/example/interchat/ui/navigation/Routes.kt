package com.example.interchat.ui.navigation

object Routes {
    // Auth
    const val LOGIN = "login"
    const val REGISTER = "auth/register"
    const val FORGOT   = "auth/forgot"

    // Bottom bar rotaları
    const val HOME = "home"
    const val ACCOUNTS = "accounts"
    const val CHAT_AI = "chat_ai"
    const val TRANSACTIONS_HOME = "transactions/home"
    const val FAQ = "faq"

    // Personal info
    const val PERSONAL_INFO = "personal_info"
    const val BALANCE = "balance"
    const val TX_LIST = "transactions_list"
    const val CARD_INFO = "card_info"
    const val RECENT_OPS = "recent_ops"

    // Detaylar
    const val ARG_ID = "id"
    const val ACCOUNT_DETAIL_ROUTE = "account_detail/{$ARG_ID}"
    fun accountDetail(id: String) = "account_detail/$id"
    const val CARD_DETAIL = "card_detail"

    // İşlemler alt sayfaları
    const val TX_TRANSFER = "transactions/transfer"
    const val TX_BILL = "transactions/bill"
    const val TX_TOPUP = "transactions/topup"
    const val TX_SCHEDULED = "transactions/scheduled"
    const val TX_HISTORY = "transactions/history"
    const val TX_CALCULATORS = "transactions/calculators"

    // Debug-only
    const val DEBUG_TOOLS = "debug/tools"
}
