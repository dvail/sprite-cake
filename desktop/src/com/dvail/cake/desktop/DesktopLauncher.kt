package com.dvail.cake.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.dvail.cake.CakeGame

object DesktopLauncher {
    @JvmStatic fun main(arg: Array<String>) {
        LwjglApplication(CakeGame(), "Sprite Cake", 200, 150)
    }
}
