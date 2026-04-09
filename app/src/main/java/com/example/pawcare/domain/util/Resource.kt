package com.example.pawcare.domain.util

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error<out T>(val message: String, val data: T? = null) : Resource<T>()
    class Loading<out T> : Resource<T>()
}

// Síncronas
inline fun <T> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

inline fun <T> Resource<T>.onFailure(action: (String) -> Unit): Resource<T> {
    if (this is Resource.Error) action(message)
    return this
}

// Suspendidas para uso en Repositorios/Flows
suspend inline fun <T> Resource<T>.onSuccessSuspend(crossinline action: suspend (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

suspend inline fun <T> Resource<T>.onFailureSuspend(crossinline action: suspend (String) -> Unit): Resource<T> {
    if (this is Resource.Error) action(message)
    return this
}
