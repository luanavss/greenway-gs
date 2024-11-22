package com.example.greenway_gs

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val emailField = findViewById<EditText>(R.id.loginEmail)
        val passwordField = findViewById<EditText>(R.id.loginPassword)
        val buttonCadastro = findViewById<Button>(R.id.cadastroButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        buttonCadastro.setOnClickListener{
            var intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        fun login(email: String, senha: String) {
            try {

                val url = URL("http://4.228.15.203:8080/cadastro/login")
                Log.d("LoginProcess", "URL: $url")

                val connection = url.openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val requestBody = JSONObject()
                requestBody.put("email", email)
                requestBody.put("senha", senha)
                Log.d("LoginProcess", "Corpo da requisição: $requestBody")

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(requestBody.toString())
                outputStream.flush()
                Log.d("LoginProcess", "Corpo da requisição enviado com sucesso")

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseStream = connection.inputStream.bufferedReader().use { it.readText() }

                    val jsonResponse = JSONObject(responseStream)
                    val token = jsonResponse.getString("token")
                    val userId = jsonResponse.getString("id")

                    Log.d("Login", "Token: $token")
                    Log.d("Login", "ID: $userId")

                    runOnUiThread {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("TOKEN", token)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.e("Login", "Erro: $responseCode ${connection.responseMessage}")
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("Login", "Erro de rede: ${e.message}", e)
            }
        }
        loginButton.setOnClickListener{
            val email = emailField.text.toString()
            val senha = passwordField.text.toString()
            Log.d("LoginProcess", "Email: $email, Senha: $senha")

            thread {
                login(email, senha)
            }
        }
    }
}
