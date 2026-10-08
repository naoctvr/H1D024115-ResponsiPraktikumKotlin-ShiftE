package com.example.katalogresep.data.model

import com.google.gson.annotations.SerializedName

data class MealDto(
    @SerializedName("idMeal") val idMeal: String?,
    @SerializedName("strMeal") val strMeal: String?,
    @SerializedName("strCategory") val strCategory: String?,
    @SerializedName("strArea") val strArea: String?,
    @SerializedName("strInstructions") val strInstructions: String?,
    @SerializedName("strMealThumb") val strMealThumb: String?,
    @SerializedName("strTags") val strTags: String?,
    @SerializedName("strYoutube") val strYoutube: String?,
    @SerializedName("strSource") val strSource: String?,

    @SerializedName("strIngredient1") val strIngredient1: String?,
    @SerializedName("strIngredient2") val strIngredient2: String?,
    @SerializedName("strIngredient3") val strIngredient3: String?,
    @SerializedName("strIngredient4") val strIngredient4: String?,
    @SerializedName("strIngredient5") val strIngredient5: String?,
    @SerializedName("strIngredient6") val strIngredient6: String?,
    @SerializedName("strIngredient7") val strIngredient7: String?,
    @SerializedName("strIngredient8") val strIngredient8: String?,
    @SerializedName("strIngredient9") val strIngredient9: String?,
    @SerializedName("strIngredient10") val strIngredient10: String?,
    @SerializedName("strIngredient11") val strIngredient11: String?,
    @SerializedName("strIngredient12") val strIngredient12: String?,
    @SerializedName("strIngredient13") val strIngredient13: String?,
    @SerializedName("strIngredient14") val strIngredient14: String?,
    @SerializedName("strIngredient15") val strIngredient15: String?,
    @SerializedName("strIngredient16") val strIngredient16: String?,
    @SerializedName("strIngredient17") val strIngredient17: String?,
    @SerializedName("strIngredient18") val strIngredient18: String?,
    @SerializedName("strIngredient19") val strIngredient19: String?,
    @SerializedName("strIngredient20") val strIngredient20: String?,

    @SerializedName("strMeasure1") val strMeasure1: String?,
    @SerializedName("strMeasure2") val strMeasure2: String?,
    @SerializedName("strMeasure3") val strMeasure3: String?,
    @SerializedName("strMeasure4") val strMeasure4: String?,
    @SerializedName("strMeasure5") val strMeasure5: String?,
    @SerializedName("strMeasure6") val strMeasure6: String?,
    @SerializedName("strMeasure7") val strMeasure7: String?,
    @SerializedName("strMeasure8") val strMeasure8: String?,
    @SerializedName("strMeasure9") val strMeasure9: String?,
    @SerializedName("strMeasure10") val strMeasure10: String?,
    @SerializedName("strMeasure11") val strMeasure11: String?,
    @SerializedName("strMeasure12") val strMeasure12: String?,
    @SerializedName("strMeasure13") val strMeasure13: String?,
    @SerializedName("strMeasure14") val strMeasure14: String?,
    @SerializedName("strMeasure15") val strMeasure15: String?,
    @SerializedName("strMeasure16") val strMeasure16: String?,
    @SerializedName("strMeasure17") val strMeasure17: String?,
    @SerializedName("strMeasure18") val strMeasure18: String?,
    @SerializedName("strMeasure19") val strMeasure19: String?,
    @SerializedName("strMeasure20") val strMeasure20: String?
)


fun translateCategoryToIndonesian(category: String?): String {
    if (category.isNullOrBlank()) return "Umum"
    return when (category.lowercase().trim()) {
        "chicken" -> "Ayam"
        "beef" -> "Daging Sapi"
        "seafood" -> "Makanan Laut"
        "dessert" -> "Pencuci Mulut"
        "vegetarian" -> "Vegetarian"
        "breakfast" -> "Sarapan"
        "side" -> "Pelengkap"
        "pasta" -> "Pasta"
        "goat", "lamb" -> "Daging Kambing"
        "miscellaneous" -> "Lainnya"
        else -> category
    }
}


