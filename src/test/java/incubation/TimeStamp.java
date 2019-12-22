package incubation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeStamp {

    public static void main(final String... args) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(LocalDateTime.now().format(format));
    }
}
