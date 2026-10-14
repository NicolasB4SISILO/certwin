package br.com.gerenciar.tls.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class FileLogFormatter extends Formatter{
    @Override 
    public String format(LogRecord record) {
        StringBuilder linha = new StringBuilder();
        linha.append(Instant.ofEpochMilli(record.getMillis())).append(' ');
        linha.append('[').append(record.getLevel()).append("] ");
        linha.append(record.getLoggerName()).append( " - ");
        linha.append(formatMessage(record)).append(System.lineSeparator());

        if(record.getThrown() != null) {
            StringWriter stringWriter = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(stringWriter));
            linha.append(stringWriter);
        }

        return linha.toString();
    }
}
