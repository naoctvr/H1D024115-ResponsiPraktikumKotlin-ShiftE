# 🍳 Aplikasi Katalog dan Eksplorasi Resep Makanan

Aplikasi mobile berbasis Android yang dikembangkan untuk memenuhi tugas **Responsi Mobile Programming**. Aplikasi ini berfungsi sebagai katalog resep interaktif yang mengambil data resep makanan dari REST API secara dinamis ([TheMealDB](https://www.themealdb.com/api.php)) menggunakan arsitektur **MVVM (Model-View-ViewModel)**, **Jetpack Compose**, **Material Design 3**, **Retrofit**, dan **StateFlow**.

---

## 📸 1. Tampilan & Screenshot Aplikasi

> **Catatan:** Screenshot berikut menampilkan antarmuka aplikasi yang telah mengimplementasikan Material Design 3 dengan tema *Warm Culinary Palette*.

| Home Screen (Grid View) | Home Screen (List View) | Detail Screen (Resep) | Interactive Checklist |
| :---: | :---: | :---: | :---: |
| ![Home Grid](docs/screenshots/home_grid.png) | ![Home List](docs/screenshots/home_list.png) | ![Detail Resep](docs/screenshots/detail_resep.png) | ![Ingredients](docs/screenshots/checklist.png) |

---

## ✨ 2. Fitur Utama Aplikasi

1. **Pencarian Resep Dinamis (*Search Functionality*)**:
   - Pengguna dapat mencari resep berdasarkan nama makanan secara *real-time*.
   - Dilengkapi fitur *debouncing* (500ms) untuk menghemat panggila API dan tombol hapus (*clear button*).

2. **Filter Kategori Makanan (*Category Chips*)**:
   - Filter cepat resep berdasarkan kategori populer seperti *Chicken, Beef, Seafood, Dessert, Pasta, Vegetarian, Breakfast*, dll.

3. **Toggle Mode Tampilan (*Grid & List View*)**:
   - Fleksibilitas memilih mode tampilan *2-Column Grid* (`LazyVerticalGrid`) atau *1-Column List* (`LazyColumn`).

4. **Tampilan Berbasis State (*State-driven UI*)**:
   - **Loading State**: Animasi indikator progres yang halus saat mengambil data dari REST API.
   - **Error State**: Tampilan error koneksi/API lengkap dengan tombol **"Coba Lagi" (Retry)**.
   - **Empty State**: Tampilan informatif saat resep yang dicari tidak ditemukan.
   - **Success State**: Menampilkan daftar resep makanan secara responsif.

5. **Detail Resep Lengkap (*Recipe Detail Screen*)**:
   - Banner gambar resolusi tinggi dengan efek *gradient overlay*.
   - Badge kategori makanan (*Category*) dan negara asal makanan (*Area*).
   - **Daftar Bahan & Takaran Interaktif**: *Checklist* interaktif bagi pengguna saat menyiapkan bahan masakan di dapur.
   - **Langkah-Langkah Memasak Terstruktur**: Instruksi memasak yang dipecah otomatis menjadi langkah berurutan 1, 2, 3.

6. **Integrasi Media & Berbagi Resep**:
   - **Video Tutorial YouTube**: Tombol langsung untuk membuka video cara memasak di YouTube via Android Intent.
   - **Sumber Web Asli**: Tombol untuk mengunjungi artikel resep asli.
   - **Fitur Bagikan (*Share Intent*)**: Tombol untuk membagikan resep ke aplikasi lain (WhatsApp, Telegram, Gmail, dll.).

---

## 🏗️ 3. Arsitektur Aplikasi (MVVM)

Aplikasi ini menerapkan pola arsitektur **MVVM (Model-View-ViewModel)** secara bersih (*Clean Architecture principles*):

```
+------------------------------------------------------------------+
|                     VIEW / UI LAYOUT (Compose)                   |
|   - HomeScreen, DetailScreen, RecipeCard, SearchBarComponent     |
+------------------------------------------------------------------+
                                 |
                                 v  (Observes StateFlow)
+------------------------------------------------------------------+
|                       VIEWMODEL LAYER                            |
|   - HomeViewModel, DetailViewModel, HomeUiState, DetailUiState   |
+------------------------------------------------------------------+
                                 |
                                 v  (Calls Repository methods)
+------------------------------------------------------------------+
|                      REPOSITORY LAYER                            |
|   - RecipeRepository (Interface & RecipeRepositoryImpl)         |
+------------------------------------------------------------------+
                                 |
                                 v  (Retrofit REST API Requests)
+------------------------------------------------------------------+
|                    DATA & REMOTE NETWORK                         |
|   - RetrofitClient, ApiService, MealDto, MealResponseDto, Recipe |
+------------------------------------------------------------------+
```

### Penjelasan Komponen MVVM:
- **View / Composable**: Bertanggung jawab menampilkan UI deklaratif menggunakan Jetpack Compose. Tidak melakukan pemrosesan data atau *network request* langsung.
- **ViewModel**: Mengelola State UI (`StateFlow`) dan *business logic*. Bertahan terhadap pergeseran konfigurasi layar (*configuration changes*).
- **Repository**: Menyediakan abstraksi data dari sumber REST API dan melakukan pemetaan DTO (*Data Transfer Object*) ke Domain Model (`Recipe`).
- **Retrofit & ApiService**: Menangani komunikasi HTTP jaringan ke endpoint TheMealDB.

---

## 🌐 4. Penjelasan REST API (TheMealDB API)

Aplikasi menggunakan **TheMealDB REST API** tanpa memerlukan API Key (bebas diakses).

### Endpoint yang digunakan:
1. **Search Recipe Endpoint**:
   - **URL**: `https://www.themealdb.com/api/json/v1/1/search.php?s={nama_makanan}`
   - **Method**: `GET`
   - **Fungsi**: Mencari resep berdasarkan nama makanan atau menampilkan resep default jika query kosong.

2. **Lookup Recipe Detail Endpoint**:
   - **URL**: `https://www.themealdb.com/api/json/v1/1/lookup.php?i={id_recipe}`
   - **Method**: `GET`
   - **Fungsi**: Mengambil informasi lengkap resep berdasarkan ID makanan (`idMeal`).

---

## 💻 5. Penjelasan Teknis & Implementasi Fitur Kotlin

### 1. Kotlin Data Class
Digunakan untuk merepresentasikan model data DTO (`MealDto`, `MealResponseDto`), Domain Model (`Recipe`, `Ingredient`), dan State UI.
```kotlin
data class Recipe(
    val id: String,
    val name: String,
    val category: String,
    val area: String,
    val instructions: String,
    val thumbUrl: String,
    val ingredients: List<Ingredient>
)
```

### 2. Null Safety
Memanfaatkan fitur null safety Kotlin (`?`, `?:`, `let`, `mapNotNull`) untuk mencegah crash akibat `NullPointerException` saat menerima data dari API:
```kotlin
val cleanIngredient = ingredient?.trim()
val cleanMeasure = measure?.trim()
```

### 3. Lambda Functions
Digunakan untuk penanganan *event handling* pada UI Composable, seperti *callback* klik kartu resep, perubahan pencarian, dan *retry action*:
```kotlin
@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit // Lambda Callback
)
```

### 4. Extension Functions
Diterapkan untuk mengonversi DTO mentah menjadi Domain Model secara rapi dan memecah instruksi memasak menjadi langkah-langkah terstruktur:
```kotlin
fun MealDto.toRecipe(): Recipe { ... }
fun MealDto.extractIngredients(): List<Ingredient> { ... }
fun String.toInstructionSteps(): List<String> { ... }
```

### 5. Jetpack Compose & Material Design 3
- Menggunakan `MaterialTheme` dengan kustomisasi warna *Warm Culinary Palette* (`Color.kt`, `Type.kt`, `Theme.kt`).
- Komponen Lazy Layout: `LazyVerticalGrid` dan `LazyColumn`.
- Pustaka **Coil** (`AsyncImage`) untuk pemuatan gambar secara *asynchronous* dan *smooth fading transition*.

---

## 🚀 6. Cara Menjalankan Project di Android Studio

1. **Clone / Buka Project**:
   - Buka Android Studio (versi Jellyfish / Koala / Ladybug atau lebih baru).
   - Pilih `File > Open` lalu arahkan ke folder `d:\responsi pemob`.
2. **Sync Gradle**:
   - Tunggu hingga proses **Gradle Sync** selesai secara otomatis.
3. **Jalankan Aplikasi**:
   - Hubungkan perangkat Android via USB Debugging atau gunakan Emulator (API Level 24+).
   - Tekan tombol **Run (Shift + F10)**.

---

## 📹 7. Panduan & Naskah Penjelasan untuk Video Presentasi Kode

Dokumen naskah ini dibuat khusus untuk mempermudah perekaman **Video Penjelasan Kode** (sesuai persyaratan poin 3 tugas submission):

### 📌 Durasi Video Direkomendasikan: 3 - 5 Menit

#### **Bagian 1: Pembuka & Overview (30 Detik)**
> *"Halo semuanya, nama saya [Nama Mahasiswa], NIM [NIM]. Pada video ini saya akan menjelaskan implementasi kode aplikasi **Katalog dan Eksplorasi Resep Makanan** yang dibangun menggunakan Kotlin, Jetpack Compose, dan arsitektur MVVM untuk tugas Responsi Mobile Programming."*

#### **Bagian 2: Penjelasan Arsitektur MVVM & Network (1 Menit)**
> *"Pertama, aplikasi menggunakan arsitektur MVVM. Di paket `data/remote`, kita mendefinisikan `ApiService` menggunakan Retrofit dengan dua endpoint dari TheMealDB: `searchRecipes` dan `getRecipeDetail`. Data mentah berupa `MealDto` dikonversi menjadi Domain Model `Recipe` menggunakan Kotlin Extension Function `MealDto.toRecipe()` dan `extractIngredients()`."*

#### **Bagian 3: ViewModel & State Management (1.5 Menit)**
> *"Kedua, pada lapisan ViewModel (`HomeViewModel`), kita mengelola State UI berbasis `StateFlow`. State dibungkus menggunakan Kotlin Sealed Interface `HomeUiState` yang memiliki 4 keadaan: Loading, Success, Error, dan Empty. Fitur pencarian menyertakan *debouncing* 500ms agar aplikasi tidak membombardir API saat pengguna mengetik.*

#### **Bagian 4: Jetpack Compose UI & Material 3 (1 Menit)**
> *"Ketiga, pada bagian UI, kita menggunakan Jetpack Compose dengan Material Design 3. Untuk menampilkan daftar resep, pengguna dapat memilih antara `LazyVerticalGrid` atau `LazyColumn`. Gambar di-load secara asynchronous menggunakan Coil `AsyncImage`. Pada Halaman Detail (`DetailScreen`), terdapat checklist bahan-bahan yang interaktif serta tombol Intent untuk membagikan resep dan membuka video tutorial YouTube."*

#### **Bagian 5: Penutup (15 Detik)**
> *"Sekian penjelasan implementasi kode aplikasi Katalog Resep Makanan ini. Terima kasih!"*

---

*Disusun untuk Tugas Responsi Perkuliahan Mobile Programming.*
