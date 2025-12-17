
# UniLost

UniLost es una aplicación universitaria de gestión de objetos perdidos y encontrados que permite a estudiantes y personal del campus reportar, buscar y reclamar objetos extraviados de manera eficiente y organizada.

## Documentación del Proyecto

## 📱 FASE 1: CONFIGURACIÓN INICIAL DEL PROYECTO
### Paso 1.1: AndroidManifest.xml - Configuración de la aplicación
🔍 Analogía: Es como el ACTA CONSTITUTIVA de una empresa - define la identidad legal, permisos y estructura básica de la app.

🎯 Función: Archivo de configuración principal que declara componentes de la app, permisos requeridos y metadatos esenciales para Android.

```xml
<manifest package="com.unilost" xmlns:android="http://schemas.android.com/apk/res/android">
    <application android:label="UniLost" android:allowBackup="true" android:theme="@android:style/Theme.NoTitleBar">
        <activity android:name="com.unilost.MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### Paso 1.2: build.gradle.kts - Configuración de dependencias
🔍 Analogía: Es como la LISTA DE PROVEEDORES Y MATERIALES para construir un edificio - especifica todo lo necesario.

🎯 Función: Archivo de configuración de Gradle que define plugins, versiones del SDK, dependencias y configuraciones de compilación.

```kotlin
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'com.google.gms.google-services'
}

