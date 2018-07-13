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

        val matrix = createMatrix(sequence)
        println(matrix)

    }

    val COLORS = Array(Aqua, Black, Blue, Fuchsia, Gray, Green, Lime, Maroon, Navy, Olive, Purple, Red, Silver, Teal, White, Yellow)


    def createMatrix(sequence: Sequence) = {
        val width = 128
        val matrix = new HashMap[(Int, Int, Int, Int), Option[Int]]().withDefaultValue(None)

        (sequence.getTracks zipWithIndex) foreach { case (track, trackNumber) =>
            val height = track.ticks.toInt

            val activeNotes = new HashMap[(Int, Int), (Long, Int)]()

            (0 until track.size) map {track get _} foreach { event =>
                val tick = event.getTick
                event.getMessage match {
                    case (sm:ShortMessage) if (sm.getCommand == ShortMessage.NOTE_ON) => {
                        val channel = sm.getChannel
                        val key = sm.getData1
                        val velocity = sm.getData2

                        activeNotes += (((channel, key), (tick, velocity)))
                    }
                    case (sm:ShortMessage) if (sm.getCommand == ShortMessage.NOTE_OFF) => {
                        val channel = sm.getChannel
                        val key = sm.getData1
                        val endVelocity = sm.getData2
                        val (startTick, velocity) = activeNotes((channel, key))
                        val endTick = tick
                        activeNotes -= ((channel, key))

                       
                        (startTick until endTick) foreach {tick =>
                          matrix += ((trackNumber, channel, key, tick.toInt) -> Some(velocity))
                        }
                        matrix += ((trackNumber, channel, key, endTick.toInt) -> Some(endVelocity))

                    }
                    case _ => {}
                }
            }
        }
        matrix
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

