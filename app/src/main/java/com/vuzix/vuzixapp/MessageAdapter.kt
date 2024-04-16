// MessageAdapter.kt
package com.vuzix.vuzixapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.security.KeyStore
import java.security.PrivateKey
import javax.crypto.Cipher
import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class MessageAdapter(private val messages: List<Message>, private val currentUserId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Constants for message types
    private val RECEIVED_MESSAGE = 0
    private val SENT_MESSAGE = 1

    // ViewHolder for received messages
    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewReceivedMessage: TextView = itemView.findViewById(R.id.textViewReceivedMessage)
    }

    // ViewHolder for sent messages
    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSentMessage: TextView = itemView.findViewById(R.id.textViewSentMessage)
    }

    // Create ViewHolder based on message type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == RECEIVED_MESSAGE) {
            Log.d("working test", "its working here 2")
            // Inflate layout for received message and return ReceivedMessageViewHolder
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_message, parent, false)
            ReceivedMessageViewHolder(view)

        } else {
            Log.d("working test", "its working here 1")
            // Inflate layout for sent message and return SentMessageViewHolder
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message, parent, false)
            SentMessageViewHolder(view)
        }
    }

    // Bind message data to ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val keyAlias = "MyKeyAlias"

        val message = messages[position]

        //val byteArrayMessage = messageToByteArray(message.content)
        //val decodedKey = android.util.Base64.decode(message, android.util.Base64.DEFAULT)
        //val byteArrayMessage = Base64.decode(message, Base64.DEFAULT)


        if (holder.itemViewType == RECEIVED_MESSAGE) {
            Log.d("working test", "private key test")
            val privateKey = getPrivateKey(keyAlias)
            val decryptedMessage = decryptMessage(message.content,privateKey)
            // Bind received message content to ReceivedMessageViewHolder
            val receivedHolder = holder as ReceivedMessageViewHolder
            receivedHolder.textViewReceivedMessage.text = decryptedMessage
        } else {
            Log.d("working test", "its working here 4")
            val secretKey =NewMessageActivity().retrieveSymmetricKey()
            if (secretKey != null) {
                Log.d("working test", "its working here 69")
                val decryptedMessage = decryptSymmetricMessage(message.content, secretKey)
                // Bind sent message content to SentMessageViewHolder
                val sentHolder = holder as SentMessageViewHolder
                sentHolder.textViewSentMessage.text = decryptedMessage
            }
        }
    }


    fun decryptSymmetricMessage(encryptedMessage: String, secretKey: SecretKey): String {
        try {
            Log.d("working test", "symmetric key test")
            val messageWithoutNewLines = encryptedMessage.replace(Regex("""(\r\n)|\n"""), "")
            val originalData = Base64.getDecoder().decode(messageWithoutNewLines)
            //val originalData = encryptedMessage.toByteArray(Charsets.UTF_8)
            Log.d("working test", "its working here glizz")
            val iv = originalData.copyOfRange(0, 16) // Extract IV from encrypted data
            Log.d("working test", "its working here 7")
            val encryptedBytes = originalData.copyOfRange(16, originalData.size)
            Log.d("working test", "its working here 8")
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            Log.d("working test", "symmetric key worked")
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            return String(decryptedBytes)

        } catch (e: Exception) {
            e.printStackTrace()
            return "message decryption for symmetric failed"
        }
    }


    fun getPrivateKey(keyAlias: String): PrivateKey? {
        return try {
            Log.d("working test", "its working here 6")
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val entry = keyStore.getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry
            Log.d("private key", "private key: ${entry.privateKey}")
            entry.privateKey

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun decryptMessage(encryptedMessage: String, privateKey: PrivateKey?): String? {
        try {
            Log.d("working test", "its working here 7")
            val messageWithoutNewLines = encryptedMessage.replace(Regex("""(\r\n)|\n"""), "")
            Log.d("Message", "encrypted message: ${encryptedMessage}")

            Log.d("key", "private key: ${privateKey}")
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            Log.d("key", "im working at line 91")

            val originalData = Base64.getDecoder().decode(messageWithoutNewLines)
            Log.d("key", "decrypted to bytes: ${originalData}")

            val test = String(originalData)
            Log.d("fuckkkkkk", "fuck fuck fuck: $test")

            val decrypted = cipher.doFinal(originalData)

            val humanReadableString = String(decrypted)
            Log.d("Message", "decrypted message: $humanReadableString")

            return humanReadableString


            //val encryptedBytes = cipher.doFinal(messageContent.toByteArray())
            //val encryptedMessage =
            //    android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)

        } catch (e: Exception) {
            Log.e("Key", "Error decrypting message", e)
            return "errorororororororr"
        }


    }
    // Get total number of messages
    override fun getItemCount(): Int {
        return messages.size
    }

    // Determine the view type for the message (received or sent)
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) SENT_MESSAGE else RECEIVED_MESSAGE
    }

}
