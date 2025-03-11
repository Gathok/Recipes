package de.malteans.recipes

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform