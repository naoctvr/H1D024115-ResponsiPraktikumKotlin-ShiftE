# 🍳 ResepKu
> Aplikasi Katalog dan Eksplorasi Resep Makanan Berbasis Android dengan Retrofit REST API, Jetpack Compose, & MVVM Architecture

---

## 👤 Identitas Praktikan
- **Nama Lengkap:** Nayla Octavia Ramadhani
- **NIM:** H1D024115
- **Shift Awal:** Shift I
- **Shift Akhir:** Shift E
- **Link Video Demo/Penjelasan:** (httpshttps://youtu.be/1EAvTE9_lAc?si=O3IbV_lJFUlyYN0S)

---

## 📱 Deskripsi Aplikasi
Aplikasi **ResepKu** merupakan aplikasi mobile Android yang berfungsi sebagai platform pencarian dan panduan memasak interaktif. Aplikasi ini mengambil data resep secara *real-time* dari [TheMealDB REST API](https://www.themealdb.com/api.php). 

Aplikasi ini dibuat untuk menyelesaikan permasalahan pengguna dalam menemukan inspirasi resep makanan, membaca takaran bahan secara terstruktur dengan checklist interaktif, serta mengakses tutorial video memasak secara langsung. Dirancang dengan pola arsitektur **MVVM (Model-View-ViewModel)** dan antarmuka modern berbasi **Jetpack Compose (Material Design 3)**.

---

## 🛠️ Penjelasan Teknis

### 1. Spesifikasi & Tech Stack
- **Bahasa:** Kotlin (JVM Target 17)
- **UI Framework:** Jetpack Compose (Material 3)
- **Min SDK:** 24 (Android 7.0) | **Target SDK:** 34 (Android 14)
- **Pola Arsitektur:** MVVM (Model-View-ViewModel) + Clean Architecture Principles
- **Library Utama:**
  - `Navigation Compose` (Routing & Navigasi antar halaman `Home` dan `Detail`)
  - `ViewModel` & `StateFlow` (State Management UI berbasis reaktif `Sealed Interface`)
  - `Retrofit 2` & `Gson` & `OkHttp Logging Interceptor` (Networking / REST API TheMealDB)
  - `Coil Compose` (Asynchronous Image Loading dengan efek fade-in)
  - `Kotlin Coroutines` (Asynchronous processing & Debouncing search 500ms)

### 2. Fitur Utama
- **Pencarian Resep & Filter Kategori (Real-time & Debounced):**
  Menggunakan `StateFlow` dan Coroutine `Job` dengan penundaan 500ms (*debouncing*) untuk efisiensi panggilan API. Pengguna dapat mencari berdasarkan nama makanan atau memfilter via *Category Chips* (Chicken, Beef, Seafood, Dessert, dll).
- **Detail Resep & Checklist Bahan Interaktif:**
  Menampilkan informasi lengkap resep (kategori, negara asal, instruksi memasak berurutan) serta *checklist* bahan makanan yang dapat ditandai interaktif saat memasak.
- **Tampilan UI Dynamic State & Mode Tampilan Flexible:**
  Mendukung 4 UI State (`Loading`, `Success`, `Error`, dan `Empty State`) serta fitur toggle mode tampilan antara **Grid 2-Kolom** (`LazyVerticalGrid`) dan **List 1-Kolom** (`LazyColumn`).
- **Integrasi Intent Media (YouTube & Share):**
  Memanfaatkan Android `Intent` untuk membuka video tutorial memasak di YouTube dan membagikan resep ke aplikasi lain (WhatsApp, Telegram, Mail, dll).

### 3. Struktur Direktori Proyek
```text
app/src/main/java/com/example/katalogresep/
├── data/
│   ├── model/         # Data Classes DTO (MealDto, MealResponseDto) & Domain Model (Recipe, Ingredient)
│   ├── remote/        # RetrofitClient & ApiService (Endpoint REST API)
│   └── repository/    # RecipeRepository & RecipeRepositoryImpl (Abstraksi data & mapper)
├── ui/
│   ├── components/    # Reusable UI (SearchBarComponent, RecipeCard, CategoryChips, Loading/Error/Empty View)
│   ├── detail/        # DetailScreen, DetailViewModel, DetailUiState
│   ├── home/          # HomeScreen, HomeViewModel, HomeUiState
│   ├── navigation/    # AppNavGraph & Screen Route Destinations
│   └── theme/         # Color, Type, Theme Material Design 3 (Warm Culinary Palette)
└── MainActivity.kt    # Single Activity Application Host
```


---

## 🚀 Cara Menjalankan Proyek

1. **Prasyarat:**
   - **Android Studio** (Koala / Ladybug / versi terbaru disarankan).
   - **JDK 17** diatur sebagai Gradle JDK.
   - Perangkat fisik Android (USB Debugging aktif) atau Emulator (API Level 24+).
   - Koneksi internet aktif (untuk fetching data TheMealDB API).

2. **Langkah-langkah:**
   ```bash
   # Clone repository ini
   git clone https://github.com/naoctvr/H1D024115-ResponsiPraktikumKotlin-ShiftE.git
   ```
3. Buka folder proyek di **Android Studio**.
4. Tunggu hingga proses **Gradle Sync** selesai secara otomatis.
5. Pilih target perangkat/emulator, lalu tekan tombol **Run (`Shift + F10`)**.
