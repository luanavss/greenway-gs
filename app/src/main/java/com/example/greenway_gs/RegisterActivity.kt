package com.example.greenway_gs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class RegisterActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonBackHome = findViewById<ImageButton>(R.id.backToHomeButton)

        buttonBackHome.setOnClickListener{
            var intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }
        val nomeField = findViewById<EditText>(R.id.nome)
        val emailField = findViewById<EditText>(R.id.email)
        val rgField = findViewById<EditText>(R.id.rgNumber)
        val cpfField = findViewById<EditText>(R.id.cpfNumber)
        val senhaField = findViewById<EditText>(R.id.passwordNumber)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Função para realizar o login após o cadastro bem-sucedido
        fun login(email: String, senha: String) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val url = URL("http://4.228.15.203:8080/cadastro/login")
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json")
                    connection.doOutput = true

                    val requestBody = JSONObject()
                    requestBody.put("email", email)
                    requestBody.put("senha", senha)

                    val outputStream = connection.outputStream.bufferedWriter()
                    outputStream.write(requestBody.toString())
                    outputStream.flush()

                    val responseCode = connection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val responseStream = connection.inputStream.bufferedReader().use { it.readText() }

                        val jsonResponse = JSONObject(responseStream)
                        val token = jsonResponse.getString("token")
                        val userId = jsonResponse.getString("id")

                        withContext(Dispatchers.Main) {
                            val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
                            intent.putExtra("TOKEN", token)
                            intent.putExtra("USER_ID", userId)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("Login", "Erro: $responseCode ${connection.responseMessage}")
                        }
                    }

                    connection.disconnect()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e("Login", "Erro de rede: ${e.message}", e)
                    }
                }
            }
        }

        fun register(nome: String, email: String, rg: String, cpf: String, senha: String) {
            try {
                val url = URL("http://4.228.15.203:8080/cadastro")
                Log.d("RegisterProcess", "URL de cadastro: $url")

                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val requestBody = JSONObject()
                requestBody.put("nome", nome)
                requestBody.put("email", email)
                requestBody.put("rg", rg)
                requestBody.put("cpf", cpf)
                requestBody.put("senha", senha)

                val outputStream = OutputStreamWriter(connection.outputStream)
                outputStream.write(requestBody.toString())
                outputStream.flush()

                val responseCode = connection.responseCode
                Log.d("RegisterProcess", "Código de resposta do cadastro: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    val responseStream = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d("RegisterProcess", "Resposta do servidor após cadastro: $responseStream")

                    runOnUiThread {
                        login(email, senha)
                    }
                } else {
                    Log.e("RegisterProcess", "Erro ao realizar cadastro. Código: $responseCode ${connection.responseMessage}")
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("RegisterProcess", "Erro no cadastro: ${e.message}", e)
            }
        }

        registerButton.setOnClickListener {
            val nome = nomeField.text.toString()
            val email = emailField.text.toString()
            val rg = rgField.text.toString()
            val cpf = cpfField.text.toString()
            val senha = senhaField.text.toString()

            thread {
                register(nome, email, rg, cpf, senha)
            }
        }
    }
}