fun MealDto.toRecipe(): Recipe {
    return Recipe(
        id = idMeal ?: "",
        name = strMeal ?: "Resep Tanpa Nama",
        category = translateCategoryToIndonesian(strCategory),
        area = strArea ?: "Internasional",
        instructions = strInstructions ?: "",
        thumbUrl = strMealThumb ?: "",
        youtubeUrl = strYoutube?.takeIf { it.isNotBlank() },
        sourceUrl = strSource?.takeIf { it.isNotBlank() },
        tags = strTags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        ingredients = extractIngredients()
    )
}

fun MealDto.extractIngredients(): List<Ingredient> {
    val rawIngredients = listOf(
        strIngredient1 to strMeasure1,
        strIngredient2 to strMeasure2,
        strIngredient3 to strMeasure3,
        strIngredient4 to strMeasure4,
        strIngredient5 to strMeasure5,
        strIngredient6 to strMeasure6,
        strIngredient7 to strMeasure7,
        strIngredient8 to strMeasure8,
        strIngredient9 to strMeasure9,
        strIngredient10 to strMeasure10,
        strIngredient11 to strMeasure11,
        strIngredient12 to strMeasure12,
        strIngredient13 to strMeasure13,
        strIngredient14 to strMeasure14,
        strIngredient15 to strMeasure15,
        strIngredient16 to strMeasure16,
        strIngredient17 to strMeasure17,
        strIngredient18 to strMeasure18,
        strIngredient19 to strMeasure19,
        strIngredient20 to strMeasure20
    )

    return rawIngredients.mapNotNull { (ingredient, measure) ->
        val cleanIngredient = ingredient?.trim()
        val cleanMeasure = measure?.trim()

        if (!cleanIngredient.isNullOrEmpty() && cleanIngredient.lowercase() != "null") {
            Ingredient(
                name = translateIngredientToIndonesian(cleanIngredient),
                measure = if (!cleanMeasure.isNullOrEmpty() && cleanMeasure.lowercase() != "null") cleanMeasure else "Secukupnya"
            )
        } else {
            null
        }
    }
}


fun translateIngredientToIndonesian(name: String): String {
    val clean = name.trim()
    val lower = clean.lowercase()

    val translations = mapOf(
        "chicken breast" to "Dada Ayam",
        "chicken thighs" to "Paha Ayam",
        "chicken" to "Daging Ayam",
        "minced beef" to "Daging Sapi Cincang",
        "beef" to "Daging Sapi",
        "garlic" to "Bawang Putih",
        "onion" to "Bawang Bombay",
        "red onion" to "Bawang Merah",
        "shallots" to "Bawang Merah",
        "spring onions" to "Daun Bawang",
        "salt" to "Garam",
        "black pepper" to "Lada Hitam",
        "pepper" to "Merica / Lada",
        "water" to "Air",
        "olive oil" to "Minyak Zaitun",
        "vegetable oil" to "Minyak Goreng",
        "oil" to "Minyak Goreng",
        "butter" to "Mentega",
        "soy sauce" to "Kecap Asin / Manis",
        "brown sugar" to "Gula Merah",
        "sugar" to "Gula Pasir",
        "lemon juice" to "Air Perasan Lemon",
        "lemon" to "Jeruk Lemon",
        "lime" to "Jeruk Nipis",
        "egg" to "Telur",
        "eggs" to "Telur",
        "plain flour" to "Tepung Terigu",
        "flour" to "Tepung Terigu",
        "milk" to "Susu Segar",
        "parmesan" to "Keju Parmesan",
        "mozzarella" to "Keju Mozzarella",
        "cheese" to "Keju",
        "heavy cream" to "Krim Kental",
        "coconut milk" to "Santan Kelapa",
        "ginger" to "Jahe",
        "red chili" to "Cabai Merah",
        "chili" to "Cabai",
        "tomato paste" to "Pasta Tomat",
        "tomatoes" to "Tomat",
        "tomato" to "Tomat",
        "rice" to "Nasi / Beras",
        "noodles" to "Mie",
        "spaghetti" to "Spaghetti / Pasta",
        "bread" to "Roti",
        "parsley" to "Daun Seledri / Parsley",
        "coriander" to "Daun Ketumbar"
    )

    return translations[lower] ?: translations.entries.firstOrNull { lower.contains(it.key) }?.value ?: clean.replaceFirstChar { it.uppercase() }
}


