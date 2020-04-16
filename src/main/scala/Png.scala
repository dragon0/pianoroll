import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.{AffineTransformOp,BufferedImage}
import java.io.File
import javax.imageio.ImageIO
import javax.sound.midi._
import scala.collection.mutable.HashMap

object Png {
    val COLORS = Array(Aqua, Black, Blue, Fuchsia, Gray, Green, Lime, Maroon, Navy, Olive, Purple, Red, Silver, Teal, White, Yellow)


    def renderPNG(sequence: Sequence) = {
        val width = 128
        var images: Seq[BufferedImage] = Vector()

        (sequence.getTracks zipWithIndex) foreach { case (track, trackNumber) =>
            val height = track.ticks.toInt

            val activeNotes = new HashMap[(Int, Int), Long]()

            val bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            images = images :+ bi
            val g = bi.createGraphics()

            g.setBackground(new Color(0, 0, 0, 0))
            g.clearRect(0, 0, width, height)

            (0 until track.size) map {track get _} foreach { event =>
                val tick = event.getTick
                event.getMessage match {
                    case (sm:ShortMessage) if (sm.getCommand == ShortMessage.NOTE_ON) => {
                        val channel = sm.getChannel
                        val key = sm.getData1
                        val velocity = sm.getData2
                        activeNotes += (((channel, key), tick))
                    }
                    case (sm:ShortMessage) if (sm.getCommand == ShortMessage.NOTE_OFF) => {
                        val channel = sm.getChannel
                        val key = sm.getData1
                        val velocity = sm.getData2
                        val startTick = activeNotes((channel, key))
                        activeNotes -= ((channel, key))

                        g.setColor(COLORS(channel))
                        g.drawLine(key, startTick.toInt, key, tick.toInt)
                    }
                    case _ => {}
                }
            }
        }

        val maxHeight = images map {_ getHeight} max
        val bi = new BufferedImage(width, maxHeight, BufferedImage.TYPE_INT_ARGB)
        val g = bi.createGraphics()
        val transform = new AffineTransform(1f,0f,0f,1f,0,0)
        g.setBackground(Color.WHITE)
        g.clearRect(0, 0, width, maxHeight)
        images foreach { g.drawImage(_, transform, null) }

        val tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -bi.getHeight(null));
        val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        ImageIO.write(op.filter(bi, null), "PNG", new File("output.png"))
    }
}

