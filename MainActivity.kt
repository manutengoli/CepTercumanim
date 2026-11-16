package com.ceptercumanim.app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.net.URL
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private lateinit var status: TextView
    private lateinit var heard: TextView
    private lateinit var translation: TextView
    private lateinit var tts: TextToSpeech
    private val worker = Executors.newSingleThreadExecutor()
    private var ttsReady = false

    private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val words = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = words?.firstOrNull()?.trim().orEmpty()
            if (spoken.isNotBlank()) {
                heard.text = "Duyulan: $spoken"
                val detected = data?.getStringExtra("android.speech.extra.DETECTED_LANGUAGE")
                    ?: data?.getStringExtra(RecognizerIntent.EXTRA_LANGUAGE)
                val source = detectLanguage(spoken, detected)
                val target = when (source) {
                    "tr" -> "en"
                    "en" -> "tr"
                    "th" -> "tr"
                    else -> "tr"
                }
                status.text = "Dil algılandı: ${languageName(source)} • Tercüme ediliyor…"
                translate(spoken, source, target)
            }
        } else {
            status.text = "Konuşma alınamadı. Tekrar deneyebilirsin."
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startListening() else Toast.makeText(this, "Mikrofon izni gerekli.", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        buildScreen()
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(0xFFF5F7FA.toInt())
        }
        val title = TextView(this).apply {
            text = "🌍 Cep Tercümanım"
            textSize = 27f; setTextColor(0xFF14213D.toInt()); gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = "Türkçe, English veya ไทย konuş. Dil otomatik algılanır."
            textSize = 15f; gravity = Gravity.CENTER; setPadding(8, 20, 8, 18)
        }
        val listen = Button(this).apply {
            text = "🎙  KONUŞ / DİNLE"
            textSize = 20f
            setOnClickListener {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) startListening()
                else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
        heard = TextView(this).apply {
            text = "Senin / karşı tarafın söylediği burada görünecek."
            textSize = 18f; setTextColor(0xFF263238.toInt()); setPadding(12, 18, 12, 18)
        }
        translation = TextView(this).apply {
            text = "Tercüme burada yazacak ve sesli okunacak."
            textSize = 21f; setTextColor(0xFF075985.toInt()); setPadding(12, 18, 12, 18)
        }
        val repeat = Button(this).apply {
            text = "🔊 Tercümeyi tekrar oku"
            setOnClickListener {
                val t = translation.text.toString()
                if (t.isNotBlank() && !t.startsWith("Tercüme burada")) speak(t, lastTarget)
            }
        }
        val note = TextView(this).apply {
            text = "Dil yönü: Türkçe → İngilizce • English → Türkçe • ไทย → Türkçe\\nReklam yok. Çeviri internet bağlantısı kullanır."
            textSize = 13f; setTextColor(0xFF64748B.toInt()); setPadding(8, 22, 8, 8)
        }
        root.addView(title)
        root.addView(status)
        root.addView(listen, LinearLayout.LayoutParams(-1, 68))
        root.addView(heard)
        root.addView(translation)
        root.addView(repeat, LinearLayout.LayoutParams(-1, 56))
        root.addView(note)
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private var lastTarget = "tr"

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            status.text = "Bu telefonda konuşma tanıma servisi bulunamadı."
            return
        }
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Türkçe, İngilizce veya Tayca konuşun")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra("android.speech.extra.LANGUAGE_DETECTION_ALLOWED", true)
            putExtra("android.speech.extra.LANGUAGE_SWITCHING_ALLOWED", true)
            putExtra("android.speech.extra.LANGUAGE_PREFERENCE", "tr-TR")
        }
        status.text = "Dinliyorum… Şimdi konuşabilirsin."
        try { speechLauncher.launch(intent) }
        catch (_: Exception) { status.text = "Sesli tanıma açılamadı. Google konuşma hizmetini kontrol et." }
    }

    private fun detectLanguage(text: String, reported: String?): String {
        val r = reported?.lowercase(Locale.ROOT).orEmpty()
        if (r.startsWith("th")) return "th"
        if (r.startsWith("tr")) return "tr"
        if (r.startsWith("en")) return "en"
        if (text.any { it in '\\u0E00'..'\\u0E7F' }) return "th"
        if (text.any { it in "çğıöşüÇĞİÖŞÜ" }) return "tr"
        return "en"
    }

    private fun translate(text: String, from: String, to: String) {
        lastTarget = to
        worker.execute {
            try {
                val q = URLEncoder.encode(text, "UTF-8")
                val url = URL("https://api.mymemory.translated.net/get?q=$q&langpair=$from|$to")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"; connectTimeout = 12000; readTimeout = 15000
                }
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                conn.disconnect()
                val translated = Regex("\"translatedText\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"")
                    .find(body)?.groupValues?.get(1)
                    ?.replace("\\\\n", "\n")?.replace("\\\\\"", "\"")?.replace("\\\\/", "/")
                    ?: throw Exception("Çeviri yanıtı okunamadı")
                runOnUiThread {
                    translation.text = translated
                    status.text = "${languageName(from)} → ${languageName(to)}"
                    speak(translated, to)
                }
            } catch (e: Exception) {
                runOnUiThread { status.text = "Çeviri yapılamadı. İnternet bağlantını kontrol et." }
            }
        }
    }

    private fun languageName(code: String) = when (code) {
        "tr" -> "Türkçe"; "en" -> "English"; "th" -> "ไทย"; else -> code
    }

    private fun speak(text: String, language: String) {
        if (!ttsReady) return
        val locale = when (language) {
            "tr" -> Locale("tr", "TR")
            "en" -> Locale.US
            "th" -> Locale("th", "TH")
            else -> Locale.getDefault()
        }
        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            status.text = "Bu dilin ses paketi telefonda yüklü değil. Ayarlardan TTS ses verisini indir."
            return
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "translation")
    }

    override fun onInit(statusCode: Int) { ttsReady = statusCode == TextToSpeech.SUCCESS }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) tts.shutdown()
        worker.shutdown()
    }
}
