package com.geeksville.mesh.model

import android.os.RemoteException
import androidx.compose.mutableStateOf
import com.geeksville.android.Logging
import com.geeksville.mesh.MeshProtos
import com.geeksville.mesh.utf8
import java.util.*

/**
 * the model object for a text message
 *
 * if errorMessage is set then we had a problem sending this message
 */
data class TextMessage(
    val from: String,
    val text: String,
    val date: Date = Date(),
    val errorMessage: String? = null
)


object MessagesState : Logging {
    val testTexts = listOf(
        TextMessage(
            "+16508765310",
            "I found the cache"
        ),
        TextMessage(
            "+16508765311",
            "Help! I've fallen and I can't get up."
        )
    )

    // If the following (unused otherwise) line is commented out, the IDE preview window works.
    // if left in the preview always renders as empty.
    val messages = mutableStateOf(testTexts, { a, b ->
        a.size == b.size // If the # of messages changes, consider it important for rerender
    })

    /// add a message our GUI list of past msgs
    fun addMessage(m: TextMessage) {
        val l = messages.value.toMutableList()
        l.add(m)
        messages.value = l
    }

    /// Send a message and added it to our GUI log
    fun sendMessage(str: String, dest: String? = null) {
        var error: String? = null
        val service = UIState.meshService
        if (service != null)
            try {
                service.sendData(
                    dest,
                    str.toByteArray(utf8),
                    MeshProtos.Data.Type.CLEAR_TEXT_VALUE
                )
            } catch (ex: RemoteException) {
                error = "Error: ${ex.message}"
            }
        else
            error = "Error: No Mesh service"

        MessagesState.addMessage(
            TextMessage(
                NodeDB.myId.value,
                str,
                errorMessage = error
            )
        )
    }
}