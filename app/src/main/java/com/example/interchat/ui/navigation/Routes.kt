package com.example.interchat.ui.navigation

object Routes {
    // Giriş
    const val LOGIN = "login"

    // Alt bar (4 sekme)
    const val TABS_CHAT          = "tabs/chat"
    const val TABS_ACCOUNTS      = "tabs/accounts"
    const val TABS_TRANSACTIONS  = "tabs/transactions"
    const val TABS_FAQ           = "tabs/faq"

    // Hesaplar alt sayfaları
    const val BALANCE  = "balance"            // Hesap bakiyesi
    const val TX       = "transactions_list"  // Hesap hareketleri & geçmiş
    const val CARD     = "card_info"          // Kart limitleri & borç
    const val RECENT   = "recent_ops"         // Son işlemler & durum


        // ... mevcut sabitlerin aynen kalsın ...

        // İşlemler grafiği
        const val TX_HOME      = "transactions/home"
        const val TX_TRANSFER  = "transactions/transfer"
        const val TX_BILL      = "transactions/bill"
        const val TX_TOPUP     = "transactions/topup"
        const val TX_SCHEDULED = "transactions/scheduled"
        const val TX_HISTORY   = "transactions/history"


}
