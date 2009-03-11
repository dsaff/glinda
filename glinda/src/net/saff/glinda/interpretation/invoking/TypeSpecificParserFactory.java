package net.saff.glinda.interpretation.invoking;

import net.saff.glinda.book.TypeSpecificParser;

import java.lang.reflect.Type;

// TODO: Control-F11 re-runs tests
public interface TypeSpecificParserFactory {
	TypeSpecificParser parserFor(Type type);
}
