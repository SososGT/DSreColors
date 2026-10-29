package com.example.sonicds

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF071B43)
private val Blue = Color(0xFF087CF0)
private val Cyan = Color(0xFF22D9FF)
private val Pale = Color(0xFFF5F9FF)

class MainActivity : ComponentActivity() {
    private var selectedRom by mutableStateOf<Uri?>(null)
    private var selectedName by mutableStateOf<String?>(null)

    private val romPicker = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            selectedRom = uri
            selectedName = uri.lastPathSegment?.substringAfterLast('/') ?: "Archivo .nds"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Home(
                    romName = selectedName,
                    onPickRom = { romPicker.launch(arrayOf("*/*")) }
                )
            }
        }
    }
}

@Composable
private fun Home(romName: String?, onPickRom: () -> Unit) {
    var showControls by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Navy, Color(0xFF063B78), Blue)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("SONIC DS", color = Pale, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text("ANDROID • PROTOTIPO", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Cyan.copy(alpha = 0.7f), RoundedCornerShape(22.dp))
                    .background(Color(0xFF06132B).copy(alpha = 0.72f), RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DOS PANTALLAS", color = Cyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Box(
                        Modifier.fillMaxWidth(0.8f).height(100.dp)
                            .border(2.dp, Blue, RoundedCornerShape(10.dp))
                            .background(Navy, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("PANTALLA DS 1", color = Pale, fontSize = 13.sp) }
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier.fillMaxWidth(0.8f).height(72.dp)
                            .border(2.dp, Cyan, RoundedCornerShape(10.dp))
                            .background(Navy, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("PANTALLA DS 2", color = Pale, fontSize = 13.sp) }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "El motor de emulación todavía no está integrado.",
                        color = Pale.copy(alpha = 0.82f),
                        fontSize = 12.sp
                    )
                }
            }

            Button(
                onClick = onPickRom,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Navy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("SELECCIONAR ROM .NDS", fontWeight = FontWeight.ExtraBold)
            }

            Text(
                romName?.let { "Archivo seleccionado: $it" } ?: "Todavía no seleccionaste un archivo",
                color = Pale,
                fontSize = 12.sp
            )

            OutlinedButton(
                onClick = { showControls = !showControls },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Pale),
                border = androidx.compose.foundation.BorderStroke(1.dp, Cyan),
                shape = RoundedCornerShape(14.dp)
            ) { Text(if (showControls) "OCULTAR CONTROLES DE PRUEBA" else "VER CONTROLES DE PRUEBA") }

            if (showControls) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DPadPreview()
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ActionKey("B", Color(0xFF1B8FE8))
                        ActionKey("A", Color(0xFF22BFA4))
                    }
                }
                Text("Vista demostrativa: aún no envía entradas a un emulador.", color = Pale, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun DPadPreview() {
    Box(Modifier.size(118.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Color(0xFF7AA8D9).copy(alpha = 0.24f))
            drawCircle(Cyan.copy(alpha = 0.7f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx()))
            val c = Color(0xFFD9F5FF)
            val w = size.width
            val h = size.height
            drawLine(c, Offset(w * .5f, h * .18f), Offset(w * .5f, h * .82f), 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(c, Offset(w * .18f, h * .5f), Offset(w * .82f, h * .5f), 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
        }
    }
}

@Composable
private fun ActionKey(label: String, color: Color) {
    Box(
        Modifier.size(58.dp).background(color.copy(alpha = .85f), CircleShape)
            .border(2.dp, Cyan, CircleShape),
        contentAlignment = Alignment.Center
    ) { Text(label, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp) }
}
