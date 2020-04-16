import java.awt.Color

sealed abstract class WebsafeColor(r:Int, g:Int, b:Int) extends Color(r, g, b){
    def this(c:Color) = this(c.getRed, c.getGreen, c.getBlue)
}

case object Aqua	extends WebsafeColor(Color.CYAN)
case object Black	extends WebsafeColor(Color.BLACK)
case object Blue	extends WebsafeColor(Color.BLUE)
case object Fuchsia	extends WebsafeColor(Color.MAGENTA)
case object Gray	extends WebsafeColor(Color.GRAY)
case object Green	extends WebsafeColor(0x00, 0x80, 0x00)
case object Lime	extends WebsafeColor(Color.GREEN)
case object Maroon	extends WebsafeColor(0x80, 0x00, 0x00)
case object Navy	extends WebsafeColor(0x00, 0x00, 0x80)
case object Olive	extends WebsafeColor(0x80, 0x80, 0x00)
case object Purple	extends WebsafeColor(0x80, 0x00, 0x80)
case object Red 	extends WebsafeColor(Color.RED)
case object Silver	extends WebsafeColor(0xC0, 0xC0, 0xC0)
case object Teal	extends WebsafeColor(0x00, 0x80, 0x80)
case object White	extends WebsafeColor(Color.WHITE)
case object Yellow	extends WebsafeColor(Color.YELLOW)

