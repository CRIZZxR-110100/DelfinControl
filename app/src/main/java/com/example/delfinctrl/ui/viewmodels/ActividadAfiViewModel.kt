package com.example.delfinctrl.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.delfinctrl.data.model.ActividadAFI
import com.example.delfinctrl.data.repository.ActividadAfiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActividadAfiViewModel(private val repository: ActividadAfiRepository) : ViewModel() {

    val actividadesState: StateFlow<List<ActividadAFI>> = repository.obtenerTodas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val horasAfiTotalesState: StateFlow<Int> = repository.obtenerHorasAfiTotales()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun guardarActividad(nombre: String, fecha: Long, lugar: String, horas: Int) {
        viewModelScope.launch {
            val nueva = ActividadAFI(
                nombre = nombre,
                fecha = fecha,
                lugar = lugar,
                horas = horas
            )
            repository.registrarActividad(nueva)
        }
    }

    fun eliminarActividad(actividadAFI: ActividadAFI) {
        viewModelScope.launch {
            repository.eliminarActividad(actividadAFI)
        }
    }
}
