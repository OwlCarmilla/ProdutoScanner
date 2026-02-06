package pt.ipt.dam.stockapp.util

/**
 * Wrapper para estados de resultado de operações
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

/**
 * Extensões para facilitar uso do Resource
 */
fun <T> Resource<T>.isSuccess() = this is Resource.Success
fun <T> Resource<T>.isError() = this is Resource.Error
fun <T> Resource<T>.isLoading() = this is Resource.Loading

fun <T> Resource<T>.getOrNull(): T? = when (this) {
    is Resource.Success -> data
    else -> null
}

fun <T> Resource<T>.getOrThrow(): T = when (this) {
    is Resource.Success -> data!!
    is Resource.Error -> throw Exception(message)
    is Resource.Loading -> throw Exception("Still loading")
}
