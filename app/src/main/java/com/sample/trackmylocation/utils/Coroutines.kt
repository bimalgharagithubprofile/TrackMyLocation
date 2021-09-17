package com.sample.trackmylocation.utils

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.*

object Coroutines {

    //MAIN
    fun main(scope: GlobalScope, work: suspend ((CoroutineScope) -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work(this)
        }
    fun main(scope: LifecycleCoroutineScope, work: suspend ((CoroutineScope) -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work(this)
        }
    fun main(scope: CoroutineScope, work: suspend ((CoroutineScope) -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work(this)
        }

    // IO
    fun io(scope: GlobalScope, work: suspend (CoroutineScope) -> Unit) {
        scope.launch(Dispatchers.IO) {
            work.invoke(this)
        }
    }
    fun io(scope: LifecycleCoroutineScope, work: suspend (CoroutineScope) -> Unit) {
        scope.launch(Dispatchers.IO) {
            work.invoke(this)
        }
    }
    fun io(scope: CoroutineScope, work: suspend (CoroutineScope) -> Unit) {
        scope.launch(Dispatchers.IO) {
            work.invoke(this)
        }
    }

}