fun String.toInstructionSteps(): List<String> {
    if (this.isBlank()) {
        return listOf(
            "Siapkan seluruh bahan dan bumbu dapur yang diperlukan, cuci bersih bahan segar.",
            "Panaskan minyak atau mentega di wajan dengan api sedang, lalu tumis bumbu hingga harum.",
            "Masukkan bahan utama (daging/ayam/seafood) dan aduk hingga meresap dan matang sempurna.",
            "Bumbui dengan garam, merica, dan penyedap rasa sesuai selera.",
            "Angkat dan sajikan masakan selagi hangat bersama nasi atau pelengkap favorit."
        )
    }

    val rawSteps = this
        .replace(Regex("(?i)STEP\\s*\\d+:?"), "\n")
        .replace(Regex("\\b(\\d+)\\.\\s+"), "\n")
        .split(Regex("\r?\n+|\\.\\s+(?=[A-Z])"))
        .map { it.trim().trimStart('.', '-', '•', ' ') }
        .filter { it.length > 5 }

    val translatedSteps = rawSteps.map { translateSentenceToIndonesian(it) }

    return if (translatedSteps.isNotEmpty()) {
        translatedSteps
    } else {
        listOf(
            "Siapkan seluruh bahan dan bumbu dapur yang diperlukan, cuci bersih bahan segar.",
            "Panaskan minyak atau mentega di wajan dengan api sedang, lalu tumis bumbu hingga harum.",
            "Masukkan bahan utama dan aduk merata hingga meresap dan matang sempurna.",
            "Angkat dan sajikan hidangan lezat selagi hangat."
        )
    }
}


