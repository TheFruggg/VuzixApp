package com.vuzix.vuzixapp

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class KeyActivity : AppCompatActivity() {

    private val KEY_ALIAS = "MyKeyAlias" // Alias for the generated key pair

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Function to generate RSA key pair and return the Base64 encoded public key
    fun GenerateKeyPair(): String {
        try {
            // Initialize KeyStore instance
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            // Generate key pair using KeyPairGenerator
            generateKeyPairUsingKeyPairGenerator()

            // Retrieve the private key
            val privateKey = keyStore.getKey(KEY_ALIAS, null) as PrivateKey

            // Retrieve the public key
            val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey

            // Log the public key
            logPublicKey(publicKey)

            // Convert public key to Base64 string
            val publicKeyString = Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)

            // Display success message
            Log.d("KeyPairGeneration", "Key pair generated successfully!")
            return publicKeyString

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("KeyPairGeneration", "Error generating key pair", e)
            throw e
        }
    }

    // Function to generate RSA key pair using KeyPairGenerator
    private fun generateKeyPairUsingKeyPairGenerator() {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                "AndroidKeyStore"
            )

            // Initialize KeyGenParameterSpec for RSA key pair generation
            keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setKeySize(2048) // Set key size to 2048 bits
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1) // Set encryption padding
                    .setUserAuthenticationRequired(false) // User authentication not required
                    .build()
            )

            // Generate key pair
            keyPairGenerator.generateKeyPair()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function to log the public key
    private fun logPublicKey(publicKey: PublicKey) {
        try {
            // Convert public key to Base64 string and log it
            val encodedPublicKey = Base64.encode(publicKey.encoded, Base64.NO_WRAP)
            val publicKeyString = String(encodedPublicKey)
            Log.d("KeyPairGeneration", "Public Key: $publicKeyString")
        } catch (e: Exception) {
            Log.e("KeyPairGeneration", "Error logging public key", e)
        }
    }
}
