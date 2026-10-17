package br.com.certwin.log;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public final class ConsoleFormatter extends Formatter {
    @Override 
    public String format(LogRecord record) {
        String prefixo = prefixoPara(record.getLevel());
        return prefixo + formatMessage(record) + System.lineSeparator();
    }

    private static String prefixoPara(Level level) {
        if(level.intValue() >= Level.SEVERE.intValue()) return "[ERRO] ";
        if(level.intValue() >= Level.WARNING.intValue()) return "[AVISO] ";

        return "";
    }
}