fun translateSentenceToIndonesian(sentence: String): String {
    var text = sentence.trim()

    val phraseMap = listOf(
        "preheat the oven to" to "Panaskan oven hingga suhu",
        "preheat oven to" to "Panaskan oven hingga suhu",
        "heat the oil in a large" to "Panaskan minyak di wajan besar",
        "heat the oil in a" to "Panaskan minyak di wajan",
        "heat oil in a" to "Panaskan minyak di wajan",
        "heat oil in" to "Panaskan minyak dalam",
        "heat the oil" to "Panaskan minyak goreng",
        "heat oil" to "Panaskan minyak",
        "add the chicken pieces" to "Masukkan potongan daging ayam",
        "add the chicken" to "Masukkan potongan daging ayam",
        "add chicken" to "Masukkan potongan daging ayam",
        "add the beef" to "Masukkan potongan daging sapi",
        "add beef" to "Masukkan potongan daging sapi",
        "add the garlic and onion" to "Tumis bawang putih dan bawang merah",
        "add garlic and onion" to "Tumis bawang putih dan bawang merah",
        "add the garlic" to "Masukkan bawang putih cincang",
        "add garlic" to "Masukkan bawang putih",
        "add the onion" to "Masukkan irisan bawang merah/bombay",
        "add onion" to "Masukkan irisan bawang",
        "season with salt and pepper" to "Bumbui dengan garam dan merica secukupnya",
        "season generously with salt" to "Bumbui dengan garam secukupnya",
        "bring to a boil" to "Didihkan kuah hingga mendidih",
        "bring to the boil" to "Didihkan kuah hingga mendidih",
        "bring to a simmer" to "Masak dengan api kecil hingga mendidih perlahan",
        "reduce heat and simmer" to "Kecilkan api dan masak hingga meresap",
        "stir well to combine" to "Aduk rata hingga seluruh bahan tercampur",
        "stir well" to "Aduk hingga tercampur rata",
        "stir occasionally" to "Aduk sesekali agar tidak gosong",
        "stir continuously" to "Aduk terus-menerus",
        "cook for" to "Masak selama",
        "simmer for" to "Masak dengan api kecil selama",
        "bake for" to "Panggang dalam oven selama",
        "fry until golden brown" to "Goreng hingga berwarna kuning keemasan",
        "cook until golden brown" to "Masak hingga berwarna kuning keemasan",
        "cook until tender" to "Masak hingga empuk dan matang",
        "serve hot with" to "Sajikan selagi hangat bersama",
        "serve with" to "Sajikan bersama",
        "serve hot" to "Sajikan selagi hangat",
        "serve immediately" to "Sajikan segera selagi hangat",
        "remove from heat" to "Angkat dari kompor",
        "place in the oven" to "Masukkan ke dalam oven",
        "medium heat" to "api sedang",
        "low heat" to "api kecil",
        "high heat" to "api besar",
        "golden brown" to "kuning keemasan",
        "large bowl" to "mangkuk besar",
        "small bowl" to "mangkuk kecil",
        "frying pan" to "wajan penggorengan",
        "saucepan" to "panci saus",
        "casserole dish" to "panci casserole",
        "baking sheet" to "loyang panggang",
        "paper towels" to "tisu dapur"
    )

    for ((eng, indo) in phraseMap) {
        text = text.replace(Regex("(?i)\\b" + Regex.escape(eng) + "\\b"), indo)
    }

    val wordMap = listOf(
        "preheat" to "panaskan",
        "heat" to "panaskan",
        "add" to "tambahkan",
        "pour" to "tuangkan",
        "mix" to "campurkan",
        "stir" to "aduk",
        "cook" to "masak",
        "fry" to "goreng",
        "boil" to "didihkan",
        "bake" to "panggang",
        "roast" to "panggang",
        "slice" to "iris",
        "chop" to "cincang",
        "dice" to "potong dadu",
        "mince" to "cincang halus",
        "garnish" to "taburi / hias",
        "season" to "bumbui",
        "pan" to "wajan",
        "skillet" to "wajan",
        "pot" to "panci",
        "oven" to "oven",
        "bowl" to "mangkuk",
        "plate" to "piring",
        "water" to "air",
        "oil" to "minyak",
        "butter" to "mentega",
        "salt" to "garam",
        "pepper" to "merica",
        "sugar" to "gula",
        "garlic" to "bawang putih",
        "onion" to "bawang merah/bombay",
        "onions" to "bawang merah/bombay",
        "ginger" to "jahe",
        "chili" to "cabai",
        "chillies" to "cabai",
        "red chili" to "cabai merah",
        "chicken" to "daging ayam",
        "beef" to "daging sapi",
        "fish" to "ikan",
        "shrimp" to "udang",
        "prawns" to "udang",
        "rice" to "nasi",
        "sauce" to "saus",
        "soup" to "sup",
        "cream" to "krim",
        "cheese" to "keju",
        "egg" to "telur",
        "eggs" to "telur",
        "flour" to "tepung",
        "milk" to "susu",
        "lemon" to "lemon",
        "lime" to "jeruk nipis",
        "minutes" to "menit",
        "mins" to "menit",
        "min" to "menit",
        "hours" to "jam",
        "hrs" to "jam",
        "until" to "hingga",
        "then" to "kemudian",
        "with" to "dengan",
        "and" to "dan",
        "for" to "selama",
        "into" to "ke dalam",
        "about" to "sekitar",
        "tender" to "empuk",
        "soft" to "halus",
        "golden" to "keemasan",
        "brown" to "kecokelatan",
        "hot" to "hangat",
        "warm" to "hangat",
        "fresh" to "segar",
        "serve" to "sajikan",
        "drain" to "tiriskan",
        "wash" to "cuci"
    )

    for ((eng, indo) in wordMap) {
        text = text.replace(Regex("(?i)\\b" + Regex.escape(eng) + "\\b"), indo)
    }

    text = text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    if (!text.endsWith('.')) {
        text += "."
    }

    return text
}
