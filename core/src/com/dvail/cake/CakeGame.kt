package com.dvail.cake

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.moandjiezana.toml.Toml
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO

enum class SpriteSlice {
    Background, Foreground, Body, Hair,
    EqBody, EqHead, EqFeet, EqLegs, EqHands, EqWeapon
}

enum class SpriteDirection {
    side, front, back
}


class CakeGame : Game() {
    val baseDir = "./recipes/humanoid/"
    val outputDir = "/tmp/sprite/"

    val SLICE_ORDER = HashMap<SpriteDirection, Array<SpriteSlice>>()

    // TODO Pull this out into a top level animation config
    val ANIMATIONS = arrayOf(
            Pair("melee-slash", 5)
    )

    init {
        val frontOrder = arrayOf(
                SpriteSlice.EqWeapon, SpriteSlice.Body, SpriteSlice.EqHands, SpriteSlice.EqFeet,
                SpriteSlice.EqLegs, SpriteSlice.EqBody, SpriteSlice.Hair, SpriteSlice.EqHead)

        val backOrder = arrayOf(
                SpriteSlice.Body, SpriteSlice.EqFeet, SpriteSlice.EqLegs, SpriteSlice.EqBody,
                SpriteSlice.EqHands, SpriteSlice.EqWeapon, SpriteSlice.Hair, SpriteSlice.EqHead)

        SLICE_ORDER.put(SpriteDirection.side, frontOrder)
        SLICE_ORDER.put(SpriteDirection.front, frontOrder)
        SLICE_ORDER.put(SpriteDirection.back, backOrder)
    }

    override fun create() {
        setScreen(TestScreen)

        val slices = hashMapOf(
                Pair(SpriteSlice.Body, "body/male-default/"),
                Pair(SpriteSlice.Hair, "hair/plain-mop/"),
                Pair(SpriteSlice.EqFeet, "eqfeet/cloth-shoes/"),
                Pair(SpriteSlice.EqLegs, "eqlegs/cloth-pants/"),
                Pair(SpriteSlice.EqBody, "eqbody/cloth-armor/")
        )

        createTest(slices)
    }

    override fun render() {
        Gdx.gl.glClearColor(0.4f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    private fun createTest(slices: HashMap<SpriteSlice, String>) {
        // TODO iterate directions here
        buildSprites(slices, SpriteDirection.side)
        // packSprites("humanoid")
    }

    private fun buildSprites(slices: HashMap<SpriteSlice, String>, direction: SpriteDirection) {
        val ordering = SLICE_ORDER[direction]
        val sliceConfigs = ArrayList<Pair<String, Toml>>(slices.size)

        ordering?.forEach { slice ->
            val slicePath = slices[slice]
            val recipe = File("$baseDir${slicePath}recipe.toml")

            if (slicePath != null && recipe.exists()) {
                sliceConfigs.add(Pair(slicePath, Toml().read(recipe)))
            }
        }

        ANIMATIONS.forEach {
            buildFrames(sliceConfigs, direction, it)
        }

    }


    // TODO This isnt working, looks like it is overwriting the file for each layer
    private fun buildFrames(recipes: List<Pair<String, Toml>>, direction: SpriteDirection, animConf: Pair<String, Int>) {
        for (i in 0..animConf.second - 1) {

            val outFile = File("$outputDir$direction-${animConf.first}-$i.png")
            outFile.mkdirs()

            var combined: BufferedImage? = null

            recipes.forEach { recipe ->
                val animation = recipe.second.getTable(direction.name)?.getTable(animConf.first)
                val frames = animation?.getTables("frames")

                if (frames == null || frames.size != animConf.second) {
                    println("Frame count invalid")
                } else {

                    val frameImages = frames.map {
                        val file = File("$baseDir${recipe.first}${it.getString("img")}")
                        ImageIO.read(file)
                    }

                    val height = frameImages.first().height
                    val width = frameImages.first().width
                    if (combined == null) {
                        combined = BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB)
                    }
                    val graphics = combined?.graphics

                    graphics?.drawImage(frameImages[i], frames[i].getLong("offset-x").toInt(), frames[i].getLong("offset-y").toInt(), null)

                }
            }

            ImageIO.write(combined, "png", outFile)
        }

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