android {
    namespace 'com.unilost'
    compileSdk 34

    defaultConfig {
        applicationId "com.unilost"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildFeatures { compose true }

    composeOptions { kotlinCompilerExtensionVersion '1.5.8' }

    kotlinOptions { jvmTarget = "1.8" }
}

repositories {
    google()
    mavenCentral()
}

dependencies {

    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.activity:activity-compose:1.8.1'

    implementation platform('androidx.compose:compose-bom:2023.10.01')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.material3:material3'

    implementation 'androidx.navigation:navigation-compose:2.7.4'
    implementation 'androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

    // Firebase BOM
    implementation platform('com.google.firebase:firebase-bom:32.7.1')

    // Firebase Auth
    implementation 'com.google.firebase:firebase-auth-ktx'

    // Firestore
    implementation 'com.google.firebase:firebase-firestore-ktx'

    // Storage
    implementation 'com.google.firebase:firebase-storage-ktx'

    // --- AGREGADAS ---
    // Cargar imágenes (Coil)
    implementation "io.coil-kt:coil-compose:2.4.0"
}
```

## 📦 CAPA DE MODELOS DE DATOS
### Paso 2.1: Area.kt - Modelo de área universitaria
🔍 Analogía: Es como el PLANO DE UN EDIFICIO que identifica cada zona del campus.

🎯 Función: Representa una ubicación física dentro de la universidad donde se pueden perder/encontrar objetos.

```kotlin
package com.unilost.data.model

data class Area(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String? = null
)
```

### Paso 2.2: Categoria.kt - Modelo de categoría de objetos
🔍 Analogía: Es como el SISTEMA DE CLASIFICACIÓN de una biblioteca - organiza objetos por tipo.

🎯 Función: Clasifica objetos perdidos en grupos lógicos para facilitar la búsqueda y organización.

```kotlin
package com.unilost.data.model

data class Categoria(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String? = null
)
```

### Paso 2.3: ItemReporte.kt - Modelo para reportes
🔍 Analogía: Es como la FICHA DE REPORTE que llena el guardia de seguridad cuando encuentra algo.

🎯 Función: Estructura de datos específica para generar reportes administrativos.

```kotlin
package com.unilost.data.model

data class ItemReporte(
    val name: String,
    val place: String,
    val date: String,
    val description: String
)
```

### Paso 2.4: LostItem.kt - Modelo principal de objeto perdido
🔍 Analogía: Es como la ETIQUETA DE IDENTIFICACIÓN que se pone a cada objeto en la oficina de perdidos.

🎯 Función: Representa el concepto central de la app - un objeto perdido o encontrado en el campus.

```kotlin
package com.unilost.data.model

data class LostItem(
    val id: String,
    val name: String,
    val description: String?,
    val category: String,
    val place: String,
    val date: String,
    val type: String, // "Perdido" or "Encontrado"
    val imagePath: String? = null,
    val ownerId: String? = null
)
```

### Paso 2.5: Reporte.kt - Modelo de reporte de objeto
🔍 Analogía: Es como el REGISTRO DE INCIDENCIA en el libro de novedades.

🎯 Función: Representa un reporte específico sobre un objeto perdido/encontrado.

```kotlin
package com.unilost.data.model

data class Reporte(
    val id: String = "",
    val itemId: String = "",       // referencia a LostItem.id
    val tipo: String = "",         // Perdido / Encontrado
    val fecha: String = "",
    val descripcion: String? = null
)
```
### Resultado de la funcionalidad principal

A continuación se muestra un ejemplo de cómo se ve la salida de la aplicación:

![Pantalla Reporte](https://raw.githubusercontent.com/GeraWest/final/main/reporte.jpeg "Ejemplo de pantalla de la app")


### Paso 2.6: Rol.kt - Modelo de roles de usuario
🔍 Analogía: Es como el CARNET DE IDENTIFICACIÓN con nivel de acceso en la universidad.

🎯 Función: Define los diferentes tipos de usuarios y sus permisos en el sistema.

```kotlin
package com.unilost.data.model

data class Rol(
    val id: String = "",
    val nombre: String = "",       // ADMIN, USUARIO
    val descripcion: String? = null
)
```



## 🔄 CAPA DE REPOSITORIOS
### Paso 3.1: LocalRepository.kt - Repositorio local de datos
🔍 Analogía: Es como la OFICINA DE OBJETOS PERDIDOS FÍSICA del campus.

🎯 Función: Gestiona datos en memoria para desarrollo y demo, simulando una base de datos real.

```kotlin
package com.unilost.data.repository

import com.unilost.data.model.LostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class LocalRepository {
    private val _items = MutableStateFlow<List<LostItem>>(sample())
    val items: StateFlow<List<LostItem>> = _items

    private val users = mutableMapOf<String, String>() // email -> password (demo only)

    fun addUser(email: String, password: String) {
        users[email] = password
    }

    fun login(email: String, password: String): Boolean {
        return users[email] == password
    }

    fun addItem(item: LostItem) {
        val list = _items.value.toMutableList()
        list.add(0, item)
        _items.value = list
    }

    fun updateItem(updated: LostItem) {
        _items.value = _items.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteItem(id: String) {
        _items.value = _items.value.filter { it.id != id }
    }

    fun findById(id: String): LostItem? = _items.value.find { it.id == id }

    companion object {
        private fun sample() = listOf(
            LostItem(UUID.randomUUID().toString(),"Mochila negra","Mochila con distintivo rojo","Mochilas","Aula 1","2025-11-01","Perdido", null,"u1"),
            LostItem(UUID.randomUUID().toString(),"Llaves","Llavero con etiqueta azul","Llaves","Biblioteca","2025-10-20","Encontrado", null,"u2")
        )
    }
}
```

## 🧭 CAPA DE NAVEGACIÓN
### Paso 4.1: NavigationGraph.kt - Sistema de navegación principal
🔍 Analogía: Es como el MAPA DE RUTAS DEL CAMPUS que muestra cómo llegar a cada edificio.

🎯 Función: Define toda la estructura de navegación de la aplicación usando Jetpack Navigation 

```kotlin
package com.unilost.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Importar todas tus pantallas:
import com.unilost.ui.screens.SplashScreen
import com.unilost.ui.screens.LoginScreen
import com.unilost.ui.screens.RegisterScreen
import com.unilost.ui.screens.HomeScreen
import com.unilost.ui.screens.ItemsListScreen
import com.unilost.ui.screens.ItemDetailScreen
import com.unilost.ui.screens.PublishItemScreen
import com.unilost.ui.screens.NotificationsScreen
import com.unilost.ui.screens.ProfileScreen
import com.unilost.ui.screens.ReportesScreen   // 👈 NUEVO

// -------------------------
// Pantallas / Rutas
// -------------------------
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object List : Screen("list")
    object Detail : Screen("detail/{id}") {
        fun create(id: String) = "detail/$id"
    }
    object Publish : Screen("publish")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object Reportes : Screen("reportes") // 👈 NUEVO
}

// -------------------------
// Navigation Graph
// -------------------------
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onOpenList = { navController.navigate(Screen.List.route) },
                onOpenPublish = { navController.navigate(Screen.Publish.route) },
                onOpenNotifications = { navController.navigate(Screen.Notifications.route) },
                onOpenProfile = { navController.navigate(Screen.Profile.route) },
                onOpenReportes = { navController.navigate(Screen.Reportes.route) } // 👈 NUEVO
            )
        }

        composable(Screen.List.route) {
            ItemsListScreen(
                onOpenDetail = { id ->
                    navController.navigate(Screen.Detail.create(id))
                },
                onOpenPublish = {
                    navController.navigate(Screen.Publish.route)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            ItemDetailScreen(
                id = id,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Publish.route) {
            PublishItemScreen(
                onPublished = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        // 🧾 REPORTES (ADMIN)
        composable(Screen.Reportes.route) {
            ReportesScreen()
        }
    }
}
```

## 🎨 CAPA DE COMPONENTES UI
### Paso 5.1: CommonComponents.kt - Componentes reutilizables
🔍 Analogía: Son como los MUEBLES ESTÁNDAR que se usan en todas las oficinas de la universidad.

🎯 Función: Colección de componentes UI reutilizables que mantienen consistencia visual en toda la app.

```kotlin

package com.unilost.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScreenTitle(title: String) {
    Text(title, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(8.dp))
}

@Composable
fun SimpleCardRow(title: String, subtitle: String, onClick: () -> Unit = {}) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
```

## 📱 CAPA DE PANTALLAS (SCREENS)
### Paso 6.1: HomeScreen.kt - Pantalla principal
🔍 Analogía: Es como la RECEPCIÓN PRINCIPAL de la oficina de objetos perdidos.

🎯 Función: Pantalla central que da acceso a todas las funcionalidades principales de la app.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)

package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 🔥 IMPORTS QUE FALTABAN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 🎨 Paleta de colores
private val BlueMain = Color(0xFF21527A)
private val BlueLight = Color(0xFF3A6D9A)
private val BlueBackground = Color(0xFFD9EAF7)

@Composable
fun HomeScreen(
    onOpenList: () -> Unit,
    onOpenPublish: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenReportes: () -> Unit
) {
    var selected by remember { mutableStateOf(0) }
    var role by remember { mutableStateOf("user") }

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // 🔐 Verificar rol (solo admin ve reportes)
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("roles")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                role = doc.getString("role") ?: "user"
            }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = BlueMain) {

                NavigationBarItem(
                    selected = selected == 0,
                    onClick = { selected = 0; onOpenList() },
                    icon = { Icon(Icons.Default.Home, tint = Color.White, contentDescription = null) },
                    label = { Text("Buscar", color = Color.White) }
                )

                NavigationBarItem(
                    selected = selected == 1,
                    onClick = { selected = 1; onOpenPublish() },
                    icon = { Icon(Icons.Default.Add, tint = Color.White, contentDescription = null) },
                    label = { Text("Publicar", color = Color.White) }
                )

                NavigationBarItem(
                    selected = selected == 2,
                    onClick = { selected = 2; onOpenNotifications() },
                    icon = { Icon(Icons.Default.Notifications, tint = Color.White, contentDescription = null) },
                    label = { Text("Notifs", color = Color.White) }
                )

                NavigationBarItem(
                    selected = selected == 3,
                    onClick = { selected = 3; onOpenProfile() },
                    icon = { Icon(Icons.Default.Person, tint = Color.White, contentDescription = null) },
                    label = { Text("Perfil", color = Color.White) }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {

            // 🔵 ENCABEZADO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .background(BlueMain, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        "Bienvenido a UniLost 👋",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "Busca y publica objetos perdidos/encontrados.",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(Modifier.height(25.dp))

            // 🔲 TARJETA DE ACCIONES
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {

                Column {

                    Text(
                        "¿Qué deseas hacer?",
                        fontWeight = FontWeight.Bold,
                        color = BlueMain,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = onOpenList,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Ver objetos publicados", color = Color.White)
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onOpenPublish,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Publicar un objeto", color = BlueMain)
                    }

                    // 🧾 SOLO ADMIN
                    if (role == "admin") {
                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = onOpenReportes,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueLight),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Generar Reporte", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
```

### Paso 6.2: ItemDetailScreen.kt - Pantalla de detalle de objeto
🔍 Analogía: Es como la VITRINA DE EXHIBICIÓN donde se muestra un objeto perdido con todos sus detalles.

🎯 Función: Muestra información completa de un objeto perdido/encontrado específico.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)
package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 🎨 Paleta azul profesional
private val BlueMain = Color(0xFF21527A)
private val BlueLight = Color(0xFF3A6D9A)
private val BlueBackground = Color(0xFFE6F0FA)

@Composable
fun ItemDetailScreen(
    id: String,
    onBack: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var item by remember { mutableStateOf<Map<String, Any>?>(null) }
    var loading by remember { mutableStateOf(true) }
    var role by remember { mutableStateOf("user") }

    // 🔐 Obtener rol
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("roles").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                role = doc.getString("role") ?: "user"
            }
    }

    // 📦 Obtener item
    LaunchedEffect(id) {
        firestore.collection("items").document(id)
            .get()
            .addOnSuccessListener { doc ->
                item = doc.data
                loading = false
            }
            .addOnFailureListener {
                loading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = item?.get("name")?.toString() ?: "Detalle",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlueMain
                )
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BlueMain)
            }
            return@Scaffold
        }

        item?.let { it ->

            val name = it["name"]?.toString() ?: ""
            val area = it["area"]?.toString() ?: ""   // ✅ CORREGIDO
            val date = it["date"]?.toString() ?: ""
            val desc = it["description"]?.toString() ?: "-"
            val ownerId = it["ownerId"]?.toString() ?: ""

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
                    .padding(16.dp)
            ) {

                // 📌 Tarjeta del objeto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {

                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlueMain
                        )

                        Spacer(Modifier.height(12.dp))

                        Text("Área:", fontWeight = FontWeight.Bold, color = BlueLight)
                        Text(area)

                        Spacer(Modifier.height(8.dp))

                        Text("Fecha:", fontWeight = FontWeight.Bold, color = BlueLight)
                        Text(date)

                        Spacer(Modifier.height(12.dp))

                        // 🔒 SOLO ADMINS ven la descripción
                        if (role == "admin") {
                            Text(
                                "Descripción:",
                                fontWeight = FontWeight.Bold,
                                color = BlueLight
                            )
                            Text(desc)
                        } else {
                            Text(
                                "La descripción solo está disponible con el administrador.",
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // 🗑️ Eliminar (solo admin o dueño)
                if (role == "admin" || ownerId == auth.currentUser?.uid) {
                    OutlinedButton(
                        onClick = {
                            firestore.collection("items").document(id).delete()
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BlueMain
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Objeto no encontrado", color = Color.Red)
        }
    }
}
```
![Pantalla Detalles de objeto perdido](https://raw.githubusercontent.com/GeraWest/final/main/detalle.jpeg "Ejemplo de pantalla de la app")

### Paso 6.3: ItemsListScreen.kt - Lista de objetos
🔍 Analogía: Es como el INVENTARIO GENERAL de la oficina de objetos perdidos.

🎯 Función: Muestra lista paginada/filtrada de todos los objetos perdidos y encontrados.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)

package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

// 🎨 Paleta unificada
private val BlueMain = Color(0xFF21527A)
private val BlueLight = Color(0xFF3A6D9A)
private val BlueBackground = Color(0xFFD9EAF7)

data class ItemModel(
    val id: String = "",
    val name: String = "Sin nombre",
    val place: String = "Lugar desconocido",
    val date: String = "Fecha desconocida"
)

@Composable
fun ItemsListScreen(
    onOpenDetail: (String) -> Unit,
    onOpenPublish: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var items by remember { mutableStateOf<List<ItemModel>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var role by remember { mutableStateOf("user") } // Estado para el rol

    // ░░ Obtener rol del usuario
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("roles").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                role = doc.getString("role") ?: "user"
            }
    }

    // ░░ Firestore Listener para items
    DisposableEffect(Unit) {
        val registration: ListenerRegistration = firestore.collection("items")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    errorMessage = error.message ?: "Error desconocido al leer Firestore"
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: "Sin nombre"
                    val place = doc.getString("place") ?: "Lugar desconocido"
                    val date = doc.getString("date") ?: "Fecha desconocida"
                    ItemModel(id = id, name = name, place = place, date = date)
                } ?: emptyList()

                items = list
            }

        onDispose { registration.remove() }
    }

    // ░░ UI ░░
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueBackground)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ⭐ Título
            Text(
                "Objetos publicados",
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally),
                color = BlueMain,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            // ⭐ Mensaje de error
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            // ⭐ LISTA DE TARJETAS
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(items) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        onClick = { onOpenDetail(item.id) }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {

                            // ⭐ Solo nombre del objeto
                            Text(
                                text = item.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlueMain
                            )
                        }
                    }
                }
            }
        }

        // ⭐ FAB solo visible si es admin
        if (role == "admin") {
            FloatingActionButton(
                onClick = onOpenPublish,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomEnd),
                containerColor = BlueMain,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Publicar")
            }
        }
    }
}
```
![Pantalla lista de objetos publicados perdidos](https://raw.githubusercontent.com/GeraWest/final/main/objeto.jpeg "Ejemplo de pantalla de la app")

### Paso 6.4: LoginScreen.kt - Pantalla de autenticación
🔍 Analogía: Es como el CONTROL DE ACCESO a la oficina de objetos perdidos.

🎯 Función: Permite a usuarios autenticarse en el sistema con correo institucional.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)

package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unilost.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

// 🎨 Paleta basada en #21527A
private val BlueMain = Color(0xFF21527A)
private val BlueLight = Color(0xFF3A6D9A)
private val BlueBackground = Color(0xFFD9EAF7)

@Composable
fun LoginScreen(
    viewModel: MainViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "Iniciar sesión",
            fontSize = 34.sp,
            color = BlueMain,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo institucional", color = BlueMain) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BlueMain) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueMain,
                unfocusedBorderColor = BlueLight,
                cursorColor = BlueMain,
                focusedLabelColor = BlueMain,
                unfocusedLabelColor = BlueLight
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña", color = BlueMain) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BlueMain) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueMain,
                unfocusedBorderColor = BlueLight,
                cursorColor = BlueMain,
                focusedLabelColor = BlueMain,
                unfocusedLabelColor = BlueLight
            )
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                loading = true
                error = null

                auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        loading = false
                        onLoginSuccess()
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        error = e.message ?: "Error al iniciar sesión"
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueMain,
                contentColor = Color.White
            )
        ) {
            Text("Entrar", fontSize = 18.sp)
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onRegister) {
            Text("Crear cuenta", color = BlueMain)
        }

        error?.let {
            Text(
                it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
```

### Paso 6.5: NotificationsScreen.kt - Pantalla de notificaciones
🔍 Analogía: Es como el TABLÓN DE AVISOS de la oficina de objetos perdidos.

🎯 Función: Muestra notificaciones del sistema sobre objetos relacionados con el usuario.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)
package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private val BlueMain = Color(0xFF21527A)
private val BlueBackground = Color(0xFFD9EAF7)

@Composable
fun NotificationsScreen() {

    val firestore = FirebaseFirestore.getInstance()
    var notifs by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        firestore.collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    notifs = snapshot.documents.mapNotNull {
                        val text = it.getString("text") ?: return@mapNotNull null
                        val ts = it.getLong("timestamp") ?: 0L
                        NotificationItem(it.id, text, ts)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", color = Color.White, fontSize = 22.sp) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = BlueMain)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier.fillMaxSize().background(BlueBackground).padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (notifs.isEmpty()) {
                Text("No hay notificaciones", color = BlueMain)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(notifs) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    firestore.collection("notifications")
                                        .document(notif.id).delete()
                                },
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Text(
                                notif.text,
                                modifier = Modifier.padding(16.dp),
                                color = BlueMain,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NotificationItem(
    val id: String,
    val text: String,
    val timestamp: Long
)
```
![Pantalla Login](https://raw.githubusercontent.com/GeraWest/final/main/login.jpeg "Ejemplo de pantalla de la app")


### Paso 6.6: ProfileScreen.kt - Pantalla de perfil
🔍 Analogía: Es como el CARNET DE USUARIO con foto e información personal.

🎯 Función: Muestra y permite gestionar el perfil del usuario autenticado.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)

package com.unilost.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth

private val BlueMain = Color(0xFF21527A)
private val BlueBackground = Color(0xFFE6F0FA)

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        photoUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(contentAlignment = Alignment.BottomEnd) {

                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .size(36.dp)
                            .background(BlueMain, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(
                                id = android.R.drawable.ic_menu_camera
                            ),
                            contentDescription = "Cambiar foto",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = user?.email ?: "Usuario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Rol: Usuario",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                auth.signOut()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}
```
![Pantalla profile](https://raw.githubusercontent.com/GeraWest/final/main/profile.jpeg "Ejemplo de pantalla de la app")

### Paso 6.7: PublishItemScreen.kt - Publicar objeto
🔍 Analogía: Es como el FORMULARIO DE REGISTRO para reportar un objeto perdido.

🎯 Función: Permite a usuarios publicar nuevos objetos perdidos o encontrados.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)
package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private val BlueMain = Color(0xFF21527A)
private val BlueBackground = Color(0xFFD9EAF7)

@Composable
fun PublishItemScreen(
    onPublished: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Perdido") }

    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf("user") }

    var categorias by remember { mutableStateOf(listOf<String>()) }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }

    val areas = listOf(
        "Cafetería",
        "Edificio A",
        "Edificio F",
        "CGTI",
        "Canchas",
        "Auditorio",
        "Edificio V"
    )
    var areaSeleccionada by remember { mutableStateOf("") }
    var expandedArea by remember { mutableStateOf(false) }

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        firestore.collection("roles").document(uid)
            .get()
            .addOnSuccessListener {
                role = it.getString("role") ?: "user"
            }

        firestore.collection("categorias")
            .get()
            .addOnSuccessListener { result ->
                categorias = result.documents.mapNotNull { it.getString("nombre") }
                if (categorias.isNotEmpty()) {
                    categoriaSeleccionada = categorias.first()
                }
            }
    }

    if (role != "admin") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No tienes permisos para publicar",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueBackground)
            .padding(16.dp)
    ) {

        Text(
            "Publicar nuevo objeto",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = BlueMain
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del objeto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandedCategoria,
            onExpandedChange = { expandedCategoria = !expandedCategoria }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expandedCategoria)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedCategoria,
                onDismissRequest = { expandedCategoria = false }
            ) {
                categorias.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            categoriaSeleccionada = it
                            expandedCategoria = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandedArea,
            onExpandedChange = { expandedArea = !expandedArea }
        ) {
            OutlinedTextField(
                value = areaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Área") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expandedArea)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedArea,
                onDismissRequest = { expandedArea = false }
            ) {
                areas.forEach { area ->
                    DropdownMenuItem(
                        text = { Text(area) },
                        onClick = {
                            areaSeleccionada = area
                            expandedArea = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Fecha (yyyy-mm-dd)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                error = null

                if (name.isBlank() || categoriaSeleccionada.isBlank() || areaSeleccionada.isBlank()) {
                    error = "Nombre, categoría y área son obligatorios"
                    return@Button
                }

                loading = true
                val uid = auth.currentUser?.uid ?: "guest"

                val itemData = hashMapOf(
                    "name" to name,
                    "description" to desc,
                    "category" to categoriaSeleccionada,
                    "area" to areaSeleccionada,
                    "date" to if (date.isBlank()) "Hoy" else date,
                    "type" to type,
                    "ownerId" to uid,
                    "timestamp" to System.currentTimeMillis()
                )

                val batch = firestore.batch()
                val itemRef = firestore.collection("items").document()
                batch.set(itemRef, itemData)

                val categoriaItemRef = firestore
                    .collection("categorias")
                    .document(categoriaSeleccionada)
                    .collection("items")
                    .document(itemRef.id)
                batch.set(categoriaItemRef, itemData)

                firestore.collection("area")
                    .whereEqualTo("nombre", areaSeleccionada)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            val areaDoc = result.documents.first()
                            val areaItemRef = areaDoc.reference
                                .collection("items")
                                .document(itemRef.id)
                            batch.set(areaItemRef, itemData)
                        }

                        batch.commit()
                            .addOnSuccessListener {
                                loading = false
                                name = ""
                                desc = ""
                                date = ""
                                onPublished()
                            }
                            .addOnFailureListener {
                                loading = false
                                error = it.message
                            }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = BlueMain)
        ) {
            Text(if (loading) "Publicando..." else "Publicar", color = Color.White)
        }

        error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
```
![Pantalla Publish](https://raw.githubusercontent.com/GeraWest/final/main/publish.jpeg "Ejemplo de pantalla de la app")

### Paso 6.8: RegisterScreen.kt - Registro de nuevo usuario
🔍 Analogía: Es como el FORMULARIO DE INSCRIPCIÓN para obtener carnet de la oficina.

🎯 Función: Permite a nuevos usuarios crear una cuenta en el sistema.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)

package com.unilost.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo institucional") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña (min 6)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                error = null

                if (email.isBlank() || pass.length < 6) {
                    error = "Email inválido o contraseña mínima de 6 caracteres"
                    return@Button
                }

                loading = true

                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        loading = false
                        onRegisterSuccess()
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        error = e.message ?: "Error al registrar"
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }

        error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
```
![Pantalla Crear cuenta](https://raw.githubusercontent.com/GeraWest/final/main/newlogin.jpeg "Ejemplo de pantalla de la app")

### Paso 6.9: ReportesScreen.kt - Pantalla de reportes (Admin)
🔍 Analogía: Es como la OFICINA DE ESTADÍSTICAS que genera informes mensuales.

🎯 Función: Permite a administradores generar reportes y estadísticas del sistema.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)
package com.unilost.ui.screens

import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// 🎨 Colores
private val BlueMain = Color(0xFF21527A)
private val BlueBackground = Color(0xFFD9EAF7)

/* =======================
   MODELO DE DATOS
   ======================= */
data class ItemReporte(
    val name: String,
    val place: String,
    val date: String,
    val description: String
)

@Composable
fun ReportesScreen() {

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var role by remember { mutableStateOf("user") }
    var loading by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    /* =======================
       VERIFICAR ROL
       ======================= */
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("roles")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                role = doc.getString("role") ?: "user"
            }
    }

    /* =======================
       BLOQUEO NO ADMIN
       ======================= */
    if (role != "admin") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Acceso solo para administradores",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        return
    }

    /* =======================
       UI PRINCIPAL
       ======================= */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Generar Reporte General",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = BlueMain
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loading = true
                mensaje = null

                firestore.collection("items")
                    .get()
                    .addOnSuccessListener { result ->

                        val items = result.documents.map {
                            ItemReporte(
                                name = it.getString("name") ?: "",
                                place = it.getString("place") ?: "",
                                date = it.getString("date") ?: "",
                                description = it.getString("description") ?: ""
                            )
                        }

                        val uid = auth.currentUser?.uid ?: "admin"

                        val reporteData = hashMapOf(
                            "titulo" to "Reporte general de objetos",
                            "generadoPor" to uid,
                            "totalItems" to items.size,
                            "timestamp" to System.currentTimeMillis()
                        )

                        firestore.collection("reportes")
                            .add(reporteData)
                            .addOnSuccessListener {

                                generarPDF(items, uid)

                                loading = false
                                mensaje = "Reporte generado correctamente"
                            }
                            .addOnFailureListener { e ->
                                loading = false
                                mensaje = e.message
                            }
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        mensaje = e.message
                    }
            },
            colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(
                if (loading) "Generando reporte..." else "Generar Reporte",
                color = Color.White
            )
        }

        mensaje?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = if (it.contains("correctamente")) Color(0xFF2E7D32)
                else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/* =======================
   GENERADOR DE PDF
   ======================= */
fun generarPDF(items: List<ItemReporte>, uid: String) {

    val pdfDocument = PdfDocument()
    val paint = android.graphics.Paint()

    var pageNumber = 1
    var y = 130

    fun nuevaPagina(): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Reporte General de Objetos Perdidos", 40f, 50f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Generado por: $uid", 40f, 80f, paint)

        val fecha = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        canvas.drawText("Fecha: $fecha", 40f, 100f, paint)

        return page
    }

    var page = nuevaPagina()
    var canvas = page.canvas

    items.forEachIndexed { index, item ->

        if (y > 780) {
            pdfDocument.finishPage(page)
            pageNumber++
            y = 130
            page = nuevaPagina()
            canvas = page.canvas
        }

        paint.isFakeBoldText = true
        paint.textSize = 15f
        canvas.drawText("${index + 1}. ${item.name}", 40f, y.toFloat(), paint)

        paint.isFakeBoldText = false
        paint.textSize = 13f

        y += 20
        canvas.drawText("Lugar: ${item.place}", 60f, y.toFloat(), paint)

        y += 20
        canvas.drawText("Fecha: ${item.date}", 60f, y.toFloat(), paint)

        y += 20
        canvas.drawText("Descripción: ${item.description}", 60f, y.toFloat(), paint)

        y += 30
    }

    pdfDocument.finishPage(page)

    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Reporte_Objetos_${System.currentTimeMillis()}.pdf"
    )

    pdfDocument.writeTo(FileOutputStream(file))
    pdfDocument.close()
}
```
![Pantalla Reporte](https://raw.githubusercontent.com/GeraWest/final/main/reporte.jpeg "Ejemplo de pantalla de la app")

### Paso 6.10: SplashScreen.kt - Pantalla de inicio
🔍 Analogía: Es como el CARTEL DE BIENVENIDA a la entrada de la oficina.

🎯 Función: Pantalla inicial que muestra todas las funciones de app y decide a dónde navegar basado en autenticación.

```kotlin
package com.unilost.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unilost.viewmodel.MainViewModel

@Composable
fun SplashScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val scale = remember { Animatable(0f) }
    val isLoggedIn = viewModel.isAuthenticated()
    LaunchedEffect(true) {
        scale.animateTo(1f, animationSpec = tween(700))
        delay(1500)
        if (isLoggedIn) onNavigateToHome() else onNavigateToLogin()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("UniLost", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(20.dp))
            CircularProgressIndicator()
        }
    }
}


```
![Pantalla Splash](https://raw.githubusercontent.com/GeraWest/final/main/splash.jpeg "Ejemplo de pantalla de la app")

### Paso 6.11: Notifications.Screen.kt - Pantalla de perfil
🔍 Analogía: Realiza notificaciones para que el usuario sepa de objetos publicados, esta funcion solo la realiza el admin 
🎯 Función: Muestra y permite recibir notificaciones el perfil del usuario autenticado.

```kotlin
@file:OptIn(ExperimentalMaterial3Api::class)
package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private val BlueMain = Color(0xFF21527A)
private val BlueBackground = Color(0xFFD9EAF7)

@Composable
fun NotificationsScreen() {

    val firestore = FirebaseFirestore.getInstance()
    var notifs by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        firestore.collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    notifs = snapshot.documents.mapNotNull {
                        val text = it.getString("text") ?: return@mapNotNull null
                        val ts = it.getLong("timestamp") ?: 0L
                        NotificationItem(it.id, text, ts)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", color = Color.White, fontSize = 22.sp) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = BlueMain)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier.fillMaxSize().background(BlueBackground).padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (notifs.isEmpty()) {
                Text("No hay notificaciones", color = BlueMain)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(notifs) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    firestore.collection("notifications")
                                        .document(notif.id).delete()
                                },
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Text(
                                notif.text,
                                modifier = Modifier.padding(16.dp),
                                color = BlueMain,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NotificationItem(
    val id: String,
    val text: String,
    val timestamp: Long
)

```
![Pantalla Notificaciones](https://raw.githubusercontent.com/GeraWest/final/main/noti.jpeg "Ejemplo de pantalla de la app")

## 🏗️ CAPA DE ACTIVITY
### Paso 8.1: MainActivity.kt - Actividad principal
🔍 Analogía: Es como el EDIFICIO PRINCIPAL que alberga todas las oficinas.

🎯 Función: Punto de entrada de la aplicación, configura Compose y lanza el contenido.

```kotlin
package com.unilost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.unilost.navigation.NavigationGraph
import com.unilost.ui.theme.UniLostTheme
import com.unilost.viewmodel.MainViewModel
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            UniLostTheme {
                Surface(modifier = Modifier) {
                    val navController = rememberNavController()
                    val vm: MainViewModel = viewModel()
                    NavigationGraph(navController = navController, startDestination = "splash")
                }
            }
        }
    }
}
```

## 🎯 RESUMEN DE ARQUITECTURA COMPLETA
### Estructura por capas:

```
📁 com.unilost/
├── 📱 AndroidManifest.xml (Configuración app)
├── 📦 build.gradle.kts (Dependencias)
│
├── 📊 data/model/ (Modelos de datos)
│   ├── Area.kt
│   ├── Categoria.kt
│   ├── ItemReporte.kt
│   ├── LostItem.kt
│   ├── Reporte.kt
│   └── Rol.kt
│
├── 🔄 data/repository/ (Repositorios)
│   └── LocalRepository.kt
│
├── 🧭 navigation/ (Navegación)
│   └── NavigationGraph.kt
│
├── 🎨 ui/components/ (Componentes UI)
│   └── CommonComponents.kt
│
├── 📱 ui/screens/ (Pantallas)
│   ├── HomeScreen.kt
│   ├── ItemDetailScreen.kt
│   ├── ItemsListScreen.kt
│   ├── LoginScreen.kt
│   ├── NotificationsScreen.kt
│   ├── ProfileScreen.kt
│   ├── PublishItemScreen.kt
│   ├── RegisterScreen.kt
│   ├── ReportesScreen.kt
│   └── SplashScreen.kt
│
├── 🧠 viewmodel/ (ViewModels)
│   └── MainViewModel.kt
│
└── 🏗️ MainActivity.kt (Punto de entrada)
```

## Tecnologías clave implementadas:

### 🔥 Firebase: Authentication, Firestore, Storage

### 🎨 Jetpack Compose: UI completamente declarativa

### 🧭 Navigation Compose: Navegación tipo-safe

### 🔄 Coroutines: Programación asíncrona

### 📄 Coil: Carga eficiente de imágenes

### 📊 Material Design 3: Sistema de diseño moderno
