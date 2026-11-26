package com.argy.questapi_0128.uicontroller.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.io.IOException
import com.argy.questapi_0128.modeldata.DataSiswa
import com.argy.questapi_0128.repositori.RepositoryDataSiswa
import retrofit2.Response

class EntryViewModel(private val repositoryDataSiswa: RepositoryDataSiswa) : ViewModel() {
    var uiStateSiswa by mutableStateOf(UIStateSiswa())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /* Fungsi untuk memvalidasi input */
    private fun validasiInput(uiState: DetailsSiswa = uiStateSiswa.detailsSiswa): Boolean {
        return with(uiState) {
            nama.isNotBlank() && alamat.isNotBlank() && telpon.isNotBlank()
        }
    }

    fun updateUiState(detailsSiswa: DetailsSiswa) {
        uiStateSiswa =
            UIStateSiswa(
                detailsSiswa = detailsSiswa,
                isEntryValid = validasiInput(detailsSiswa)
            )
    }

    /* Fungsi untuk menyimpan data yang di-entry */
    suspend fun addSiswa(): Boolean {
        if (!validasiInput()) {
            errorMessage = "Semua kolom wajib diisi."
            return false
        }

        errorMessage = null
        return try {
            val response: Response<Void> = repositoryDataSiswa.postDataSiswa(
                uiStateSiswa.detailsSiswa.toDataSiswa()
            )
            if (response.isSuccessful) {
                true
            } else {
                errorMessage = "Data gagal disimpan (HTTP ${response.code()})."
                false
            }
        } catch (exception: IOException) {
            errorMessage = "Tidak dapat terhubung ke server. Periksa koneksi dan alamat server."
            false
        } catch (exception: retrofit2.HttpException) {
            errorMessage = "Server mengembalikan kesalahan (${exception.code()})."
            false
        }
    }
}

data class UIStateSiswa(
    val detailsSiswa: DetailsSiswa = DetailsSiswa(),
    val isEntryValid: Boolean = false
)

data class DetailsSiswa(
    val id: Int = 0,
    val nama: String = "",
    val alamat: String = "",
    val telpon: String = "",
)

fun DetailsSiswa.toDataSiswa(): DataSiswa = DataSiswa(
    id = id,
    nama = nama,
    alamat = alamat,
    telpon = telpon
)

fun DataSiswa.toUiStateSiswa(isEntryValid: Boolean = false): UIStateSiswa =
    UIStateSiswa(
        detailsSiswa = this.toDetailsSiswa(),
        isEntryValid = isEntryValid
    )

fun DataSiswa.toDetailsSiswa(): DetailsSiswa = DetailsSiswa(
    id = id,
    nama = nama,
    alamat = alamat,
    telpon = telpon
)
