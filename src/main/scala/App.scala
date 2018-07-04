import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.{AffineTransformOp,BufferedImage}
import java.io.File
import javax.imageio.ImageIO
import javax.sound.midi._
import scala.collection.mutable.HashMap

object App {

    val NOTE_NAMES = Array("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");

    def main(args: Array[String]): Unit = {
        if(args.size < 1){
            System.err.println("Usage: app file.midi")
            System.exit(1)
        }

        val sequence = MidiSystem.getSequence(new File(args(0)))

        printInfo(sequence)

        renderPNG(sequence)

    }

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

    def printInfo(sequence: Sequence) =
        (sequence.getTracks zipWithIndex) foreach { case (track, trackNumber) =>
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();
            for (i <- 0 until track.size()) { 
                val event = track.get(i);
                System.out.print("@" + event.getTick() + " ");
                val message = event.getMessage();
                message match {
                    case (sm:ShortMessage) => {
                        System.out.print("Channel: " + sm.getChannel() + " ");
                        if (sm.getCommand() == ShortMessage.NOTE_ON) {
                            val key = sm.getData1();
                            val octave = (key / 12)-1;
                            val note = key % 12;
                            val noteName = NOTE_NAMES(note);
                            val velocity = sm.getData2();
                            System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        } else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                            val key = sm.getData1();
                            val octave = (key / 12)-1;
                            val note = key % 12;
                            val noteName = NOTE_NAMES(note);
                            val velocity = sm.getData2();
                            System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        } else {
                            System.out.println("Command:" + sm.getCommand());
                        }
                    }
                    case (message) =>
                        System.out.println("Other message: " + message.getClass());

                }
            }

            System.out.println();
        }
}

