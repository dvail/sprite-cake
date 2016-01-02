package com.dvail.cake

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.moandjiezana.toml.Toml
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class CakeGame : Game() {
    val inputDir = "./recipes/humanoid/"
    val outputDir = "/tmp/sprite/"

    override fun create() {
        setScreen(TestScreen)
        createTest()
    }

    override fun render() {
        Gdx.gl.glClearColor(0.4f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    private fun createTest() {
        File(outputDir).mkdirs()
        buildSprites()
        packSprites("humanoid")
    }

    private fun buildSprites() {
        val recipe = Toml().read(File("${inputDir}recipe.toml"))
        recipe.getTables("animations").forEach { buildFrames(it) }
    }

    private fun buildFrames(animation: Toml) {
        val direction = animation.getString("direction")
        val name = animation.getString("name")
        val frames = animation.getTables("frames")

        frames.forEachIndexed { index, frames ->
            val frame = combineFrames(frames.getTables("stack"))
            val file = File("$outputDir$direction-$name-$index.png")
            ImageIO.write(frame, "png", file)
        }
    }

    private fun combineFrames(frameStack: List<Toml>) : BufferedImage {
       val frames = frameStack.map { it ->
            val file = File("$inputDir${it.getString("img")}")
            ImageIO.read(file)
        }
        val height = frames.first().height
        val width = frames.first().width
        val combined = BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB)
        val graphics = combined.graphics

        frameStack.zip(frames).forEach { pair ->
            val toml = pair.first
            graphics.drawImage(pair.second, toml.getLong("offset-x").toInt(), toml.getLong("offset-y").toInt(), null)
        }
        graphics.dispose()

        return combined
    }

    private fun packSprites(packName: String) {
        TexturePacker.process(outputDir, outputDir, packName)
    }

}

object TestScreen : Screen {
    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun hide() {
    }

    override fun render(delta: Float) {
    }

    override fun resume() {
    }

    override fun dispose() {
    }

    override fun show() {
    }
}
