import javax.sound.midi._
object Debug {
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
                            val noteName = MidiUtil.NOTE_NAMES(note);
                            val velocity = sm.getData2();
                            System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        } else if (sm.getCommand() == ShortMessage.NOTE_OFF) {
                            val key = sm.getData1();
                            val octave = (key / 12)-1;
                            val note = key % 12;
                            val noteName = MidiUtil.NOTE_NAMES(note);
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

