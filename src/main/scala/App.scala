import javax.sound.midi._
import java.io.File

object App {

    val NOTE_ON = 0x90;
    val NOTE_OFF = 0x80;
    val NOTE_NAMES = Array("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");

    def main(args: Array[String]): Unit = {
        if(args.size < 1){
            System.err.println("Usage: app file.midi")
            System.exit(1)
        }

        val sequence = MidiSystem.getSequence(new File(args(0)))

        printInfo(sequence)

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
                        if (sm.getCommand() == NOTE_ON) {
                            val key = sm.getData1();
                            val octave = (key / 12)-1;
                            val note = key % 12;
                            val noteName = NOTE_NAMES(note);
                            val velocity = sm.getData2();
                            System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        } else if (sm.getCommand() == NOTE_OFF) {
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

