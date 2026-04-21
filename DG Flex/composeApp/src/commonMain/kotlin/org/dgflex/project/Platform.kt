package org.dgflex.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform