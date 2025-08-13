package com.example.interchat.ui.navigation

object Routes {
    // Auth
    const val LOGIN = "login"

    // Bottom tabs
    const val HOME = "home"
    const val ACCOUNTS = "accounts"
    const val CHAT_AI = "chat_ai"                 // ✅ yeni sekme
    const val TRANSACTIONS_HOME = "transactions/home"
    const val FAQ = "faq"

    // Accounts & details
    const val ACCOUNT_DETAIL = "account_detail"
    const val CARD_DETAIL = "card_detail"
    const val ARG_ID = "id"
    const val ACCOUNT_DETAIL_ROUTE = "$ACCOUNT_DETAIL/{$ARG_ID}"
    fun accountDetail(id: String) = "$ACCOUNT_DETAIL/$id"

    // Personal info (opsiyonel alt sayfalar)
    const val PERSONAL_INFO = "personal_info"
    const val BALANCE = "balance"
    const val TX_LIST = "transactions_list"
    const val CARD_INFO = "card_info"
    const val RECENT_OPS = "recent_ops"

    // Transactions sub pages
    const val TX_TRANSFER  = "transactions/transfer"
    const val TX_BILL      = "transactions/bill"
    const val TX_TOPUP     = "transactions/topup"
    const val TX_SCHEDULED = "transactions/scheduled"
    const val TX_HISTORY   = "transactions/history"
    const val TX_CALCULATORS = "transactions/calculators"   // Hesaplamalar: İşlemler altında
}
