package com.vuzix.vuzixapp

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.security.KeyPairGenerator
import javax.crypto.KeyGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class KeyActivity : AppCompatActivity() {

    // create key alias
    // for future development alias should be created unique to each user
    private val KEY_ALIAS = "MyKeyAlias"
    private val SYMMETRIC_KEY_ALIAS = "MySymmetricKeyAlias"
    private val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //Generate user key pair and symmetric key
    fun GenerateKeyPair(): String {
        try {
            //initalise
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            // generate public private key pair
            generateKeyPairUsingKeyPairGenerator()
            //generate symmetric key
            generateSymmetricKey()


            // Retrieve the public key
            val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey

            // Log the public key
            //logPublicKey(publicKey)

            // encode public key to string to allow storage in database
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
    fun generateSymmetricKey(): String {
        try {
            //initialise keystore
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)


            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE_PROVIDER
            )

            //specify key specs
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                SYMMETRIC_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setKeySize(256) // Adjust key size as needed
                .build()

            //generate key
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()


            return SYMMETRIC_KEY_ALIAS

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    //generate public key and private key pair
    private fun generateKeyPairUsingKeyPairGenerator() {
        try {
            //initialise rsa algorithm
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                "AndroidKeyStore"
            )
            //initialise key specs
            keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setKeySize(2048)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setUserAuthenticationRequired(false)
                    .build()
            )

            //generate keys
            keyPairGenerator.generateKeyPair()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //log the generation of the public key for testing purposes
    private fun logPublicKey(publicKey: PublicKey) {
        try {
            val encodedPublicKey = Base64.encode(publicKey.encoded, Base64.NO_WRAP)
            val publicKeyString = String(encodedPublicKey)
            Log.d("KeyPairGeneration", "Public Key: $publicKeyString")
        } catch (e: Exception) {
            Log.e("KeyPairGeneration", "Error logging public key", e)
        }
    }
}