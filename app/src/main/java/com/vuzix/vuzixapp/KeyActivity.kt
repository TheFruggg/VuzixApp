package com.vuzix.vuzixapp

//import modules needed
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

    //inintialise variables
    private val KEY_ALIAS = "MyKeyAlias"
    
    //function to generate new key pair
    fun GenerateKeyPair() {
        try {
            //create/access andriod keystore
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            //checks for existing key under the specified alias
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                //if not call generate key function
                generateKeyPairUsingKeyPairGenerator()
            }

            // Retrieve the private key
            val privateKey = keyStore.getKey(KEY_ALIAS, null) as PrivateKey

            // Retrieve the public key
            val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey

            // Log the public key
            logPublicKey(publicKey)

           

            // Display success message
            Log.d("KeyPairGeneration", "Key pair generated successfully!")

        // catch any errors 
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("KeyPairGeneration", "Error generating key pair", e)
        }
    }

    //function that generates key pair using Android keystore
    private fun generateKeyPairUsingKeyPairGenerator() {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                "AndroidKeyStore"
            )
            //select key parmaters
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

            keyPairGenerator.generateKeyPair()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Purely testing function to log public key after creation to check if working
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
