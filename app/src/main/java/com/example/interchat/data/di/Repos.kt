package com.example.interchat.data.di

//Bütün ekranlar veriyi buradan çağırır
//mock ise finanse/mock altındakileri backend ise finance/remote daki verileri çeker.

import com.example.interchat.data.finance.mock.MockAccountRepository
import com.example.interchat.data.finance.mock.MockTransactionRepository
import com.example.interchat.data.finance.mock.MockTransferRepository
import com.example.interchat.data.finance.remote.RemoteAccountRepository
import com.example.interchat.data.finance.remote.RemoteTransactionRepository
import com.example.interchat.data.finance.remote.RemoteTransferRepository
import com.example.interchat.data.net.ApiClient
import com.example.interchat.data.net.DummyApiClient
import com.example.interchat.data.net.HttpApiClient
import com.example.interchat.domain.finance.AccountRepository
import com.example.interchat.domain.finance.TransactionRepository
import com.example.interchat.domain.finance.TransferRepository

object Repos {

    private val useRemote: Boolean = AppConfig.API_BASE_URL.isNotBlank()

    val api: ApiClient =
        if (useRemote) HttpApiClient(AppConfig.API_BASE_URL)
        else DummyApiClient()

    val accountRepo: AccountRepository =
        if (useRemote) RemoteAccountRepository(api) else MockAccountRepository()

    val transactionRepo: TransactionRepository =
        if (useRemote) RemoteTransactionRepository(api) else MockTransactionRepository()

    val transferRepo: TransferRepository =
        if (useRemote) RemoteTransferRepository(api) else MockTransferRepository(api = api)
}
