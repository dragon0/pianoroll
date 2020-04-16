import java.io.File
import javax.sound.midi.MidiSystem

object App {

    def main(args: Array[String]): Unit = {
        if(args.size < 1){
            System.err.println("Usage: app file.midi")
            System.exit(1)
        }

        val sequence = MidiSystem.getSequence(new File(args(0)))

        Debug.printInfo(sequence)

        Png.renderPNG(sequence)

    }
}

